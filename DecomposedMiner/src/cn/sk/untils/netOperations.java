package cn.sk.untils;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;

public class netOperations {

	int inDegree;
	int outDegree;
	Petrinet net;

	netOperations(Petrinet net) {

		this.net = net;
		

	}

	public int getInDegree(Place place) {
		
		return 0;

	}

	public int getOutDegree() {
		return 0;

	}

}
