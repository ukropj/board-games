package com.dill.agricola.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dill.agricola.Main;
import com.dill.agricola.common.Dir;
import com.dill.agricola.model.types.Animal;

public abstract class Space {

	protected final static int FEEDER_MULTI = 2;

	protected final boolean[] borders = new boolean[Dir.values().length];

	protected boolean trough = false;
	protected int enclosedFeeders = 0;

	protected Animal animalType = null;
	protected int animals = 0;

	protected boolean enclosed = false;
	private Set<Animal> animalTypesPerPasture = new HashSet<Animal>();
	private Set<Space> pastureSpaces = new HashSet<Space>();

	public Space() {
		if (isAlwaysEnclosed()) {
			enclosed = true;
		}
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

	public void setAnimalType(Animal type) {
		animalType = type;
	}

	public Animal getAnimalType() {
		return animalType;
	}

	public void addAnimals(int count) {
		animals += count;
		if (animals <= 0) {
			animalType = null;
		} else {
			Main.asrtNotNull(animalType, "Cannot have animals without type");
		}
	}

	public int getAnimals() {
		return animals;
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

	public int getActualCapacity(Animal type) {
		return animalType == null || animalType == type ? Math.max(0, getMaxCapacity() - getAnimals()) : 0;
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
		int extraAnimals = Math.max(0, getAnimals() - getMaxCapacity());
		int animalTypes = animalTypesPerPasture.size();
		return extraAnimals == 0 && animalTypes <= 1;
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

}
