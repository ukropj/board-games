package com.dill.agricola.model.buildings.more;

import com.dill.agricola.actions.simple.AnimalAction;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class CowStall extends Building {

	public final static Materials COST = new Materials(Material.WOOD, Material.WOOD, Material.WOOD, Material.WOOD, Material.REED);
	private final static Animals REWARD = new Animals(Animal.COW);
	
	public CowStall() {
		super(BuildingType.COW_STALL, 0, 3, Animal.COW, new AnimalAction(ActionType.BUILDING_REWARD, REWARD));
	}

	// 4 points if at least 11 cows
	public float getVictoryPoints(Player player) {
		return player.getAnimal(Animal.COW) >= 11 ? 4 : 0;
	}
}
