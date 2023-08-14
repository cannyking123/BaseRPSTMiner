package cn.sk.untils;

import java.io.Serializable;
import java.util.Iterator;

import org.deckfour.xes.model.XLog;
import org.jbpt.algo.tree.rpst.RPSTNode;
import org.jbpt.graph.DirectedEdge;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.log.LogEvent;
import org.processmining.framework.log.LogEvents;
import org.processmining.framework.log.LogReader;
import org.processmining.framework.log.rfb.LogData;
import org.processmining.framework.models.petrinet.PetriNet;
/**
 * 
 * @author cannyking
 *
 */

public class IntermediateData implements Serializable{
	private LogEvents eventSet;//���
	private XLog xlog;//����־
	private LogData subLogData;//����־����
	private LogReader subLogReader;//����־����
	//private Petrinet inductiveSubModel;//����ʽ�ھ����ģ��
	private PetriNet  subModel;//��ģ��
	private AcceptingPetriNet  inductiveSubModel;//�����ھ����ģ��
	private Boolean trivialDealMark;//trivial����Ƿ��Ѵ���ı��
	private Boolean polygonDealMark;//polygon����Ƿ��Ѵ���ı��
	private Boolean hasCycle;//SESE����Ƿ���ѭ��
	private Boolean hasModel;
	 


	public IntermediateData(RPSTNode rpstNode) {
		this.eventSet = getEventsInNode(rpstNode);
		this.xlog =null;
		this.subLogData = null;
		this.subLogReader = null;
		this.subModel = null;
		this.inductiveSubModel=null;
		this.trivialDealMark = false;
		this.polygonDealMark = false;
		this.hasCycle = false;
		this.hasModel = false;
	}
	
	/**
	 * ��ȡRPST�����������������event
	 * @param node
	 * @return
	 */
	public LogEvents getEventsInNode(RPSTNode node){
		LogEvents eventSet = new LogEvents();
		Iterator iterator = node.getFragment().iterator();
		while (iterator.hasNext()){
			DirectedEdge directedEdge = (DirectedEdge) iterator.next();
			if (directedEdge.getSource() instanceof EventVertex&&isHas(((EventVertex) directedEdge.getSource()).getLogEvent(),eventSet)==false) {
				eventSet.add(((EventVertex) directedEdge.getSource()).getLogEvent());
			}
			if (directedEdge.getTarget() instanceof EventVertex&&isHas(((EventVertex) directedEdge.getTarget()).getLogEvent(),eventSet)==false) {
				
				eventSet.add(((EventVertex) directedEdge.getTarget()).getLogEvent());
			}
		}
		return eventSet;
	}
	private boolean isHas(LogEvent logEvent, LogEvents eventSet2) {
		
		for(LogEvent e :eventSet2) {
			if(e==logEvent) {
				return true;
			}
		}
		return false;
	}

	public Boolean getHasModel() {
		return hasModel;
	}

	public void setHasModel(Boolean hasModel) {
		this.hasModel = hasModel;
	}
	
	public LogData getSubLogData() {
		return subLogData;
	}

	public synchronized void setSubLogData(LogData subLogData) {
		this.subLogData = subLogData;
	}

	public LogReader getSubLogReader() {
		return subLogReader;
	}

	public synchronized void setSubLogReader(LogReader subLogReader) {
		this.subLogReader = subLogReader;
	}

	public PetriNet getSubModel() {
		return subModel;
	}
	
	public synchronized void setSubModel(PetriNet subModel) {
		this.subModel = subModel;
	}
	
	public AcceptingPetriNet getSubModelIM() {
		return inductiveSubModel;
	}
	
	public synchronized void setSubModelIM(AcceptingPetriNet reducedNet) {
		this.inductiveSubModel = reducedNet;
	}

	public  XLog getXlog() {
		return xlog;
	}

	public  void setXlog(XLog xlog) {
		this.xlog = xlog;
	}
	
	

	public LogEvents getEventSet() {
		return eventSet;
	}

	public Boolean getTrivialDealMark() {
		return trivialDealMark;
	}

	public synchronized void setTrivialDealMark(Boolean trivialDealMark) {
		this.trivialDealMark = trivialDealMark;
	}

	public Boolean getPolygonDealMark() {
		return polygonDealMark;
	}

	public synchronized void setPolygonDealMark(Boolean polygonDealMark) {
		this.polygonDealMark = polygonDealMark;
	}
	
	public Boolean gethasCycle() {
		return hasCycle;
	}

	public synchronized void sethasCycle(Boolean hasCycle) {
		this.hasCycle = hasCycle;
	}
}