package com.dill.agricola.model.buildings.more;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class Sawmill extends Building {

	public final static Materials COST = new Materials(Material.STONE, 5);

	public Sawmill() {
		super(BuildingType.SAWMILL, 4, 0);
		setActive(true);
	}

	// buildings and troughs cost one wood less
}
