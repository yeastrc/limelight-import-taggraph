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

import org.yeastrc.limelight.xml.taggraph.objects.PLinkLinker;
import org.yeastrc.limelight.xml.taggraph.objects.PLinkLinkerEnd;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PLinkLinkerUtils {

	/**
	 * Get a PLinkLinker object populated according to the data presented in the space delimited
	 * text associated with a linker, as found in the xlink.ini file
	 *
	 * @param pLinkLinkerTextFormat
	 * @return
	 * @throws Exception
	 */
	public static PLinkLinker getPLinkLinker( String name, String pLinkLinkerTextFormat ) throws Exception {
		PLinkLinker linker = new PLinkLinker();
		
		String[] fields = pLinkLinkerTextFormat.split( " " );

		linker.setName( name );

		List<PLinkLinkerEnd> linkerEnds = new ArrayList<>(2);
		linkerEnds.add(getLinkerEndFromMotif(fields[0]));
		linkerEnds.add(getLinkerEndFromMotif(fields[1]));
		linker.setLinkerEnds(linkerEnds);

		linker.setMonoCrosslinkMass(Double.valueOf(fields[2]));

		linker.setFormula(getFormulaFromMotif(fields[6]));

		if (!fields[4].equals("") && !fields[4].equals("0"))
			linker.setMonoMonolinkMass(Double.valueOf(fields[4]));


		if( fields.length == 10) {

			if (!fields[8].equals("0") || !fields[9].equals("0")) {

				List<Double> cleavedMasses = new ArrayList<>();

				if (!fields[8].equals("0")) {
					cleavedMasses.add(Double.parseDouble(fields[8]));
				}

				if (!fields[9].equals("0")) {
					cleavedMasses.add(Double.parseDouble(fields[9]));
				}

				linker.setCleavedLinkerMasses(cleavedMasses);
			}

			return linker;
		}

		// starting with plink 2.3.9, they added a 1/0 field at position 8 to indicate
		// whether this is a cleavable linker. we'll ignore it and assume it's cleavable if
		// cleaved linker masses are present.
		if( fields.length == 11) {

			if (!fields[9].equals("0") || !fields[10].equals("0")) {

				List<Double> cleavedMasses = new ArrayList<>();

				if (!fields[9].equals("0")) {
					cleavedMasses.add(Double.parseDouble(fields[9]));
				}

				if (!fields[10].equals("0")) {
					cleavedMasses.add(Double.parseDouble(fields[10]));
				}

				linker.setCleavedLinkerMasses(cleavedMasses);
			}

			return linker;
		}

		throw new Exception("Got unexpected number of fields in linker definition in xlink.ini. Got:\n" + pLinkLinkerTextFormat );
	}

	private static PLinkLinkerEnd getLinkerEndFromMotif( String motif ) {

		boolean linksNTerminus = false;
		boolean linksCTerminus = false;
		List<String> residues = new ArrayList<>();

		for (int i = 0; i < motif.length(); i++){
			String residue = String.valueOf( motif.charAt(i) );

			if( residue.equals( "[" ) ) {
				linksNTerminus = true;
			} else if( residue.equals( "]" ) ) {
				linksCTerminus = true;
			} else {
				residues.add( residue );

				if( residue.equals( "K" ) ) {
					linksNTerminus = true;		// this seems to be what pLink is doing with DSSO
				}
			}

		}

		return new PLinkLinkerEnd( residues, linksNTerminus, linksCTerminus );
	}

	private static String getFormulaFromMotif( String motif ) {

		try {

			motif = motif.replaceAll("1H", "H");
			motif = motif.replaceAll("2H", "D");

			Map<String, Integer> atomCounts = new HashMap<>();

			Pattern p = Pattern.compile("(\\w+)\\(([\\-0-9]+)\\)");
			Matcher m = p.matcher(motif);

			while (m.find()) {

				String atom = m.group(1);
				Integer count = Integer.parseInt(m.group(2));

				if (!atomCounts.containsKey(atom)) {
					atomCounts.put(atom, 0);
				}

				atomCounts.put(atom, atomCounts.get(atom) + count);

			}

			String formula = "";
			for (String atom : atomCounts.keySet()) {
				formula += atom + atomCounts.get(atom);
			}

			return formula;

		} catch( Throwable t ) {
			return null;
		}

	}

}
