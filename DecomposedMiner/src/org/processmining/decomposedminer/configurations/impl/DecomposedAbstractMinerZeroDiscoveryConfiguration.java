package org.processmining.decomposedminer.configurations.impl;

import org.deckfour.xes.model.XLog;
import org.processmining.causalactivitygraph.models.CausalActivityGraph;
import org.processmining.causalactivitygraphcreator.parameters.ConvertCausalActivityMatrixToCausalActivityGraphParameters;
import org.processmining.causalactivitygraphcreator.plugins.ConvertCausalActivityMatrixToCausalActivityGraphPlugin;
import org.processmining.causalactivitymatrix.models.CausalActivityMatrix;
import org.processmining.causalactivitymatrixminer.parameters.DiscoverFromEventLogParameters;
import org.processmining.causalactivitymatrixminer.plugins.DiscoverFromEventLogPlugin;
import org.processmining.decomposedminer.parameters.DecomposedDiscoveryParameters;
import org.processmining.framework.plugin.PluginContext;

public abstract class DecomposedAbstractMinerZeroDiscoveryConfiguration extends DecomposedAbstractDiscoveryConfiguration {
	
	public abstract double getZeroValue(DecomposedDiscoveryParameters parameters);
	
	public abstract String getMiner(DecomposedDiscoveryParameters parameters);
	
	public CausalActivityMatrix getMatrix(PluginContext context, XLog log, DecomposedDiscoveryParameters parameters) {
		DiscoverFromEventLogPlugin plugin = new DiscoverFromEventLogPlugin();
		DiscoverFromEventLogParameters params = new DiscoverFromEventLogParameters(log);
		params.setClassifier(parameters.getClassifier());
		params.setMiner(getMiner(parameters));
		return plugin.run(context, log, params);
	}

	public CausalActivityGraph getGraph(PluginContext context, CausalActivityMatrix matrix,
			DecomposedDiscoveryParameters parameters) {
		ConvertCausalActivityMatrixToCausalActivityGraphPlugin plugin = new ConvertCausalActivityMatrixToCausalActivityGraphPlugin();
		ConvertCausalActivityMatrixToCausalActivityGraphParameters params = new ConvertCausalActivityMatrixToCausalActivityGraphParameters();
		params.setZeroValue(getZeroValue(parameters));
		return plugin.run(context, matrix, params);
	}

}

