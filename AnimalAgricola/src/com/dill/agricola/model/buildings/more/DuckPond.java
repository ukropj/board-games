package com.dill.agricola.model.buildings.more;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class DuckPond extends Building {

	public final static Materials COST = new Materials();

	public DuckPond() {
		super(BuildingType.DUCK_POND, 1, 0, null, new Materials[] { new Materials(Material.REED) });
	}

}
