package org.yeastrc.limelight.xml.taggraph.objects;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public class MetamorpheusPSM {

	private BigDecimal qValue;
	private BigDecimal massDiff;
	private BigDecimal score;
	private BigDecimal rank;

	private int scanNumber;
	private BigDecimal precursorNeutralMass;
	private int charge;

	private String peptideSequence;
	private Map<Integer,BigDecimal> modifications;

	private boolean isDecoy;

	public boolean isDecoy() {
		return isDecoy;
	}

	public void setDecoy(boolean decoy) {
		isDecoy = decoy;
	}

	@Override
	public String toString() {
		return "MetamorpheusPSM{" +
				"qValue=" + qValue +
				", massDiff=" + massDiff +
				", score=" + score +
				", rank=" + rank +
				", scanNumber=" + scanNumber +
				", precursorNeutralMass=" + precursorNeutralMass +
				", charge=" + charge +
				", peptideSequence='" + peptideSequence + '\'' +
				", modifications=" + modifications +
				", isDecoy=" + isDecoy +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		org.yeastrc.limelight.xml.metamorpheus.objects.MetamorpheusPSM metamorpheusPSM = (org.yeastrc.limelight.xml.metamorpheus.objects.MetamorpheusPSM) o;
		return scanNumber == metamorpheusPSM.scanNumber &&
				rank == metamorpheusPSM.rank &&
				charge == metamorpheusPSM.charge &&
				precursorNeutralMass.equals(metamorpheusPSM.precursorNeutralMass) &&
				peptideSequence.equals(metamorpheusPSM.peptideSequence);
	}

	@Override
	public int hashCode() {
		return Objects.hash(scanNumber, rank, precursorNeutralMass, charge, peptideSequence);
	}

	public BigDecimal getqValue() {
		return qValue;
	}

	public void setqValue(BigDecimal qValue) {
		this.qValue = qValue;
	}

	public BigDecimal getMassDiff() {
		return massDiff;
	}

	public void setMassDiff(BigDecimal massDiff) {
		this.massDiff = massDiff;
	}

	public BigDecimal getScore() {
		return score;
	}

	public void setScore(BigDecimal score) {
		this.score = score;
	}

	public int getScanNumber() {
		return scanNumber;
	}

	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}

	public BigDecimal getPrecursorNeutralMass() {
		return precursorNeutralMass;
	}

	public void setPrecursorNeutralMass(BigDecimal precursorNeutralMass) {
		this.precursorNeutralMass = precursorNeutralMass;
	}

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
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

	public BigDecimal getRank() {
		return rank;
	}

	public void setRank(BigDecimal rank) {
		this.rank = rank;
	}
}
