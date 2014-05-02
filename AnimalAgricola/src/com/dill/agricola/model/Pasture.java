package com.dill.agricola.model;

import java.util.HashSet;
import java.util.Set;

import com.dill.agricola.common.Animals;
import com.dill.agricola.model.types.Animal;

public class Pasture extends Space {

	private final static int CAP = 2;
	private final static int SOLO_FEEDER_CAP = 1;

	public Pasture() {
	}

	public int getMaxCapacity() {
		if (extraAnimalCaps.isEmpty()) {
			if (!isEnclosed()) {
				return hasTrough() ? SOLO_FEEDER_CAP : 0;
			}
			int feeders = getTroughCount();
			return feeders == 0 ? CAP : CAP * feeders * FEEDER_MULTI;
		} else {
			int cap = 0;
			for (Animals combo : extraAnimalCaps) {
				cap = Math.max(cap, combo.size());
			}
			return cap;
		}
	}

	public Set<Animal> getRequiredAnimals() {
		if (extraAnimalCaps.isEmpty()) {
			return null;
		} else {
			Set<Animal> req = new HashSet<Animal>();
			for (Animals combo : extraAnimalCaps) {
				req.addAll(combo.types());
			}
			return req;
		}
	}

	public boolean isAlwaysEnclosed() {
		return false;
	}

}
