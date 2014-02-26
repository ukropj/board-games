package com.dill.agricola.actions;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.support.Namer;

public class Millpond extends AnimalAction {

	public static final Materials REFILL = new Materials(Material.REED, 1);

	public static final Animal OTHER_ANIMAL = Animal.SHEEP;
	
	private final Materials materials = new Materials();
	private final Materials lastTakenMaterials = new Materials();
	
	public Millpond() {
		super(ActionType.MILLPOND, null, OTHER_ANIMAL);
	}
	
	public void reset() {
		super.reset();
		materials.clear();
	}
	
	public void init() {
		used = false;
		if (materials.isEmpty()) {
			materials.add(REFILL);
		} else {
			animals.add(other, 1);
		}
		lastTakenAnimals.clear();
		lastTakenMaterials.clear();
		setChanged();
	}

	public boolean doOnce(Player player) {
		super.doOnce(player);
		player.addMaterial(materials);
		lastTakenMaterials.set(materials);
		materials.clear();
		return true;
	}

	public boolean undoOnce(Player player) {
		boolean done = super.undoOnce(player);
		if (done) {
			player.removeMaterial(lastTakenMaterials);
			materials.set(lastTakenMaterials);
			lastTakenMaterials.clear();			
		}
		return done;
	}
	
	public Materials getAccumulatedMaterials() {
		return materials;
	}
	
	public String toString() {
		return "<html>" + Namer.getName(this) + "<br>+" + materials + animals;
	}
}
