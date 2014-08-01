package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.actions.extra.AddAnimalPerPasture;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class OrganicFarm extends Building {

	public final static Materials COST = new Materials(
			Material.WOOD, Material.WOOD,
			Material.STONE, Material.STONE, Material.STONE,
			Material.REED);
	

	public OrganicFarm() {
		super(BuildingType.ORGANIC_FARM, 4, 0, new AddAnimalPerPasture(ActionType.BUILDING_REWARD));
	}
}
