package com.dill.agricola.model;


public class Pasture extends Space {

	private final static int CAP = 2;
	private final static int SOLO_FEEDER_CAP = 1;

	public Pasture() {
	}

	public int getMaxCapacity() {
		if (!isEnclosed()) {
			if (!extraAnimalCaps.isEmpty()) {
				return extraAnimalCaps.size();
			}
			return hasTrough() ? SOLO_FEEDER_CAP : 0;
		}
		int troughs = getTroughCount();
		int cap = troughs == 0 ? CAP : CAP * troughs * FEEDER_MULTI;
		if (extraCap) {
			cap++;
		}
		return cap;
	}

	public boolean isAlwaysEnclosed() {
		return false;
	}

}
