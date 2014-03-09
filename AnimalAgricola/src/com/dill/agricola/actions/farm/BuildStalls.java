package com.dill.agricola.actions.farm;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.GeneralSupply.Supplyable;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.buildings.Stall;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;

public class BuildStalls extends BuildAction {

	public BuildStalls() {
		super(ActionType.STALLS, BuildingType.STALL);
	}

	protected boolean isAnyLeft() {
		return GeneralSupply.getLeft(Supplyable.STALL) > 0;
	}
	
	protected Materials getCost(int doneSoFar) {
		return Stall.COST;
	}
	
	protected Building getBuildingInstance(BuildingType type) {
		return GeneralSupply.useStall();
	}
	
	public boolean canDo(Player player, DirPoint pos, int doneSoFar) {
		return doneSoFar < 1 && super.canDo(player, pos, doneSoFar);
	}
	
	protected void postUndo(Player player, Building b) {
		GeneralSupply.unuseStall((Stall) b);
	}

}
