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

import org.yeastrc.limelight.xml.taggraph.reader.PLinkSearchParameters;

/**
 * Some utility methods for querying specific parameters from the INI files.
 * 
 * @author Michael Riffle
 * @date Mar 23, 2016
 *
 */
public class PLinkUtils {

	public static String getOutputDirectory( PLinkSearchParameters params ) throws Exception {
		String dir = params.getPlinkINI().getConfig().getString( "flow/result_output_path" );
		
		if( dir == null )
			throw new Exception( "could not get output directory..." );
		
		return dir;
	}
	
	public static String getSearchTitle( PLinkSearchParameters params ) throws Exception {
		String dir = params.getPlinkINI().getConfig().getString( "spectrum/spec_title" );
		
		if( dir == null )
			throw new Exception( "could not get search title..." );
		
		return dir;
	}

	public static String getVersion( PLinkSearchParameters params ) throws Exception {
		return params.getPlinkINI().getConfig().getString( "version/version" );
	}
	
	public static boolean evaluePresent( PLinkSearchParameters params ) throws Exception {
		
		try {
			int evaluePresent = params.getPlinkINI().getConfig().getInt( "filter/evalue" );
			if( evaluePresent > 0 )
				return true;
		} catch( Exception e ) { ; }
		
		return false;
	}
}
