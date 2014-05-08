package com.dill.agricola.actions.simple;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;

public class BuildingMaterial extends MaterialAction {

	public static final Materials MATERIALS = new Materials(Material.WOOD, Material.STONE, Material.REED);

	public BuildingMaterial() {
		super(ActionType.BUILDING_MATERIAL, MATERIALS);
	}

}
