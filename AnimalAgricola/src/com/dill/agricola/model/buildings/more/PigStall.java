package com.dill.agricola.model.buildings.more;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class PigStall extends Building {

	public final static Materials COST = new Materials(Material.WOOD, Material.WOOD, Material.WOOD, Material.WOOD, Material.REED);

	public PigStall() {
		super(BuildingType.PIG_STALL, 0, 3, Animal.PIG, new Animals[] { 
				new Animals(Animal.PIG) }, null);
	}

	public float getVictoryPoints(Player player) {
		return player.getAnimal(Animal.PIG) >= 13 ? 4 : 0;
	}

}
