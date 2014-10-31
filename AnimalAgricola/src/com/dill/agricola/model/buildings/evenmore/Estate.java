package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.actions.simple.AnimalAction;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class Estate extends Building {

	public final static Materials COST = new Materials(Material.STONE, Material.STONE, Material.REED);
	private final static Animals REWARD = new Animals(Animal.HORSE);
	
	public Estate() {
		super(BuildingType.ESTATE, 0, 2, Animal.HORSE, new AnimalAction(ActionType.BUILDING_REWARD, REWARD));
		setActive(true);
	}

	// 3 points if at least 5 special buildings
	public float getVictoryPoints(Player player) {
		int count = 0;
		for (Building b : player.farm.getFarmBuildings()) {
			if (b.getType().set > 0) {
				count++;
			}
		}
		return count >= 5 ? 3 : 0;
	}
}
