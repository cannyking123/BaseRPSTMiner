package cn.sk.RPSTMethod;

import java.util.concurrent.ConcurrentHashMap;

import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.rpst.RPSTNode;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNetArray;
import org.processmining.causalactivitymatrix.parameters.ModifyCausalActivityMatrixParameters;
import org.processmining.framework.log.LogEvents;

import cern.colt.matrix.DoubleMatrix2D;
import cn.sk.untils.AndOrAnalysisParameters;
import cn.sk.untils.IntermediateData;
import cn.sk.untils.RPSTParameter;

public class getFinalmodle {

	DoubleMatrix2D directSuccession;
	LogEvents sortedEvents;
	RPST rpst;
	ConcurrentHashMap<RPSTNode, IntermediateData> internalData;
	ModifyCausalActivityMatrixParameters parameters2;
	public AcceptingPetriNetArray nets=null;

	public getFinalmodle(DoubleMatrix2D directSuccession, LogEvents sortedEvents,
			ConcurrentHashMap<RPSTNode, IntermediateData> internalData, RPST rpst,
			ModifyCausalActivityMatrixParameters parameters2) {

		this.rpst = rpst;
		this.directSuccession = directSuccession;
		this.sortedEvents = sortedEvents;
		this.parameters2 = parameters2;
		this.internalData = internalData;

	}

	public AcceptingPetriNet getNet() {
		AndOrAnalysisParameters andOrParameters = new AndOrAnalysisParameters(1, 1, 0.1);

		AndOrAnalysis andOrAnalysis = new AndOrAnalysis(sortedEvents, directSuccession, andOrParameters);

		megeSubLogs mege = new megeSubLogs(internalData, rpst, sortedEvents);
	//megeSubLogsOld mege = new megeSubLogsOld(internalData, rpst, sortedEvents);

		RPSTParameter rpstParameter = new RPSTParameter(2, parameters2.getP(),
				parameters2.getB(), 10000);
		AcceptingPetriNet net = mege.mergeSubModelsByDFS((RPSTNode) rpst.getRoot(), rpstParameter, andOrAnalysis);
		//nets = mege.getNets();
		

		return net;
	}

	public AcceptingPetriNetArray getAcceptingPetriNetArray() {
		
		return nets;
	}
}
