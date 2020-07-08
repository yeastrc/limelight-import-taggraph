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

package org.yeastrc.limelight.xml.taggraph.ini;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.yeastrc.limelight.xml.taggraph.utils.INIUtils;

/**
 * Represents a parsed INI file
 * 
 * @author Michael Riffle
 * @date Mar 5, 2016
 *
 */
public class ParsedINIFile {

	public ParsedINIFile( String filename ) throws ConfigurationException, FileNotFoundException, IOException {

		this.filename = filename;
		
		config = new INIConfiguration();
		config.setExpressionEngine( INIUtils.EXPRESSION_ENGINE );
		config.read( new FileReader( new File( filename ) ) );
	}
	
	public ParsedINIFile( File file ) throws ConfigurationException, FileNotFoundException, IOException {
		
		this.filename = file.getAbsolutePath();
		
		config = new INIConfiguration();
		config.setExpressionEngine( INIUtils.EXPRESSION_ENGINE );
		config.read( new FileReader( file ) );
	}
	
	
	
	
	public String getFilename() {
		return filename;
	}

	/**
	 * Gets an objects where properties from the INI file may be referenced as:
	 * "section_name/key_name" E.g., for:
	 * [Section]
	 * foo=bar
	 * number=6
	 * 
	 * The value for "foo" would be referenced as: iniFile.getConfig.getString( "Section/foo" )
	 * 
	 * The value for "number" would be referenced as: iniFile.getConfig.getInt( "Section/number" );
	 * 
	 * @return
	 */
	public INIConfiguration getConfig() {
		return config;
	}


	private INIConfiguration config;
	private String filename;
}
