package cn.sk.someTool;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;

public class aaa2 {

	@Plugin(name = "AAA NOW", level = PluginLevel.Regular, returnLabels = { "LOG" }, returnTypes = {
			XLog.class }, parameterLabels = { "net1" }, userAccessible = true, help = "---")
	@UITopiaVariant(affiliation = "", author = "-", email = "-")
	@PluginVariant(variantLabel = "Mine a PetriNet, dialog", requiredParameterLabels = { 0})
	public XLog mineCAM(UIPluginContext context, XLog log) {
		for(XTrace trace :log) {
			for (XEvent event : trace) {
				String nowLogEventName = XConceptExtension.instance().extractName(event);
				println(nowLogEventName);
			}
			System.out.println();
		}
		return log;
		
	}

	private void println(String nowLogEventName) {
		
		
		if(nowLogEventName.equals("t1")) {
			System.out.print("i");
		}
		if(nowLogEventName.equals("t2")) {
			System.out.print("a");
		}
		if(nowLogEventName.equals("t3")) {
			System.out.print("X");
		}
		if(nowLogEventName.equals("t4")) {
			System.out.print("b");
		}
		if(nowLogEventName.equals("t5")) {
			System.out.print("c");
		}
		if(nowLogEventName.equals("t6")) {
			System.out.print("d");
		}
		if(nowLogEventName.equals("t7")) {
			System.out.print("e");
		}
		if(nowLogEventName.equals("t8")) {
			System.out.print("o");
		}
		if(nowLogEventName.equals("t9")) {
			System.out.print("f");
		}
		if(nowLogEventName.equals("t10")) {
			System.out.print("g");
		}
		
		
		
		
	}
}
