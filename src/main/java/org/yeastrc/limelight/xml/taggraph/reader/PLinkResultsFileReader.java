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

import java.io.File;
import java.io.FileReader;

import com.opencsv.CSVReader;
import org.yeastrc.limelight.xml.taggraph.utils.PLinkReportedPeptideUtils;
import org.yeastrc.limelight.xml.taggraph.utils.ScanParsingUtils;
import org.yeastrc.limelight.xml.taggraph.objects.PLinkReportedPeptide;
import org.yeastrc.limelight.xml.taggraph.objects.PLinkResult;

/**
 * Reads all results from a plink2 results file.
 * 
 * @author Michael Riffle
 * @date Mar 23, 2016
 *
 */
public class PLinkResultsFileReader {

	private PLinkResultsFileReader( File file, int type, PLinkSearchParameters params ) {
		this.file = file;
		this.type = type;
		this.params = params;
	}
	
	/**
	 * Get a new results file reader
	 * @param file The file to read
	 * @param type The type of links being read (e.g. PLinkConstants.LINK_TYPE_CROSSLINK)
	 * @return
	 */
	public static PLinkResultsFileReader getPLinkResultsFileReader( File file, int type, PLinkSearchParameters params ) {
		return new PLinkResultsFileReader( file, type, params );
	}
	
	/**
	 * Close this reader, be sure to do this
	 */
	public void close() {
		this.isClosed = true;
		
		if( this.csvReader != null ) {
			try { this.csvReader.close(); this.csvReader = null; }
			catch( Exception e ) { ; }
		}
	}
	
	/**
	 * Read the next plink result from the results file
	 * @return the next plink result, null if they have all been returned
	 * @throws Exception
	 */
	public PLinkResult readNextResult() throws Exception {
	
		if( this.isClosed )
			throw new Exception( "Called readNextResult() on closed result file reader." );
		
		if( this.isDone )
			return null;
		
		if( this.csvReader == null ) {
			this.csvReader = new CSVReader( new FileReader( this.file ) );

			this.csvReader.readNext();	// throw away header
		}		
		

		String[] fields = this.csvReader.readNext();

		if( fields == null ) {
			this.isDone = true;
			return null;
		}
		
		PLinkResult result = new PLinkResult();

		try {
			result.setType( type );

			if( fields.length != 21 )
				throw new Exception( "Expected 21 fields, got " + fields.length );
			
			result.setScanNumber( ScanParsingUtils.getScanNumberFromReportedScan( fields[ 1 ] ) );
			result.setScanFilePrefix( ScanParsingUtils.getScanFilePrefixFromReportedScan( fields[ 1 ] ) );

			result.setCharge( Integer.parseInt( fields[ 2 ] ) );
			
			String rp = fields[ 4 ];
			String mods = fields[ 8 ];
			
			PLinkReportedPeptide reportedPeptide = PLinkReportedPeptideUtils.getReportedPeptide( rp, mods, type, params);
			result.setReportedPeptide( reportedPeptide );
			
			result.setCalculatedMass( Double.parseDouble( fields[ 7 ] ) );
			result.setPrecursorMass( Double.parseDouble( fields[ 3 ] ) );
			result.setDeltaMass( Double.parseDouble( fields[ 11 ] ) );
			result.setDeltaMassPPM( Double.parseDouble( fields[ 12 ] ) );

			result.setEvalue( Double.parseDouble( fields[ 9 ] ) );
			result.setScore( Double.parseDouble( fields[ 10 ] ) );
			
			result.setAlphaMatched( Double.parseDouble( fields[ 17 ] ) );
			result.setBetaMatched( Double.parseDouble( fields[ 18 ] ) );
		
			result.setAlphaEValue( Double.parseDouble( fields[ 19 ] ) );
			result.setBetaEValue( Double.parseDouble( fields[ 20 ] ) );
		
		} catch (Exception e) {

			System.err.println( "Got error processing pLink result:" );
			System.err.println( "\tFields: " + fields );
			System.err.println( "Reason: " + e.getMessage() + "\n" );
			
			throw e;
		}
		
		return result;
	}
	
	
	private File file;
	private CSVReader csvReader;
	private boolean isDone = false;
	private int type;
	private boolean isClosed = false;
	private PLinkSearchParameters params;
}
