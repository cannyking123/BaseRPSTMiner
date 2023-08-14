package org.processmining.decomposedminer.plugins;

import java.util.Collection;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinetminer.miners.DecomposedMinerManager;
import org.processmining.activityclusterarray.models.ActivityClusterArray;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.decomposedminer.algorithms.DecomposedDiscoveryAlgorithm;
import org.processmining.decomposedminer.connections.DecomposedDiscoveryConnection;
import org.processmining.decomposedminer.dialogs.DecomposedDiscoveryDialog;
import org.processmining.decomposedminer.help.DecomposedDiscoveryHelp;
import org.processmining.decomposedminer.parameters.DecomposedDiscoveryParameters;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.logdecomposer.classifiers.StartEndClassifier;

@Plugin(name = "AAA Discover using Decomposition", categories = { PluginCategory.Discovery }, parameterLabels = { "Event Log", "Parameters",
		"Activity Cluster Array" }, returnLabels = { "Accepting Petri net" }, returnTypes = { AcceptingPetriNet.class }, help = DecomposedDiscoveryHelp.TEXT)
public class DecomposedDiscoveryPlugin extends DecomposedDiscoveryAlgorithm {

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "H.M.W. Verbeek", email = "h.m.w.verbeek@tue.nl")
	@PluginVariant(variantLabel = "Decomposed Discovery, UI", requiredParameterLabels = { 0 })
	public AcceptingPetriNet runUI(UIPluginContext context, XLog log) {
		
		DecomposedDiscoveryParameters parameters = new DecomposedDiscoveryParameters(log);
		DecomposedDiscoveryDialog dialog = new DecomposedDiscoveryDialog(log, parameters);
		InteractionResult result = context.showWizard("Configure discovery (configuration, classifier, miner)", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			return null;
		}
		long startTime = System.currentTimeMillis();
		// Swap the selected classifier to be able to distinguish start and end events later on.
		parameters.setClassifier(new StartEndClassifier(parameters.getClassifier()));
		System.out.println("名字"+parameters.getMiner());
		AcceptingPetriNet r = runConnection(context, log, null, parameters);
		long endTime = System.currentTimeMillis();
		System.out.println("*******DC消耗时间*********"+(endTime-startTime));
		endTime=0;
		startTime=0;
		return r;
	}

	@Deprecated
	public AcceptingPetriNet discoverUI(UIPluginContext context, XLog log) {
		return runUI(context, log);
	}

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "H.M.W. Verbeek", email = "h.m.w.verbeek@tue.nl")
	@PluginVariant(variantLabel = "Decomposed Discovery, Default", requiredParameterLabels = { 0 })
	public AcceptingPetriNet runDefault(PluginContext context, XLog log) {
		DecomposedDiscoveryParameters parameters = new DecomposedDiscoveryParameters(log);
		parameters.setClassifier(new StartEndClassifier(parameters.getClassifier()));
		parameters.setMiner(DecomposedMinerManager.getInstance().getMiner(null).getName());
		return runConnection(context, log, null, parameters);
	}

	@Deprecated
	public AcceptingPetriNet discoverDefault(PluginContext context, XLog log) {
		return runDefault(context, log);
	}

	@PluginVariant(variantLabel = "Decomposed Discovery, Parameters", requiredParameterLabels = { 0, 1 })
	public AcceptingPetriNet run(PluginContext context, XLog log, DecomposedDiscoveryParameters parameters) {
		return runConnection(context, log, null, parameters);
	}

	@Deprecated
	public AcceptingPetriNet discoverDefault(PluginContext context, XLog log, DecomposedDiscoveryParameters parameters) {
		return run(context, log, parameters);
	}

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "H.M.W. Verbeek", email = "h.m.w.verbeek@tue.nl")
	@PluginVariant(variantLabel = "Decomposed Discovery, Parameters, Clusters", requiredParameterLabels = {
			0, 2 })
	public AcceptingPetriNet runDefault(UIPluginContext context, XLog log, ActivityClusterArray clusters) {
		DecomposedDiscoveryParameters parameters = new DecomposedDiscoveryParameters(log);
		DecomposedDiscoveryDialog dialog = new DecomposedDiscoveryDialog(log, parameters, clusters);
		InteractionResult result = context.showWizard("Configure discovery (miner, classifier, algorithm)", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			return null;
		}
		// Swap the selected classifier to be able to distinguish start and end events later on.
		parameters.setClassifier(new StartEndClassifier(parameters.getClassifier()));
		return runConnection(context, log, clusters, parameters);
	}

	@Deprecated
	public AcceptingPetriNet discoverDefault(UIPluginContext context, XLog log, ActivityClusterArray clusters) {
		return runDefault(context, log, clusters);
	}

	private AcceptingPetriNet runConnection(PluginContext context, XLog log, ActivityClusterArray clusters,
			DecomposedDiscoveryParameters parameters) {
		parameters.displayMessage("[DecomposedDiscoveryPlugin] Configuration: " + parameters.getConfiguration());
		parameters.displayMessage("[DecomposedDiscoveryPlugin] Classifier: " + parameters.getClassifier().name());
		parameters.displayMessage("[DecomposedDiscoveryPlugin] Miner: " + parameters.getMiner());
//		parameters.displayMessage("[DecomposedDiscoveryPlugin] Combine clusters: " + parameters.isCombineClusters());
		if (parameters.isTryConnections()) {
			Collection<DecomposedDiscoveryConnection> connections;
			try {
				connections = context.getConnectionManager().getConnections(DecomposedDiscoveryConnection.class,
						context, log);
				for (DecomposedDiscoveryConnection connection : connections) {
					if (connection.getObjectWithRole(DecomposedDiscoveryConnection.LOG).equals(log)
							&& connection.getObjectWithRole(DecomposedDiscoveryConnection.CLUSTERS).equals(clusters)
							&& connection.getParameters().equals(parameters)) {
						return connection.getObjectWithRole(DecomposedDiscoveryConnection.NET);
					}
				}
			} catch (ConnectionCannotBeObtained e) {
			}
		}

		AcceptingPetriNet net = apply(context, log, clusters, parameters);

		if (parameters.isTryConnections()) {
			context.getConnectionManager().addConnection(
					new DecomposedDiscoveryConnection(log, net, clusters, parameters));
		}
//		parameters.displayMessage("[DecomposedDiscoveryPlugin] Configuration: " + parameters.getConfiguration());
//		parameters.displayMessage("[DecomposedDiscoveryPlugin] Classifier: " + parameters.getClassifier().name());
//		parameters.displayMessage("[DecomposedDiscoveryPlugin] Miner: " + parameters.getMiner());
//		parameters.displayMessage("[DecomposedDiscoveryPlugin] Combine clusters: " + parameters.isCombineClusters());
		return net;
	}
}
