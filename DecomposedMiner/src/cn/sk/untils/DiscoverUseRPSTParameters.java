package cn.sk.untils;

public class DiscoverUseRPSTParameters {

	private String miner;
	private double ZeroValue;
	private double ConcurrencyRatio;
	private double IncludeThreshold;
	private int Gammer;
	
	
	public DiscoverUseRPSTParameters(){
		miner = "ILP";
		ZeroValue = 0;
		ConcurrencyRatio = 0;
		IncludeThreshold = 0;
		Gammer = 4;
	}

	public String getMiner() {
		return miner;
	}

	public void setMiner(String miner) {
		this.miner = miner;
	}

	public double getZeroValue() {
		return ZeroValue;
	}

	public void setZeroValue(double d) {
		ZeroValue = d;
	}

	public double getConcurrencyRatio() {
		return ConcurrencyRatio;
	}

	public void setConcurrencyRatio(double d) {
		ConcurrencyRatio = d;
	}

	public double getIncludeThreshold() {
		return IncludeThreshold;
	}

	public void setIncludeThreshold(double d) {
		IncludeThreshold = d;
	}

	public int getGammer() {
		return Gammer;
	}

	public void setGammer(int gammer) {
		Gammer = gammer;
	}

}
