package com.dill.agricola.model;

import com.dill.agricola.model.types.Animal;


public class Pasture extends Space {

	private final static int CAP = 2;
	private final static int SOLO_FEEDER_CAP = 1;
	
	public Pasture() {
	}
	
	public int getMaxCapacity() {
		if (! isEnclosed()) {
			return hasTrough() ? SOLO_FEEDER_CAP : 0;
		}
		int feeders = getTroughCount();
		return feeders == 0 ? CAP : CAP * feeders * FEEDER_MULTI;
	}
	
	public Animal getRequiredAnimal() {
		return null;
	}

	public boolean isAlwaysEnclosed() {
		return false;
	}

}
