/*
 * Original author: Michael Riffle <mriffle .at. uw.edu>
 *                  
 * Copyright 2016-2018 University of Washington - Seattle, WA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.yeastrc.limelight.xml.taggraph.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yeastrc.limelight.xml.taggraph.objects.PLinkModification;
import org.yeastrc.limelight.xml.taggraph.reader.PLinkSearchParameters;

/**
 * Some utility methods for working with modifications.
 * 
 * @author Michael Riffle
 * @date Mar 23, 2016
 *
 */
public class ModificationLookupUtils {

	private static Map<String, PLinkModification> modCache = new HashMap<String, PLinkModification>();
	private static Collection<String> staticModCache = null;
	
	/**
	 * Get the names of all static mods in this search
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Collection<String> getStaticModificationNames( PLinkSearchParameters params ) throws Exception {

		if( staticModCache == null ) {
			Collection<String> mods = new ArrayList<String>();
			
			String numModsString = params.getPlinkINI().getConfig().getString( "modification/fix_total" );
			if( numModsString == null )
				throw new Exception( "Could not determine number of static mods." );
			
			int numMods = Integer.parseInt( numModsString );
			
			for( int i = 1; i <= numMods; i++ ) {
				String modName = params.getPlinkINI().getConfig().getString( "modification/fix_mod" + i );
				if( modName == null )
					throw new Exception( "Could not find pLink/mod.fixed." + i + " in pLink.ini." );
				
				mods.add( modName );
			}
			
			staticModCache = mods;
		}
				
		return staticModCache;
	}

	/**
	 * Get the PLinkModification object for the modification described by the given name in the supplied search params
	 * @param name
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static PLinkModification getPLinkModificationFromParameters( String name, PLinkSearchParameters params ) throws Exception {
		
		if( !modCache.containsKey( name ) ) {
			String definition = params.getModifyINI().getConfig().getString( name );
			modCache.put( name, parsePLinkModificationDefinition( name, definition ) );
		}
		
		return modCache.get( name );
	}
	
	/**
	 * Parse, populate, and return a PLinkModification object w/ the given name and definition (as it appears in the modify.ini file)
	 * @param name
	 * @param definition
	 * @return
	 * @throws Exception
	 */
	protected static PLinkModification parsePLinkModificationDefinition( String name, String definition ) throws Exception {
		
		String[] fields = definition.split( " " );
		
		Collection<String> residues = new HashSet<String>();
		for (int i = 0; i < fields[ 0 ].length(); i++){
		    residues.add( String.valueOf( fields[ 0 ].charAt( i ) ) );
		}
		
		double monoMass = Double.parseDouble( fields[ 2 ] );
//		double avgMass = Double.parseDouble( fields[ 3 ] );
		
		PLinkModification mod = new PLinkModification();
		mod.setName( name );
		mod.setMonoisotopicMass( monoMass );
		mod.setResidues( residues );
		
		return mod;
	}
	
	
	/**
	 * Get all dynamic mods found in a mod string (containing multiple mods) as reported by plink.
	 * E.g., "3,C(Carbamidomethyl[C]);5,K(Methyl[K]);26,M(Oxidation[M]);"
	 * @param plinkModString
	 * @param params
	 * @return A map, keyed by peptide position (starting at one), with the value a collection of modifications at that position
	 * @throws Exception
	 */
	public static Map<Integer, Collection<PLinkModification>> getDynamicModificationsFromModString( String plinkModString, PLinkSearchParameters params ) throws Exception {
		Map<Integer, Collection<PLinkModification>> mods = getModificationsFromModString( plinkModString, params );
		
		// remove the static mods
		Collection<String> staticMods = ModificationLookupUtils.getStaticModificationNames( params );
		Collection<Integer> positionsToRemove = new ArrayList<Integer>();
		
		for( int position : mods.keySet() ) {
			Collection<PLinkModification> dMods = new ArrayList<PLinkModification>();

			// iterate over the set of mods, and add only the dynamic mods to dMods
			for( PLinkModification mod : mods.get( position ) ) {
				if( !staticMods.contains( mod.getName() ) )
						dMods.add( mod );
			}
				
			if( dMods.size() == 0 )
				positionsToRemove.add( position );
			else
				mods.put( position, dMods );
		}
		
		for( int positionToRemove : positionsToRemove )
			mods.remove( positionToRemove );
		
		
		return mods;
	}
	
	
	/**
	 * Get all mods (incl. static) found in a mod string (containing multiple mods) as reported by plink2.
	 * E.g., "Carbamidomethyl[C](4);Carbamidomethyl[C](18)"
	 * @param plinkModString
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Map<Integer, Collection<PLinkModification>> getModificationsFromModString( String plinkModString, PLinkSearchParameters params ) throws Exception {
		Map<Integer, Collection<PLinkModification>> retMap = new HashMap<Integer, Collection<PLinkModification>>();
		
		if( plinkModString.equals( "null" ) )
			return retMap;
		
		String[] modStrings = plinkModString.split( ";" );
		if( modStrings.length < 1 ) {
			throw new Exception( "Unexpected syntax of mod string: " + plinkModString );
		}
		
		// Example mod string: Carbamidomethyl[C](18)  18 == position "Carbamidomethyl[C]" == mod name
		//Pattern p = Pattern.compile( "^(\\d+),\\w{1}\\((.+)\\)$" );
		Pattern p = Pattern.compile( "^(.+)\\((.+)\\)$" );
		
		for( String modString : modStrings ) {
			
			Matcher m = p.matcher( modString );
			if( !m.matches() ) {
				throw new Exception( "Unexpected syntax of reported mod: " + modString );
			}
			
			int position = Integer.parseInt( m.group( 2 ) );
			String name = m.group( 1 );
			
			PLinkModification mod = ModificationLookupUtils.getPLinkModificationFromParameters(name, params);
			
			if( !retMap.containsKey( position ) ) {
				retMap.put( position, new ArrayList<PLinkModification>() );
			}
			
			retMap.get( position ).add( mod );			
		}
		
		return retMap;
	}
	
}
