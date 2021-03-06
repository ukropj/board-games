package com.dill.agricola.model.buildings.more;

import com.dill.agricola.actions.simple.MaterialAction;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class DuckPond extends Building {

	// requires 6 empty spaces
	public final static Materials COST = Materials.EMPTY;
	public final static Materials REWARD = new Materials(Material.REED);

	public DuckPond() {
		super(BuildingType.DUCK_POND, 1, 0, new MaterialAction(ActionType.BUILDING_REWARD, REWARD));
	}

}
