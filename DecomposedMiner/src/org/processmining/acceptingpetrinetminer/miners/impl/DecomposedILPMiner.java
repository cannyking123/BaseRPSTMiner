package org.processmining.acceptingpetrinetminer.miners.impl;

import java.util.prefs.Preferences;

import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinetminer.miners.DecomposedImplicitMiner;
import org.processmining.acceptingpetrinetminer.miners.DecomposedMinerParameters;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.packages.PackageManager;
import org.processmining.framework.packages.UnknownPackageException;
import org.processmining.framework.packages.UnknownPackageTypeException;
import org.processmining.framework.packages.impl.CancelledException;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.ilpminer.ILPMiner;
import org.processmining.plugins.ilpminer.ILPMinerSettings;
import org.processmining.plugins.ilpminer.ILPMinerSettings.SolverSetting;
import org.processmining.plugins.ilpminer.ILPMinerSettings.SolverType;
import org.processmining.plugins.ilpminer.ILPMinerStrategyManager;
import org.processmining.plugins.ilpminer.ILPMinerUI;
import org.processmining.plugins.ilpminer.templates.PetriNetILPModelSettings;
import org.processmining.plugins.ilpminer.templates.javailp.EmptyAfterCompletionILPModel;

public class DecomposedILPMiner implements DecomposedImplicitMiner {

	private static final String NAME = "ILP Miner";

	public Petrinet mineImplicit(PluginContext context, XLog eventLog, DecomposedMinerParameters parameters)
			throws CancelledException, UnknownPackageTypeException, UnknownPackageException, ConnectionCannotBeObtained {
		XLogInfo info = XLogInfoFactory.createLogInfo(eventLog, parameters.getClassifier());
		ILPMinerSettings settings = (new ILPMinerUI()).getSettings();
		settings.setVariant(EmptyAfterCompletionILPModel.class);
		//		settings.setVariant(PetriNetILPModel.class);
		PetriNetILPModelSettings modelSettings = new PetriNetILPModelSettings();
		modelSettings.setSearchType(PetriNetILPModelSettings.SearchType.PER_CD);
		settings.setModelSettings(modelSettings);
		Class<?>[] strategies = ILPMinerStrategyManager
				.getILPMinerStrategyExtensions();
		String s = "class org.processmining.plugins.ilpminer.templates.PureNetILPModelExtension";
		Class<?>[] e = new Class[1];
		for(Class<?> a :strategies){
			if(a.toString().equals(s)) {
				e[0] = a;
			}
		}
		
		settings.setExtensions(e);
       System.out.println("+++++++++++++++++"+e[0]);

		Preferences prefs = Preferences.userNodeForPackage(ILPMiner.class);
		prefs.putInt("SolverEnum", ((SolverType) settings.getSolverSetting(SolverSetting.TYPE)).ordinal());
		prefs.put("LicenseDir", (String) settings.getSolverSetting(SolverSetting.LICENSE_DIR));
		PackageManager.getInstance().findOrInstallPackages("ILPMiner");
//		long millis = -System.currentTimeMillis();
		Petrinet pn = context.tryToFindOrConstructFirstNamedObject(Petrinet.class, "ILP Miner", null, null, eventLog,
				info, settings);
//		millis += System.currentTimeMillis();
//		System.out.println("ILP Miner: " + millis + " milliseconds.");
		Marking finalMarking = new Marking();
		context.getProvidedObjectManager().createProvidedObject("Final marking", finalMarking, Marking.class, context);
		context.getConnectionManager().addConnection(new FinalMarkingConnection(pn, finalMarking));
		return pn;
	}


	public String getName() {
		return NAME;
	}


	public boolean requiresStartEndEvents() {
		return false;
	}
}
