package cn.sk.RPSTMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jbpt.algo.tree.rpst.RPSTNode;
import org.processmining.causalactivitymatrix.models.CausalActivityMatrix;
import org.processmining.framework.log.LogEvent;
import org.processmining.framework.log.LogEvents;

import cern.colt.matrix.DoubleMatrix2D;
import cn.sk.untils.EventVertex;
import cn.sk.untils.IntermediateData;

public class getSubLogsOld {
	ConcurrentHashMap<RPSTNode, IntermediateData> internalData = new ConcurrentHashMap<RPSTNode, IntermediateData>();
	ArrayList<RPSTNode> neededNode;
	XLog xLog;
	String miner;

	CausalActivityMatrix causalMatrix;
	DoubleMatrix2D directSuccession;
	LogEvents sortLogEvent;

	public getSubLogsOld(ArrayList<RPSTNode> neededNode, XLog log, String miner, LogEvents logEvents,
			CausalActivityMatrix causalMatrix) {
		getRelation rel = new getRelation();
		sortLogEvent = rel.getSortLogEvents(logEvents);
		directSuccession = rel.getOneDirectSuccession(sortLogEvent, log);
		this.xLog = log;
		this.neededNode = neededNode;
		this.miner = miner;
		this.causalMatrix = causalMatrix;

	}

	public DoubleMatrix2D getDirectSuccession() {
		return directSuccession;
	}

	public LogEvents getSortLogEvent() {
		return sortLogEvent;
	}

	public ConcurrentHashMap generateSubLogs() {
		// 初始化RPST结点对应的相关信息
		for (RPSTNode node : neededNode) {
			internalData.put(node, new IntermediateData(node));
		}

		//自循环处理
		List<LogEvent> loopTransition = new ArrayList<LogEvent>();
		for (int i = 0; i < directSuccession.rows(); i++) {
			double number = directSuccession.get(i, i);
			double loop = number / (number + 1);

			if (loop >= 0.9) {
				System.out.println(i + "=" + loop + sortLogEvent.get(i));
				loopTransition.add(sortLogEvent.get(i));
			}

		}

		//开始构造子日志
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		//Step 1.1: 初始化每个子日志的基本信息

		for (RPSTNode node : internalData.keySet()) {
			XLog subLog = factory.createLog((XAttributeMap) xLog.getAttributes().clone());
			//			XAttributeMap logattlist = XLogFunctions.copyAttMap(xLog.getAttributes());
			//			XLog newLog = new XLogImpl(logattlist);
			internalData.get(node).setXlog(subLog);
			LogEvent entryEvent = ((EventVertex) node.getEntry()).getLogEvent();
			LogEvent exitEvent = ((EventVertex) node.getExit()).getLogEvent();
			Boolean hasCycle = cycleJudge(causalMatrix, entryEvent, exitEvent);

		}
		// Step 6.2: 根据RPST结点的活动集过滤源日志

		for (RPSTNode node : internalData.keySet()) {
			XLog log = internalData.get(node).getXlog();
			//入口
			String entryName = ((EventVertex) node.getEntry()).getLogEvent().getModelElementName();
			//String entryType = ((EventVertex) node.getEntry()).getLogEvent().getEventType();
			//出口
			String exitName = ((EventVertex) node.getExit()).getLogEvent().getModelElementName();
			//String exitType = ((EventVertex) node.getExit()).getLogEvent().getEventType();

			for (XTrace trace : xLog) {
				int entryId = getEntryId(trace, entryName);
				int exitId = getExitId(trace, exitName);
				
				XTrace subTrace = factory.createTrace((XAttributeMap) trace.getAttributes().clone());
				if (entryId < exitId) {
					for (int i = entryId; i <= exitId; i++) {
						
						subTrace.add(trace.get(i));
					}

				}
				log.add(subTrace);
				
			}

		}

		return internalData;

	}

	private int getEntryId(XTrace trace, String entryName) {
		// TODO Auto-generated method stub
		
		int size = trace.size();
		String name = " ";
		for (int i = 0; i < size; i++) {
			name = XConceptExtension.instance().extractName(trace.get(i));
			if (name.equals(entryName)) {
				return i;
			}

		}
		return 0;
	}

	private int getExitId(XTrace trace, String exitName) {
		// TODO Auto-generated method stub
		
		int size = trace.size();
		String name = " ";
		for (int i = size - 1; i >= 0; i--) {
			name = XConceptExtension.instance().extractName(trace.get(i));
			if (name.equals(exitName)) {
				return i;
			}

		}
		return 0;
	}

	private XTrace addTrace(XTrace subTrace) {
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		XTrace newTrace = factory.createTrace((XAttributeMap) subTrace.getAttributes().clone());
		for (XEvent e : subTrace) {
			newTrace.add(e);
		}

		return newTrace;
	}

	private boolean isCycleElement(String nowLogEventName, List<LogEvent> loopTransition) {

		for (LogEvent l : loopTransition) {
			if (l.getModelElementName().equals(nowLogEventName)) {
				return true;
			}

		}
		return false;
	}

	private boolean findLogEvent(String nowLogEventName, RPSTNode node) {
		for (LogEvent event : internalData.get(node).getEventSet()) {
			if (event.getModelElementName().equals(nowLogEventName)) {

				return true;
			}
		}

		return false;
	}

	private int getSize(LogEvents eventSet) {

		LogEvents logEvents = new LogEvents();
		for (LogEvent olds : eventSet) {
			if (logEvents.contains(olds) == false) {
				logEvents.add(olds);
			}
		}
		return logEvents.size();

	}

	private Boolean cycleJudge(CausalActivityMatrix causalMatrix, LogEvent entryEvent, LogEvent exitEvent) {
		String entryName = entryEvent.getModelElementName();
		String entryType = entryEvent.getEventType();
		String exitName = exitEvent.getModelElementName();
		String exitType = exitEvent.getEventType();

		if (causalMatrix.getValue(new XEventClass(exitName + "+" + exitType, 0),
				new XEventClass(entryName + "+" + entryType, 1)) > 0.5) {
			return true;
		}

		return false;
	}

}
