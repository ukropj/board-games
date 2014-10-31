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

public class PigStall extends Building {

	public final static Materials COST = new Materials(Material.WOOD, Material.WOOD, Material.WOOD, Material.WOOD, Material.REED);
	private final static Animals REWARD = new Animals(Animal.PIG);
	
	public PigStall() {
		super(BuildingType.PIG_STALL, 0, 3, Animal.PIG, new AnimalAction(ActionType.BUILDING_REWARD, REWARD));
		setActive(true);
	}

	// 4 points if at least 13 pigs
	public float getVictoryPoints(Player player) {
		return player.getAnimal(Animal.PIG) >= 13 ? 4 : 0;
	}

}
