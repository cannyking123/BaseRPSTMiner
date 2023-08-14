package cn.sk.RPSTMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.rpst.RPSTNode;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.graph.DirectedGraph;
import org.processmining.causalactivitymatrix.models.CausalActivityMatrix;
import org.processmining.causalactivitymatrix.parameters.ModifyCausalActivityMatrixParameters;
import org.processmining.framework.log.LogEvents;

import cn.sk.untils.RPSTParameter;

public class getNeedRPSTNodesInfo {

	private int Grammer;
	private CausalActivityMatrix matrix;
	public ArrayList<RPSTNode> neededNode;
	public LogEvents logEvents;
	public RPST rpst;
	public ModifyCausalActivityMatrixParameters parameters2;

	public getNeedRPSTNodesInfo(ModifyCausalActivityMatrixParameters parameters2, CausalActivityMatrix matrix) {

		this.parameters2 = parameters2;
		this.matrix = matrix;
		getNeedNode();

	}

	public void getNeedNode() {

		//create RPST form
		causalGraph2DirectedGraph convert = new causalGraph2DirectedGraph();
		DirectedGraph dg = convert.getCausalActivityGraph(matrix);
		//get RPST tree
		myRPSTEngine en = new myRPSTEngine();
		rpst = en.processRPST(dg);
		
		logEvents = convert.getLogEvents();
		System.out.println("events" + logEvents);

		//
		
		

		ArrayList<Integer> BOND = new ArrayList<Integer>();
		ArrayList<Integer> POLYGON = new ArrayList<Integer>();
		ArrayList<Integer> RIGID = new ArrayList<Integer>();
		Set b = rpst.getRPSTNodes(TCType.BOND);
		Set p = rpst.getRPSTNodes(TCType.POLYGON);
		Set r = rpst.getRPSTNodes(TCType.RIGID);

		for (Object node : b) {
			int size = ((RPSTNode) node).getFragment().size();
			if (BOND.contains(size) == false) {
				BOND.add(((RPSTNode) node).getFragment().size());
			}

		}
		for (Object node1 : p) {
			int size = ((RPSTNode) node1).getFragment().size();
			if (POLYGON.contains(size) == false) {
				POLYGON.add(((RPSTNode) node1).getFragment().size());
			}

		}
		for (Object node2 : r) {
			int size = ((RPSTNode) node2).getFragment().size();
			if (RIGID.contains(size) == false) {
				RIGID.add(((RPSTNode) node2).getFragment().size());
			}

		}
		Collections.sort(BOND);
		Collections.sort(POLYGON);
		Collections.sort(RIGID);

		System.out.println("划分推荐");
		System.out.println("BOND" + BOND.toString());
		System.out.println("POLYGON" + POLYGON.toString());
		System.out.println("RIGID" + RIGID.toString());
		System.out.println("划分组合BOND+POLYGON");

		
		
		//get RPST tree
		
		
		
		
		

		for (int i = 0; i < BOND.size(); i++) {
			int bond = BOND.get(i);

			for (int j = 0; j < POLYGON.size(); j++) {
				int polygon = POLYGON.get(j);
				
				RPSTParameter rpstParameter = new RPSTParameter(2, bond+1,polygon+1, 10000);
				getActivitiesGroup getActGroup = new getActivitiesGroup(rpst, (RPSTNode) rpst.getRoot(), rpstParameter,
						logEvents);
				
				ArrayList<RPSTNode> neededNode1 = getActGroup.getNeedNodeList();
				
				
				String bp = (bond+1) + " " + (polygon+1)+ " ";
				
				System.out.println(bp);
				System.out.println(neededNode1.size());

			}
		}

	
//		System.out.println("满足要求的节点数量" + neededNode.size());
//		
//		
//		System.out.println("RPST" + rpst.getRPSTNodes());
//
//		HashSet<RPSTNode> nodes =  (HashSet<RPSTNode>) rpst.getRPSTNodes();
//		ArrayList  nodes1 = new ArrayList();
//		nodes1.addAll(nodes);
//		for(RPSTNode n :nodes) {
//			System.out.println(n.toString());
//		}
		
	
		

	}



	



}
