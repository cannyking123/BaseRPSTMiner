package org.processmining.decomposedminer.parameters;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.basicutils.parameters.impl.PluginParametersImpl;
import org.processmining.decomposedminer.configurations.DecomposedDiscoveryConfigurationManager;
import org.processmining.log.parameters.ClassifierParameter;
import org.processmining.log.parameters.MinerParameter;
import org.processmining.log.utils.XUtils;

public class DecomposedDiscoveryParameters extends PluginParametersImpl implements ClassifierParameter, MinerParameter {

	public enum ArtificalStrategy {
		ALWAYS("Always"), NEVER("Never"), MINER("Let miner decide");

		private String label;

		ArtificalStrategy(String label) {
			this.label = label;
		}

		public String toString() {
			return label;
		}
	};

	private XEventClassifier classifier;
	private String miner;
	private boolean combineClusters;
	private String configuration;
	private ArtificalStrategy artificialStrategy;
	private int percentage;

	public DecomposedDiscoveryParameters(XLog log) {
		super();
		setClassifier(XUtils.getDefaultClassifier(log));
		setCombineClusters(false);
		setConfiguration(DecomposedDiscoveryConfigurationManager.getInstance().getConfiguration(null).getName());
		setArtificialStrategy(ArtificalStrategy.MINER);
		setPercentage(100);
	}

	public DecomposedDiscoveryParameters(DecomposedDiscoveryParameters parameters) {
		super(parameters);
		setClassifier(parameters.getClassifier());
		setCombineClusters(parameters.isCombineClusters());
		setConfiguration(parameters.getConfiguration());
		setArtificialStrategy(parameters.getArtificialStrategy());
		setPercentage(parameters.getPercentage());
	}

	public boolean equals(Object object) {
		if (object instanceof DecomposedDiscoveryParameters) {
			DecomposedDiscoveryParameters parameters = (DecomposedDiscoveryParameters) object;
			return super.equals(parameters)
					&& getClassifier().equals(parameters.getClassifier())
					&& isCombineClusters() == parameters.isCombineClusters()
					&& getConfiguration().equals(parameters.getConfiguration())
					&& getArtificialStrategy() == parameters.getArtificialStrategy()
					&& getPercentage() == parameters.getPercentage();
		}
		return false;
	}

	public void setClassifier(XEventClassifier classifier) {
		this.classifier = classifier;
	}

	public XEventClassifier getClassifier() {
		return classifier;
	}

	public void setCombineClusters(boolean combineClusters) {
		this.combineClusters = combineClusters;
	}

	public boolean isCombineClusters() {
		return combineClusters;
	}

	public void setMiner(String miner) {
		this.miner = miner;
	}

	public String getMiner() {
		return miner;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public ArtificalStrategy getArtificialStrategy() {
		return artificialStrategy;
	}

	public void setArtificialStrategy(ArtificalStrategy artificialStrategy) {
		this.artificialStrategy = artificialStrategy;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}
}
