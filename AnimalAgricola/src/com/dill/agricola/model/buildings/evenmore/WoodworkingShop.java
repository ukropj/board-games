package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class WoodworkingShop extends Building {

	public final static Materials COST = new Materials(
			Material.WOOD, Material.WOOD, Material.STONE, Material.STONE);

	public WoodworkingShop() {
		super(BuildingType.WOODWORKING_SHOP, 1, 2);
	}

	// build any new fences for free
}
