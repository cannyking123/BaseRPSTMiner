package org.processmining.decomposedminer.configurations.impl;

import org.processmining.causalactivitymatrixminer.miners.impl.HeuristicsMatrixMiner;
import org.processmining.decomposedminer.parameters.DecomposedDiscoveryParameters;

import com.fluxicon.slickerbox.components.NiceSlider;

public class DecomposedHeuristicsNegDiscoveryConfiguration extends DecomposedAbstractMinerZeroDiscoveryConfiguration {

	public final static String NAME = "Heuristics -0.5";
	
	public String getName() {
		return NAME;
	}

	public double getZeroValue(DecomposedDiscoveryParameters parameters) {
		return -0.5;
	}

	public String getMiner(DecomposedDiscoveryParameters parameters) {
		return (new HeuristicsMatrixMiner()).getName();
	}

	public void update(NiceSlider slider) {
		slider.getSlider().setValue(100);
		slider.setEnabled(false);
	}
}
