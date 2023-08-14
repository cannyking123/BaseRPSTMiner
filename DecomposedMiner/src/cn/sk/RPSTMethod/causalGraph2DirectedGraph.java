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
		//String str=aa.substring(0, aa.indexOf(",")); 截取某个字符前的字符串
		//String bb=aa.substring(str.length()+1, aa.length()); 截取某个字符后的字符串
		DirectedGraph dg = new DirectedGraph();//RPST的输入
		
		List<XEventClass> activities = matrix.getActivities();
		for(XEventClass a :activities) {
			String name = a.toString();
			//System.out.println("shuchumingzi ----"+name);
			
			LogEvent logEvent = new LogEvent(name,"");
			logEvents.add(logEvent);
			
		}
		
		//System.out.println("LogEvents大小"+logEvents.size());
		//添加节点
		for(LogEvent logEvent :logEvents) {
			dg.addVertex(new EventVertex(logEvent));
		}
		
		//加边
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
		System.out.println("因果图边的数量"+edgeNumber);
	
		//输出dg图的点的信息
		 /*Collection<Vertex> allV = dg.getVertices();
		 for(Vertex v :allV) {
			 String vName = v.getName();
			 System.out.println("点的名字"+vName);
		 }
		 for(LogEvent logEvent :logEvents) {
			 System.out.println("logEvent的名字"+logEvent);
			}
		*/
		System.out.println("点="+dg.getVertices().size()+"边="+dg.getEdges().size());
		return dg;
		
	}
	
	/**
	 * 找DirectGraph的边，通过因果矩阵对应的活动名称
	 * @param a
	 * @param logEvents
	 * @param dg
	 * @return
	 */
	private EventVertex getEventVertexInGraph(XEventClass a, LogEvents logEvents,DirectedGraph dg) {
		String name = a.toString();//矩阵中的活动名称
		
		 Collection<Vertex> allV = dg.getVertices();
		 
		 for(Vertex v :allV) {
			 String vName = v.getName().toString();
			 //System.out.println("点的名字"+vName+"活动名字"+name);
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
