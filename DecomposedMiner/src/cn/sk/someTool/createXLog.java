package cn.sk.someTool;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.ilpminer.ILPMinerSettings;
import org.processmining.plugins.ilpminer.ILPMinerStrategyManager;
import org.processmining.plugins.ilpminer.ILPMinerUI;
import org.processmining.plugins.ilpminer.templates.PureNetILPModelExtension;

public class createXLog {
	@Plugin( 
			name = "AAAB", 
			parameterLabels = { "log" }, 
			returnLabels = { "log" },
			returnTypes = { XLog.class },
			userAccessible = true)     ///
	@UITopiaVariant(affiliation ="" , author = "  ", email = "  ")
	@PluginVariant(variantLabel = "createXLog", requiredParameterLabels = { 0 })
	public XLog removeEdgePoints(UIPluginContext context, XLog log) {
		//XAttributeMap at = log.getAttributes();
		
		//创建一个新的日志
		ILPMinerUI ui;
//		try {
//			ILPMinerLogPetrinetConnection conn = context.getConnectionManager()
//					.getFirstConnection(ILPMinerLogPetrinetConnection.class,
//							context, log);
//			ui = new ILPMinerUI((ILPMinerSettings) conn
//					.getObjectWithRole(ILPMinerLogPetrinetConnection.SETTINGS));
//		} catch (Exception e) {
			ui = new ILPMinerUI();
//		}
		InteractionResult r = context.showWizard(
				"Configure the ILP Mining Algorithm", true, true, ui
						.initComponents());
		ILPMinerSettings sets = ui.getSettings();
		PureNetILPModelExtension c;
		Class<?>[] x = sets.getExtensions();
		System.out.println(x.length);
		for(Class<?> a :sets.getExtensions()){
			System.out.println(a+"  "+a.getTypeName());
		}
		ILPMinerSettings settings = (new ILPMinerUI()).getSettings();
		
		
		Class<?>[] strategies = ILPMinerStrategyManager
				.getILPMinerStrategyExtensions();
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		for(Class<?> a :strategies){
			System.out.println(a+"  "+a.getTypeName());
			System.out.println(a.toString());
		}
		
		return log;
		
	}

}
