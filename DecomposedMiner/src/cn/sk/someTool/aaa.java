package cn.sk.someTool;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.loggenerator.PetriNetLogGenerator;

public class aaa {

	@Plugin(name = "AAA PetriNetLogGenerator", level = PluginLevel.Regular, returnLabels = { "LOG" }, returnTypes = {
			XLog.class }, parameterLabels = { "net1","marking" }, userAccessible = true, help = "---")
	@UITopiaVariant(affiliation = "", author = "-", email = "-")
	@PluginVariant(variantLabel = "Mine a PetriNet, dialog", requiredParameterLabels = { 0,1 })
	public XLog mineCAM(UIPluginContext context, Petrinet petriNet, Marking marking) {
		
		PetriNetLogGenerator pp = new PetriNetLogGenerator();
		XLog log = pp.main(context, petriNet, marking);
		
		return log;
		
	}
}
