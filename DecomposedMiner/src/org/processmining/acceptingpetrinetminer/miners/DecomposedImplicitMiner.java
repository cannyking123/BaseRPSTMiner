package org.processmining.acceptingpetrinetminer.miners;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.packages.UnknownPackageException;
import org.processmining.framework.packages.UnknownPackageTypeException;
import org.processmining.framework.packages.impl.CancelledException;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

public interface DecomposedImplicitMiner {

	/*
	 * The specific miner wrapped.
	 * 
	 * This wrapped miner should result in a Petri net, and, if possible, should
	 * also construct an initial marking and a collection of final markings. The
	 * initial marking should be accessible through an InitialMarkingConnection
	 * connection. The final markings should be accessible through a series of
	 * FinalMarkingConnection connections. If no initial marking is constructed,
	 * an initial marking with all source places marked will be assumed. If no
	 * final markings are given, a collection of final markings is assumed such
	 * that every final marking corresponds to a single sink place marked.
	 */
	public Petrinet mineImplicit(PluginContext context, XLog eventLog, DecomposedMinerParameters parameters)
			throws CancelledException, UnknownPackageTypeException, UnknownPackageException, ConnectionCannotBeObtained;

	/*
	 * Gets the name of the miner.
	 */
	public abstract String getName();
	
	/*
	 * Whether this miner requires artificial [start> and [end] events to do the job.
	 */
	public boolean requiresStartEndEvents();
}
