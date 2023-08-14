package cn.sk.RPSTMethod;

import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.rpst.RPSTNode;
import org.jbpt.graph.DirectedGraph;

public class myRPSTEngine {
	public RPST processRPST(DirectedGraph directedgraph) {
		
		RPST rpst = new RPST(directedgraph);
		RPSTNode root = (RPSTNode) rpst.getRoot();
		return rpst;
		
	}

}
