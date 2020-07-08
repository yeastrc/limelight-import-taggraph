package org.yeastrc.limelight.xml.taggraph.objects;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class TagGraphReportedPeptide {

	@Override
	public String toString() {
		return "TagGraphReportedPeptide{" +
				"reportedPeptideString='" + reportedPeptideString + '\'' +
				", nakedPeptide='" + nakedPeptide + '\'' +
				", mods=" + mods +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TagGraphReportedPeptide that = (TagGraphReportedPeptide) o;
		return reportedPeptideString.equals(that.reportedPeptideString);
	}

	@Override
	public int hashCode() {
		return Objects.hash(reportedPeptideString);
	}

	public String getReportedPeptideString() {
		return reportedPeptideString;
	}

	public void setReportedPeptideString(String reportedPeptideString) {
		this.reportedPeptideString = reportedPeptideString;
	}

	public String getNakedPeptide() {
		return nakedPeptide;
	}

	public void setNakedPeptide(String nakedPeptide) {
		this.nakedPeptide = nakedPeptide;
	}

	public Map<Integer, BigDecimal> getMods() {
		return mods;
	}

	public void setMods(Map<Integer, BigDecimal> mods) {
		this.mods = mods;
	}

	private String reportedPeptideString;
	private String nakedPeptide;
	private Map<Integer, BigDecimal> mods;

}
