package org.yeastrc.limelight.xml.taggraph.objects;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

public class TagGraphResults {

	private Map<TagGraphReportedPeptide, Collection<TagGraphPSM>> peptidePSMMap;
	private Map<String, BigDecimal> staticMods;
	private String version;

	public Map<TagGraphReportedPeptide, Collection<TagGraphPSM>> getPeptidePSMMap() {
		return peptidePSMMap;
	}

	public void setPeptidePSMMap(Map<TagGraphReportedPeptide, Collection<TagGraphPSM>> peptidePSMMap) {
		this.peptidePSMMap = peptidePSMMap;
	}

	public Map<String, BigDecimal> getStaticMods() {
		return staticMods;
	}


	public void setStaticMods(Map<String, BigDecimal> staticMods) {
		this.staticMods = staticMods;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
