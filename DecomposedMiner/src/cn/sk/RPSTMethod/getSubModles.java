package cn.sk.RPSTMethod;

import java.util.concurrent.ConcurrentHashMap;

import org.jbpt.algo.tree.rpst.RPSTNode;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNetArray;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetArrayFactory;
import org.processmining.acceptingpetrinetminer.parameters.DiscoverAcceptingPetriNetArrayFromEventLogArrayParameters;
import org.processmining.acceptingpetrinetminer.plugins.DiscoverAcceptingPetriNetArrayFromEventLogArrayPlugin;
import org.processmining.causalactivitymatrixminer.parameters.DiscoverFromEventLogParameters;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.log.models.EventLogArray;
import org.processmining.log.models.impl.EventLogArrayImpl;

import cn.sk.untils.IntermediateData;

public class getSubModles {

	ConcurrentHashMap<RPSTNode, IntermediateData> internalData;
	DiscoverFromEventLogParameters parameters;
	UIPluginContext context;

	public getSubModles(ConcurrentHashMap<RPSTNode, IntermediateData> internalData,
			DiscoverFromEventLogParameters parameters, UIPluginContext context) {
		this.internalData = internalData;
		this.parameters = parameters;
		this.context = context;
	}

	public ConcurrentHashMap<RPSTNode, IntermediateData> generateSubModles() {
		EventLogArray logs = new EventLogArrayImpl();
		AcceptingPetriNetArray nets = AcceptingPetriNetArrayFactory.createAcceptingPetriNetArray();
		for (RPSTNode node : internalData.keySet()) {
			logs.addLog(internalData.get(node).getXlog());
			nets.addNet((internalData.get(node).getSubModelIM()));
		}
		DiscoverAcceptingPetriNetArrayFromEventLogArrayPlugin plugin = new DiscoverAcceptingPetriNetArrayFromEventLogArrayPlugin();
		DiscoverAcceptingPetriNetArrayFromEventLogArrayParameters params = new DiscoverAcceptingPetriNetArrayFromEventLogArrayParameters(
				logs);
		params.setClassifier(parameters.getClassifier());
		params.setMiner(parameters.getMiner());
		params.setTransitionBordered(false);
		params.setRemoveGamma(false);
		params.setTryConnections(false);
		params.setTryConnections(parameters.isTryConnections());
		nets = plugin.run(context, logs, params);
		int i = 0;
		for (RPSTNode node : internalData.keySet()) {

			internalData.get(node).setSubModelIM(nets.getNet(i++));
			internalData.get(node).setHasModel(true);;
			
		}

		return internalData;

	}
}
