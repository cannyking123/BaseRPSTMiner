package cn.sk.RPSTMethod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.rpst.RPSTNode;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.hypergraph.abs.Vertex;
import org.processmining.framework.log.LogEvents;

import cn.sk.untils.RPSTParameter;



public class getActivitiesGroup {

	ArrayList<RPSTNode> neededNode = new ArrayList<RPSTNode>();
	
	public getActivitiesGroup(RPST rpst, RPSTNode node, RPSTParameter rpstParameter, LogEvents events) {
		getNeedNode(rpst,node,rpstParameter,events);
	}

	public void getNeedNode(RPST rpst, RPSTNode node, RPSTParameter rpstParameter, LogEvents events) {

		if (node == null) {
			return;
		} else if (node.getFragment().size() == 0) {
			return;
		} else if (/* rpst.getChildren(node).size() == 0 */
		node.getFragment().size() < rpstParameter.getThreshold(node.getType()) && node.getEntry() != null
				&& node.getExit() != null && !hasCircle(node)) {
			neededNode.add(node);
		} else {
			List<RPSTNode> children = rpst.getChildren1(node, events);
			//
			//			for(RPSTNode n :children) {
			//				System.out.println(n);
			//				
			//				
			//			}
			//			System.out.println("------");
			//
			Iterator childIterator = children.iterator();
			while (childIterator.hasNext()) {
				RPSTNode child = (RPSTNode) childIterator.next();
				getNeedNode(rpst, child, rpstParameter, events);
			}
		}

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

	public ArrayList<RPSTNode> getNeedNodeList() {
		return neededNode;

	}

}
