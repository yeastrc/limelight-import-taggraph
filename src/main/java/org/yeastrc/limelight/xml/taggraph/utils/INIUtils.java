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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.DefaultExpressionEngine;
import org.apache.commons.configuration2.tree.DefaultExpressionEngineSymbols;

/**
 * Some util methods for INI file parsing.
 * 
 * @author Michael Riffle
 * @date Mar 5, 2016
 *
 */
public class INIUtils {

	/**
	 * Get an expression engine that separates sections from keys using a "/" instead of a
	 * "."
	 */
	public static final DefaultExpressionEngine EXPRESSION_ENGINE = getExpressionEngine();

	/**
	 * Get a INIConfiguration representing the supplied INI file, using a "/" instead of a "."
	 * to separate sections from key names.
	 * 
	 * @param filename The full path (including file name) of the INI file.
	 * @return
	 * @throws FileNotFoundException
	 * @throws ConfigurationException
	 * @throws IOException
	 */
	public static INIConfiguration getINIConfiguration( String filename ) throws FileNotFoundException, ConfigurationException, IOException {
		INIConfiguration config = new INIConfiguration();
		config.setExpressionEngine( INIUtils.EXPRESSION_ENGINE );
		config.read( new FileReader( new File( filename ) ) );
		return config;
	}
	
	/**
	 * Get an expression engine that uses "/" to separate section names from key names
	 * @return
	 */
	private static DefaultExpressionEngine getExpressionEngine() {
		DefaultExpressionEngineSymbols symbols =
			    new DefaultExpressionEngineSymbols.Builder(
			        DefaultExpressionEngineSymbols.DEFAULT_SYMBOLS)
			        // Use a slash as property delimiter
			        .setPropertyDelimiter("/")
			        // Indices should be specified in curly brackets
			        .setIndexStart("{")
			        .setIndexEnd("}")
			        // For attributes use simply a @
			        .setAttributeStart("@")
			        .setAttributeEnd(null)
			        // A Backslash is used for escaping property delimiters
			        .setEscapedDelimiter("\\/")
			        .create();
		
		DefaultExpressionEngine engine = new DefaultExpressionEngine(symbols);
		return engine;
	}
	
}
