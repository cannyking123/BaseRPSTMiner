package org.processmining.decomposedminer.configurations.impl;

import org.processmining.causalactivitymatrixminer.miners.impl.HAFMidiMatrixMiner;
import org.processmining.decomposedminer.parameters.DecomposedDiscoveryParameters;

import com.fluxicon.slickerbox.components.NiceSlider;

public class DecomposedMidiNegDiscoveryConfiguration extends DecomposedAbstractMinerZeroDiscoveryConfiguration {

	public final static String NAME = "Midi -0.5";
	
	public String getName() {
		return NAME;
	}

	public double getZeroValue(DecomposedDiscoveryParameters parameters) {
		return -0.5;
	}

	public String getMiner(DecomposedDiscoveryParameters parameters) {
		return (new HAFMidiMatrixMiner()).getName();
	}

	public void update(NiceSlider slider) {
		slider.getSlider().setValue(100);
		slider.setEnabled(false);
	}
}
