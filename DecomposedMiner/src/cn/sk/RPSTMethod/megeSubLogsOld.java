package cn.sk.RPSTMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.rpst.RPSTNode;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.hypergraph.abs.Vertex;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.framework.log.LogEvents;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;

import cn.sk.untils.IntermediateData;
import cn.sk.untils.RPSTParameter;

public class megeSubLogsOld {

	public static ConcurrentHashMap<RPSTNode, IntermediateData> internalData;
	public static RPST rpst;
	public static LogEvents logEvents;
	public static Set<String> allcomTranNames = new HashSet<String>();

	megeSubLogsOld(ConcurrentHashMap<RPSTNode, IntermediateData> internalData, RPST rpst, LogEvents logEvents) {
		this.internalData = internalData;
		this.rpst = rpst;
		this.logEvents = logEvents;
	}

	public AcceptingPetriNet mergeSubModelsByDFS(RPSTNode node, RPSTParameter rpstParameter,
			AndOrAnalysis andOrAnalysis) {

		if (node == null) {
			return null;
		} else if (node.getFragment().size() == 0) {
			return null;
		} else if (/* rpst.getChildren(node).size() == 0 */
		node.getFragment().size() < rpstParameter.getThreshold(node.getType()) && node.getEntry() != null
				&& node.getExit() != null && !hasCircle(node)) {

			return internalData.get(node).getSubModelIM();
		} else {

			List<AcceptingPetriNet> minedModels = new ArrayList<AcceptingPetriNet>();

			List<RPSTNode> children = rpst.getChildren1(node, logEvents); //原来版本
			//Set<RPSTNode> children = mege.sort(rpst.getChildren(node), logEvents,internalData);
			//			System.out.println("-排序后----");
			//			for(RPSTNode n :children) {
			//				System.out.println(n);		
			//			}
			Iterator childIterator = children.iterator();
			while (childIterator.hasNext()) {
				RPSTNode child = (RPSTNode) childIterator.next();
				AcceptingPetriNet childModel = mergeSubModelsByDFS(child, rpstParameter, andOrAnalysis);
				if (childModel != null) {
					minedModels.add(preProcessOfPetriNet(childModel));
				}
			}
			return minedModels.isEmpty() ? null : mergePetriNets(minedModels, andOrAnalysis);
		}
	}

	private AcceptingPetriNet preProcessOfPetriNet(AcceptingPetriNet childModel) {

		List<Place> delSourcePlace = new ArrayList<Place>();

		Petrinet net = childModel.getNet();

		for (Place place : net.getPlaces()) {
			if (net.getInEdges(place).isEmpty() == true || net.getOutEdges(place).isEmpty() == true) {
				delSourcePlace.add(place);
			}
		}

		for (int i = 0; i < delSourcePlace.size(); i++) {
			net.removePlace(delSourcePlace.get(i));
		}

		return childModel;
	}

	private AcceptingPetriNet mergePetriNets(List<AcceptingPetriNet> minedModels, AndOrAnalysis andOrAnalysis) {

		AcceptingPetriNet mergedNet = AcceptingPetriNetFactory.createAcceptingPetriNet();
		mergedNet.init(PetrinetFactory
				.newPetrinet(minedModels.size() > 0 ? minedModels.get(0).getNet().getLabel() : "Empty net"));
		//Marking initialMarking = mergedNet.getInitialMarking();
		mergedNet.getFinalMarkings().add(new Marking());
		Map<Place, Place> placeMap = new HashMap<Place, Place>();
		Map<Transition, Transition> transitionMap = new HashMap<Transition, Transition>();
		Map<String, Transition> transitionLabelMap = new HashMap<String, Transition>();
		for (int index = 0; index < minedModels.size(); index++) {
			Petrinet net = minedModels.get(index).getNet();
			List<String> comTranNames = new ArrayList<String>();
			for (Transition transition : net.getTransitions()) {
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
			for (Place place : net.getPlaces()) {

				Place mergedPlace = mergedNet.getNet().addPlace(place.getLabel());
				placeMap.put(place, mergedPlace);
				if (minedModels.get(index).getInitialMarking().contains(place)) {
					//initialMarking.add(mergedPlace, minedModels.get(index).getInitialMarking().occurrences(place));
				}
			}

			for (PetrinetEdge<?, ?> edge : net.getEdges()) {
				if (edge instanceof Arc) {
					Arc arc = (Arc) edge;
					if (arc.getSource() instanceof Place) {
						mergedNet.getNet().addArc(placeMap.get(arc.getSource()), transitionMap.get(arc.getTarget()));
					} else {
						mergedNet.getNet().addArc(transitionMap.get(arc.getSource()), placeMap.get(arc.getTarget()));
					}
				}
			}
			// 处理共用变迁的后继库所（分支的AND/OR关系挖掘）
			for (String commonTranName : comTranNames) {
				//Transition comTransition = transitionLabelMap.get(commonTranName);
				Transition comTransition = getTransition(mergedNet.getNet(), commonTranName);
				//System.out.println("mergedNet公共变迁" + comTransition);
				if (mergedNet.getNet().getOutEdges(comTransition).size() < 2) {
					continue;
				} else {
					Transition comTranInNet = getTransition(net, commonTranName);
					//System.out.println("Net公共变迁" + comTranInNet);
					if (net.getOutEdges(comTranInNet).size() == 0 || mergedNet.getNet().getOutEdges(comTransition)
							.size() == net.getOutEdges(comTranInNet).size()) {
						continue;
					} else {
						List<Place> sucPlacesOfNet2 = new ArrayList<Place>();
						//公共变迁将要合并过来网的后继库所
						Iterator iterator2 = getSuccessorsP(net, comTranInNet).iterator();
						while (iterator2.hasNext()) {
							//System.out.println((Place) iterator2.next());
							sucPlacesOfNet2.add((Place) iterator2.next());
						}

						Collections.sort(sucPlacesOfNet2);
						List<Place> sucPlacesOfNet1 = new ArrayList<Place>();
						Iterator iterator1 = getSuccessorsP(mergedNet.getNet(), comTransition).iterator();
						while (iterator1.hasNext()) {
							sucPlacesOfNet1.add((Place) iterator1.next());
						}
//						System.out.println("Net公共变迁后继库所" + sucPlacesOfNet2);
//						System.out.println("megedNet公共变迁后继库所" + sucPlacesOfNet1);
						sucPlacesOfNet1 = del(sucPlacesOfNet1, sucPlacesOfNet2);//获得了已经合并了的网中的公共变迁的后继库所
//						System.out.println("megedNet公共变迁后继库所" + sucPlacesOfNet1);
						Collections.sort(sucPlacesOfNet1);

						Map<Place, List<Transition>> sucPlaceMapOfNet1 = new HashMap<Place, List<Transition>>();
						Map<Place, List<Transition>> sucPlaceMapOfNet2 = new HashMap<Place, List<Transition>>();

						for (Place place : sucPlacesOfNet1) {
							List<Transition> tranSet = new ArrayList<Transition>();
							Iterator iterator = getSuccessorsT(mergedNet.getNet(), place).iterator();
							while (iterator.hasNext()) {
								tranSet.add((Transition) iterator.next());
							}
							Collections.sort(tranSet);
							sucPlaceMapOfNet1.put(place, tranSet);
						}
//						System.out.println("megedNet公共变迁后继库所后变迁" + sucPlaceMapOfNet1);

						for (Place place : sucPlacesOfNet2) {

							List<Transition> tranSet = new ArrayList<Transition>();
							Iterator iterator = getSuccessorsT(net, place).iterator();
							while (iterator.hasNext()) {
								tranSet.add((Transition) iterator.next());
							}
							Collections.sort(tranSet);
							sucPlaceMapOfNet2.put(place, tranSet);

						}
						//System.out.println("Net公共变迁后继库所后变迁" + sucPlaceMapOfNet2);
						for (Place place2 : sucPlaceMapOfNet2.keySet()) {
							for (Transition newTran : sucPlaceMapOfNet2.get(place2)) {
								for (Place place1 : sucPlaceMapOfNet1.keySet()) {

									if (andOrAnalysis.allIsOrAnalysis_Out(comTransition, sucPlaceMapOfNet1.get(place1),
											newTran)) {

										Place placeDel = getPlace(mergedNet.getNet(), place2);

										//if (net.getOutEdges(place2).size() <= 1 && net.getInEdges(place2).size() <= 1) {

											mergedNet.getNet().removePlace(placeDel);

										//} else {
											mergedNet.getNet().removeArc(placeDel, getTransition(mergedNet.getNet(),newTran.getLabel()));
										//}
										mergedNet.getNet().addArc(place1, getTransition(mergedNet.getNet(),newTran.getLabel()));
									} else {
										for (Transition oldTran : sucPlaceMapOfNet1.get(place1)) {
											if (andOrAnalysis.isOrAnalysis_Out(comTransition, oldTran, newTran)) {
												if(getPlace(mergedNet.getNet(), place2)!=null) {
												mergedNet.getNet().addArc(getPlace(mergedNet.getNet(), place2), getTransition(mergedNet.getNet(),oldTran.getLabel()));}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			// 处理共用变迁的前驱库所（合并的AND/OR关系挖掘）
			for (String commonTranName : comTranNames) {

				//Transition comTransition = transitionLabelMap.get(commonTranName);
				Transition comTransition = getTransition(mergedNet.getNet(), commonTranName);
				//System.out.println("mergedNet公共变迁" + comTransition);
				if (mergedNet.getNet().getInEdges(comTransition).size() < 2) {
					continue;
				} else {
					Transition comTranInNet = getTransition(net, commonTranName);
					//System.out.println("Net公共变迁" + comTranInNet);
					if (net.getInEdges(comTranInNet).size() == 0 || mergedNet.getNet().getInEdges(comTransition)
							.size() == net.getInEdges(comTranInNet).size()) {
						continue;
					} else {
						List<Place> prePlacesOfNet2 = new ArrayList<Place>();
						//公共变迁将要合并过来网的后继库所
						Iterator iterator2 = getPredecessorsP(net, comTranInNet).iterator();
						while (iterator2.hasNext()) {
							//System.out.println((Place) iterator2.next());
							prePlacesOfNet2.add((Place) iterator2.next());
						}

						Collections.sort(prePlacesOfNet2);
						List<Place> prePlacesOfNet1 = new ArrayList<Place>();
						Iterator iterator1 = getPredecessorsP(mergedNet.getNet(), comTransition).iterator();
						while (iterator1.hasNext()) {
							prePlacesOfNet1.add((Place) iterator1.next());
						}
						//System.out.println(commonTranName+"Net公共变迁后继库所" + prePlacesOfNet2);
						//System.out.println(commonTranName+"megedNet公共变迁后继库所" + prePlacesOfNet1);
						prePlacesOfNet1 = del(prePlacesOfNet1, prePlacesOfNet2);//获得了已经合并了的网中的公共变迁的后继库所
						//System.out.println(commonTranName+"megedNet公共变迁后继库所" + prePlacesOfNet1);
						Collections.sort(prePlacesOfNet1);

						Map<Place, List<Transition>> prePlaceMapOfNet1 = new HashMap<Place, List<Transition>>();
						Map<Place, List<Transition>> prePlaceMapOfNet2 = new HashMap<Place, List<Transition>>();

						for (Place place : prePlacesOfNet1) {
							List<Transition> tranSet = new ArrayList<Transition>();
							Iterator iterator = getPredecessorsT(mergedNet.getNet(), place).iterator();
							while (iterator.hasNext()) {
								tranSet.add((Transition) iterator.next());
							}
							Collections.sort(tranSet);
							prePlaceMapOfNet1.put(place, tranSet);
						}
//						System.out.println("megedNet公共变迁后继库所后变迁" + sucPlaceMapOfNet1);

						for (Place place : prePlacesOfNet2) {

							List<Transition> tranSet = new ArrayList<Transition>();
							Iterator iterator = getPredecessorsT(net, place).iterator();
							while (iterator.hasNext()) {
								tranSet.add((Transition) iterator.next());
							}
							Collections.sort(tranSet);
							prePlaceMapOfNet2.put(place, tranSet);

						}
//						System.out.println("Net公共变迁后继库所后变迁" + prePlaceMapOfNet2);
						for (Place place2 : prePlaceMapOfNet2.keySet()) {
							for (Transition newTran : prePlaceMapOfNet2.get(place2)) {
								for (Place place1 : prePlaceMapOfNet1.keySet()) {

									if (andOrAnalysis.allIsOrAnalysis_Out(comTransition, prePlaceMapOfNet1.get(place1),
											newTran)) {

										Place placeDel = getPlace(mergedNet.getNet(), place2);

										//if (net.getOutEdges(place2).size() <= 1 && net.getInEdges(place2).size() <= 1) {

											mergedNet.getNet().removePlace(placeDel);

										//} else {
											//mergedNet.getNet().removeArc(getTransition(mergedNet.getNet(),newTran.getLabel()),placeDel);
										//}
										mergedNet.getNet().addArc(getTransition(mergedNet.getNet(),newTran.getLabel()),place1);
									} else {
										for (Transition oldTran : prePlaceMapOfNet1.get(place1)) {
											if (andOrAnalysis.isOrAnalysis_Out(comTransition, oldTran, newTran)) {
												if(getPlace(mergedNet.getNet(), place2)!=null) {
													mergedNet.getNet().addArc(getTransition(mergedNet.getNet(),oldTran.getLabel()),getPlace(mergedNet.getNet(), place2));
												}
												
												
											}
										}
									}
								}
							}
						}
					}
				}		
			}
		}

		return mergedNet;

	}

	private HashSet getPredecessorsT(Petrinet net, Place place) {
		// TODO Auto-generated method stub
		HashSet<Transition> transitionSet = new HashSet<Transition>();
		for(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : net.getInEdges(place)) {
			transitionSet.add((Transition)edge.getSource());
		}
		
		
		return transitionSet;
	}

	private HashSet getPredecessorsP(Petrinet net, Transition comTranInNet) {
		// TODO Auto-generated method stub
		HashSet<Place> placeSet = new HashSet<Place>();
		for(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : net.getInEdges(comTranInNet)) {
			placeSet.add((Place)edge.getSource());
		}
		
		return placeSet;
	}

	private Place getPlace(Petrinet net, Place place2) {
		// TODO Auto-generated method stub

		for (Place place : net.getPlaces()) {
			if (place.getLabel().equals(place2.getLabel())) {
				return place;
			}
		}
		return null;
	}

	private List<Place> del(List<Place> sucPlacesOfNet1, List<Place> sucPlacesOfNet2) {
		List<Place> sucPlacesOfNet3 = new ArrayList<Place>();
		// TODO Auto-generated method stub
		for (Place p1 : sucPlacesOfNet1) {
			for (Place p2 : sucPlacesOfNet2) {
				if (p1.getLabel().equals(p2.getLabel())) {
					sucPlacesOfNet3.add(p1);
				}
			}
		}

		boolean x = sucPlacesOfNet1.removeAll(sucPlacesOfNet3);

		return sucPlacesOfNet1;
	}

	private HashSet getSuccessorsT(Petrinet net, Place place) {
		HashSet<Transition> transitions = new HashSet<Transition>();
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : net.getOutEdges(place)) {
			transitions.add((Transition) edge.getTarget());
		}

		// TODO Auto-generated method stub
		return transitions;
	}

	private HashSet getSuccessorsP(Petrinet net, Transition comTranInNet) {
		HashSet<Place> places = new HashSet<Place>();
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : net.getOutEdges(comTranInNet)) {
			places.add((Place) edge.getTarget());
		}

		// TODO Auto-generated method stub
		return places;
	}

	private Transition getTransition(Petrinet net, String commonTranName) {

		for (Transition transition : net.getTransitions()) {
			if (transition.getLabel().equals(commonTranName)) {
				return transition;
			}
		}
		return null;
	}

	private static boolean hasCircle(RPSTNode node) {

		if (node.getType().equals(TCType.TRIVIAL) || node.getFragment().size() < 2) {
			return false;
		}

		if (node.getEntry() == node.getExit()) {
			return true;
		}

		Iterator iterator = node.getFragment().iterator();
		while (iterator.hasNext()) {
			DirectedEdge directedEdge = (DirectedEdge) iterator.next();
			Vertex src = directedEdge.getSource();
			Vertex tgt = directedEdge.getTarget();
			if (src.equals(node.getExit()) || tgt.equals(node.getEntry())) {
				return true;
			}
		}

		return false;
	}



}
