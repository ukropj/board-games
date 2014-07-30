package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.actions.simple.MaterialAction;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class ReedHut extends Building {

	public final static Materials COST = new Materials(Material.WOOD,Material.REED,Material.REED,Material.REED);
	public final static Materials REWARD = new Materials(Material.REED, 3);

	public ReedHut() {
		super(BuildingType.REED_HUT, 0, 3, new MaterialAction(ActionType.BUILDING_REWARD, REWARD));
	}

}
