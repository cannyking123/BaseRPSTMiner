package org.processmining.decomposedminer.configurations.impl;

import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.filters.impl.StartEndHideFilter;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNetArray;
import org.processmining.acceptingpetrinet.parameters.MergeAcceptingPetriNetArrayIntoAcceptingPetriNetParameters;
import org.processmining.acceptingpetrinet.parameters.RemoveStartEndTransitionsParameters;
import org.processmining.acceptingpetrinet.plugins.MergeAcceptingPetriNetArrayIntoAcceptingPetriNetPlugin;
import org.processmining.acceptingpetrinet.plugins.RemoveStartEndTransitionsPlugin;
import org.processmining.acceptingpetrinetclassicalreductor.parameters.ReduceUsingMurataRulesParameters;
import org.processmining.acceptingpetrinetclassicalreductor.plugins.ReduceUsingMurataRulesPlugin;
import org.processmining.acceptingpetrinetminer.miners.DecomposedMinerManager;
import org.processmining.acceptingpetrinetminer.miners.DecomposedMinerParameters;
import org.processmining.acceptingpetrinetminer.miners.impl.DecomposedILPMiner;
import org.processmining.acceptingpetrinetminer.parameters.DiscoverAcceptingPetriNetArrayFromEventLogArrayParameters;
import org.processmining.acceptingpetrinetminer.plugins.DiscoverAcceptingPetriNetArrayFromEventLogArrayPlugin;
import org.processmining.acceptingpetrinetredundantplacesreductor.parameters.RemoveStructuralRedundantPlacesParameters;
import org.processmining.acceptingpetrinetredundantplacesreductor.plugins.RemoveStructuralRedundantPlacesPlugin;
import org.processmining.activityclusterarray.models.ActivityClusterArray;
import org.processmining.activityclusterarray.models.impl.ActivityClusterArrayFactory;
import org.processmining.activityclusterarraycreator.parameters.ConvertCausalActivityGraphToActivityClusterArrayParameters;
import org.processmining.activityclusterarraycreator.parameters.ModifyActivityClusterArrayParameters;
import org.processmining.activityclusterarraycreator.plugins.ConvertCausalActivityGraphToActivityClusterArrayPlugin;
import org.processmining.activityclusterarraycreator.plugins.ModifyActivityClusterArrayPlugin;
import org.processmining.causalactivitygraph.models.CausalActivityGraph;
import org.processmining.causalactivitygraphcreator.parameters.ConvertCausalActivityMatrixToCausalActivityGraphParameters;
import org.processmining.causalactivitygraphcreator.plugins.ConvertCausalActivityMatrixToCausalActivityGraphPlugin;
import org.processmining.causalactivitymatrix.models.CausalActivityMatrix;
import org.processmining.causalactivitymatrixminer.miners.impl.HAFMidiMatrixMiner;
import org.processmining.causalactivitymatrixminer.parameters.DiscoverFromEventLogParameters;
import org.processmining.causalactivitymatrixminer.plugins.DiscoverFromEventLogPlugin;
import org.processmining.decomposedminer.configurations.DecomposedDiscoveryConfiguration;
import org.processmining.decomposedminer.parameters.DecomposedDiscoveryParameters;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.log.models.EventLogArray;
import org.processmining.logdecomposer.filters.impl.DecompositionInFilter;
import org.processmining.logdecomposer.parameters.DecomposeEventLogUsingActivityClusterArrayParameters;
import org.processmining.logdecomposer.plugins.DecomposeEventLogUsingActivityClusterArrayPlugin;

public abstract class DecomposedAbstractDiscoveryConfiguration implements DecomposedDiscoveryConfiguration {

	public AcceptingPetriNet mine(PluginContext context, XLog log, DecomposedMinerParameters parameters) {
		DecomposedDiscoveryParameters params = new DecomposedDiscoveryParameters(log);
		params.setClassifier(parameters.getClassifier());
		params.setMiner((new DecomposedILPMiner()).getName());
		return apply(context, log, params, null);
	}

	public AcceptingPetriNet apply(PluginContext context, XLog log, DecomposedDiscoveryParameters parameters,
			ActivityClusterArray clusters) {
		long msecs;
		if (clusters == null) {
			msecs = -System.currentTimeMillis();
			CausalActivityMatrix matrix = getMatrix(context, log, parameters);
			context.getProvidedObjectManager().createProvidedObject("Matrix", matrix, CausalActivityMatrix.class,
					context);
			msecs += System.currentTimeMillis();
			parameters.displayMessage("[DecomposedDiscoveryPlugin] Creating matrix took " + msecs + " milliseconds.");
			msecs = -System.currentTimeMillis();
			CausalActivityGraph graph = getGraph(context, matrix, parameters);
			context.getProvidedObjectManager().createProvidedObject("Graph", graph, CausalActivityGraph.class, context);
			msecs += System.currentTimeMillis();
			parameters.displayMessage("[DecomposedDiscoveryPlugin] Creating graph took " + msecs + " milliseconds.");
			msecs = -System.currentTimeMillis();
			clusters = getClusters(context, graph, parameters);
			if (parameters.isCombineClusters()) {
				clusters = combineClusters(clusters);
			}
			context.getProvidedObjectManager().createProvidedObject("Clusters", clusters, ActivityClusterArray.class,
					context);
			msecs += System.currentTimeMillis();
			parameters.displayMessage("[DecomposedDiscoveryPlugin] Creating " + clusters.getClusters().size()
					+ " clusters took " + msecs + " milliseconds.");
			msecs = -System.currentTimeMillis();
			clusters = getModifiedClusters(context, graph, clusters, parameters);
			context.getProvidedObjectManager().createProvidedObject("Modified Clusters", clusters, ActivityClusterArray.class, context);
			msecs += System.currentTimeMillis();
			parameters.displayMessage("[DecomposedDiscoveryPlugin] Modifying to " + clusters.getClusters().size()
					+ " clusters took " + msecs + " milliseconds.");
		} else {
			parameters.displayMessage("[DecomposedDiscoveryPlugin] Found " + clusters.getClusters().size()
					+ " clusters.");
		}

		msecs = -System.currentTimeMillis();
		EventLogArray logs = getLogs(context, log, clusters, parameters);
		context.getProvidedObjectManager().createProvidedObject("Logs", logs, EventLogArray.class, context);
		msecs += System.currentTimeMillis();
		parameters.displayMessage("[DecomposedDiscoveryPlugin] Decomposing log took " + msecs + " milliseconds.");

		msecs = -System.currentTimeMillis();
		logs = getFilteredLogs(context, logs, parameters);
		context.getProvidedObjectManager().createProvidedObject("Logs", logs, EventLogArray.class, context);
		msecs += System.currentTimeMillis();
		parameters.displayMessage("[DecomposedDiscoveryPlugin] Filtering logs took " + msecs + " milliseconds.");

		msecs = -System.currentTimeMillis();
		AcceptingPetriNetArray nets = getNets(context, logs, parameters);
		context.getProvidedObjectManager().createProvidedObject("Nets", nets, AcceptingPetriNetArray.class, context);
		msecs += System.currentTimeMillis();
		parameters.displayMessage("[DecomposedDiscoveryPlugin] Decomposed discovery took " + msecs + " milliseconds.");

		msecs = -System.currentTimeMillis();
		AcceptingPetriNet net = getNet(context, nets, parameters);
		msecs += System.currentTimeMillis();
		parameters.displayMessage("[DecomposedDiscoveryPlugin] Merging nets took " + msecs + " milliseconds.");

		msecs = -System.currentTimeMillis();
		net = getNetStartEnd(context, net, parameters); // Remove start-end transitions
		context.log("Removing structurally redundant places");
		net = getNetBerthelot(context, net, parameters); // Remove structurally redundant places (cf Berthelot)
		context.log("Reducing net using Murata rules");
		net = getNetMurata(context, net, parameters); // Apply Murata rules
		msecs += System.currentTimeMillis();

		parameters.displayMessage("[DecomposedDiscoveryPlugin] Reducing net took " + msecs + " milliseconds.");

		return net;
	}

	public CausalActivityMatrix getMatrix(PluginContext context, XLog log, DecomposedDiscoveryParameters parameters) {
		DiscoverFromEventLogPlugin plugin = new DiscoverFromEventLogPlugin();
		DiscoverFromEventLogParameters params = new DiscoverFromEventLogParameters(log);
		params.setClassifier(parameters.getClassifier());
		params.setMiner((new HAFMidiMatrixMiner()).getName());
		params.setTryConnections(parameters.isTryConnections());
		return plugin.run(context, log, params);
	}

	public CausalActivityGraph getGraph(PluginContext context, CausalActivityMatrix matrix,
			DecomposedDiscoveryParameters parameters) {
		ConvertCausalActivityMatrixToCausalActivityGraphPlugin plugin = new ConvertCausalActivityMatrixToCausalActivityGraphPlugin();
		ConvertCausalActivityMatrixToCausalActivityGraphParameters params = new ConvertCausalActivityMatrixToCausalActivityGraphParameters();
		params.setZeroValue(0.6);//Ô­À´ÊÇ0.5
		params.setConcurrencyRatio(0.005);
		params.setTryConnections(parameters.isTryConnections());
		return plugin.run(context, matrix, params);
	}

	public ActivityClusterArray getClusters(PluginContext context, CausalActivityGraph graph,
			DecomposedDiscoveryParameters parameters) {
		ConvertCausalActivityGraphToActivityClusterArrayPlugin plugin = new ConvertCausalActivityGraphToActivityClusterArrayPlugin();
		ConvertCausalActivityGraphToActivityClusterArrayParameters params = new ConvertCausalActivityGraphToActivityClusterArrayParameters(
				graph);
		params.setCheckBackwardArc(false);
		params.setIncludeAll(true);
		params.setTryConnections(parameters.isTryConnections());
		return plugin.run(context, graph, params);
	}

	public ActivityClusterArray getModifiedClusters(PluginContext context, CausalActivityGraph graph, ActivityClusterArray clusters,
			DecomposedDiscoveryParameters parameters) {
		ModifyActivityClusterArrayPlugin plugin = new ModifyActivityClusterArrayPlugin();
		ModifyActivityClusterArrayParameters params = new ModifyActivityClusterArrayParameters(clusters);
		params.setNrOfClusters((clusters.getClusters().size() * parameters.getPercentage()) / 100);
		return plugin.run(context, clusters, graph, params);
	}
	
	public EventLogArray getLogs(PluginContext context, XLog log, ActivityClusterArray clusters,
			DecomposedDiscoveryParameters parameters) {
		DecomposeEventLogUsingActivityClusterArrayPlugin plugin = new DecomposeEventLogUsingActivityClusterArrayPlugin();
		DecomposeEventLogUsingActivityClusterArrayParameters params = new DecomposeEventLogUsingActivityClusterArrayParameters(
				log);
		params.setClassifier(parameters.getClassifier());
		params.setRemoveEmptyTraces(false);
		params.setAddStartEndEvents(needStartEnd(parameters));
		params.setFilter(DecompositionInFilter.NAME);
		params.setTryConnections(parameters.isTryConnections());
		return plugin.run(context, log, clusters, params);
	}

	private boolean needStartEnd(DecomposedDiscoveryParameters parameters) {
		switch (parameters.getArtificialStrategy()) {
			case ALWAYS: return true;
			case NEVER: return false;
			case MINER: // Fall through
			default: return DecomposedMinerManager.getInstance().getMiner(parameters.getMiner()).requiresStartEndEvents();
		}
	}
	
	public EventLogArray getFilteredLogs(PluginContext context, EventLogArray logs,
			DecomposedDiscoveryParameters parameters) {
		/*
		 * No filtering.
		 */
		return logs;
	}

	public AcceptingPetriNetArray getNets(PluginContext context, EventLogArray logs,
			DecomposedDiscoveryParameters parameters) {
		DiscoverAcceptingPetriNetArrayFromEventLogArrayPlugin plugin = new DiscoverAcceptingPetriNetArrayFromEventLogArrayPlugin();
		DiscoverAcceptingPetriNetArrayFromEventLogArrayParameters params = new DiscoverAcceptingPetriNetArrayFromEventLogArrayParameters(
				logs);
		params.setClassifier(parameters.getClassifier());
		params.setMiner(parameters.getMiner());
		params.setTransitionBordered(false);
		params.setRemoveGamma(false);
		params.setTryConnections(false);
		params.setTryConnections(parameters.isTryConnections());
		return plugin.run(context, logs, params);
	}

	public AcceptingPetriNet getNet(PluginContext context, AcceptingPetriNetArray nets,
			DecomposedDiscoveryParameters parameters) {
		MergeAcceptingPetriNetArrayIntoAcceptingPetriNetPlugin plugin = new MergeAcceptingPetriNetArrayIntoAcceptingPetriNetPlugin();
		MergeAcceptingPetriNetArrayIntoAcceptingPetriNetParameters params = new MergeAcceptingPetriNetArrayIntoAcceptingPetriNetParameters();
		params.setAddWFPlaces(false);
		params.setTryConnections(parameters.isTryConnections());
		return plugin.run(context, nets, params);
	}

	public AcceptingPetriNet getNetBerthelot(PluginContext context, AcceptingPetriNet net,
			DecomposedDiscoveryParameters parameters) {
		RemoveStructuralRedundantPlacesPlugin plugin = new RemoveStructuralRedundantPlacesPlugin();
		RemoveStructuralRedundantPlacesParameters params = new RemoveStructuralRedundantPlacesParameters();
		params.setTryConnections(parameters.isTryConnections());
		return plugin.run(context, net, params);
	}

	public AcceptingPetriNet getNetStartEnd(PluginContext context, AcceptingPetriNet net,
			DecomposedDiscoveryParameters parameters) {
		if (!needStartEnd(parameters)) {
			return net;
		}
		RemoveStartEndTransitionsPlugin plugin = new RemoveStartEndTransitionsPlugin();
		RemoveStartEndTransitionsParameters params = new RemoveStartEndTransitionsParameters();
		params.setAbstraction(StartEndHideFilter.NAME);
		params.setTryConnections(parameters.isTryConnections());
		return plugin.run(context, net, params);
	}

	public AcceptingPetriNet getNetMurata(PluginContext context, AcceptingPetriNet net,
			DecomposedDiscoveryParameters parameters) {
		ReduceUsingMurataRulesPlugin plugin = new ReduceUsingMurataRulesPlugin();
		ReduceUsingMurataRulesParameters params = new ReduceUsingMurataRulesParameters();
		params.setTryConnections(parameters.isTryConnections());
		return plugin.run(context, net, params);
	}

	private ActivityClusterArray combineClusters(ActivityClusterArray clusters) {
		ActivityClusterArray combinedClusters = ActivityClusterArrayFactory.createActivityClusterArray();
		Set<XEventClass> activities = new HashSet<XEventClass>();
		for (Set<XEventClass> cluster : clusters.getClusters()) {
			activities.addAll(cluster);
		}
		combinedClusters.init("", activities);
		combinedClusters.addCluster(activities);
		ActivityClusterArray results = combinedClusters;
		return results;
	}
}
