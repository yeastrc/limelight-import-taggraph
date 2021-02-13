package org.yeastrc.limelight.xml.taggraph.objects;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public class TagGraphPSM {

	private BigDecimal obsMH;
	private BigDecimal theoMH;
	private BigDecimal retentionTime;
	private BigDecimal ppm;
	private BigDecimal fdr;
	private BigDecimal emProbability;
	private BigDecimal oneMinusLog10Em;
	private BigDecimal spectrumScore;
	private BigDecimal alignmentScore;
	private BigDecimal compositeScore;

	private int scanNumber;
	private int charge;
	private String scanFilePrefix;

	private String peptideSequence;
	private Map<Integer,BigDecimal> modifications;

	@Override
	public String toString() {
		return "TagGraphPSM{" +
				"obsMH=" + obsMH +
				", theoMH=" + theoMH +
				", retentionTime=" + retentionTime +
				", ppm=" + ppm +
				", fdr=" + fdr +
				", emProbability=" + emProbability +
				", oneMinusLog10Em=" + oneMinusLog10Em +
				", spectrumScore=" + spectrumScore +
				", alignmentScore=" + alignmentScore +
				", compositeScore=" + compositeScore +
				", scanNumber=" + scanNumber +
				", charge=" + charge +
				", scanFilePrefix='" + scanFilePrefix + '\'' +
				", peptideSequence='" + peptideSequence + '\'' +
				", modifications=" + modifications +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TagGraphPSM that = (TagGraphPSM) o;
		return scanNumber == that.scanNumber &&
				charge == that.charge &&
				scanFilePrefix.equals(that.scanFilePrefix) &&
				peptideSequence.equals(that.peptideSequence) &&
				Objects.equals(modifications, that.modifications);
	}

	@Override
	public int hashCode() {
		return Objects.hash(scanNumber, charge, scanFilePrefix, peptideSequence, modifications);
	}

	public BigDecimal getObsMH() {
		return obsMH;
	}

	public void setObsMH(BigDecimal obsMH) {
		this.obsMH = obsMH;
	}

	public BigDecimal getTheoMH() {
		return theoMH;
	}

	public void setTheoMH(BigDecimal theoMH) {
		this.theoMH = theoMH;
	}

	public BigDecimal getPpm() {
		return ppm;
	}

	public void setPpm(BigDecimal ppm) {
		this.ppm = ppm;
	}

	public BigDecimal getFdr() {
		return fdr;
	}

	public void setFdr(BigDecimal fdr) {
		this.fdr = fdr;
	}

	public BigDecimal getEmProbability() {
		return emProbability;
	}

	public void setEmProbability(BigDecimal emProbability) {
		this.emProbability = emProbability;
	}

	public BigDecimal getOneMinusLog10Em() {
		return oneMinusLog10Em;
	}

	public void setOneMinusLog10Em(BigDecimal oneMinusLog10Em) {
		this.oneMinusLog10Em = oneMinusLog10Em;
	}

	public BigDecimal getSpectrumScore() {
		return spectrumScore;
	}

	public void setSpectrumScore(BigDecimal spectrumScore) {
		this.spectrumScore = spectrumScore;
	}

	public BigDecimal getAlignmentScore() {
		return alignmentScore;
	}

	public void setAlignmentScore(BigDecimal alignmentScore) {
		this.alignmentScore = alignmentScore;
	}

	public BigDecimal getCompositeScore() {
		return compositeScore;
	}

	public void setCompositeScore(BigDecimal compositeScore) {
		this.compositeScore = compositeScore;
	}

	public int getScanNumber() {
		return scanNumber;
	}

	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public String getScanFilePrefix() {
		return scanFilePrefix;
	}

	public void setScanFilePrefix(String scanFilePrefix) {
		this.scanFilePrefix = scanFilePrefix;
	}

	public String getPeptideSequence() {
		return peptideSequence;
	}

	public void setPeptideSequence(String peptideSequence) {
		this.peptideSequence = peptideSequence;
	}

	public Map<Integer, BigDecimal> getModifications() {
		return modifications;
	}

	public void setModifications(Map<Integer, BigDecimal> modifications) {
		this.modifications = modifications;
	}

	public BigDecimal getRetentionTime() {
		return retentionTime;
	}

	public void setRetentionTime(BigDecimal retentionTime) {
		this.retentionTime = retentionTime;
	}
}
