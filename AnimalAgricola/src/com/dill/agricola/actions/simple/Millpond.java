package com.dill.agricola.actions.simple;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.Material;

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
		setChanged();
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

	public boolean doo(Player player, int doneSoFar) {
		player.addMaterial(materials);
		lastTakenMaterials.set(materials);
		materials.clear();
		return super.doo(player, doneSoFar);
	}

	public boolean undo(Player player, int doneSoFar) {
		boolean done = super.undo(player, doneSoFar);
		if (done) {
			player.removeMaterial(lastTakenMaterials);
			materials.set(lastTakenMaterials);
			lastTakenMaterials.clear();
			setChanged();
		}
		return done;
	}
	
	public Materials getAccumulatedMaterials() {
		return materials;
	}
	
}
