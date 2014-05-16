package com.dill.agricola.model.buildings.more;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class CattleFarm extends Building {

	public final static Materials COST = new Materials(Material.WOOD, Material.WOOD,
			Material.STONE, Material.STONE);

	public CattleFarm() {
		super(BuildingType.CATTLE_FARM, 1, 3, Animal.COW);
	}

}
