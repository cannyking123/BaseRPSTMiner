package org.processmining.acceptingpetrinetminer.miners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.processmining.acceptingpetrinetminer.miners.impl.DecomposedAlphaMiner;
import org.processmining.acceptingpetrinetminer.miners.impl.DecomposedETMiner;
import org.processmining.acceptingpetrinetminer.miners.impl.DecomposedHeuristicsMiner;
import org.processmining.acceptingpetrinetminer.miners.impl.DecomposedHybridILPMiner;
import org.processmining.acceptingpetrinetminer.miners.impl.DecomposedILPMiner;
import org.processmining.acceptingpetrinetminer.miners.impl.DecomposedInductiveMiner;
import org.processmining.acceptingpetrinetminer.miners.impl.DecomposedInductiveMinerOR;
import org.processmining.acceptingpetrinetminer.miners.impl.DecomposedInductiveMinerPerfectFitness;
import org.processmining.acceptingpetrinetminer.miners.impl.DecomposedPrefixHybridILPMiner;
import org.processmining.acceptingpetrinetminer.miners.impl.DecomposedSequenceEncodingHybridILPMiner;
import org.processmining.acceptingpetrinetminer.miners.impl.DecomposedSingleHybridILPMiner;
import org.processmining.acceptingpetrinetminer.miners.impl.DecomposedSplitMiner;
import org.processmining.acceptingpetrinetminer.miners.impl.DecomposedWrappedMiner;

public class DecomposedMinerManager {

	private static DecomposedMinerManager instance = null;
	private List<DecomposedMiner> miners;
	private DecomposedMiner defaultMiner;
	private boolean sorted;

	private DecomposedMinerManager() {
		miners = new ArrayList<DecomposedMiner>();
		defaultMiner = null;
		sorted = false;
		register(new DecomposedWrappedMiner(new DecomposedAlphaMiner()), false);
		register(new DecomposedWrappedMiner(new DecomposedHeuristicsMiner()), false);
		register(new DecomposedWrappedMiner(new DecomposedILPMiner()), false);
		register(new DecomposedWrappedMiner(new DecomposedHybridILPMiner()), false);
		register(new DecomposedWrappedMiner(new DecomposedSingleHybridILPMiner()), false);
		register(new DecomposedWrappedMiner(new DecomposedPrefixHybridILPMiner()), false);
		register(new DecomposedWrappedMiner(new DecomposedSequenceEncodingHybridILPMiner()), false);
		register(new DecomposedWrappedMiner(new DecomposedInductiveMiner()), true);
		register(new DecomposedWrappedMiner(new DecomposedInductiveMinerPerfectFitness()), false);
		register(new DecomposedWrappedMiner(new DecomposedInductiveMinerOR()), false);
		register(new DecomposedWrappedMiner(new DecomposedETMiner()), false);
		register(new DecomposedWrappedMiner(new DecomposedSplitMiner()), false);
	}

	public static DecomposedMinerManager getInstance() {
		if (instance == null) {
			instance = new DecomposedMinerManager();
		}
		return instance;
	}
	
	public void register(DecomposedMiner miner, boolean isDefault) {
		miners.add(miner);
		if (isDefault) {
			defaultMiner = miner;
		}
		sorted = false;
	}

	public List<DecomposedMiner> getMiners() {
		if (!sorted) {
			Collections.sort(miners, new Comparator<DecomposedMiner>() {
				public int compare(DecomposedMiner m1, DecomposedMiner m2) {
					return m1.getName().compareTo(m2.getName());
				}
			});
			sorted = true;
		}
		return miners;
	}
	
	public boolean isDefault(DecomposedMiner miner) {
		return miner == defaultMiner;
	}
	
	public DecomposedMiner getMiner(String name) {
		if (name != null) {
			for (DecomposedMiner miner : miners) {
				if (name.equals(miner.getName())) {
					return miner;
				}
			}
		}
		return defaultMiner;
	}
}
