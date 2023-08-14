package cn.sk.untils;
/**
 * 分支的AND/OR关系挖掘的参数设置
 * @author cannyking
 *
 */

public class AndOrAnalysisParameters {

	private int positiveObservations;//正观察值阈值
	private int dependencyDivisor;
	private double andThreshold;//并发阈值
	
	public AndOrAnalysisParameters() {
		// TODO Auto-generated constructor stub
		this.positiveObservations = 0;
		this.dependencyDivisor = 1;
		andThreshold = 0.10;
	}

	public AndOrAnalysisParameters(int positiveObservations, 
			int dependencyDivisor, double andThreshold) {
		this.positiveObservations = positiveObservations;
		this.dependencyDivisor = dependencyDivisor;
		this.andThreshold = andThreshold;
	}

	public int getPositiveObservations() {
		return positiveObservations;
	}

	public void setPositiveObservations(int positiveObservations) {
		this.positiveObservations = positiveObservations;
	}

	public int getDependencyDivisor() {
		return dependencyDivisor;
	}

	public void setDependencyDivisor(int dependencyDivisor) {
		this.dependencyDivisor = dependencyDivisor;
	}

	public double getAndThreshold() {
		return andThreshold;
	}

	public void setAndThreshold(double andThreshold) {
		this.andThreshold = andThreshold;
	}
}
