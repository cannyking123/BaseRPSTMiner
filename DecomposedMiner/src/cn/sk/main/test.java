package cn.sk.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.jbpt.algo.tree.rpst.RPSTNode;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNetArray;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetArrayFactory;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.acceptingpetrinetredundantplacesreductor.parameters.RemoveStructuralRedundantPlacesParameters;
import org.processmining.acceptingpetrinetredundantplacesreductor.plugins.RemoveStructuralRedundantPlacesPlugin;
import org.processmining.causalactivitymatrix.connections.ModifyCausalActivityMatrixConnection;
import org.processmining.causalactivitymatrix.models.CausalActivityMatrix;
import org.processmining.causalactivitymatrix.modifiers.CausalActivityMatrixModifier;
import org.processmining.causalactivitymatrix.modifiers.impl.CausalActivityMatrixConcurrencyRatioModifier;
import org.processmining.causalactivitymatrix.modifiers.impl.CausalActivityMatrixIncludeThresholdModifier;
import org.processmining.causalactivitymatrix.modifiers.impl.CausalActivityMatrixStackedModifier;
import org.processmining.causalactivitymatrix.modifiers.impl.CausalActivityMatrixZeroValueModifier;
import org.processmining.causalactivitymatrix.parameters.ModifyCausalActivityMatrixParameters;
import org.processmining.causalactivitymatrix.utils.CausalActivityMatrixModifiers;
import org.processmining.causalactivitymatrixminer.connections.DiscoverFromEventLogConnection;
import org.processmining.causalactivitymatrixminer.dialogs.DiscoverFromEventLogDialog;
import org.processmining.causalactivitymatrixminer.miners.MatrixMiner;
import org.processmining.causalactivitymatrixminer.miners.MatrixMinerManager;
import org.processmining.causalactivitymatrixminer.parameters.DiscoverFromEventLogParameters;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.log.LogEvents;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.log.models.EventLogArray;
import org.processmining.log.models.impl.EventLogArrayImpl;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;

import cn.sk.RPSTMethod.getFinalmodle;
import cn.sk.RPSTMethod.getNeedRPSTNodes;
import cn.sk.RPSTMethod.getSubLogs;
import cn.sk.RPSTMethod.getSubModles;
import cn.sk.dialogs.DialogMy2;
import cn.sk.untils.IntermediateData;

public class test {

	@Plugin(name = "AAAAselectmethod", level = PluginLevel.Regular, returnLabels = { "String" }, returnTypes = {
			AcceptingPetriNet.class }, parameterLabels = { "Log" }, userAccessible = true, help = "selectmethod")
	@UITopiaVariant(affiliation = "", author = "wangkang", email = "1159501573@qq.com")
	@PluginVariant(variantLabel = "selectmethod", requiredParameterLabels = { 0 })
	public AcceptingPetriNet mineCAM(UIPluginContext context, XLog log) {
		//initialize
		ConcurrentHashMap<RPSTNode, IntermediateData> internalData = new ConcurrentHashMap<RPSTNode, IntermediateData>();

		//step1 get graph
		
		DiscoverFromEventLogParameters parameters = new DiscoverFromEventLogParameters(log);
		DiscoverFromEventLogDialog dialog = new DiscoverFromEventLogDialog(log, parameters);

		InteractionResult result1 = context.showWizard("Configure discovery (classifier, miner)", true, true, dialog);
		if (result1 != InteractionResult.FINISHED) {
			return null;
		}
		long Time1 = System.currentTimeMillis();
		
		CausalActivityMatrix matrix = minePrivateConnection(context, log, parameters);
		long Time2 = System.currentTimeMillis();
		long Time3 = Time2-Time1;
		
		ModifyCausalActivityMatrixParameters parameters2 = new ModifyCausalActivityMatrixParameters();
		DialogMy2 dia = new DialogMy2(context, matrix, parameters2);
		parameters2.setIncludeThreshold(0.005);//3
		parameters2.setConcurrencyRatio(0.1);//2
		parameters2.setZeroValue(0.7);//1

		InteractionResult result2 = context.showWizard("Configure discovery (configuration, classifier, miner)", true,
				true, dia);
		if (result2 != InteractionResult.FINISHED) {
			return null;
		}
		System.out.println("00" + parameters2.getZeroValue());
		System.out.println("B" + parameters2.getB());
		System.out.println("P" + parameters2.getP());
		long Time4 = System.currentTimeMillis();
		CausalActivityMatrix finalCausalMatrix = runConnections(context, matrix, parameters2);
		//step2 get RPST
		//System.out.println("伽马" + parameters2.getGammer());
		getNeedRPSTNodes getNodes = new getNeedRPSTNodes(parameters2, finalCausalMatrix);
		ArrayList<RPSTNode> neededNode = getNodes.getArrayListRPSTNode();
		
		
		LogEvents logEvents = getNodes.getLogEvents();

		//step3 get sublogs

		getSubLogs getLog = new getSubLogs(neededNode, log, parameters2.getMiner(), logEvents, finalCausalMatrix);
		//getSubLogsOld getLog = new getSubLogsOld(neededNode, log, parameters2.getMiner(), logEvents, finalCausalMatrix);
		internalData = getLog.generateSubLogs();
		//step4 get submodles
		parameters.setMiner(parameters2.getMiner());
		getSubModles getModle = new getSubModles(internalData, parameters, context);
		internalData = getModle.generateSubModles();
		//stept5 get finalmodle
		AcceptingPetriNetArray nets = AcceptingPetriNetArrayFactory.createAcceptingPetriNetArray();

		EventLogArray array = new EventLogArrayImpl();
		int i = 0;
		int ti = 0;
		//rename place Lable
		for (RPSTNode node : internalData.keySet()) {
			array.addLog(internalData.get(node).getXlog());
			nets.addNet((internalData.get(node).getSubModelIM()));
			Petrinet net = internalData.get(node).getSubModelIM().getNet();
			for (Place place : net.getPlaces()) {
				//place重命名
				place.reLable("n" + i++);
			}
			for (Transition transition : net.getTransitions()) {
				//place重命名
				if (transition.isInvisible()) {
					transition.reLable("tau from tree" + ti++);
				}

			}

		}
		getFinalmodle getFinal = new getFinalmodle(getLog.getDirectSuccession(), getLog.getSortLogEvent(), internalData,
				getNodes.getRPST(), parameters2);
		AcceptingPetriNet finalNet = getFinal.getNet();
		//add start and end
		finalNet = deal(finalNet);
		//old subLog test

		//		for (RPSTNode node : neededNode) {
		//			System.out.println("活动数量："+internalData.get(node).getEventsInNode(node).size());
		//			System.out.println(internalData.get(node).getEventsInNode(node));
		//			int o = 0;
		//			for(XTrace trace :internalData.get(node).getXlog()) {
		//				if(o<50&&o<internalData.get(node).getXlog().size()&&trace.isEmpty()==false) {
		//				System.out.println("轨迹大小"+trace.size());
		//				for(XEvent event :trace) {
		//					
		//					
		//						System.out.print(XConceptExtension.instance().extractName(event));
		//						o++;
		//					}
		//				System.out.println();
		//				}
		//				
		//			}
		//			
		//		}

		//			
		//			System.out.println("----------");
		//		}
		//		for (RPSTNode node : neededNode) {
		//			System.out.println("入口是"+node.getEntry()+" 出口是"+node.getExit());	
		//		}
		//		
		RemoveStructuralRedundantPlacesPlugin pl = new RemoveStructuralRedundantPlacesPlugin();
		RemoveStructuralRedundantPlacesParameters parametersR = new RemoveStructuralRedundantPlacesParameters();
		//pl.apply(context, finalNet, parametersR);
		long Time5 = System.currentTimeMillis();
		long Time6 =Time5-Time4;
		System.out.println("*******第二阶段消耗时间*********"+(Time6+Time3));
		//return pl.apply(context, finalNet, parametersR);
		//System.out.println("返回模型");
		return finalNet;
		//		return getFinal.getAcceptingPetriNetArray();
		//		return nets;
		//		return parameters2.getMiner() + " " + parameters2.getGammer() + " " + parameters2.getIncludeThreshold() + " "
		//				+ parameters2.getZeroValue() + " " + parameters2.getConcurrencyRatio();
	}

	private AcceptingPetriNet deal(AcceptingPetriNet newNet) {

		Petrinet net2 = newNet.getNet();

		AcceptingPetriNet mergedNet = AcceptingPetriNetFactory.createAcceptingPetriNet();
		mergedNet.init(PetrinetFactory.newPetrinet("final"));

		Map<Place, Place> placeMap = new HashMap<Place, Place>();
		Map<Transition, Transition> transitionMap = new HashMap<Transition, Transition>();
		Map<String, Transition> transitionLabelMap = new HashMap<String, Transition>();

		List<String> comTranNames = new ArrayList<String>();
		for (Transition transition : net2.getTransitions()) {
			Transition mergedTransition = null;
			if (transition.isInvisible()) {
				mergedTransition = mergedNet.getNet().addTransition(transition.getLabel());
				mergedTransition.setInvisible(true);
			} else {
				String label = transition.getLabel();
				mergedTransition = transitionLabelMap.get(label);
				if (mergedTransition == null) {
					mergedTransition = mergedNet.getNet().addTransition(label);
					mergedTransition.setInvisible(false);
					transitionLabelMap.put(label, mergedTransition);
				} else {
					comTranNames.add(label);//公共变迁
					//System.out.println("公共变迁是" + label);
				}
			}
			transitionMap.put(transition, mergedTransition);
		}
		for (Place place : net2.getPlaces()) {

			Place mergedPlace = mergedNet.getNet().addPlace(place.getLabel());
			placeMap.put(place, mergedPlace);

		}

		for (PetrinetEdge<?, ?> edge : net2.getEdges()) {
			if (edge instanceof Arc) {
				Arc arc = (Arc) edge;
				if (arc.getSource() instanceof Place) {
					mergedNet.getNet().addArc(placeMap.get(arc.getSource()), transitionMap.get(arc.getTarget()));
				} else {
					mergedNet.getNet().addArc(transitionMap.get(arc.getSource()), placeMap.get(arc.getTarget()));
				}
			}
		}

		Petrinet net = mergedNet.getNet();

		Collection<Transition> places = net.getTransitions();
		ArrayList<Transition> places1 = new ArrayList<Transition>();
		int size = places.size();
		for (Transition p : places) {
			places1.add(p);

		}

		for (int i = 0; i < size; i++) {
			Transition p = places1.get(i);
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outEdge = net.getOutEdges(p);
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> inEdge = net.getInEdges(p);

			if (outEdge.size() == 0 && inEdge.size() == 0) {
				net.removeNode(p);
			}
		}

		//newTau
		for (Transition t : net.getTransitions()) {
			if (t.getLabel().contains("newTau")) {
				t.setInvisible(true);
			}

			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outEdge = net.getOutEdges(t);
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> inEdge = net.getInEdges(t);

			if (outEdge.size() == 0) {
				net.addPlace("end");
				net.addArc(t, getP(net, "end"));
			}

			if (inEdge.size() == 0) {
				net.addPlace("start");
				net.addArc(getP(net, "start"), t);
			}

		}
		Marking marking = new Marking();
		Set<Marking> markings = new HashSet<Marking>();
		for (Place place : net.getPlaces()) {
			if (net.getInEdges(place).isEmpty()) {
				marking.add(place);
			}
			if (net.getOutEdges(place).isEmpty()) {
				Marking finalMarking = new Marking();
				finalMarking.add(place);
				markings.add(finalMarking);
			}
		}
		if (markings.isEmpty()) {
			markings.add(new Marking());
		}
		mergedNet.setInitialMarking(marking);
		mergedNet.setFinalMarkings(markings);

		return mergedNet;
	}

	private Place getP(Petrinet net, String string) {

		for (Place p : net.getPlaces()) {
			if (p.getLabel().equals(string)) {
				return p;
			}
		}

		return null;
	}

	private Transition getT(Petrinet net, String string) {

		for (Transition p : net.getTransitions()) {
			if (p.getLabel().equals(string)) {
				return p;
			}
		}

		return null;
	}

	private boolean hasEdeg(PetrinetEdge<?, ?> edge1,
			Set<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> e2) {

		for (PetrinetEdge<?, ?> edge : e2) {
			//System.out.println(edge.getSource().getLabel()+"  "+edge1.getSource().getLabel()+"  "+edge.getTarget().getLabel()+"   "+edge1.getTarget().getLabel());
			if (edge.getSource().getLabel().equals(edge1.getSource().getLabel())
					&& edge.getTarget().getLabel().equals(edge1.getTarget().getLabel())) {
				System.out.println("hasEdge返回true");
				return true;
			}
		}

		return false;
	}

	private CausalActivityMatrix minePrivateConnection(PluginContext context, XLog log,
			DiscoverFromEventLogParameters parameters) {
		if (parameters.isTryConnections()) {
			Collection<DiscoverFromEventLogConnection> connections;
			try {
				connections = context.getConnectionManager().getConnections(DiscoverFromEventLogConnection.class,
						context, log);
				for (DiscoverFromEventLogConnection connection : connections) {
					if (connection.getObjectWithRole(DiscoverFromEventLogConnection.LOG).equals(log)
							&& connection.getParameters().equals(parameters)) {
						return connection.getObjectWithRole(DiscoverFromEventLogConnection.MATRIX);
					}
				}
			} catch (ConnectionCannotBeObtained e) {
			}
		}
		CausalActivityMatrix matrix = apply(context, log, parameters);
		if (parameters.isTryConnections()) {
		}
		return matrix;
	}

	private CausalActivityMatrix apply(PluginContext context, XLog log, DiscoverFromEventLogParameters parameters) {
		MatrixMiner miner = MatrixMinerManager.getInstance().getMiner(parameters.getMiner());
		CausalActivityMatrix matrix = miner.mineMatrix(context, log, parameters);
		if (parameters.isAutoAdjust()) {
			if (miner.getZeroValue() != 0.0) {
				matrix = CausalActivityMatrixModifiers.applyZeroValue(matrix, miner.getZeroValue());
			}
			if (miner.getConcurrencyRatio() != 0.0) {
				matrix = CausalActivityMatrixModifiers.applyConcurrencyRatio(matrix, miner.getConcurrencyRatio());
			}
			matrix = CausalActivityMatrixModifiers.applyIncludeThreshold(matrix, miner.getIncludeThreshold());
		}
		return matrix;
	}

	private CausalActivityMatrix runConnections(PluginContext context, CausalActivityMatrix matrix,
			ModifyCausalActivityMatrixParameters parameters) {
		if (parameters.isTryConnections()) {
			Collection<ModifyCausalActivityMatrixConnection> connections;
			try {
				connections = context.getConnectionManager().getConnections(ModifyCausalActivityMatrixConnection.class,
						context, matrix);
				for (ModifyCausalActivityMatrixConnection connection : connections) {
					if (connection.getObjectWithRole(ModifyCausalActivityMatrixConnection.MATRIX).equals(matrix)
							&& connection.getParameters().equals(parameters)) {
						return connection.getObjectWithRole(ModifyCausalActivityMatrixConnection.MODIFIEDMATRIX);
					}
				}
			} catch (ConnectionCannotBeObtained e) {
			}
		}
		CausalActivityMatrix modifiedMatrix = apply1(context, matrix, parameters);
		if (parameters.isTryConnections()) {
			context.getConnectionManager()
					.addConnection(new ModifyCausalActivityMatrixConnection(matrix, modifiedMatrix, parameters));
		}
		return modifiedMatrix;
	}

	private CausalActivityMatrix apply1(PluginContext context, CausalActivityMatrix matrix,
			ModifyCausalActivityMatrixParameters parameters) {
		CausalActivityMatrixModifier modifier = new CausalActivityMatrixStackedModifier(
				new CausalActivityMatrixZeroValueModifier(parameters.getZeroValue()),
				new CausalActivityMatrixConcurrencyRatioModifier(parameters.getConcurrencyRatio()),
				new CausalActivityMatrixIncludeThresholdModifier(parameters.getIncludeThreshold()));
		return modifier.apply(matrix);
	}

}
