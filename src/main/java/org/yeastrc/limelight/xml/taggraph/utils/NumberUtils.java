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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Some utility methods for working with numbers.
 * 
 * @author Michael Riffle
 * @date Mar 23, 2016
 *
 */
public class NumberUtils {

	public static final int NUMBER_DECIMAL_PLACES = 4;

	/**
	 * Get a big decimal rounded out to NUMBER_DECIMAL_PLACES places
	 * from the supplied double.
	 * 
	 * @param value
	 * @return
	 */
	public static BigDecimal getRoundedBigDecimal( double value ) {
		BigDecimal bd = new BigDecimal( value );
		bd = bd.setScale( NUMBER_DECIMAL_PLACES, RoundingMode.HALF_UP );
		
		return bd;
	}


	public static BigDecimal getScientificNotationBigDecimal( double value ) {
		return new BigDecimal( getScientificNotation( value ) );
	}

	public static String getScientificNotation( double value ) {
		//NumberFormat formatter = new DecimalFormat("0.0E0");

		DecimalFormat formatter = (DecimalFormat)NumberFormat.getNumberInstance(Locale.US);
		formatter.applyPattern("0.0E0");
		formatter.setRoundingMode(RoundingMode.HALF_UP);
		formatter.setMinimumFractionDigits( NUMBER_DECIMAL_PLACES );
		return formatter.format( value );
	}
	
}
