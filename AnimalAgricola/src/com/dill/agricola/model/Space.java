package com.dill.agricola.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Dir;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;

public abstract class Space {

	protected final static int FEEDER_MULTI = 2;

	protected final boolean[] borders = new boolean[Dir.values().length];

	protected boolean trough = false;
	protected int enclosedFeeders = 0;

	protected Animal animalType = null;
	protected Animals animalCount = new Animals();

	protected boolean enclosed = false;
	private Set<Animal> animalTypesPerPasture = new HashSet<Animal>();
	private Set<Space> pastureSpaces = new HashSet<Space>();
	protected Animals extraAnimalCaps = new Animals();
	protected boolean extraCap;

	public Space() {
		if (isAlwaysEnclosed()) {
			enclosed = true;
		}
	}
	
	public BuildingType getType() {
		return BuildingType.EMPTY;
	}

	public boolean setBorder(Dir d, boolean add) {
		boolean orig = borders[d.ordinal()];
		if (orig != add) {
			borders[d.ordinal()] = add;
			return true;
		}
		return false;
	}

	public boolean hasBorder(Dir d) {
		return borders[d.ordinal()];
	}

	public abstract boolean isAlwaysEnclosed();

	public Animal getAnimalType() {
		return animalType;
	}

	public Set<Animal> getAnimalTypes() {
		return animalCount.types();
	}

	public void addAnimals(Animal type, int count) {
		animalCount.add(type, count);
		if (animalCount.size() <= 0) {
			animalType = null;
		} else {
			animalType = type;
		}
	}

	public int getAnimals() {
		return animalCount.size();
	}

	public int getAnimals(Animal type) {
		return animalCount.get(type);
	}

	public boolean isEnclosed() {
		return enclosed;
	}

	public void setTrough(boolean add) {
		this.trough = add;
	}

	public boolean hasTrough() {
		return trough;
	}

	public int getTroughCount() {
		return enclosedFeeders;
	}

	public abstract int getMaxCapacity();
	
	public int getMaxCapacity(Animal type) {
		if (extraAnimalCaps.isEmpty()) {
			return getMaxCapacity();
		} else {
			return extraAnimalCaps.get(type);
		}
	}
	
	public Set<Animal> getRequiredAnimals() {
		if (extraAnimalCaps.isEmpty()) {
			return Collections.emptySet();
		} else {
			return new TreeSet<Animal>(extraAnimalCaps.types());
		}
	}

	public int getActualCapacity(Animal type) {
		Set<Animal> req = getRequiredAnimals();
		if (req.isEmpty()) {
			return (animalType == null || animalType == type) ? Math.max(0, getMaxCapacity(type) - getAnimals(type)) : 0;
		} else {
			return req.contains(type) ? Math.max(0, getMaxCapacity(type) - getAnimals(type)) : 0;			
		}
	}

	public boolean isUsed() {
		return hasTrough() || isEnclosed();
	}

	public String toString() {
		return "A:" + getAnimals() + "/" + getMaxCapacity() + (trough ? " F" : "");
	}

	public Set<Animal> getAnimalTypesPerPasture() {
		return animalTypesPerPasture;
	}

	public boolean isValid() {
		int extraAnimals = 0;
		for (Animal a : getAnimalTypes()) {
			extraAnimals += Math.max(0, getAnimals(a) - getMaxCapacity(a));
		}
		int animalTypes = animalTypesPerPasture.size();
		return extraAnimals == 0 && animalTypes <= 1;
	}
	
	public boolean hasExtraCapacity() {
		return !extraAnimalCaps.isEmpty();
	}
	
	public void clearExtraCapacity() {
		this.extraAnimalCaps.clear();
	}
	
	public void addExtraCapacity(Animals animalCombo) {
		if (animalCombo != null) {
			this.extraAnimalCaps.add(animalCombo);			
		}
	}

	public void setPasture(List<Space> spaces) {
		pastureSpaces.clear();
		if (spaces != null) {
			pastureSpaces.addAll(spaces);
		}
		// update enclosed
		enclosed = isAlwaysEnclosed() || spaces != null;

		enclosedFeeders = 0;
		animalTypesPerPasture.clear();
		for (Space space : pastureSpaces) {
			// update feeder count
			enclosedFeeders += space.hasTrough() ? 1 : 0;
			if (space.getAnimals() > 0) {
				// update animalTypes
				animalTypesPerPasture.add(space.getAnimalType());
			}
		}
	}
	
	public Set<Space> getPastureSpaces() {
		return pastureSpaces;
	}
	
	public int getAnimalsPerPasture() {
		int count = 0;
		for (Space space : pastureSpaces) {
			count += space.getAnimals();
		}
		return count;
	}
	
	public void setExtraCap(boolean extraCap) {
		this.extraCap = extraCap;
	}


}
