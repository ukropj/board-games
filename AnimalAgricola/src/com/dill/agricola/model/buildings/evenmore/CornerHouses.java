package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class CornerHouses extends Building {

	public final static Materials COST = new Materials(Material.STONE, Material.STONE, Material.REED);

	public CornerHouses() {
		super(BuildingType.CORNER_HOUSES, 4, 3);
	}

	// you can no longer expand farm
}
