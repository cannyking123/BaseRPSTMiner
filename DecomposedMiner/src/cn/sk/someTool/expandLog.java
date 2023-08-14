package cn.sk.someTool;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;

public class expandLog {

	@Plugin(name = "AAA expandLog", level = PluginLevel.Regular, returnLabels = { "LOG" }, returnTypes = {
			XLog.class }, parameterLabels = { "net1" }, userAccessible = true, help = "---")
	@UITopiaVariant(affiliation = "", author = "wangkang", email = "1159501573@qq.com")
	@PluginVariant(variantLabel = "Mine a PetriNet, dialog", requiredParameterLabels = { 0 })
	public XLog mineCAM(UIPluginContext context, XLog log) {
		//扩展日志
				XFactory factory = XFactoryRegistry.instance().currentDefault();
				XLog subLog = factory.createLog((XAttributeMap) log.getAttributes().clone());
				for (XTrace trace : log) {
					for (int i = 0; i < 100; i++) {
						subLog.add(trace);
					}
				}

		//日志加噪声
//		int i = 0;
//
//		for (XTrace trace : log) {
//			if (i < 70) {
//				trace.remove(i % trace.size());
//				i++;
//
//			}else {
//				
//				
//				trace.add(i % trace.size(), trace.get(i % trace.size()));
//				i++;
//				
//			}
//
//		}

		return subLog;

	}
}
