package org.processmining.decomposedminer.configurations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.processmining.decomposedminer.configurations.impl.DecomposedClusteringDiscoveryConfiguration;

public class DecomposedDiscoveryConfigurationManager {

	private static DecomposedDiscoveryConfigurationManager instance = null;
	private List<DecomposedDiscoveryConfiguration> configurations;
	private DecomposedDiscoveryConfiguration defaultConfiguration;
	private boolean sorted;

	private DecomposedDiscoveryConfigurationManager() {
		configurations = new ArrayList<DecomposedDiscoveryConfiguration>();
		defaultConfiguration = null;
		sorted = false;

		register(new DecomposedClusteringDiscoveryConfiguration(), true);


	}

	public static DecomposedDiscoveryConfigurationManager getInstance() {
		if (instance == null) {
			instance = new DecomposedDiscoveryConfigurationManager();
		}
		return instance;
	}
	
	public void register(DecomposedDiscoveryConfiguration Configuration, boolean isDefault) {
		configurations.add(Configuration);
		if (isDefault) {
			defaultConfiguration = Configuration;
		}
		sorted = false;
	}

	public List<DecomposedDiscoveryConfiguration> getConfigurations() {
		if (!sorted) {
			Collections.sort(configurations, new Comparator<DecomposedDiscoveryConfiguration>() {
				public int compare(DecomposedDiscoveryConfiguration m1, DecomposedDiscoveryConfiguration m2) {
					return m1.getName().compareTo(m2.getName());
				}
			});
			sorted = true;
		}
		return configurations;
	}
	
	public boolean isDefault(DecomposedDiscoveryConfiguration Configuration) {
		return Configuration == defaultConfiguration;
	}
	
	public DecomposedDiscoveryConfiguration getConfiguration(String name) {
		if (name != null) {
			for (DecomposedDiscoveryConfiguration configuration : configurations) {
				if (name.equals(configuration.getName())) {
					return configuration;
				}
			}
		}
		return defaultConfiguration;
	}
}
