package org.processmining.decomposedminer.connections;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.activityclusterarray.models.ActivityClusterArray;
import org.processmining.decomposedminer.parameters.DecomposedDiscoveryParameters;
import org.processmining.framework.connections.impl.AbstractConnection;

public class DecomposedDiscoveryConnection extends AbstractConnection {

	public final static String LOG = "Log";
	public final static String NET = "Net";
	public final static String CLUSTERS = "Clusters";

	private DecomposedDiscoveryParameters parameters;

	public DecomposedDiscoveryConnection(XLog log, AcceptingPetriNet net, ActivityClusterArray clusters,
			DecomposedDiscoveryParameters parameters) {
		super("Discover Accepting Petri Net Using Decomposition Connection");
		put(LOG, log);
		put(NET, net);
		put(CLUSTERS, clusters);
		this.parameters = new DecomposedDiscoveryParameters(parameters);
	}

	public DecomposedDiscoveryParameters getParameters() {
		return parameters;
	}
}
