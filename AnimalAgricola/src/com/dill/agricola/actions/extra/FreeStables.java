package com.dill.agricola.actions.extra;

import com.dill.agricola.actions.farm.BuildAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.buildings.Stables;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.undo.UndoableFarmEdit;

public class FreeStables extends BuildAction {

	private final static Materials COST = new Materials();
	
	private DirPoint posToBuild = null;

	public FreeStables() {
		super(ActionType.FREE_STABLES, BuildingType.STABLES);
	}
	
	public UndoableFarmEdit init() {
		// dont reset use count
		return null;
	}

	protected Materials getCost(Player player) {
		return COST;
	}
	
	public boolean canDo(Player player) {
		return !isUsed() && posToBuild != null && super.canDo(player);
	}
	
	public boolean canDoOnFarm(Player player) {
		return false;
	}

	protected Building getBuildingInstance(BuildingType type) {
		return new Stables();
	}
	
	public UndoableFarmEdit doo(Player player) {
		if (canDo(player)) {
			return doOnFarm(player, posToBuild);			
		}
		return null;
	}	

	public void setPosToBuild(DirPoint pos) {
		posToBuild = pos;
	}
	
	public boolean hasPosToBuild() {
		return posToBuild != null;
	}

}
