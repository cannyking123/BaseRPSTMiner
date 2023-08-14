package org.processmining.causalactivitymatrix.parameters;

import org.processmining.basicutils.parameters.impl.PluginParametersImpl;

/**
 * 
 * @author cannyking modify
 * 
 *         include zeroValue,concurrencyRatio,includeThreshold,gammer,miner 
 *         
 *
 */
public class ModifyCausalActivityMatrixParameters extends PluginParametersImpl {

	private double zeroValue;
	private double concurrencyRatio;
	private double includeThreshold;
	private int gammer;
	private int activeNum;
	private String miner;
	private int B;
	private int P;

	public String getMiner() {
		return miner;
	}

	public void setMiner(String miner) {
		this.miner = miner;
	}

	public ModifyCausalActivityMatrixParameters() {
		super();
		setZeroValue(0.0);
		setConcurrencyRatio(0.0);
		setIncludeThreshold(0.0);
		setGammer(6);
		setMiner("ILP miner");
		setActiveNum(0);
		setP(0);
		setB(0);

	}

	public ModifyCausalActivityMatrixParameters(ModifyCausalActivityMatrixParameters parameters) {
		super(parameters);
		setZeroValue(parameters.getZeroValue());
		setConcurrencyRatio(parameters.getConcurrencyRatio());
		setIncludeThreshold(parameters.getIncludeThreshold());
		setP(parameters.getP());
		setB(parameters.getB());
	}

	public double getZeroValue() {
		return zeroValue;
	}

	public void setZeroValue(double zeroValue) {
		this.zeroValue = zeroValue;
	}

	public double getConcurrencyRatio() {
		return concurrencyRatio;
	}

	public void setConcurrencyRatio(double concurrencyRatio) {
		this.concurrencyRatio = concurrencyRatio;
	}

	public double getIncludeThreshold() {
		return includeThreshold;
	}

	public void setIncludeThreshold(double includeThreshold) {
		this.includeThreshold = includeThreshold;
	}

	public int getGammer() {
		return gammer;
	}

	public void setGammer(int gammer) {
		this.gammer = gammer;
	}

	public int getActiveNum() {
		return activeNum;
	}

	public void setActiveNum(int activeNum) {
		this.activeNum = activeNum;
	}

	

	public int getB() {
		return B;
	}

	public void setB(int b) {
		B = b;
	}

	public int getP() {
		return P;
	}

	public void setP(int p) {
		P = p;
	}

	
	//	
	//	public boolean equals(Object object) {
	//		if (object instanceof MyDialogParameters) {
	//			MyDialogParameters parameters = (MyDialogParameters) object;
	//			return super.equals(parameters) && getZeroValue() == parameters.getZeroValue() &&
	//					getConcurrencyRatio() == parameters.getConcurrencyRatio() &&
	//					getIncludeThreshold() == parameters.getIncludeThreshold();
	//		}
	//		return false;
	//	}

}
