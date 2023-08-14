package org.processmining.decomposedminer.configurations;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNetArray;
import org.processmining.acceptingpetrinetminer.miners.DecomposedMinerParameters;
import org.processmining.activityclusterarray.models.ActivityClusterArray;
import org.processmining.causalactivitygraph.models.CausalActivityGraph;
import org.processmining.causalactivitymatrix.models.CausalActivityMatrix;
import org.processmining.decomposedminer.parameters.DecomposedDiscoveryParameters;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.log.models.EventLogArray;

import com.fluxicon.slickerbox.components.NiceSlider;

public interface DecomposedDiscoveryConfiguration /* extends DecomposedMiner */{

	public AcceptingPetriNet mine(PluginContext context, XLog log, DecomposedMinerParameters parameters);

	public CausalActivityMatrix getMatrix(PluginContext context, XLog log, DecomposedDiscoveryParameters parameters);

	public AcceptingPetriNet apply(PluginContext context, XLog log, DecomposedDiscoveryParameters parameters,
			ActivityClusterArray clusters);

	public CausalActivityGraph getGraph(PluginContext context, CausalActivityMatrix matrix,
			DecomposedDiscoveryParameters parameters);

	public ActivityClusterArray getClusters(PluginContext context, CausalActivityGraph graph,
			DecomposedDiscoveryParameters parameters);

	public ActivityClusterArray getModifiedClusters(PluginContext context, CausalActivityGraph graph,
			ActivityClusterArray clusters, DecomposedDiscoveryParameters parameters);

	public EventLogArray getLogs(PluginContext context, XLog log, ActivityClusterArray clusters,
			DecomposedDiscoveryParameters parameters);

	public EventLogArray getFilteredLogs(PluginContext context, EventLogArray logs,
			DecomposedDiscoveryParameters parameters);

	public AcceptingPetriNetArray getNets(PluginContext context, EventLogArray logs,
			DecomposedDiscoveryParameters parameters);

	public AcceptingPetriNet getNet(PluginContext context, AcceptingPetriNetArray nets,
			DecomposedDiscoveryParameters parameters);

	public AcceptingPetriNet getNetBerthelot(PluginContext context, AcceptingPetriNet net,
			DecomposedDiscoveryParameters parameters);

	public AcceptingPetriNet getNetStartEnd(PluginContext context, AcceptingPetriNet net,
			DecomposedDiscoveryParameters parameters);

	public AcceptingPetriNet getNetMurata(PluginContext context, AcceptingPetriNet net,
			DecomposedDiscoveryParameters parameters);

	public String getName();
	
	public void update(NiceSlider slider);
}
