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

package org.yeastrc.limelight.xml.taggraph.reader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PLinkConstants {

	public static final String SEARCH_PROGRAM_NAME = "pLink";	

	public static final String _DEFAULT_PLINK_INSTALL_BIN_DIRECTORY = "C:\\pFindStudio\\pLink\\$$VERSION$$\\bin";
	
	public static final int LINK_TYPE_MONOLINK = 0;
	public static final int LINK_TYPE_CROSSLINK = 1;
	public static final int LINK_TYPE_LOOPLINK = 2;
	public static final int LINK_TYPE_UNLINKED = 3;
	
	public static final String MODIFY_INI_FILENAME = "modification.ini";
	public static final String XLINK_INI_FILENAME = "xlink.ini";
	
	public static final String DATA_SUBDIRECTORY = "reports";
	
	/**
	 * The string with which plink refers to the types of linked peptides, used
	 * to find subdirectories for data
	 */
	public static final Map<Integer, String> PLINK_NAME_FOR_TYPE;
	

	static {
        
        
        Map<Integer, String> tempLinkTypeMap = new HashMap<Integer, String>();
        tempLinkTypeMap.put( LINK_TYPE_MONOLINK, "mono-linked" );
        tempLinkTypeMap.put( LINK_TYPE_CROSSLINK, "cross-linked" );
        tempLinkTypeMap.put( LINK_TYPE_LOOPLINK, "loop-linked" );
        tempLinkTypeMap.put( LINK_TYPE_UNLINKED, "regular" );
        
        PLINK_NAME_FOR_TYPE = Collections.unmodifiableMap( tempLinkTypeMap );
	}

}
