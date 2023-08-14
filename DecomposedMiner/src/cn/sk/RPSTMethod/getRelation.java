package cn.sk.RPSTMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.log.LogEvent;
import org.processmining.framework.log.LogEvents;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

public class getRelation {
	
	DoubleMatrix2D directSuccession;
	LogEvents sortedEvents;
	

	
	public LogEvents getSortLogEvents(LogEvents logEvents) {
	    sortedEvents = new LogEvents();
		List<LogEvent> sortedEventsList = new ArrayList<LogEvent>();
		for(LogEvent logEvent :logEvents) {
			sortedEventsList.add(logEvent);
		}
		Collections.sort(sortedEventsList);
		sortedEventsList = Collections.unmodifiableList(sortedEventsList);
		for(LogEvent logEvent :sortedEventsList) {
			sortedEvents.add(logEvent);
		}
//		System.out.println("日志时间排序输出测试");
//		for(LogEvent logEvent :sortedEvents) {
//			System.out.println(logEvent);
//		}

		return sortedEvents;
		
	}
	public HashMap<LogEvent,Integer> getSortLogEvents2Integer(LogEvents sortedEvents){
		HashMap<LogEvent,Integer> SortLogEvents2Integer = new HashMap<LogEvent,Integer>();
		int countEventNumber = 0;
		//System.out.println("哈希map输出测试");
		for (LogEvent logEvent : sortedEvents) {
			SortLogEvents2Integer.put(logEvent, countEventNumber++);
			//System.out.println(logEvent + "" + countEventNumber);
		} 
		
		
		return SortLogEvents2Integer;
	}
	
	public  DoubleMatrix2D getOneDirectSuccession(LogEvents logEvents,XLog log) {
		
	 directSuccession = new DenseDoubleMatrix2D(logEvents.size(), logEvents.size());
		
		HashMap<LogEvent,Integer> SortLogEvents2Integer = getSortLogEvents2Integer(logEvents);
		
		LogEvent temp1;
		LogEvent temp2;

		for (XTrace trace : log) {
			for (int i = 0; i < trace.size() - 1; i++) {
				XEvent source = trace.get(i);
				XEvent target = trace.get(i + 1);

				String name1 = XConceptExtension.instance().extractName(source).toString();
				//String lifeCycle1 = source.getAttributes().get("lifecycle:transition").toString();

				int hang = getIndex(SortLogEvents2Integer, name1);

				String name2 = XConceptExtension.instance().extractName(target).toString();
				//String lifeCycle2 = target.getAttributes().get("lifecycle:transition").toString();

				int lie = getIndex(SortLogEvents2Integer, name2);

				double oldNumber = directSuccession.get(hang, lie);
				directSuccession.set(hang, lie, oldNumber + 1.0);

			}
		}
		//直接跟随次数输出测试
//				System.out.println("直接跟随次数输出测试");
//				for(int i= 0;i<values.rows();i++) {
//					for(int j= 0;j<values.rows();j++) {
//						System.out.print(values.get(i, j)+" ");
//					}
//					System.out.println();
//				}
		return directSuccession;
	}
	private int getIndex(HashMap<LogEvent, Integer> sortLogEvents2Integer, String name) {

		//System.out.println("遍历哈希集合的大小" + sortLogEvents2Integer.size());
		for (LogEvent logEvent : sortLogEvents2Integer.keySet()) {
			if(logEvent.getModelElementName().equals(name)) {
				return sortLogEvents2Integer.get(logEvent);
			}
		}

		return 0;

	
	} 
	
	public DoubleMatrix1D getStartCount(LogEvents logEvents,XLog log) {
		DoubleMatrix1D values = new DenseDoubleMatrix1D(logEvents.size());
		int count = 0;
		int location = 0;
		for (XTrace trace : log) {
			for(XEvent event :trace) {
				String name = XConceptExtension.instance().extractName(event);
				if(name.equals("start")) {
					count++;
				}
			}
		}
		for(LogEvent logEvent : logEvents) {
			
			if(logEvent.getModelElementName().equals("start")) {
				System.out.println(logEvent.getModelElementName());
				break;
			}
			else {
				location++;
			}
			
		}
		values.set(location, count);
		
		return values;
		
	}
	public DoubleMatrix1D getEndCount(LogEvents logEvents,XLog log) {
		DoubleMatrix1D values = new DenseDoubleMatrix1D(logEvents.size());
		int count = 0;
		int location = 0;
		for (XTrace trace : log) {
			for(XEvent event :trace) {
				String name = XConceptExtension.instance().extractName(event);
				if(name.equals("end")) {
					count++;
				}
			}
		}
		for(LogEvent logEvent : logEvents) {
			
			if(logEvent.getModelElementName().equals("end")) {
				break;
			}else {
				location++;
			}
		}
		values.set(location, count);
		
		return values;
		
	}


	

}
