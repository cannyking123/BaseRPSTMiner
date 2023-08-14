package cn.sk.untils;

import org.jbpt.hypergraph.abs.Vertex;
import org.processmining.framework.log.LogEvent;

public class EventVertex extends Vertex {

	private LogEvent logEvent;

	public EventVertex(LogEvent logEvent) {
		//super(logEvent.getModelElementName());
		super(logEvent.getModelElementName() + "+" + logEvent.getEventType());
		this.logEvent = logEvent;
	}
	public EventVertex(String name) {
		//super(logEvent.getModelElementName());
		super(name);
		
	}

	public LogEvent getLogEvent() {
		return logEvent;
	}
}
