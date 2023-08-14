package cn.sk.untils;
/**
 * ��֧��AND/OR��ϵ�ھ�Ĳ�������
 * @author cannyking
 *
 */

public class AndOrAnalysisParameters {

	private int positiveObservations;//���۲�ֵ��ֵ
	private int dependencyDivisor;
	private double andThreshold;//������ֵ
	
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
