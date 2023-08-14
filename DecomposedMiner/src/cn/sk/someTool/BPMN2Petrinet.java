package cn.sk.someTool;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;

import com.raffaeleconforti.conversion.bpmn.BPMNToPetriNetConverter;

public class BPMN2Petrinet {

	@Plugin(name = "AAAASMPetri", level = PluginLevel.Regular, returnLabels = { "Petri net", "initial marking", "final marking" }, returnTypes = {
			Petrinet.class, Marking.class,Marking.class }, parameterLabels = { "Log" }, userAccessible = true, help = "selectmethod")
	@UITopiaVariant(affiliation = "", author = "wangkang", email = "1159501573@qq.com")
	@PluginVariant(variantLabel = "selectmethod", requiredParameterLabels = { 0 })
	public Object[] mineCAM(UIPluginContext context, BPMNDiagram output) {
		boolean debug = false;
        BPMNToPetriNetConverter convert  = new BPMNToPetriNetConverter();
        return convert.convert(output);
	}
}
