package org.processmining.decomposedminer.configurations.impl;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNetArray;
import org.processmining.activityclusterarray.models.ActivityClusterArray;
import org.processmining.activityclusterarraycreator.metrics.ActivityClusterArrayMetric;
import org.processmining.activityclusterarraycreator.metrics.impl.ActivityClusterArrayBalanceMetric;
import org.processmining.activityclusterarraycreator.metrics.impl.ActivityClusterArrayCohesionMetric;
import org.processmining.activityclusterarraycreator.metrics.impl.ActivityClusterArrayCouplingMetric;
import org.processmining.activityclusterarraycreator.metrics.impl.ActivityClusterArrayOverlapMetric;
import org.processmining.activityclusterarraycreator.metrics.impl.ActivityClusterArrayWeightedMetric;
import org.processmining.causalactivitygraph.models.CausalActivityGraph;
import org.processmining.causalactivitymatrix.models.CausalActivityMatrix;
import org.processmining.causalactivitymatrixminer.miners.impl.HeuristicsMatrixMiner;
import org.processmining.decomposedminer.parameters.DecomposedDiscoveryParameters;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.log.models.EventLogArray;

public abstract class DecomposedAbstractClusteringDiscoveryConfiguration extends DecomposedAbstractDiscoveryConfiguration {

	private double bestScore = 0.0;

	public abstract int getPercentage(DecomposedDiscoveryParameters parameters);
	
	public AcceptingPetriNet apply(PluginContext context, XLog log, DecomposedDiscoveryParameters parameters,
			ActivityClusterArray clusters) {
		long msecs;
		DecomposedFlexibleDiscoveryConfiguration configuration = new DecomposedFlexibleDiscoveryConfiguration();
//		String[] miners = new String[] {
//			(new FuzzyMatrixMiner()).getName(),
//			(new HeuristicsMatrixMiner()).getName(),
//			(new HAFMidiMatrixMiner()).getName()
//		};
		String[] miners = new String[] {
				
				(new HeuristicsMatrixMiner()).getName()
				
			};
//		double zeroValues[] = new double[] { -0.6, -0.5, 0.0, 0.5, 0.9 };
//		double concurrencyRatios[] = new double[] { 0.0, 0.005 };
		double zeroValues[] = new double[] { 0.7};
		double concurrencyRatios[] = new double[] {  0.10 };
		System.out.println("���ѡ��");
		
//		Collection<DecomposedAbstractMinerZeroDiscoveryConfiguration> configurations = new HashSet<DecomposedAbstractMinerZeroDiscoveryConfiguration>();
//		configurations.add(new DecomposedFuzzyNegDiscoveryConfiguration());
//		configurations.add(new DecomposedFuzzyOptDiscoveryConfiguration());
//		configurations.add(new DecomposedFuzzyPosDiscoveryConfiguration());
//		configurations.add(new DecomposedFuzzyPosDiscoveryConfiguration());
//		configurations.add(new DecomposedHeuristicsNegDiscoveryConfiguration());
//		configurations.add(new DecomposedHeuristicsOptDiscoveryConfiguration());
//		configurations.add(new DecomposedHeuristicsPosDiscoveryConfiguration());
//		configurations.add(new DecomposedHeuristicsZeroDiscoveryConfiguration());
//		configurations.add(new DecomposedMidiNegDiscoveryConfiguration());
//		configurations.add(new DecomposedMidiOptDiscoveryConfiguration());
//		configurations.add(new DecomposedMidiPosDiscoveryConfiguration());
//		configurations.add(new DecomposedMidiZeroDiscoveryConfiguration());
		ActivityClusterArray bestClusters = null;
		bestScore = 0.0;
		for (int m = 0; m < miners.length; m++) {
			configuration.setMiner(miners[m]);
			for (int z = 0; z < zeroValues.length; z++) {
				configuration.setZeroValue(zeroValues[z]);
				for (int c = 0; c < concurrencyRatios.length; c++) {
					configuration.setConcurrencyRatio(concurrencyRatios[c]);
					DecomposedDiscoveryParameters tempParameters = new DecomposedDiscoveryParameters(parameters);
					tempParameters.setConfiguration(configuration.getName());
					tempParameters.setPercentage(getPercentage(parameters));
					msecs = -System.currentTimeMillis();
					CausalActivityMatrix matrix = configuration.getMatrix(context, log, tempParameters);
					context.getProvidedObjectManager().createProvidedObject("Matrix", matrix, CausalActivityMatrix.class,
							context);
					msecs += System.currentTimeMillis();
					parameters.displayMessage("[DecomposedDiscoveryPlugin] Creating matrix took " + msecs + " milliseconds.");
					msecs = -System.currentTimeMillis();
					CausalActivityGraph graph = configuration.getGraph(context, matrix, tempParameters);
					context.getProvidedObjectManager().createProvidedObject("Graph", graph, CausalActivityGraph.class, context);
					msecs += System.currentTimeMillis();
					parameters.displayMessage("[DecomposedDiscoveryPlugin] Creating graph took " + msecs + " milliseconds.");
					msecs = -System.currentTimeMillis();
					clusters = configuration.getClusters(context, graph, tempParameters);
					
					
					
					
					
					context.getProvidedObjectManager().createProvidedObject("Clusters", clusters, ActivityClusterArray.class,
							context);
					msecs += System.currentTimeMillis();
					parameters.displayMessage("[DecomposedDiscoveryPlugin] Creating " + clusters.getClusters().size()
							+ " clusters took " + msecs + " milliseconds.");
					msecs = -System.currentTimeMillis();
					clusters = configuration.getModifiedClusters(context, graph, clusters, tempParameters);
					System.out.println("*************clusters.getClusters().size()"+clusters.getClusters().size());
					context.getProvidedObjectManager().createProvidedObject("Modified Clusters", clusters, ActivityClusterArray.class, context);
					msecs += System.currentTimeMillis();
					parameters.displayMessage("[DecomposedDiscoveryPlugin] Modifying to " + clusters.getClusters().size()
							+ " clusters took " + msecs + " milliseconds.");
					if (isBetter(context, clusters, graph)) {
						parameters.displayMessage("[DecomposedDiscoveryPlugin] New best score " + bestScore + ", configuration is " + configuration.getName());
						bestClusters = clusters;
					}
				}
			}
		}
//		for (DecomposedAbstractMinerZeroDiscoveryConfiguration configuration : configurations) {
//			DecomposedDiscoveryParameters tempParameters = new DecomposedDiscoveryParameters(parameters);
//			tempParameters.setConfiguration(configuration.getName());
//			tempParameters.setPercentage(getPercentage(parameters));
//			msecs = -System.currentTimeMillis();
//			CausalActivityMatrix matrix = configuration.getMatrix(context, log, tempParameters);
//			context.getProvidedObjectManager().createProvidedObject("Matrix", matrix, CausalActivityMatrix.class,
//					context);
//			msecs += System.currentTimeMillis();
//			parameters.displayMessage("[DecomposedDiscoveryPlugin] Creating matrix took " + msecs + " milliseconds.");
//			msecs = -System.currentTimeMillis();
//			CausalActivityGraph graph = configuration.getGraph(context, matrix, tempParameters);
//			context.getProvidedObjectManager().createProvidedObject("Graph", graph, CausalActivityGraph.class, context);
//			msecs += System.currentTimeMillis();
//			parameters.displayMessage("[DecomposedDiscoveryPlugin] Creating graph took " + msecs + " milliseconds.");
//			msecs = -System.currentTimeMillis();
//			clusters = configuration.getClusters(context, graph, tempParameters);
//			context.getProvidedObjectManager().createProvidedObject("Clusters", clusters, ActivityClusterArray.class,
//					context);
//			msecs += System.currentTimeMillis();
//			parameters.displayMessage("[DecomposedDiscoveryPlugin] Creating " + clusters.getClusters().size()
//					+ " clusters took " + msecs + " milliseconds.");
//			msecs = -System.currentTimeMillis();
//			clusters = configuration.getModifiedClusters(context, graph, clusters, tempParameters);
//			context.getProvidedObjectManager().createProvidedObject("Modified Clusters", clusters, ActivityClusterArray.class, context);
//			msecs += System.currentTimeMillis();
//			parameters.displayMessage("[DecomposedDiscoveryPlugin] Modifying to " + clusters.getClusters().size()
//					+ " clusters took " + msecs + " milliseconds.");
//			if (isBetter(context, clusters, graph)) {
//				parameters.displayMessage("[DecomposedDiscoveryPlugin] New best score " + bestScore + ", configuration is " + configuration.getName());
//				bestClusters = clusters;
//			}
//			
//		}
		clusters = bestClusters;

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
		//
		
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
	
	private boolean isBetter(PluginContext context, ActivityClusterArray clusters, CausalActivityGraph graph) {
		ActivityClusterArrayMetric weighted = new ActivityClusterArrayWeightedMetric(
				new ActivityClusterArrayOverlapMetric(100),
				new ActivityClusterArrayCohesionMetric(100),
				new ActivityClusterArrayCouplingMetric(100),
				new ActivityClusterArrayBalanceMetric(100));

		double score = clusters.getClusters().size() * weighted.compute(context, clusters, graph);
		if (score > bestScore) {
			bestScore = score;
			return true;
		}
		return false;
	}
}
