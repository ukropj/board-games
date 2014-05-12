package com.dill.agricola.model.buildings.more;

import com.dill.agricola.actions.farm.Fences;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class FenceManufacturer extends Building {

	public final static Materials COST = new Materials(Material.STONE, Material.REED);

	public FenceManufacturer() {
		super(BuildingType.FENCE_MANUFACTURER, 0, 1, new Fences());
	}

}
