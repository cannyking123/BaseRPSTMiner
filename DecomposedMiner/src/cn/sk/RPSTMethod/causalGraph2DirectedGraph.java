package cn.sk.RPSTMethod;

import java.util.Collection;
import java.util.List;

import org.deckfour.xes.classification.XEventClass;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;
import org.processmining.causalactivitymatrix.models.CausalActivityMatrix;
import org.processmining.framework.log.LogEvent;
import org.processmining.framework.log.LogEvents;

import cn.sk.untils.EventVertex;

public class causalGraph2DirectedGraph {
	LogEvents logEvents = new LogEvents();
	public DirectedGraph getCausalActivityGraph(CausalActivityMatrix matrix) {
		//String str=aa.substring(0, aa.indexOf(",")); ��ȡĳ���ַ�ǰ���ַ���
		//String bb=aa.substring(str.length()+1, aa.length()); ��ȡĳ���ַ�����ַ���
		DirectedGraph dg = new DirectedGraph();//RPST������
		
		List<XEventClass> activities = matrix.getActivities();
		for(XEventClass a :activities) {
			String name = a.toString();
			//System.out.println("shuchumingzi ----"+name);
			
			LogEvent logEvent = new LogEvent(name,"");
			logEvents.add(logEvent);
			
		}
		
		//System.out.println("LogEvents��С"+logEvents.size());
		//��ӽڵ�
		for(LogEvent logEvent :logEvents) {
			dg.addVertex(new EventVertex(logEvent));
		}
		
		//�ӱ�
		int edgeNumber = 0;
		for(XEventClass a :activities) {
			for(XEventClass b :activities) {
				if(matrix.getValue(a, b)>0.0) {
					EventVertex vertexA = getEventVertexInGraph(a,logEvents,dg);
					EventVertex vertexB = getEventVertexInGraph(b,logEvents,dg);
					dg.addEdge(vertexA,vertexB);
					edgeNumber++;
				}
				
			}
		}
		System.out.println("���ͼ�ߵ�����"+edgeNumber);
	
		//���dgͼ�ĵ����Ϣ
		 /*Collection<Vertex> allV = dg.getVertices();
		 for(Vertex v :allV) {
			 String vName = v.getName();
			 System.out.println("�������"+vName);
		 }
		 for(LogEvent logEvent :logEvents) {
			 System.out.println("logEvent������"+logEvent);
			}
		*/
		System.out.println("��="+dg.getVertices().size()+"��="+dg.getEdges().size());
		return dg;
		
	}
	
	/**
	 * ��DirectGraph�ıߣ�ͨ����������Ӧ�Ļ����
	 * @param a
	 * @param logEvents
	 * @param dg
	 * @return
	 */
	private EventVertex getEventVertexInGraph(XEventClass a, LogEvents logEvents,DirectedGraph dg) {
		String name = a.toString();//�����еĻ����
		
		 Collection<Vertex> allV = dg.getVertices();
		 
		 for(Vertex v :allV) {
			 String vName = v.getName().toString();
			 //System.out.println("�������"+vName+"�����"+name);
			 if(vName.equals(name+"+")) {
				 return (EventVertex) v;
			 }
		 }
		return null;
	}


	public Vertex getVertex(DirectedGraph dg,String name) {
		Collection<Vertex> ver = dg.getVertices();
		for(Vertex v :ver) {
			if(v.getName().equals(name)) {
				return v;
			}
			
		}
		return null;
		
	}

	public LogEvents getLogEvents() {
		// TODO Auto-generated method stub
		return logEvents;
	}

}
