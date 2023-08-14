package org.processmining.decomposedminer.algorithms;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.activityclusterarray.models.ActivityClusterArray;
import org.processmining.decomposedminer.configurations.DecomposedDiscoveryConfigurationManager;
import org.processmining.decomposedminer.parameters.DecomposedDiscoveryParameters;
import org.processmining.framework.plugin.PluginContext;

public class DecomposedDiscoveryAlgorithm {

	public AcceptingPetriNet apply(PluginContext context, XLog log, ActivityClusterArray clusters,
			DecomposedDiscoveryParameters parameters) {

		return DecomposedDiscoveryConfigurationManager.getInstance().getConfiguration(parameters.getConfiguration())
				.apply(context, log, parameters, clusters);
	}

}
