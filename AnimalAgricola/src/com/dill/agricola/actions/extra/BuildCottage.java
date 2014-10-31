package com.dill.agricola.actions.extra;

import com.dill.agricola.actions.farm.BuildAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.buildings.Cottage;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;

public class BuildCottage extends BuildAction {
	
	public BuildCottage() {
		this(ActionType.COTTAGE, true, true);
	}
	
	public BuildCottage(ActionType type, boolean standardAction, boolean allowOne) {
		super(type, BuildingType.COTTAGE);
	}

	protected boolean isAnyLeft() {
		return true;
	}

	protected Materials getCost(Player player) {
		return Cottage.COST;
	}

	protected Building getBuildingInstance(BuildingType type) {
		return new Cottage();
	}

	public boolean canDoOnFarm(Player player, DirPoint pos) {
		return getUseCount() < 1 && super.canDoOnFarm(player, pos);
	}

}
