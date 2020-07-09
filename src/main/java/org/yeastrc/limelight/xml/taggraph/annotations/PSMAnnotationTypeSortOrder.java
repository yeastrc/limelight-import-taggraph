package org.yeastrc.limelight.xml.taggraph.annotations;

import org.yeastrc.limelight.limelight_import.api.xml_dto.SearchAnnotation;
import org.yeastrc.limelight.xml.taggraph.constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class PSMAnnotationTypeSortOrder {

	public static List<SearchAnnotation> getPSMAnnotationTypeSortOrder() {
		List<SearchAnnotation> annotations = new ArrayList<SearchAnnotation>();

		{
			SearchAnnotation annotation = new SearchAnnotation();
			annotation.setAnnotationName( PSMAnnotationTypes.ANNOTATION_TYPE_FDR );
			annotation.setSearchProgram( Constants.PROGRAM_NAME );
			annotations.add( annotation );
		}

		{
			SearchAnnotation annotation = new SearchAnnotation();
			annotation.setAnnotationName( PSMAnnotationTypes.ANNOTATION_TYPE_EM_PROB );
			annotation.setSearchProgram( Constants.PROGRAM_NAME );
			annotations.add( annotation );
		}
		
		return annotations;
	}
}
