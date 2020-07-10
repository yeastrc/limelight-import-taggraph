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

package org.yeastrc.limelight.xml.taggraph.annotations;

import org.yeastrc.limelight.limelight_import.api.xml_dto.DescriptivePsmAnnotationType;
import org.yeastrc.limelight.limelight_import.api.xml_dto.FilterDirectionType;
import org.yeastrc.limelight.limelight_import.api.xml_dto.FilterablePsmAnnotationType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PSMAnnotationTypes {

	public static final String ANNOTATION_TYPE_FDR = "FDR";
	public static final String ANNOTATION_TYPE_PPM = "PPM";
	public static final String ANNOTATION_TYPE_EM_PROB = "EM Probability";
	public static final String ANNOTATION_TYPE_ONE_MINUS_EM = "1-lg10 EM";
	public static final String ANNOTATION_TYPE_SPECTRUM_SCORE = "Spectrum Score";
	public static final String ANNOTATION_TYPE_ALIGNMENT_SCORE = "Alignment Score";
	public static final String ANNOTATION_TYPE_COMPOSITE_SCORE = "Composite Score";

	public static final String ANNOTATION_TYPE_OBS_MH = "Obs M+H";
	public static final String ANNOTATION_TYPE_THEO_MH = "Theo M+H";



	/**
	 * Get the list of filterable PSM annotation types in TagGraph data
	 * @return
	 */
	public static List<FilterablePsmAnnotationType> getFilterablePsmAnnotationTypes() {
		List<FilterablePsmAnnotationType> types = new ArrayList<FilterablePsmAnnotationType>();

		{
			FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
			type.setName( ANNOTATION_TYPE_FDR );
			type.setDescription( "FDR calculated by TagGraph" );
			type.setDefaultFilterValue( new BigDecimal( "0.01" ) );
			type.setFilterDirection( FilterDirectionType.BELOW );
			
			types.add( type );
		}

		{
			FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
			type.setName( ANNOTATION_TYPE_PPM );
			type.setDescription( "PPM error" );
			type.setFilterDirection( FilterDirectionType.BELOW );

			types.add( type );
		}
		
		{
			FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
			type.setName( ANNOTATION_TYPE_EM_PROB );
			type.setDescription( "Probability derived using a hierarchical Bayes Model optimized by expectation maximization (EM)" );
			type.setFilterDirection( FilterDirectionType.ABOVE );
			
			types.add( type );
		}

		{
			FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
			type.setName( ANNOTATION_TYPE_ONE_MINUS_EM );
			type.setDescription( "1-lg10 EM" );
			type.setFilterDirection( FilterDirectionType.ABOVE );

			types.add( type );
		}

		{
			FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
			type.setName( ANNOTATION_TYPE_SPECTRUM_SCORE );
			type.setDescription( "Spectrum score" );
			type.setFilterDirection( FilterDirectionType.ABOVE );

			types.add( type );
		}

		{
			FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
			type.setName( ANNOTATION_TYPE_ALIGNMENT_SCORE );
			type.setDescription( "Alignment Score" );
			type.setFilterDirection( FilterDirectionType.ABOVE );

			types.add( type );
		}

		{
			FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
			type.setName( ANNOTATION_TYPE_COMPOSITE_SCORE );
			type.setDescription( "Composite Score" );
			type.setFilterDirection( FilterDirectionType.ABOVE );

			types.add( type );
		}
		
		return types;
	}
	
	/**
	 * Get the list of descriptive (non-filterable) PSM annotation types in TagGraph data
	 * @return
	 */
	public static List<DescriptivePsmAnnotationType> getDescriptivePsmAnnotationTypes() {
		List<DescriptivePsmAnnotationType> types = new ArrayList<DescriptivePsmAnnotationType>();
		
		{
			DescriptivePsmAnnotationType type = new DescriptivePsmAnnotationType();
			type.setName( ANNOTATION_TYPE_OBS_MH );
			type.setDescription( type.getName() );
			
			types.add( type );
		}
		
		{
			DescriptivePsmAnnotationType type = new DescriptivePsmAnnotationType();
			type.setName( ANNOTATION_TYPE_THEO_MH );
			type.setDescription( type.getName() );
			
			types.add( type );
		}
		
		return types;		
	}
	
}
