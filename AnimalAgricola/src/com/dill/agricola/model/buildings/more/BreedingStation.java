package com.dill.agricola.model.buildings.more;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class BreedingStation extends Building {

	public final static Materials COST = new Materials(Material.WOOD, Material.WOOD,
			Material.STONE, Material.STONE, Material.STONE);

	public BreedingStation() {
		super(BuildingType.BREEDING_STATION, 0, 2);
		setActive(true);
	}
	
	// causes extra breeding phase at the end of the game

}
