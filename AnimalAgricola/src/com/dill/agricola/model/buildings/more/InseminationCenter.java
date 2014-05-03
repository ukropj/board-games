package com.dill.agricola.model.buildings.more;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class InseminationCenter extends Building {

	public final static Materials COST = new Materials(Material.STONE, Material.STONE);

	public InseminationCenter() {
		super(BuildingType.INSEMINATION_CENTER, 0, 0);
	}

	// allows breeding even with 1 animal
}
