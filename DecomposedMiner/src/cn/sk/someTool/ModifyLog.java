package cn.sk.someTool;

import java.util.Date;

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.processmining.plugins.simplelogoperations.XLogFunctions;

public class ModifyLog {
	
	public XEvent makeEvent(String name, Date time){ //add the attribute 
		XAttributeMap attMap = new XAttributeMapImpl();
		XLogFunctions.putLiteral(attMap, "concept:name", name);
		XLogFunctions.putLiteral(attMap, "lifecycle:transition", "COMPLETE");
		XLogFunctions.putLiteral(attMap, "Message:Sent", "sent");
		XLogFunctions.putLiteral(attMap, "org:resource", "artificial");
		XLogFunctions.putTimestamp(attMap, "time:timestamp", time);
		XEvent newEvent = new XEventImpl(attMap);
		return newEvent;
		}

		//change the numerical attribute 
		public XEvent changeNumericalAttribute(XEvent oldEvent) {
		XAttributeMap attMap = new XAttributeMapImpl();
		XLogFunctions.putLiteral(attMap, "concept:name", oldEvent.getAttributes().get("concept:name").toString());
		XLogFunctions.putLiteral(attMap, "lifecycle:transition", "complete");
		XLogFunctions.putLiteral(attMap, "org:resource", oldEvent.getAttributes().get("org:resource").toString());
		XLogFunctions.putTimestamp(attMap, "time:timestamp", XLogFunctions.getTime(oldEvent));
		Long numericalAttribute=Long.parseLong(oldEvent.getAttributes().get("Amount").toString());
		numericalAttribute = 1000 - numericalAttribute;
		XLogFunctions.putLiteral(attMap, "Amount",numericalAttribute.toString());
		XEvent newEvent = new XEventImpl(attMap);
		return newEvent;
		}
		
		public static XEvent makeEventSimpleness(String name, String transition){ //add the attribute 
			XAttributeMap attMap = new XAttributeMapImpl();
			XLogFunctions.putLiteral(attMap, "concept:name", name);
			XLogFunctions.putLiteral(attMap, "lifecycle:transition", transition);
			XEvent newEvent = new XEventImpl(attMap);
			return newEvent;
			}

}
