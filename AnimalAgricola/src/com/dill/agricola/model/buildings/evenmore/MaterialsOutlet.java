package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.actions.simple.MaterialAction;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class MaterialsOutlet extends Building {

	public final static Materials COST = new Materials(Material.REED);
	public final static Materials REWARD = new Materials(
			Material.WOOD, Material.WOOD, Material.WOOD, Material.WOOD,
			Material.STONE, Material.STONE, Material.STONE);

	public MaterialsOutlet() {
		super(BuildingType.MATERIALS_OUTLET, 7, 1, Animal.HORSE, new MaterialAction(ActionType.BUILDING_REWARD, REWARD));
	}

}
