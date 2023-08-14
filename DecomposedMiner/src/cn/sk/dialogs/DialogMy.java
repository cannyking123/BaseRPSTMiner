package cn.sk.dialogs;



import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.acceptingpetrinetminer.miners.DecomposedMiner;
import org.processmining.acceptingpetrinetminer.miners.DecomposedMinerManager;
import org.processmining.causalactivitymatrix.models.CausalActivityMatrix;
import org.processmining.causalactivitymatrix.models.impl.CausalActivityMatrixFactory;
import org.processmining.causalactivitymatrix.modifiers.CausalActivityMatrixModifier;
import org.processmining.causalactivitymatrix.modifiers.impl.CausalActivityMatrixConcurrencyRatioModifier;
import org.processmining.causalactivitymatrix.modifiers.impl.CausalActivityMatrixIncludeThresholdModifier;
import org.processmining.causalactivitymatrix.modifiers.impl.CausalActivityMatrixStackedModifier;
import org.processmining.causalactivitymatrix.modifiers.impl.CausalActivityMatrixZeroValueModifier;
import org.processmining.causalactivitymatrix.parameters.ModifyCausalActivityMatrixParameters;
import org.processmining.causalactivitymatrix.plugins.VisualizeCausalActivityMatrixColoredTablePlugin;
import org.processmining.contexts.uitopia.UIPluginContext;

import com.fluxicon.slickerbox.components.NiceSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class DialogMy extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6769295313371512778L;

	public DialogMy(final UIPluginContext context, final CausalActivityMatrix matrix,
			final ModifyCausalActivityMatrixParameters parameters) {
		double size[][] = { { TableLayoutConstants.FILL}, { 300, 30, 30, 30 ,30, 30} };
		setLayout(new TableLayout(size));

		final CausalActivityMatrix viewMatrix = CausalActivityMatrixFactory.createCausalActivityMatrix();
		viewMatrix.init(matrix.getLabel(), new HashSet<XEventClass>(matrix.getActivities()));
		updateViewMatrix(viewMatrix, matrix, parameters);

		final JPanel panel = new JPanel();
		//panel.setPreferredSize(new Dimension(100, 100));
		double panelSize[][] = { { TableLayoutConstants.FILL }, { TableLayoutConstants.FILL } };
		panel.setLayout(new TableLayout(panelSize));
		add(panel, "0, 0");

		final VisualizeCausalActivityMatrixColoredTablePlugin visualizer = new VisualizeCausalActivityMatrixColoredTablePlugin();
		panel.add(visualizer.runUI(context, viewMatrix), "0, 0");

		final NiceSlider zeroSlider = SlickerFactory.instance().createNiceDoubleSlider("Zero Value", -1.0, 1.0,
				parameters.getZeroValue(), Orientation.HORIZONTAL);
		zeroSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				parameters.setZeroValue(-1.0 + zeroSlider.getSlider().getValue() * 2.0 / 10000.0);
				panel.removeAll();
				updateViewMatrix(viewMatrix, matrix, parameters);
				panel.add(visualizer.runUI(context, viewMatrix), "0, 0");
				revalidate();
				repaint();
			}
		});
		add(zeroSlider, "0, 1");

		final NiceSlider concSlider = SlickerFactory.instance().createNiceDoubleSlider("Concurrency Ratio", 0.0, 2.0,
				parameters.getConcurrencyRatio(), Orientation.HORIZONTAL);
		concSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				parameters.setConcurrencyRatio(concSlider.getSlider().getValue() * 2.0 / 10000.0);
				panel.removeAll();
				updateViewMatrix(viewMatrix, matrix, parameters);
				panel.add(visualizer.runUI(context, viewMatrix), "0, 0");
				revalidate();
				repaint();
			}
		});
		add(concSlider, "0, 2");

		final NiceSlider bestSlider = SlickerFactory.instance().createNiceDoubleSlider("Include Threshold", 0.0, 2.0,
				parameters.getIncludeThreshold(), Orientation.HORIZONTAL);
		bestSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				parameters.setIncludeThreshold(bestSlider.getSlider().getValue() * 2.0 / 10000.0);
				panel.removeAll();
				updateViewMatrix(viewMatrix, matrix, parameters);
				panel.add(visualizer.runUI(context, viewMatrix), "0, 0");
				revalidate();
				repaint();
			}
		});
		add(bestSlider, "0, 3");
		
		final NiceSlider gammer = SlickerFactory.instance().createNiceIntegerSlider("Gammer", 2, 100, 2, Orientation.HORIZONTAL);
		
		
		gammer.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				parameters.setGammer(gammer.getSlider().getValue());
//				panel.removeAll();
//				updateViewMatrix(viewMatrix, matrix, parameters);
//				panel.add(visualizer.runUI(context, viewMatrix), "0, 0");
//				revalidate();
//				repaint();
			}
		});
		add(gammer, "0, 4");
		ArrayList<String> values = new ArrayList<String>();
		int i = 0;
		for (DecomposedMiner miner: DecomposedMinerManager.getInstance().getMiners()) {
			values.add(miner.getName());
		}
		int size1 = values.size();
		String value[] = new String[size1]; 
		
		for(int j = 0;j<size1;j++) {
			value[j] = values.get(j);
		}
		
		
		final JComboBox miner = SlickerFactory.instance().createComboBox(value);
	
		miner.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				String a = (String)arg0.getItem();
				parameters.setMiner(a);
			}
			
		} );
	
		
		add(miner, "0, 5");
		
		
	}

	private void updateViewMatrix(CausalActivityMatrix viewMatrix, CausalActivityMatrix matrix,
			ModifyCausalActivityMatrixParameters parameters) {
		CausalActivityMatrixModifier modifier = new CausalActivityMatrixStackedModifier(
				new CausalActivityMatrixZeroValueModifier(parameters.getZeroValue()),
				new CausalActivityMatrixConcurrencyRatioModifier(parameters.getConcurrencyRatio()),
				new CausalActivityMatrixIncludeThresholdModifier(parameters.getIncludeThreshold()));
		CausalActivityMatrix tmpMatrix = modifier.apply(matrix);
		for (XEventClass rowActivity : matrix.getActivities()) {
			for (XEventClass columnActivity : matrix.getActivities()) {
				viewMatrix.setValue(rowActivity, columnActivity, tmpMatrix.getValue(rowActivity, columnActivity));
			}
		}
	}
}
