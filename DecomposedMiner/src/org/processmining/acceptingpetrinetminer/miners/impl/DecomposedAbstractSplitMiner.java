package org.processmining.acceptingpetrinetminer.miners.impl;

import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinetminer.miners.DecomposedImplicitMiner;
import org.processmining.acceptingpetrinetminer.miners.DecomposedMinerParameters;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.packages.UnknownPackageException;
import org.processmining.framework.packages.UnknownPackageTypeException;
import org.processmining.framework.packages.impl.CancelledException;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;

import com.raffaeleconforti.conversion.bpmn.BPMNToPetriNetConverter;

import au.edu.qut.processmining.miners.splitminer.SplitMiner;
import au.edu.qut.processmining.miners.splitminer.ui.dfgp.DFGPUIResult.FilterType;
import au.edu.qut.processmining.miners.splitminer.ui.miner.SplitMinerUI;
import au.edu.qut.processmining.miners.splitminer.ui.miner.SplitMinerUIResult;
import au.edu.qut.processmining.miners.splitminer.ui.miner.SplitMinerUIResult.StructuringTime;

public abstract class DecomposedAbstractSplitMiner implements DecomposedImplicitMiner{
	private static final String NAME = "Split Miner";
	
	public Petrinet mineImplicit(PluginContext context, XLog eventLog, DecomposedMinerParameters parameters)
			throws CancelledException, UnknownPackageTypeException, UnknownPackageException, ConnectionCannotBeObtained {
		
		System.out.println("Ê¹ÓÃ·ÖÁÑÍÚ¾ò");
		
		SplitMinerUI gui;		
		SplitMinerUIResult result = new SplitMinerUIResult();
		
		result.setPercentileFrequencyThreshold(0.4);
		result.setParallelismsThreshold(0.1);
		result.setFilterType(FilterType.FWG);
		result.setParallelismsFirst(false);
		result.setReplaceIORs(true);
		result.setStructuringTime(StructuringTime.NONE);
		SplitMiner sm = new SplitMiner();
		
		BPMNDiagram output = sm.mineBPMNModel(eventLog, new XEventNameClassifier(), result.getPercentileFrequencyThreshold(), result.getParallelismsThreshold(), result.getFilterType(), result.isParallelismsFirst(), result.isReplaceIORs(), result.isRemoveLoopActivities(), result.getStructuringTime());
		
		Petrinet pn = (Petrinet) BPMNToPetriNetConverter.convert(output)[0];
		
		
		return pn;
	}
	public abstract MiningParameters createParameters(); 
	public String getName() {
		return NAME;
	}
	
	public abstract void setAdditionalParameters(MiningParameters minerParameters);
	
	public boolean requiresStartEndEvents() {
		return false;
	}	
	
	

}
