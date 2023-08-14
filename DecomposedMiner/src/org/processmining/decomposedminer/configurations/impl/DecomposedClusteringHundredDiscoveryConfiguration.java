package org.processmining.decomposedminer.configurations.impl;

import org.processmining.decomposedminer.parameters.DecomposedDiscoveryParameters;

import com.fluxicon.slickerbox.components.NiceSlider;

public class DecomposedClusteringHundredDiscoveryConfiguration extends DecomposedAbstractClusteringDiscoveryConfiguration {

	public final static String NAME = "Decompose 100%";

	public int getPercentage(DecomposedDiscoveryParameters parameters) {
		return 100;
	}
	
	public String getName() {
		return NAME;
	}

	public void update(NiceSlider slider) {
		slider.getSlider().setValue(100);
		slider.setEnabled(false);
	}
}
