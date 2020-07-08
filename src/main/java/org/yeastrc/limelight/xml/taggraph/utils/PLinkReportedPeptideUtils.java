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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yeastrc.limelight.xml.taggraph.objects.PLinkModification;
import org.yeastrc.limelight.xml.taggraph.objects.PLinkPeptide;
import org.yeastrc.limelight.xml.taggraph.objects.PLinkReportedPeptide;
import org.yeastrc.limelight.xml.taggraph.reader.PLinkConstants;
import org.yeastrc.limelight.xml.taggraph.reader.PLinkSearchParameters;

/**
 * Some utility methods for working with reported peptides.
 * 
 * @author Michael Riffle
 * @date Mar 23, 2016
 *
 */
public class PLinkReportedPeptideUtils {

	/**
	 * Get the reported peptide object based on the supplied peptide string and type of peptide
	 * @param plinkPeptideString The peptide, as reported by plink (e.g. "NTLQPVEKALNDAKMDK(14)-KSNISEKTK(1)")
	 * @param peptideType The type, as defined in PLinkConstants (e.g. PLinkConstants.LINK_TYPE_CROSSLINK)
	 * @return The populated reported peptide object
	 * @throws Exception
	 */
	public static PLinkReportedPeptide getReportedPeptide( String plinkPeptideString, String plinkModString, int peptideType, PLinkSearchParameters params ) throws Exception {
		
		if ( peptideType == PLinkConstants.LINK_TYPE_CROSSLINK ) {
			return getReportedPeptideForCrosslink( plinkPeptideString, plinkModString, params );
		}
		
		if( peptideType == PLinkConstants.LINK_TYPE_LOOPLINK ) {
			return getReportedPeptideForLooplink( plinkPeptideString, plinkModString, params );
		}
		
		if( peptideType == PLinkConstants.LINK_TYPE_MONOLINK ) {
			return getReportedPeptideForMonolink( plinkPeptideString, plinkModString, params );
		}
		
		if( peptideType == PLinkConstants.LINK_TYPE_UNLINKED ) {
			return getReportedPeptideForUnlinked( plinkPeptideString, plinkModString, params );
		}
		
		throw new Exception( "unknown peptide type: " + peptideType );
	}

	/**
	 * Get the reported peptide for a crosslink peptide reported by plink
	 * @param plinkPeptideString
	 * @param plinkModString
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private static PLinkReportedPeptide getReportedPeptideForCrosslink( String plinkPeptideString, String plinkModString, PLinkSearchParameters params ) throws Exception {
		
		PLinkReportedPeptide rp = new PLinkReportedPeptide();
		rp.setType( PLinkConstants.LINK_TYPE_CROSSLINK );
		
		// example peptide string: EMNQMTHGDNNEVKR(14)-MTKTKGEK(1)		
		String[] sequences = plinkPeptideString.split( "-" );
		if( sequences.length != 2 )
			throw new Exception( "Did not get two sequences from crosslinked sequence pair: " + plinkPeptideString );
		
		Pattern p = Pattern.compile( "^([A-Z]+)\\((\\d+)\\)$" );

		for( int i = 0; i < 2; i++ ) {
			String sequence = sequences[ i ];
			
			Matcher m = p.matcher( sequence );
			
			if( !m.matches() ) {
				throw new Exception( "Unexpected syntax for looplink peptide: " + sequence );
			}
			
			String parsedSequence = m.group( 1 );
			int position = Integer.parseInt( m.group( 2 ) );
			
			if( position > parsedSequence.length() )
				throw new Exception( "Linked position is outside of peptide: " + plinkPeptideString );
			
			PLinkPeptide pep = new PLinkPeptide();
			pep.setSequence( parsedSequence );
			
			if( i == 0 ) {
				rp.setPeptide1( pep );
				rp.setPosition1( position );
			} else {
				rp.setPeptide2( pep );
				rp.setPosition2( position );
			}
		}


		/*
		 * Add dynamic mods to the two peptides:
		 * 
		 * Mods are not reported separately for each peptide, but altogether
		 * e.g.: KILTTYSVFPAR(1)-MDPSFKTFSRR(6) Oxidation[M](16)
		 * length of first peptide: 12
		 * starting position of second peptide: 16
		 * linked position in second peptide: 1
		 * 
		 * e.g.: YFNDYEPSDPETPIEFKIAK(17)-MDPSFKTFSR(6)   Oxidation[M](24)
		 * length of first peptide: 20
		 * starting position of second peptide: 25
		 * linked position in second peptide: 1
		 * 
		 * This looks to me (Mike Riffle) like we can find the position in the second peptide
		 * by subtracting the length of the first peptide + 3 from the reported position in
		 * the second peptide. I can only hazard a guess as to why this is how it's reported...
		 */
		
		Map<Integer, Collection<PLinkModification>> mods = ModificationLookupUtils.getDynamicModificationsFromModString( plinkModString, params );
		Map<Integer, Collection<PLinkModification>> mods1 = new HashMap<Integer, Collection<PLinkModification>>();	// mods belonging to peptide1
		Map<Integer, Collection<PLinkModification>> mods2 = new HashMap<Integer, Collection<PLinkModification>>();	// mods belonging to peptide2
		for( int modPosition : mods.keySet() ) {
			Map<Integer, Collection<PLinkModification>> tmods = null;
			int position = modPosition;
			
			if( modPosition > rp.getPeptide1().getSequence().length() ) {
				position -= (rp.getPeptide1().getSequence().length() + 3);
				
				if( position < 0 )
					throw new Exception( "Got a bad position (" + position + ") for peptide to in this link: " + plinkPeptideString );
				
				if( position > rp.getPeptide2().getSequence().length() )
					throw new Exception( "Got a bad position (" + position + ") for peptide to in this link: " + plinkPeptideString );
								
				tmods = mods2;
			} else {
				tmods = mods1;
			}
			
			tmods.put( position, mods.get( modPosition ) );
		}
		
		rp.getPeptide1().setMods( mods1 );
		rp.getPeptide2().setMods( mods2 );
		
		
		// ensure peptide1 is never alphabetically greater than peptide1
		if( rp.getPeptide1().getSequence().compareTo( rp.getPeptide2().getSequence() ) > 0 ) {
			
			PLinkPeptide tmpPeptide = rp.getPeptide1();
			int tmpPosition = rp.getPosition1();
			
			rp.setPeptide1( rp.getPeptide2() );
			rp.setPosition1( rp.getPosition2() );
			
			rp.setPeptide2( tmpPeptide );
			rp.setPosition2( tmpPosition);
			
		} else if( rp.getPeptide1().getSequence().equals( rp.getPeptide2().getSequence() ) ) {
			if( rp.getPosition1() > rp.getPosition2() ) {
				int tmpPosition = rp.getPosition1();
				
				rp.setPosition1( rp.getPosition2() );
				rp.setPosition2( tmpPosition );
			}
		}
		
		return rp;
	}
	
	/**
	 * Get the reported peptide for a monolink peptide reported by plink
	 * @param plinkPeptideString
	 * @param plinkModString
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private static PLinkReportedPeptide getReportedPeptideForMonolink( String plinkPeptideString, String plinkModString, PLinkSearchParameters params ) throws Exception {
		
		PLinkReportedPeptide rp = new PLinkReportedPeptide();
		rp.setType( PLinkConstants.LINK_TYPE_MONOLINK );
		
		// example peptide string: VFAPEEISAMVLGKMK(14)

		String sequence = plinkPeptideString;
		Pattern p = Pattern.compile( "^([A-Z]+)\\((\\d+)\\)$" );
		Matcher m = p.matcher( sequence );
		
		if( !m.matches() ) {
			throw new Exception( "Unexpected syntax for looplink peptide: " + sequence );
		}
		
		String parsedSequence = m.group( 1 );
		int position1 = Integer.parseInt( m.group( 2 ) );
		
		
		if( position1 > parsedSequence.length() )
			throw new Exception( "The parsed position from: " + plinkPeptideString + " is greater than the length of the parsed sequence." );
		
		Map<Integer, Collection<PLinkModification>> mods = ModificationLookupUtils.getDynamicModificationsFromModString( plinkModString, params );
		
		// only add monolink as a mod, if the monolink has a mass
		// pLink2 reports monolinks as mass 0.000 for EDC--which is no bueno
		if( params.getLinker().getMonoMonolinkMass() > 0.00001 ) {

			// add monolink as a mod
			if( !mods.containsKey( position1 ) )
				mods.put( position1, new ArrayList<PLinkModification>() );
			
			PLinkModification monoLinkMod = new PLinkModification();
			monoLinkMod.setMonoisotopicMass( params.getLinker().getMonoMonolinkMass() );
			monoLinkMod.setMonolink( true );
			monoLinkMod.setName( "monolink" );
			
			mods.get( position1 ).add( monoLinkMod );
		}
		
		PLinkPeptide pep = new PLinkPeptide();
		pep.setSequence( parsedSequence );
		pep.setMods( mods );
		
		rp.setPeptide1( pep );
		rp.setPosition1( position1 );
		
		return rp;
	}
	
	/**
	 * Get the reported peptide for a looplink peptide reported by plink
	 * @param plinkPeptideString
	 * @param plinkModString
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private static PLinkReportedPeptide getReportedPeptideForLooplink( String plinkPeptideString, String plinkModString, PLinkSearchParameters params ) throws Exception {
		
		PLinkReportedPeptide rp = new PLinkReportedPeptide();
		rp.setType( PLinkConstants.LINK_TYPE_LOOPLINK );
		
		// example peptide string: DEQGENDLAKASQNK(10)(15)
		String sequence = plinkPeptideString;
		Pattern p = Pattern.compile( "^([A-Z]+)\\((\\d+)\\)\\((\\d+)\\)$" );
		Matcher m = p.matcher( sequence );
		
		if( !m.matches() ) {
			throw new Exception( "Unexpected syntax for looplink peptide: " + sequence );
		}
		
		String parsedSequence = m.group( 1 );
		int position1 = Integer.parseInt( m.group( 2 ) );
		int position2 = Integer.parseInt( m.group( 3 ) );
		
		if( position1 > parsedSequence.length() || position2 > parsedSequence.length() )
			throw new Exception( "One of the parsed positions from: " + plinkPeptideString + " is greater than the length of the parsed sequence." );
		
		Map<Integer, Collection<PLinkModification>> mods = ModificationLookupUtils.getDynamicModificationsFromModString( plinkModString, params );
		
		
		PLinkPeptide pep = new PLinkPeptide();
		pep.setSequence( parsedSequence );
		pep.setMods( mods );
		
		rp.setPeptide1( pep );
		rp.setPosition1( position1 );
		rp.setPosition2( position2 );
		
		return rp;
	}
	
	/**
	 * Get the reported peptide for an unlinked peptide reported by plink
	 * @param plinkPeptideString
	 * @param plinkModString
	 * @return
	 * @throws Exception
	 */
	private static PLinkReportedPeptide getReportedPeptideForUnlinked( String plinkPeptideString, String plinkModString, PLinkSearchParameters params ) throws Exception {

		PLinkReportedPeptide rp = new PLinkReportedPeptide();
		rp.setType( PLinkConstants.LINK_TYPE_UNLINKED );
		
		// example peptide string: QRVESHFDLELRASVMHDIVDMMPEGIK
		String sequence = plinkPeptideString;
		
		Map<Integer, Collection<PLinkModification>> mods = ModificationLookupUtils.getDynamicModificationsFromModString( plinkModString, params );
				
		PLinkPeptide pep = new PLinkPeptide();
		pep.setSequence( sequence );
		pep.setMods( mods );
		
		rp.setPeptide1( pep );
		
		return rp;
	}
	
	
	
	

	
}
