package com.dill.agricola.model;

import java.util.Collections;
import java.util.Set;

import com.dill.agricola.Game.Phase;
import com.dill.agricola.Main;
import com.dill.agricola.actions.Action;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.support.Namer;

public abstract class Building extends Space {

	private final BuildingType type;
	private final int cap;
	private final int vp;
	private final Animal reqiredAnimal;
	private final Action subAction;

	protected Space buildSpace = null;
	protected Materials paidCost; // actual cost payed for this building instance
	protected DirPoint buildPos;

	public Building(BuildingType type, int vp, int cap) {
		this(type, vp, cap, null, null);
	}

	public Building(BuildingType type, int vp, int cap, Animal reqiredAnimal) {
		this(type, vp, cap, reqiredAnimal, null);
	}
	
	public Building(BuildingType type, int vp, int cap, Action subAction) {
		this(type, vp, cap, null, subAction);
	}
	
	public Building(BuildingType type, int vp, int cap, Animal reqiredAnimal, Action subAction) {
		this.type = type;
		this.vp = vp;
		this.cap = cap;
		this.reqiredAnimal = reqiredAnimal;
		this.subAction = subAction;
	}

	public BuildingType getType() {
		return type;
	}

	public Materials getPaidCost() {
		return paidCost;
	}

	public void setPaidCost(Materials paidCost) {
		this.paidCost = paidCost;
	}

	public float getVictoryPoints(Player player) {
		return vp;
	}

	public int getMaxCapacity() {
		if (!extraAnimalCaps.isEmpty()) {
			return extraAnimalCaps.size();
		}
		return hasTrough() ? cap * FEEDER_MULTI : cap;
	}

	public Set<Animal> getRequiredAnimals() {
		if (reqiredAnimal != null) {
			return Collections.singleton(reqiredAnimal);
		} else {
			return Collections.emptySet();
		}
	}
	
	public Action getSubAction() {
		// action invoked when building is built
		return subAction;
	}

	public Action[] getExtraActions(Phase phase, int round) {
		return null;
	}

	public boolean isAlwaysEnclosed() {
		return true;
	}

	public int getTroughCount() {
		return trough ? 1 : 0;
	}

	public boolean isUsed() {
		return true;
	}

	public boolean canBuildAt(Space space) {
		// override for buildings that can be only upgraded
		return space instanceof Pasture;
	}

	public void buildAt(Space space) {
		Main.asrtNotNull(space, "Cannot build on null space");
		for (int i = 0; i < borders.length; i++) {
			borders[i] = space.borders[i];
		}
		trough = space.trough;
		animalType = space.animalType;
		animalCount.set(space.animalCount);
		buildSpace = space;
	}

	public Space unbuild() {
		if (buildSpace == null) {
			throw new IllegalStateException("Building without building space");
		}
		Space space = buildSpace;
		for (int i = 0; i < borders.length; i++) {
			space.borders[i] = borders[i];
		}
		space.trough = trough;
		space.animalType = animalType;
		space.animalCount.set(animalCount);
		reset();
		return space;
	}

	protected void reset() {
		for (int i = 0; i < borders.length; i++) {
			borders[i] = false;
		}
		buildSpace = null;
		trough = false;
		animalType = null;
		animalCount.clear();
	}

	protected void insertIntermediary(Building intermediary) {
		if (intermediary != null) {
			intermediary.buildSpace = this.buildSpace;
			this.buildSpace = intermediary;
		}
	}

	protected Building removeIntermediary() {
		if (buildSpace != null && buildSpace instanceof Building) {
			Building intermediary = (Building) buildSpace;
			this.buildSpace = intermediary.buildSpace;
			intermediary.reset();
			return intermediary;
		}
		return null;
	}
	
	public void setPosition(DirPoint pos) {
		this.buildPos = pos;
	}

	public String toString() {
		return Namer.getShortName(this) + " " + super.toString();
	}

	public String getShortName() {
		return Namer.getName(this, 2);
	}
}
