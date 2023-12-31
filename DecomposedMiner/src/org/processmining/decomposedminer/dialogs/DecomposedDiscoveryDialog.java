package org.processmining.decomposedminer.dialogs;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinetminer.miners.DecomposedMiner;
import org.processmining.acceptingpetrinetminer.miners.DecomposedMinerManager;
import org.processmining.activityclusterarray.models.ActivityClusterArray;
import org.processmining.decomposedminer.configurations.DecomposedDiscoveryConfiguration;
import org.processmining.decomposedminer.configurations.DecomposedDiscoveryConfigurationManager;
import org.processmining.decomposedminer.parameters.DecomposedDiscoveryParameters;
import org.processmining.framework.util.ui.widgets.ProMList;
import org.processmining.log.dialogs.ClassifierPanel;
import org.processmining.log.dialogs.MinerPanel;
import org.processmining.log.parameters.UpdateParameter;

import com.fluxicon.slickerbox.components.NiceSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class DecomposedDiscoveryDialog extends JPanel implements UpdateParameter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -271992065683956889L;
	
	private ProMList<String> list = null;
	private NiceSlider percentageSlider = null;

	public DecomposedDiscoveryDialog(XLog eventLog, final DecomposedDiscoveryParameters parameters) {
		double size[][] = { { TableLayoutConstants.FILL, TableLayoutConstants.FILL }, { TableLayoutConstants.FILL, TableLayoutConstants.FILL, 30 } };
		setLayout(new TableLayout(size));
		
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		for (DecomposedDiscoveryConfiguration configuration: DecomposedDiscoveryConfigurationManager.getInstance().getConfigurations()) {
			listModel.addElement(configuration.getName());
		}
		list = new ProMList<String>("Select configuration", listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final String defaultConfiguration = parameters.getConfiguration();
		list.setSelection(defaultConfiguration);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				List<String> selected = list.getSelectedValuesList();
				if (selected.size() == 1) {
					parameters.setConfiguration(selected.get(0));
				} else {
					/*
					 * Nothing selected. Revert to selection of default classifier.
					 */
					list.setSelection(defaultConfiguration);
					parameters.setConfiguration(defaultConfiguration);
				}
				update();
			}
		});
		list.setPreferredSize(new Dimension(100, 100));
		add(list, "0, 0, 0, 1");

		add(new ClassifierPanel(eventLog.getClassifiers(), parameters), "1, 0");
		
		List<String> availableMiners = new ArrayList<String>();
		for (DecomposedMiner miner: DecomposedMinerManager.getInstance().getMiners()) {
			availableMiners.add(miner.getName());
		}
		//�Լ��ӵ�
		//availableMiners.add("Split Miner");
		parameters.setMiner(DecomposedMinerManager.getInstance().getMiner(null).getName());
		add(new MinerPanel(availableMiners, parameters), "1, 1, 1, 1");

		percentageSlider = SlickerFactory.instance().createNiceIntegerSlider(
				"Decomposition percentage", 0, 100, parameters.getPercentage(), Orientation.HORIZONTAL);
		percentageSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				parameters.setPercentage(percentageSlider.getSlider().getValue());
			}
		});
		add(percentageSlider, "0, 2, 1, 2");
		
		update();
		
//		final JCheckBox checkBox = SlickerFactory.instance().createCheckBox("Join all clusters", false);
//		checkBox.setSelected(parameters.isCombineClusters());
//		checkBox.addActionListener(new ActionListener() {
//
//			public void actionPerformed(ActionEvent e) {
//				parameters.setCombineClusters(checkBox.isSelected());
//			}
//
//		});
//		checkBox.setOpaque(false);
//		checkBox.setPreferredSize(new Dimension(100, 30));
//		add(checkBox, "0, 2");
	}

	public DecomposedDiscoveryDialog(XLog eventLog, final DecomposedDiscoveryParameters parameters, final ActivityClusterArray clusters) {
		double size[][] = { { TableLayoutConstants.FILL }, { TableLayoutConstants.FILL, TableLayoutConstants.FILL } };
		setLayout(new TableLayout(size));
		add(new ClassifierPanel(eventLog.getClassifiers(), parameters), "0, 0");
		List<String> availableMiners = new ArrayList<String>();
		for (DecomposedMiner miner: DecomposedMinerManager.getInstance().getMiners()) {
			availableMiners.add(miner.getName());
		}
		parameters.setMiner(DecomposedMinerManager.getInstance().getMiner(null).getName());
		add(new MinerPanel(availableMiners, parameters), "0, 1");
	}

	public void update() {
		if (percentageSlider != null && list != null && !list.getSelectedValuesList().isEmpty()) {
			DecomposedDiscoveryConfigurationManager.getInstance().getConfiguration(list.getSelectedValuesList().get(0))
					.update(percentageSlider);
		}
	}
}
