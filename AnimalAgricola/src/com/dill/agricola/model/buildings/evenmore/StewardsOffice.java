package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class StewardsOffice extends Building {

	public final static Materials COST = new Materials(
			Material.WOOD, Material.WOOD, Material.WOOD, Material.WOOD,
			Material.STONE, Material.STONE, Material.STONE,
			Material.REED, Material.REED);

	public StewardsOffice() {
		super(BuildingType.STEWARDS_OFFICE, -3, 1);
		setActive(true);
	}
	
	// gives 1 extra worker

}
