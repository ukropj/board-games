package com.dill.agricola.actions.extra;

import com.dill.agricola.actions.farm.BuildAction;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.buildings.Stables;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;

public class FreeStables extends BuildAction {

	private final static Materials COST = new Materials();

	public FreeStables() {
		super(ActionType.FREE_STABLES, BuildingType.STABLES);
	}

	protected Materials getCost(Player player) {
		return COST;
	}

	protected Building getBuildingInstance(BuildingType type) {
		return new Stables();
	}

}
