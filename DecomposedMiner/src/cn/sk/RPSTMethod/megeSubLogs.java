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
import org.processmining.acceptingpetrinet.models.AcceptingPetriNetArray;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetArrayFactory;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.framework.log.LogEvents;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;

import cn.sk.untils.IntermediateData;
import cn.sk.untils.RPSTParameter;

public class megeSubLogs {

	public static ConcurrentHashMap<RPSTNode, IntermediateData> internalData;
	public static RPST rpst;
	public static LogEvents logEvents;
	public static Set<String> allcomTranNames = new HashSet<String>();
	int i = 0;
	int placeId = 0;
	HashMap<String, ArrayList<Transition>> tau2t = new HashMap<String, ArrayList<Transition>>();//0:pre;1:suc
	HashMap<String, ArrayList<ArrayList<Transition>>> tau22t = new HashMap<String, ArrayList<ArrayList<Transition>>>();
	AcceptingPetriNetArray NetArray = AcceptingPetriNetArrayFactory.createAcceptingPetriNetArray();

	public AcceptingPetriNetArray nets = AcceptingPetriNetArrayFactory.createAcceptingPetriNetArray();

	public AcceptingPetriNetArray getNets() {
		return NetArray;
	}

	megeSubLogs(ConcurrentHashMap<RPSTNode, IntermediateData> internalData, RPST rpst, LogEvents logEvents) {
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

			List<RPSTNode> children = rpst.getChildren1(node, logEvents); //ԭ���汾
			//Set<RPSTNode> children = mege.sort(rpst.getChildren(node), logEvents,internalData);
			//			System.out.println("-�����----");
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

	public AcceptingPetriNet mergeSubModelsByBFS(RPSTNode node, RPSTParameter rpstParameter,
			AndOrAnalysis andOrAnalysis) {

		List<RPSTNode> queue = new ArrayList<RPSTNode>();
		queue.add(node);

		while (queue.size() > 0) {

			List<RPSTNode> children = rpst.getChildren1(queue.get(queue.size() - 1), logEvents);
			if (allHasModel(children) == true) {
				int queueSize = queue.size() - 1;
				List<AcceptingPetriNet> minedModels = new ArrayList<AcceptingPetriNet>();
				for (int i = 0; i < children.size(); i++) {
					minedModels.add(preProcessOfPetriNet(internalData.get(children.get(i)).getSubModelIM()));
					nets.addNet(internalData.get(children.get(i)).getSubModelIM());
					NetArray.addNet(internalData.get(children.get(i)).getSubModelIM());
				}
				AcceptingPetriNet childModel = mergePetriNets(minedModels, andOrAnalysis);
				internalData.put(queue.get(queueSize), new IntermediateData(queue.get(queueSize)));
				internalData.get(queue.get(queueSize)).setSubModelIM(childModel);
				internalData.get(queue.get(queueSize)).setHasModel(true);
				queue.remove(queue.size() - 1);
			} else {

				for (int i = 0; i < children.size(); i++) {
					RPSTNode nowNode = children.get(i);
					if (internalData.get(nowNode) == null) {
						queue.add((nowNode));

					}
				}
			}

		}

		return internalData.get(node).getSubModelIM();

	}

	private boolean allHasModel(List<RPSTNode> children) {
		Iterator childIterator = children.iterator();
		while (childIterator.hasNext()) {
			RPSTNode child = (RPSTNode) childIterator.next();
			if (internalData.get((RPSTNode) child) == null) {
				//System.out.println("allHasModel����false");
				return false;
			}

		}
		return true;
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
		//mergedNet.getFinalMarkings().add(new Marking());
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
						comTranNames.add(label);//������Ǩ
						//System.out.println("������Ǩ��" + label);
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
			// �����ñ�Ǩ�ĺ�̿�������֧��AND/OR��ϵ�ھ�
			for (String commonTranName : comTranNames) {
				ArrayList<String> workedT = new ArrayList<String>();
				boolean addTau = true;
				//Transition comTransition = transitionLabelMap.get(commonTranName);
				Transition comTransition = getTransition(mergedNet.getNet(), commonTranName);
				//System.out.println("mergedNet������Ǩ" + comTransition);
				if (mergedNet.getNet().getOutEdges(comTransition).size() < 2) {
					continue;
				} else {
					Transition comTranInNet = getTransition(net, commonTranName);
					//System.out.println("Net������Ǩ" + comTranInNet);
					if (net.getOutEdges(comTranInNet).size() == 0 || mergedNet.getNet().getOutEdges(comTransition)
							.size() == net.getOutEdges(comTranInNet).size()) {
						continue;
					} else {
						List<Place> sucPlacesOfNet2 = new ArrayList<Place>();
						//������Ǩ��Ҫ�ϲ��������ĺ�̿���
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
						//						System.out.println("Net������Ǩ��̿���" + sucPlacesOfNet2);
						//						System.out.println("megedNet������Ǩ��̿���" + sucPlacesOfNet1);
						sucPlacesOfNet1 = del(sucPlacesOfNet1, sucPlacesOfNet2);//������Ѿ��ϲ��˵����еĹ�����Ǩ�ĺ�̿���
						//						System.out.println("megedNet������Ǩ��̿���" + sucPlacesOfNet1);
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
						//						System.out.println("megedNet������Ǩ��̿������Ǩ" + sucPlaceMapOfNet1);

						for (Place place : sucPlacesOfNet2) {

							List<Transition> tranSet = new ArrayList<Transition>();
							Iterator iterator = getSuccessorsT(net, place).iterator();
							while (iterator.hasNext()) {
								tranSet.add((Transition) iterator.next());
							}
							Collections.sort(tranSet);
							sucPlaceMapOfNet2.put(place, tranSet);

						}
						//System.out.println("Net������Ǩ��̿������Ǩ" + sucPlaceMapOfNet2);
						for (Place place2 : sucPlaceMapOfNet2.keySet()) {
							ArrayList<Transition> addTransition = new ArrayList<Transition>();
							for (Transition newTran : sucPlaceMapOfNet2.get(place2)) {
								for (Place place1 : sucPlaceMapOfNet1.keySet()) {

									

									//all and,megeing xor
									boolean allAnd = isAllAnd(sucPlaceMapOfNet1.get(place1), comTransition,
											andOrAnalysis);
									if (sucPlaceMapOfNet1.get(place1).isEmpty() == false) {
										//System.out.println("������ѡ��");
										//										if (allAnd && andOrAnalysis.isOrAnalysis_Out(comTransition,
										//												sucPlaceMapOfNet1.get(place1).get(0), newTran)) {
										//
										//											mergedNet.getNet().addPlace("newPlace" + i);
										//											mergedNet.getNet().addTransition("newTau" + i);
										//											mergedNet.getNet().addArc(getT(mergedNet.getNet(), "newTau" + i),
										//													getP(mergedNet.getNet(), "newPlace" + i));
										//											List<Place> tions = getPredecessorsPP(mergedNet.getNet(), comTransition);
										//											mergedNet.getNet().addArc(getP(mergedNet.getNet(), "newPlace" + i),
										//													getT(mergedNet.getNet(), comTransition.getLabel()));
										//											addTransition.add(getT(mergedNet.getNet(), "newTau" + i));
										//
										//											for (int j = 0; j < tions.size(); j++) {
										//												mergedNet.getNet().addArc(
										//														getP(mergedNet.getNet(), tions.get(j).getLabel()),
										//														getT(mergedNet.getNet(), "newTau" + i));
										//												mergedNet.getNet().removeArc(
										//														getP(mergedNet.getNet(), tions.get(j).getLabel()),
										//														getT(mergedNet.getNet(), comTransition.getLabel()));
										//											}
										//
										//											Place placeDel = getPlace(mergedNet.getNet(), place2);
										//											mergedNet.getNet().removePlace(placeDel);
										//											mergedNet.getNet().addArc(getT(mergedNet.getNet(), newTran.getLabel()),
										//													getP(mergedNet.getNet(), "newPlace" + i));
										//											i++;
										//										}
									}
									//meged commonT outdegree not equal zero
									//�ж�����-��̲���һ��,ǰ����ȫΪ��
									//�߽��Ǩ�����к�̿���

									int num1 = mergedNet.getNet().getInEdges(getT(mergedNet.getNet(), commonTranName))
											.size();
									int num2 = net.getInEdges(getT(net, commonTranName)).size();
									int num3 = num1 - num2;
									//update transition

									//�����������к��
									List<Place> placeAll = getSuccessorsPP(mergedNet.getNet(),
											getT(mergedNet.getNet(), commonTranName));

									//���ϲ����ĺ��
									List<Place> placesInNet = getSuccessorsPP(net, getT(net, commonTranName));
									for (Place p : placesInNet) {
										for (Object t : getPredecessorsT(net, p)) {
											workedT.add(((Transition) t).getLabel());
										}
									}
									List<Place> placesInMege = new ArrayList<Place>();

									if ((num2 != 0 || num3 != 0) && addTau
									//&&false
									) {
//										System.out.println("���������ѡ��Ͳ�����");
//										System.out.println(commonTranName);

										addTau = false;
										//���һ��������һ����Ǩ
										mergedNet.getNet().addTransition("newTau" + i);
										addTransition.add(getT(mergedNet.getNet(), "newTau" + i));
										ArrayList<Transition> tauPreT = new ArrayList<Transition>();
										ArrayList<Transition> tauSucT = new ArrayList<Transition>();
										ArrayList<ArrayList<Transition>> tauSPT = new ArrayList<ArrayList<Transition>>();

										if (num2 == 0) {
											//System.out.println("net����");
											//�����������к��

											for (Place p : placesInNet) {
												for (Place p2 : placeAll) {
													if (p2.getLabel().equals(p.getLabel()) == false) {
														placesInMege.add(getP(mergedNet.getNet(), p2.getLabel()));
													}
												}
											}
											int placesSize = placesInNet.size();
											//System.out.println("placesInMege"+placesInMege.toString());

											for (int x = 0; x < placesSize; x++) {
												//System.out.println("��ӱߵı�Ǩ"+placesInMege.get(x).getLabel());
												mergedNet.getNet().addArc(getT(mergedNet.getNet(), "newTau" + i),
														getP(mergedNet.getNet(), placesInNet.get(x).getLabel()));
												//System.out.println("��ӱ�"+getT(mergedNet.getNet(), "newTau" + i).getLabel()+"->"+placesInNet.get(x).getLabel());
												mergedNet.getNet().removeArc(getT(mergedNet.getNet(), commonTranName),
														getP(mergedNet.getNet(), placesInNet.get(x).getLabel()));
												//System.out.println("ɾ����"+commonTranName+"->"+placesInNet.get(x).getLabel());
												tauSucT.addAll(getSuccessorsT(mergedNet.getNet(), placesInNet.get(x)));
											}
											mergedNet.getNet().addArc(
													getP(mergedNet.getNet(), placesInMege.get(0).getLabel()),
													getT(mergedNet.getNet(), "newTau" + i));
											//System.out.println("��ӱ�"+placesInMege.get(0).getLabel()+"->"+getT(mergedNet.getNet(), "newTau" + i).getLabel());
											tauPreT.addAll(getPredecessorsT(mergedNet.getNet(), placesInMege.get(0)));
											tauSPT.add(tauPreT);
											tauSPT.add(tauSucT);
											tau22t.put("newTau" + i, tauSPT);
											

										} else {
											//System.out.println("net����");
											for (Place p : placesInNet) {
												for (Place p2 : placeAll) {
													if (p2.getLabel().equals(p.getLabel()) == false) {
														placesInMege.add(getP(mergedNet.getNet(), p2.getLabel()));
													}
												}
											}
											int placesSize = placesInMege.size();

											for (int x = 0; x < placesSize; x++) {
												//System.out.println("��ӱߵı�Ǩ"+placesInMege.get(x).getLabel());
												mergedNet.getNet().addArc(getT(mergedNet.getNet(), "newTau" + i),
														getP(mergedNet.getNet(), placesInMege.get(x).getLabel()));
												//System.out.println("��ӱ�"+getT(mergedNet.getNet(), "newTau" + i).getLabel()+"->"+placesInMege.get(x).getLabel());
												mergedNet.getNet().removeArc(getT(mergedNet.getNet(), commonTranName),
														getP(mergedNet.getNet(), placesInMege.get(x).getLabel()));
												//System.out.println("ɾ����"+commonTranName+"->"+placesInMege.get(x).getLabel());
												tauSucT.addAll(getSuccessorsT(mergedNet.getNet(), placesInMege.get(x)));
									}
											mergedNet.getNet().addArc(
													getP(mergedNet.getNet(), placesInNet.get(0).getLabel()),
													getT(mergedNet.getNet(), "newTau" + i));
											//System.out.println("��ӱ�"+placesInNet.get(0).getLabel()+"->"+getT(mergedNet.getNet(), "newTau" + i).getLabel());
											tauPreT.addAll(getPredecessorsT(net, placesInNet.get(0)));
											tauSPT.add(tauPreT);
											tauSPT.add(tauSucT);
											tau22t.put("newTau" + i, tauSPT);
											

										}

									}

									i++;
									//									}
									int addTransitionSize = addTransition.size();
									
									if (addTransitionSize > 0) {
										
										
										//										for (int j = 0; j < addTransitionSize; j++) {
										//											Transition tau = addTransition.get(j);
										//											if (andOrAnalysis.allIsOrAnalysis_Out(comTransition,
										//													sucPlaceMapOfNet1.get(place1), tau)) {
										//
										//												Place placeDel = getP(mergedNet.getNet(),
										//														"newplace" + String.valueOf(i - 1));
										//
										//												if (net.getOutEdges(place2).size() <= 1
										//														&& net.getInEdges(place2).size() <= 1) {
										//
										//													mergedNet.getNet().removePlace(placeDel);
										//
										//												} else {
										//													mergedNet.getNet().removeArc(placeDel,
										//															getTransition(mergedNet.getNet(), tau.getLabel()));
										//												}
										//												mergedNet.getNet().addArc(place1,
										//														getTransition(mergedNet.getNet(), tau.getLabel()));
										//											} else {
										//												for (Transition oldTran : sucPlaceMapOfNet1.get(place1)) {
										//													if (andOrAnalysis.isOrAnalysis_Out(comTransition, oldTran, tau)) {
										//														//mergedNet.getNet().addArc(place2,getTransition(mergedNet.getNet(), oldTran.getLabel()));
										//													}
										//												}
										//											}
										//
										//										}
									} else {
										System.out.println("�߽��� "+comTransition.getLabel());
										List<Transition> sucPlaceMapOfNet1P = new ArrayList<Transition>();
										Transition newTran1;
										for (Transition t : sucPlaceMapOfNet1.get(place1)) {
											if (t.getLabel().contains("newTau")) {
												sucPlaceMapOfNet1P.addAll(tau22t.get(t.getLabel()).get(1));
											}else {
												sucPlaceMapOfNet1P.add(t);
											}
										}
										if (newTran.getLabel().contains("newTau")) {
											 
											 newTran1 = tau22t.get(newTran.getLabel()).get(1).get(0);
											 //System.out.println("�²��ɼ���Ǩ���� "+newTran.getLabel()+"�滻Ϊ "+newTran1);
										}else {
											newTran1 = newTran;
										}
										
										if (andOrAnalysis.allIsOrAnalysis_Out(comTransition,
												sucPlaceMapOfNet1P, newTran1) && isWorked(newTran1, workedT)) {
											

											Place placeDel = getPlace(mergedNet.getNet(), place2);
//											System.out.println("ȫΪxorɾ��"+placeDel.getLabel());

											//											if (net.getOutEdges(place2).size() <= 1
											//													&& net.getInEdges(place2).size() <= 1) {

											mergedNet.getNet().removePlace(placeDel);

											//											} else {
											mergedNet.getNet().removeArc(placeDel,
													getTransition(mergedNet.getNet(), newTran.getLabel()));
//											System.out.println("ɾ����"+placeDel.getLabel()+"->"+getTransition(mergedNet.getNet(), newTran.getLabel()));
											//											}
											mergedNet.getNet().addArc(place1,
													getTransition(mergedNet.getNet(), newTran.getLabel()));
//											System.out.println("��ӱ�"+place1.getLabel()+"->"+getTransition(mergedNet.getNet(), newTran.getLabel()));
										} else {
											for (Transition oldTran : sucPlaceMapOfNet1P) {
												if (andOrAnalysis.isOrAnalysis_Out(comTransition, oldTran, newTran1)) {
													//mergedNet.getNet().addArc(place2,getTransition(mergedNet.getNet(), oldTran.getLabel()));
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
			// �����ñ�Ǩ��ǰ���������ϲ���AND/OR��ϵ�ھ�
			for (String commonTranName : comTranNames) {
				ArrayList<String> workedT = new ArrayList<String>();
				boolean addTau = true;

				//Transition comTransition = transitionLabelMap.get(commonTranName);
				Transition comTransition = getTransition(mergedNet.getNet(), commonTranName);
				//System.out.println("mergedNet������Ǩ" + comTransition);
				if (mergedNet.getNet().getInEdges(comTransition).size() < 2) {
					continue;
				} else {
					Transition comTranInNet = getTransition(net, commonTranName);
					//System.out.println("Net������Ǩ" + comTranInNet);
					if (net.getInEdges(comTranInNet).size() == 0 || mergedNet.getNet().getInEdges(comTransition)
							.size() == net.getInEdges(comTranInNet).size()) {
						continue;
					} else {
						List<Place> prePlacesOfNet2 = new ArrayList<Place>();
						//������Ǩ��Ҫ�ϲ��������ĺ�̿���
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
						//System.out.println(commonTranName+"Net������Ǩ��̿���" + prePlacesOfNet2);
						//System.out.println(commonTranName+"megedNet������Ǩ��̿���" + prePlacesOfNet1);
						prePlacesOfNet1 = del(prePlacesOfNet1, prePlacesOfNet2);//������Ѿ��ϲ��˵����еĹ�����Ǩ�ĺ�̿���
						//System.out.println(commonTranName+"megedNet������Ǩ��̿���" + prePlacesOfNet1);
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
						//						System.out.println("megedNet������Ǩ��̿������Ǩ" + sucPlaceMapOfNet1);

						for (Place place : prePlacesOfNet2) {

							List<Transition> tranSet = new ArrayList<Transition>();
							Iterator iterator = getPredecessorsT(net, place).iterator();
							while (iterator.hasNext()) {
								tranSet.add((Transition) iterator.next());
							}
							Collections.sort(tranSet);
							prePlaceMapOfNet2.put(place, tranSet);

						}

						//						System.out.println("Net������Ǩ��̿������Ǩ" + prePlaceMapOfNet2);
						for (Place place2 : prePlaceMapOfNet2.keySet()) {
							ArrayList<Transition> addTransition = new ArrayList<Transition>();
							for (Transition newTran : prePlaceMapOfNet2.get(place2)) {
								for (Place place1 : prePlaceMapOfNet1.keySet()) {
									
									//all and,megeing xor
									boolean allAnd = isAllAnd1(prePlaceMapOfNet1.get(place1), comTransition,
											andOrAnalysis);
									if (prePlaceMapOfNet1.get(place1).isEmpty() == false) {
										//System.out.println("������ѡ��");
										//			
										//													if (allAnd && andOrAnalysis.isOrAnalysis_In(comTransition,
										//															prePlaceMapOfNet1.get(place1).get(0), newTran)) {
										//			
										//														mergedNet.getNet().addPlace("newPlace" + i);
										//														mergedNet.getNet().addTransition("newTau" + i);
										//														mergedNet.getNet().addArc(getP(mergedNet.getNet(), "newPlace" + i),
										//																getT(mergedNet.getNet(), "newTau" + i));
										//														List<Place> tions = getSuccessorsPP(mergedNet.getNet(), comTransition);
										//														mergedNet.getNet().addArc(
										//																getT(mergedNet.getNet(), comTransition.getLabel()),
										//																getP(mergedNet.getNet(), "newPlace" + i));
										//			
										//														for (int j = 0; j < tions.size(); j++) {
										//															mergedNet.getNet().addArc(getT(mergedNet.getNet(), "newTau" + i),
										//																	getP(mergedNet.getNet(), tions.get(j).getLabel()));
										//															mergedNet.getNet().removeArc(
										//																	getT(mergedNet.getNet(), comTransition.getLabel()),
										//																	getP(mergedNet.getNet(), tions.get(j).getLabel()));
										//														}
										//			
										//														Place placeDel = getPlace(mergedNet.getNet(), place2);
										//														mergedNet.getNet().removePlace(placeDel);
										//														mergedNet.getNet().addArc(getP(mergedNet.getNet(), "newPlace" + i),
										//																getT(mergedNet.getNet(), newTran.getLabel()));
										//			
										//														i++;
										//													}
									}

									//meged commonT outdegree not equal zero
									int num1 = mergedNet.getNet().getOutEdges(getT(mergedNet.getNet(), commonTranName))
											.size();
									int num2 = net.getOutEdges(getT(net, commonTranName)).size();
									int num3 = num1 - num2;
									//update transition

									//�����������к��
									List<Place> placeAll = getPredecessorsPP(mergedNet.getNet(),
											getT(mergedNet.getNet(), commonTranName));
									//System.out.println("placeAll" + placeAll);
									//���ϲ����ĺ��
									List<Place> placesInNet = getPredecessorsPP(net, getT(net, commonTranName));

									for (Place p : placesInNet) {

										for (Object t : getSuccessorsT(net, p)) {
											workedT.add(((Transition) t).getLabel());
										}
									}

									//System.out.println("placesInNet" + placesInNet);
									List<Place> placesInMege = new ArrayList<Place>();

									//System.out.println("p1=" + place1);
									if ((num2 != 0 || num3 != 0) && addTau
									//&&false
									) {
										//System.out.println("���������ѡ��Ͳ�����");
										//System.out.println(commonTranName);

										addTau = false;
										//���һ��������һ����Ǩ
										mergedNet.getNet().addTransition("newTau" + i);
										addTransition.add(getT(mergedNet.getNet(), "newTau" + i));
										ArrayList<Transition> tauPreT = new ArrayList<Transition>();
										ArrayList<Transition> tauSucT = new ArrayList<Transition>();
										ArrayList<ArrayList<Transition>> tauSPT = new ArrayList<ArrayList<Transition>>();

										if (num2 == 0) {
											System.out.println("net����");
											//�����������к��

											for (Place p : placesInNet) {
												for (Place p2 : placeAll) {
													if (p2.getLabel().equals(p.getLabel()) == false) {
														placesInMege.add(getP(mergedNet.getNet(), p2.getLabel()));
													}
												}
											}
											int placesSize = placesInNet.size();
											//System.out.println("placesInMege" + placesInMege.toString());
											for (int x = 0; x < placesSize; x++) {
												//System.out.println("��ӱߵı�Ǩ" + placesInMege.get(x).getLabel());
												mergedNet.getNet().addArc(
														getP(mergedNet.getNet(), placesInNet.get(x).getLabel()),
														getT(mergedNet.getNet(), "newTau" + i));
												mergedNet.getNet().removeArc(
														getP(mergedNet.getNet(), placesInNet.get(x).getLabel()),
														getT(mergedNet.getNet(), commonTranName));

												tauPreT.addAll(
														getPredecessorsT(net, placesInNet.get(x)));
											}
											mergedNet.getNet().addArc(getT(mergedNet.getNet(), "newTau" + i),
													getP(mergedNet.getNet(), placesInMege.get(0).getLabel()));
											tauSucT.addAll(getSuccessorsT(mergedNet.getNet(), placesInMege.get(0)));
											tauSPT.add(tauPreT);
											tauSPT.add(tauSucT);
											tau22t.put("newTau" + i, tauSPT);
											
											
										} else {
											//System.out.println("net������");
											for (Place p : placesInNet) {
												for (Place p2 : placeAll) {
													if (p2.getLabel().equals(p.getLabel()) == false) {
														placesInMege.add(getP(mergedNet.getNet(), p2.getLabel()));
													}
												}
											}
											int placesSize = placesInMege.size();

											for (int x = 0; x < placesSize; x++) {
												
												mergedNet.getNet().addArc(
														getP(mergedNet.getNet(), placesInMege.get(x).getLabel()),
														getT(mergedNet.getNet(), "newTau" + i));
												mergedNet.getNet().removeArc(
														getP(mergedNet.getNet(), placesInMege.get(x).getLabel()),
														getT(mergedNet.getNet(), commonTranName));
												tauPreT.addAll(
														getPredecessorsT(mergedNet.getNet(), placesInMege.get(x)));
											}
											mergedNet.getNet().addArc(getT(mergedNet.getNet(), "newTau" + i),
													getP(mergedNet.getNet(), placesInNet.get(0).toString()));
											tauSucT.addAll(getSuccessorsT(net, placesInNet.get(0)));
											tauSPT.add(tauPreT);
											tauSPT.add(tauSucT);
											tau22t.put("newTau" + i, tauSPT);
											
											
										}
										i++;
									}
									int addTransitionSize = addTransition.size();
									if (addTransitionSize > 0) {
										//										System.out.println("���tauǰ��");
										//										for (int j = 0; j < addTransitionSize; j++) {
										//											Transition tau = addTransition.get(j);
										//											if (andOrAnalysis.allIsOrAnalysis_Out(comTransition,
										//													prePlaceMapOfNet1.get(place1), tau)) {
										//
										//												Place placeDel = getP(mergedNet.getNet(),
										//														"newplace" + String.valueOf(i - 1));
										//
										//												if (net.getOutEdges(place2).size() <= 1
										//														&& net.getInEdges(place2).size() <= 1) {
										//
										//													mergedNet.getNet().removePlace(placeDel);
										//
										//												} else {
										//													mergedNet.getNet().removeArc(
										//															getTransition(mergedNet.getNet(), tau.getLabel()),
										//															placeDel);
										//												}
										//												mergedNet.getNet().addArc(
										//														getTransition(mergedNet.getNet(), tau.getLabel()), place1);
										//											} else {
										//												for (Transition oldTran : prePlaceMapOfNet1.get(place1)) {
										//													if (andOrAnalysis.isOrAnalysis_Out(comTransition, oldTran, tau)) {
										//														//mergedNet.getNet().addArc(place2,getTransition(mergedNet.getNet(), oldTran.getLabel()));
										//													}
										//												}
										//											}
										//
									}

									//										} 
									else {
//										//�滻���ɼ���ǨnewTau
										List<Transition> prePlaceMapOfNet1P = new ArrayList<Transition>();

										for (Transition t : prePlaceMapOfNet1.get(place1)) {
											if (t.getLabel().contains("newTau")) {
												prePlaceMapOfNet1P.addAll(tau22t.get(t.getLabel()).get(0));
											}else {
												prePlaceMapOfNet1P.add(t);
											}
										}
										Transition newTran1;
										if (newTran.getLabel().contains("newTau")) {
											newTran1 = tau22t.get(newTran.getLabel()).get(0).get(0);
										}else {
											newTran1 = newTran;
										}

										if (andOrAnalysis.allIsOrAnalysis_In(comTransition,
												prePlaceMapOfNet1P, newTran1) && isWorked(newTran1, workedT)) {

											Place placeDel = getPlace(mergedNet.getNet(), place2);

											//											if (net.getInEdges(place2).size() <= 1
											//													&& net.getOutEdges(place2).size() <= 1) {

											mergedNet.getNet().removePlace(placeDel);

											//											} 
											//											else {
											mergedNet.getNet().removeArc(
													getTransition(mergedNet.getNet(), newTran.getLabel()), placeDel);
											//											}
											mergedNet.getNet().addArc(
													getTransition(mergedNet.getNet(), newTran.getLabel()), place1);
										} else {
											for (Transition oldTran : prePlaceMapOfNet1P) {
												if (andOrAnalysis.isOrAnalysis_Out(comTransition, oldTran, newTran1)) {
													//mergedNet.getNet().addArc(getTransition(mergedNet.getNet(), oldTran.getLabel()), place2);
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

	private boolean isWorked(Transition newTran, ArrayList<String> workedT) {

		for (String s : workedT) {
			if (s.equals(newTran.getLabel())) {
				return false;
			}
		}

		return true;
	}

	private Map<Place, List<Transition>> delupdate(Map<Place, List<Transition>> sucPlaceMapOfNet2, String label) {

		ArrayList<Place> places = new ArrayList<Place>();

		for (Place place : sucPlaceMapOfNet2.keySet()) {
			if (place.getLabel().equals(label)) {
				places.add(place);
			}
		}
		int size = places.size();
		for (int i = 0; i < size; i++) {
			sucPlaceMapOfNet2.remove(places.get(i));
		}

		return sucPlaceMapOfNet2;
	}

	private boolean isAllAnd1(List<Transition> list, Transition comTransition, AndOrAnalysis andOrAnalysis) {

		for (int i = 0; i < list.size(); i++) {
			if (andOrAnalysis.allIsOrAnalysis_In(comTransition, list, list.get(i)) == false) {
				continue;
			} else {
				return false;
			}
		}

		return true;
	}

	private List<Place> getSuccessorsPP(Petrinet net, Transition comTransition) {

		List<Place> place = new ArrayList<Place>();
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : net.getOutEdges(comTransition)) {
			place.add((Place) edge.getTarget());
		}

		//System.out.println("�õ��ı߽��Ǩ�ĺ���" + place);
		return place;
	}

	private List<Place> getPredecessorsPP(Petrinet net, Transition comTransition) {

		List<Place> place = new ArrayList<Place>();
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : net.getInEdges(comTransition)) {
			place.add((Place) edge.getSource());
		}

		//System.out.println("�õ��ı߽��Ǩ��ǰ����" + place);
		return place;
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

		for (Transition t : net.getTransitions()) {
			if (t.getLabel().equals(string)) {
				return t;
			}
		}

		return null;
	}

	private boolean isAllAnd(List<Transition> list, Transition comTransition, AndOrAnalysis andOrAnalysis) {

		for (int i = 0; i < list.size(); i++) {
			if (andOrAnalysis.allIsOrAnalysis_Out(comTransition, list, list.get(i)) == false) {
				continue;
			} else {
				return false;
			}
		}

		return true;
	}

	private HashSet getPredecessorsT(Petrinet net, Place place) {

		HashSet<Transition> transitionSet = new HashSet<Transition>();
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : net.getInEdges(place)) {
			transitionSet.add((Transition) edge.getSource());
		}

		return transitionSet;
	}

	private HashSet getPredecessorsP(Petrinet net, Transition comTranInNet) {

		HashSet<Place> placeSet = new HashSet<Place>();
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : net.getInEdges(comTranInNet)) {
			placeSet.add((Place) edge.getSource());
		}

		return placeSet;
	}

	private Place getPlace(Petrinet net, Place place2) {

		for (Place place : net.getPlaces()) {
			if (place.getLabel().equals(place2.getLabel())) {
				return place;
			}
		}
		return null;
	}

	private List<Place> del(List<Place> sucPlacesOfNet1, List<Place> sucPlacesOfNet2) {
		List<Place> sucPlacesOfNet3 = new ArrayList<Place>();

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
