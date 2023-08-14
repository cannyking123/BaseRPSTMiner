package cn.sk.someTool;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.plugins.simplelogoperations.XLogFunctions;

public class Chuan_C {
//改进版本
	@Plugin( 
			name = "Chuan_C", 
			parameterLabels = { "log" }, 
			returnLabels = { "log" },
			returnTypes = { XLog.class },
			userAccessible = true)     ///
	@UITopiaVariant(affiliation ="" , author = "  ", email = "    ")
	
	public XLog removeEdgePoints(PluginContext context, XLog log) {
		
		XAttributeMap logattlist = XLogFunctions.copyAttMap(log.getAttributes());
		XLog newLog = new XLogImpl(logattlist);

		for (XTrace trace: log)	{   ///all trace size!=0
			XTrace newTrace = new XTraceImpl(XLogFunctions.copyAttMap(trace.getAttributes()));
			for(XEvent event: trace ){  // //For every event  int i = 0; i < trace.size()-1; i++
			//	System.out.println(XConceptExtension.instance().extractName(event));
//*************************************In-patient Department	
				String name = XConceptExtension.instance().extractName(event);
				if (name.equals("D20")||name.equals("D21")||name.equals("D22")||name.equals("D24")||name.equals("D25") ) {    
					newTrace.add(changeNumericalAttribute(event));
				}	
				else if(name.equals("D23") ) {    //.equals("t15")
//					
					newTrace.add(makeEvent_D23(event));
				}
				
/////**********************************************Charge_office
				else if(name.equals("D26")) {    //.equals("t15") //原31
//					Date time = new Date();					
//					time = XLogFunctions.getTime(trace.get(0));
//					time.setTime(time.getTime() - 1);
					newTrace.add(makeEvent_D26(event));
				}
				else if(name.equals("D27")) {    //.equals("t15") //原32
//					Date time = new Date();					
//					time = XLogFunctions.getTime(trace.get(0));
//					time.setTime(time.getTime() - 1);
					newTrace.add(makeEvent_D27(event));
				}
/////**********************************************Pharmacy	
				else if(name.equals("D28")) {    //.equals("t15")
//					Date time = new Date();					
//					time = XLogFunctions.getTime(trace.get(0));
//					time.setTime(time.getTime() - 1);
					newTrace.add(makeEvent_D28(event));
				}
				else if(name.equals("D29")) {    //.equals("t15")
//					Date time = new Date();					
//					time = XLogFunctions.getTime(trace.get(0));
//					time.setTime(time.getTime() - 1);
					newTrace.add(makeEvent_D29(event));
				}
				else if(name.equals("D30")) {    //.equals("t15")
					newTrace.add(changeNumericalAttribute_D30(event));
				}
				
/////**********************************************Receptionist
				else if (name.equals("D3")) {    //.equals("t15")
//					Date time = new Date();					
//					time = XLogFunctions.getTime(trace.get(0));
//					time.setTime(time.getTime() - 1);
					newTrace.add(makeEvent_D3(event));
				}					
				else if(name.equals( "D1" )||name.equals( "D2" )) {    
					newTrace.add(changeNumericalAttribute_D12(event));
				}
/////**********************************************Out_patient
				else if(name.equals("D4")) {    //.equals("t15")
//					Date time = new Date();					
//					time = XLogFunctions.getTime(trace.get(0));
//					time.setTime(time.getTime() - 1);
					newTrace.add(makeEvent_D4(event));
				}
				else if(name.equals("D14")) {    //.equals("t15")
//					Date time = new Date();					
//					time = XLogFunctions.getTime(trace.get(0));
//					time.setTime(time.getTime() - 1);
					newTrace.add(makeEvent_D14(event));
				}
				else if(name.equals("D5")||name.equals("D6")||name.equals("D7")||name.equals("D8")||
						name.equals("D9")||name.equals("D10")||name.equals("D11")||name.equals("D12")||name.equals("D13")) {    //.equals("t15")
					newTrace.add(changeNumericalAttribute_General(event));
				}
/////**********************************************Fever
				else if(name.equals("D15")) {    //.equals("t15")
//					Date time = new Date();					
//					time = XLogFunctions.getTime(trace.get(0));
//					time.setTime(time.getTime() - 1);
					newTrace.add(makeEvent_D15(event));
				}
				else if(name.equals("D16")) {    //.equals("t15")
//					Date time = new Date();					
//					time = XLogFunctions.getTime(trace.get(0));
//					time.setTime(time.getTime() - 1);
					newTrace.add(makeEvent_D16(event));
				}
				else if(name.equals("D17")||name.equals("D18")||name.equals("D19")) {    //.equals("t15")
					newTrace.add(changeNumericalAttribute_Fever(event));
				}
///////////////////////////////////////////////////////////////////////////////////////				
				else     //.equals("t15")
					newTrace.add(event);//event);
			}
					//String sourceN=XConceptExtension.instance().extractName(trace.get(i));
			newLog.add(newTrace);
			context.getProgress().inc();	
		 }
		return newLog;  ///
	}
	
	 private XEvent changeNumericalAttribute(XEvent oldEvent) {
	    	XAttributeMap attMap = new XAttributeMapImpl();
	    	XLogFunctions.putLiteral(attMap, "concept:name", oldEvent.getAttributes().get("concept:name").toString());
	    	XLogFunctions.putLiteral(attMap, "Message:Rec",  "null"); //MessageRc 接收
			XLogFunctions.putLiteral(attMap, "Message:Sent", "null");   //MessageSent 发送
			XLogFunctions.putLiteral(attMap, "resource",  "null");
			XLogFunctions.putLiteral(attMap, "org:resource", "Record Room");
	    	XLogFunctions.putTimestamp(attMap, "time:timestamp", XLogFunctions.getTime(oldEvent));
	    	XLogFunctions.putLiteral(attMap, "lifecycle:transition", "COMPLETE");
	    	XEvent newEvent = new XEventImpl(attMap);
	    	return newEvent;
	    	}
    private XEvent makeEvent_D23(XEvent oldEvent) {
		XAttributeMap attMap = new XAttributeMapImpl();
		XLogFunctions.putLiteral(attMap, "concept:name",  oldEvent.getAttributes().get("concept:name").toString());
		XLogFunctions.putLiteral(attMap, "Message:Rec", "m6"); //MessageRc 接收
		XLogFunctions.putLiteral(attMap, "Message:Sent", "null");   //MessageSent 发送
		XLogFunctions.putLiteral(attMap, "resource", "null");
		XLogFunctions.putLiteral(attMap, "org:resource", "Record Room");
		XLogFunctions.putTimestamp(attMap, "time:timestamp",XLogFunctions.getTime(oldEvent));
		XLogFunctions.putLiteral(attMap, "lifecycle:transition", "COMPLETE");
		XEvent newEvent = new XEventImpl(attMap);  // Creates a new event.
		return newEvent;
		}       
/////**********************************************Charge_office
    private XEvent makeEvent_D26(XEvent oldEvent) {
		XAttributeMap attMap = new XAttributeMapImpl();
		XLogFunctions.putLiteral(attMap, "concept:name", oldEvent.getAttributes().get("concept:name").toString());
		XLogFunctions.putLiteral(attMap, "Message:Rec", "m4"); //MessageRc 接收
		XLogFunctions.putLiteral(attMap, "Message:Sent", "null");   //MessageSent 发送
		XLogFunctions.putLiteral(attMap, "resource", "null");
		XLogFunctions.putLiteral(attMap, "org:resource", "Charge office Department");
		XLogFunctions.putTimestamp(attMap, "time:timestamp",XLogFunctions.getTime(oldEvent));
		XLogFunctions.putLiteral(attMap, "lifecycle:transition", "COMPLETE");
		XEvent newEvent = new XEventImpl(attMap);  // Creates a new event.
		return newEvent;
		}    
    private XEvent makeEvent_D27(XEvent oldEvent) {
		XAttributeMap attMap = new XAttributeMapImpl();
		XLogFunctions.putLiteral(attMap, "concept:name", oldEvent.getAttributes().get("concept:name").toString());
		XLogFunctions.putLiteral(attMap, "Message:Rec", "null"); //MessageRc 接收
		XLogFunctions.putLiteral(attMap, "Message:Sent", "m5");   //MessageSent 发送
		XLogFunctions.putLiteral(attMap, "resource", "null");
		XLogFunctions.putLiteral(attMap, "org:resource", "Charge office Department");
		XLogFunctions.putTimestamp(attMap, "time:timestamp", XLogFunctions.getTime(oldEvent));
		XLogFunctions.putLiteral(attMap, "lifecycle:transition", "COMPLETE");
		XEvent newEvent = new XEventImpl(attMap);  // Creates a new event.
		return newEvent;
		}    
    
/////**********************************************Pharmacy
    private XEvent makeEvent_D28(XEvent oldEvent) {
		XAttributeMap attMap = new XAttributeMapImpl();
		XLogFunctions.putLiteral(attMap, "concept:name", oldEvent.getAttributes().get("concept:name").toString());
		XLogFunctions.putLiteral(attMap, "Message:Rec", "m3"); //MessageRc 接收
		XLogFunctions.putLiteral(attMap, "Message:Sent", "m4");   //MessageSent 发送
		XLogFunctions.putLiteral(attMap, "resource", "null");
		XLogFunctions.putLiteral(attMap, "org:resource", "Pharmacy Department");
		XLogFunctions.putTimestamp(attMap, "time:timestamp",XLogFunctions.getTime(oldEvent));
		XLogFunctions.putLiteral(attMap, "lifecycle:transition", "COMPLETE");
		XEvent newEvent = new XEventImpl(attMap);  // Creates a new event.
		return newEvent;
		}    
    private XEvent makeEvent_D29(XEvent oldEvent) {
		XAttributeMap attMap = new XAttributeMapImpl();
		XLogFunctions.putLiteral(attMap, "concept:name",oldEvent.getAttributes().get("concept:name").toString());
		XLogFunctions.putLiteral(attMap, "Message:Rec", "m5"); //MessageRc 接收
		XLogFunctions.putLiteral(attMap, "Message:Sent", "null");   //MessageSent 发送
		XLogFunctions.putLiteral(attMap, "resource", "null");
		XLogFunctions.putLiteral(attMap, "org:resource", "Pharmacy Department");
		XLogFunctions.putTimestamp(attMap, "time:timestamp", XLogFunctions.getTime(oldEvent));
		XLogFunctions.putLiteral(attMap, "lifecycle:transition", "COMPLETE");
		XEvent newEvent = new XEventImpl(attMap);  // Creates a new event.
		return newEvent;
		}
    private XEvent changeNumericalAttribute_D30(XEvent oldEvent) {
    	XAttributeMap attMap = new XAttributeMapImpl();
    	XLogFunctions.putLiteral(attMap, "concept:name", oldEvent.getAttributes().get("concept:name").toString());
    	XLogFunctions.putLiteral(attMap, "Message:Rec",  "null"); //MessageRc 接收
		XLogFunctions.putLiteral(attMap, "Message:Sent", "null");   //MessageSent 发送
		XLogFunctions.putLiteral(attMap, "resource",  "null");
		XLogFunctions.putLiteral(attMap, "org:resource", "Pharmacy Department");
    	XLogFunctions.putTimestamp(attMap, "time:timestamp", XLogFunctions.getTime(oldEvent));
    	XLogFunctions.putLiteral(attMap, "lifecycle:transition", "COMPLETE");
    	XEvent newEvent = new XEventImpl(attMap);
    	return newEvent;
    	}

/////**********************************************Receptionist
    private XEvent changeNumericalAttribute_D12(XEvent oldEvent) {
    	XAttributeMap attMap = new XAttributeMapImpl();
    	XLogFunctions.putLiteral(attMap, "concept:name", oldEvent.getAttributes().get("concept:name").toString());
    	XLogFunctions.putLiteral(attMap, "Message:Rec",  "null"); //MessageRc 接收
		XLogFunctions.putLiteral(attMap, "Message:Sent", "null");   //MessageSent 发送
		XLogFunctions.putLiteral(attMap, "resource",  "null");
		XLogFunctions.putLiteral(attMap, "org:resource", "Receptionist Department");
    	XLogFunctions.putTimestamp(attMap, "time:timestamp", XLogFunctions.getTime(oldEvent));
    	XLogFunctions.putLiteral(attMap, "lifecycle:transition", "COMPLETE");
    	XEvent newEvent = new XEventImpl(attMap);
    	return newEvent;
    	}  
    private XEvent makeEvent_D3(XEvent oldEvent) {
    	XAttributeMap attMap = new XAttributeMapImpl();
    	XLogFunctions.putLiteral(attMap, "concept:name", oldEvent.getAttributes().get("concept:name").toString());
    	XLogFunctions.putLiteral(attMap, "Message:Rec", "null"); //MessageRc 接收
    	XLogFunctions.putLiteral(attMap, "Message:Sent", "m2, m1");   //MessageSent 发送
    	XLogFunctions.putLiteral(attMap, "resource", "null");
    	XLogFunctions.putLiteral(attMap, "org:resource", "Receptionist Department");
    	XLogFunctions.putTimestamp(attMap, "time:timestamp", XLogFunctions.getTime(oldEvent));
    	XLogFunctions.putLiteral(attMap, "lifecycle:transition", "COMPLETE");
    	XEvent newEvent = new XEventImpl(attMap);  // Creates a new event.
    	return newEvent;
	}
    
/////**********************************************Surgical
    private XEvent makeEvent_D4(XEvent oldEvent) {
		XAttributeMap attMap = new XAttributeMapImpl();
		XLogFunctions.putLiteral(attMap, "concept:name",oldEvent.getAttributes().get("concept:name").toString());
		XLogFunctions.putLiteral(attMap, "Message:Rec", "m1"); //MessageRc 接收
		XLogFunctions.putLiteral(attMap, "Message:Sent", "null");   //MessageSent 发送
		XLogFunctions.putLiteral(attMap, "resource", "null");
		XLogFunctions.putLiteral(attMap, "org:resource", "General Out-patient Department");
		XLogFunctions.putTimestamp(attMap, "time:timestamp", XLogFunctions.getTime(oldEvent));
		XLogFunctions.putLiteral(attMap, "lifecycle:transition", "COMPLETE");
		XEvent newEvent = new XEventImpl(attMap);  // Creates a new event.
		return newEvent;
		}	
	private XEvent makeEvent_D14(XEvent oldEvent) {
		XAttributeMap attMap = new XAttributeMapImpl();
		XLogFunctions.putLiteral(attMap, "concept:name",oldEvent.getAttributes().get("concept:name").toString());
		XLogFunctions.putLiteral(attMap, "Message:Rec", "null"); //MessageRc 接收
		XLogFunctions.putLiteral(attMap, "Message:Sent", "m3");   //MessageSent 发送
		XLogFunctions.putLiteral(attMap, "resource", "null");
		XLogFunctions.putLiteral(attMap, "org:resource", "General Out-patient Department");
		XLogFunctions.putTimestamp(attMap, "time:timestamp", XLogFunctions.getTime(oldEvent));
		XLogFunctions.putLiteral(attMap, "lifecycle:transition", "COMPLETE");
		XEvent newEvent = new XEventImpl(attMap);  // Creates a new event.
		return newEvent;
		}	
	private XEvent changeNumericalAttribute_General(XEvent oldEvent) {
    	XAttributeMap attMap = new XAttributeMapImpl();
    	XLogFunctions.putLiteral(attMap, "concept:name", oldEvent.getAttributes().get("concept:name").toString());
    	XLogFunctions.putLiteral(attMap, "Message:Rec",  "null"); //MessageRc 接收
		XLogFunctions.putLiteral(attMap, "Message:Sent", "null");   //MessageSent 发送
		XLogFunctions.putLiteral(attMap, "resource",  "null");
		XLogFunctions.putLiteral(attMap, "org:resource", "General Out-patient Department");
    	XLogFunctions.putTimestamp(attMap, "time:timestamp", XLogFunctions.getTime(oldEvent));
    	XLogFunctions.putLiteral(attMap, "lifecycle:transition", "COMPLETE");
    	XEvent newEvent = new XEventImpl(attMap);
    	return newEvent;
    	}

    
/////**********************************************X_ray
    private XEvent makeEvent_D15(XEvent oldEvent) {
		XAttributeMap attMap = new XAttributeMapImpl();
		XLogFunctions.putLiteral(attMap, "concept:name", oldEvent.getAttributes().get("concept:name").toString());
		XLogFunctions.putLiteral(attMap, "Message:Rec", "m2"); //MessageRc 接收
		XLogFunctions.putLiteral(attMap, "Message:Sent", "null");   //MessageSent 发送
		XLogFunctions.putLiteral(attMap, "resource", "null");
		XLogFunctions.putLiteral(attMap, "org:resource", "Fever Out-patient Department");
		XLogFunctions.putTimestamp(attMap, "time:timestamp",XLogFunctions.getTime(oldEvent));
		XLogFunctions.putLiteral(attMap, "lifecycle:transition", "COMPLETE");
		XEvent newEvent = new XEventImpl(attMap);  // Creates a new event.
		return newEvent;
		}
	
	private XEvent makeEvent_D16(XEvent oldEvent) {
		XAttributeMap attMap = new XAttributeMapImpl();
		XLogFunctions.putLiteral(attMap, "concept:name", oldEvent.getAttributes().get("concept:name").toString());
		XLogFunctions.putLiteral(attMap, "Message:Rec", "null"); //MessageRc 接收
		XLogFunctions.putLiteral(attMap, "Message:Sent", "m6");   //MessageSent 发送
		XLogFunctions.putLiteral(attMap, "resource", "null");
		XLogFunctions.putLiteral(attMap, "org:resource", "Fever Out-patient Department");
		XLogFunctions.putTimestamp(attMap, "time:timestamp", XLogFunctions.getTime(oldEvent));
		XLogFunctions.putLiteral(attMap, "lifecycle:transition", "COMPLETE");
		XEvent newEvent = new XEventImpl(attMap);  // Creates a new event.
		return newEvent;
		}	
	private XEvent changeNumericalAttribute_Fever(XEvent oldEvent) {
    	XAttributeMap attMap = new XAttributeMapImpl();
    	XLogFunctions.putLiteral(attMap, "concept:name", oldEvent.getAttributes().get("concept:name").toString());
    	XLogFunctions.putLiteral(attMap, "Message:Rec",  "null"); //MessageRc 接收
		XLogFunctions.putLiteral(attMap, "Message:Sent", "null");   //MessageSent 发送
		XLogFunctions.putLiteral(attMap, "resource",  "null");
		XLogFunctions.putLiteral(attMap, "org:resource", "Fever Out-patient Department");
    	XLogFunctions.putTimestamp(attMap, "time:timestamp", XLogFunctions.getTime(oldEvent));
    	XLogFunctions.putLiteral(attMap, "lifecycle:transition", "COMPLETE");
    	XEvent newEvent = new XEventImpl(attMap);
    	return newEvent;
    	}
}
