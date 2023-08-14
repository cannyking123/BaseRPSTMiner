package cn.sk.someTool;

import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.pnanalysis.metrics.PetriNetMetric;
import org.processmining.pnanalysis.metrics.PetriNetMetricManager;




@Plugin(
		name = "AAAAAAtest AcceptingPetri Complexity Plugin",// plugin name
		
		returnLabels = {"complexValue"}, //return labels
		returnTypes = {String.class},//return class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = { "petri" },
		userAccessible = true,
		help = "Petri Complexity" 
		)
public class testComplexityAcceptingPetriNet {

	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl"
	        )
	@PluginVariant(
			variantLabel = "Merge two Event Log, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {0}
			)
	public static String SimRankSamplingTechnique(UIPluginContext context,AcceptingPetriNet anet)
	{
		Petrinet net = anet.getNet();
		Marking marking = new Marking();
		for(Place P :net.getPlaces()) {
//			System.out.println(P.getLabel());
//			System.out.println(P.toString()+"==");
			if(P.getLabel().equals("start")) {
				System.out.println(P.getLabel());
				marking.add(P);
			}
		}
		
		double CardosoValue=computeCardoso(context, net, marking);
		double CyclomaticValue=computeCyclomatic(context, net, marking);
		
		double complexity = (2*CardosoValue*CyclomaticValue)/(CardosoValue+CyclomaticValue);
		System.out.println("*********************");
		System.out.println("CardosoValue:"+CardosoValue);
		System.out.println("CyclomaticValue:"+CyclomaticValue);
		System.out.println("Complexity:"+complexity);
		System.out.println("*********************");
		String show="CardosoValue:"+CardosoValue+";     \n"+"CyclomaticValue:"+CyclomaticValue+";\n		"+"Complexity:"+complexity;
		return show;
	
	}
	public static double computeCardoso(PluginContext context, Petrinet net, Marking marking)
	{
		double value =0;
		for (PetriNetMetric metric : PetriNetMetricManager.getInstance().getMetrics()) {

			
			String metricName = metric.getName();
			if(metricName.equals("Extended Cardoso metric"))
			{
				value = metric.compute(context, net, marking);
//				System.out.println(metricName+" is "+value);	
			}
		}
		return value;
	}
	public static double computeCyclomatic(PluginContext context, Petrinet net, Marking marking)
	{
		double value =0;
		for (PetriNetMetric metric : PetriNetMetricManager.getInstance().getMetrics()) {

			String metricName = metric.getName();
			
			if(metricName.equals("Extended Cyclomatic metric"))
			{
				value = metric.compute(context, net, marking);
//				System.out.println(metricName+" is "+value);
			}
		}
		return value;
	}
}
