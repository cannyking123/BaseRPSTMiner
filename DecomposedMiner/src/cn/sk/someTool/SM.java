package cn.sk.someTool;

import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

import au.edu.qut.processmining.miners.splitminer.SplitMiner;
import au.edu.qut.processmining.miners.splitminer.ui.miner.SplitMinerUI;
import au.edu.qut.processmining.miners.splitminer.ui.miner.SplitMinerUIResult;

public class SM {

	@Plugin(name = "AAAASM", level = PluginLevel.Regular, returnLabels = { "String" }, returnTypes = {
			BPMNDiagram.class }, parameterLabels = { "Log" }, userAccessible = true, help = "selectmethod")
	@UITopiaVariant(affiliation = "", author = "wangkang", email = "1159501573@qq.com")
	@PluginVariant(variantLabel = "selectmethod", requiredParameterLabels = { 0 })
	public BPMNDiagram mineCAM(UIPluginContext context, XLog log) {
		boolean debug = false;
        SplitMinerUI gui = new SplitMinerUI();
        SplitMinerUIResult result = gui.showGUI(context, "Setup HM+");
        SplitMiner sm = new SplitMiner();
        BPMNDiagram output = sm.mineBPMNModel(log, new XEventNameClassifier(), result.getPercentileFrequencyThreshold(), result.getParallelismsThreshold(), result.getFilterType(), result.isParallelismsFirst(), result.isReplaceIORs(), result.isRemoveLoopActivities(), result.getStructuringTime());
        return output;
	}
}
