package org.yeastrc.limelight.xml.taggraph.utils;

import org.yeastrc.limelight.xml.taggraph.objects.TagGraphPSM;
import org.yeastrc.limelight.xml.taggraph.objects.TagGraphReportedPeptide;

public class ReportedPeptideUtils {

	public static TagGraphReportedPeptide getReportedPeptideForPSM(TagGraphPSM psm ) throws Exception {

		TagGraphReportedPeptide rp = new TagGraphReportedPeptide();
		
		rp.setNakedPeptide( psm.getPeptideSequence() );
		rp.setMods( psm.getModifications() );
		rp.setReportedPeptideString( ModParsingUtils.getRoundedReportedPeptideString( psm.getPeptideSequence(), psm.getModifications() ));

		return rp;
	}

}
