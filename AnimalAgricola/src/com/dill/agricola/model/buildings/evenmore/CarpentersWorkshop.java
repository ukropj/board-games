package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class CarpentersWorkshop extends Building {

	public final static Materials COST = new Materials(Material.WOOD, Material.WOOD, Material.STONE);

	public CarpentersWorkshop() {
		super(BuildingType.CARPENTERS_WORKSHOP, 2, 1);
		setActive(true);
	}

	// when you carry out 'Stalls' action, build 2 fences for free
}
