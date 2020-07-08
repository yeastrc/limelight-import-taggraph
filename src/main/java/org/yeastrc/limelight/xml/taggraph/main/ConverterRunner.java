/*
 * Original author: Michael Riffle <mriffle .at. uw.edu>
 *                  
 * Copyright 2018 University of Washington - Seattle, WA
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

package org.yeastrc.limelight.xml.taggraph.main;

import org.yeastrc.limelight.xml.taggraph.objects.ConversionParameters;
import org.yeastrc.limelight.xml.taggraph.reader.INIFileReader;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

public class ConverterRunner {

	// conveniently get a new instance of this class
	public static ConverterRunner createInstance() { return new ConverterRunner(); }
	
	
	public void convertToLimelightXML(ConversionParameters conversionParameters ) throws Throwable {

		// read mods from ini file
		System.err.print( "\nReading variable and static mods from INI file... ");
		INIFileReader iniFileReader = INIFileReader.getInstance(conversionParameters.getIniFile());
		Map<String, BigDecimal> staticMods = iniFileReader.getStaticMods();
		Map<String, Collection<BigDecimal>> variableMods = iniFileReader.getVariableMods();
		System.err.println("Done.");

		// read in the results
		System.err.println( "\nLoading MetaMorpheus results into memory...");
		MetamorpheusResults results = MetamorpheusResultsReader.getResults( conversionParameters.getMzidFile() );

		// write out the limelight xml
		System.err.print( "\nWriting out XML..." );
		(new XMLBuilder()).buildAndSaveXML(conversionParameters, results);
		System.err.println( " Done." );

		// validate the limelight xml

	}
}
