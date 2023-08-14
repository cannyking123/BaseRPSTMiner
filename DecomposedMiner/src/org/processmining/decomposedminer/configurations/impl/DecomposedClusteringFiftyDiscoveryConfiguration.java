package org.processmining.decomposedminer.configurations.impl;

import org.processmining.decomposedminer.parameters.DecomposedDiscoveryParameters;

import com.fluxicon.slickerbox.components.NiceSlider;

public class DecomposedClusteringFiftyDiscoveryConfiguration extends DecomposedAbstractClusteringDiscoveryConfiguration {

	public final static String NAME = "Decompose 50%";
	
	public int getPercentage(DecomposedDiscoveryParameters parameters) {
		return 50;
	}
	
	public String getName() {
		return NAME;
	}


	public void update(NiceSlider slider) {
		slider.getSlider().setValue(50);
		slider.setEnabled(false);
	}
}