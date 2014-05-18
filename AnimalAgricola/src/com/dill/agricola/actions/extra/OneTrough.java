package com.dill.agricola.actions.extra;

import com.dill.agricola.actions.farm.Troughs;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;

public class OneTrough extends Troughs {

	public final Materials cost;

	public OneTrough(Materials cost) {
		super(ActionType.ONE_TROUGH);
		this.cost=  cost;
	}

	protected Materials getCost(Player player) {
		return cost;
	}

	public boolean canDo(Player player) {
		return isAnyLeft() && player.canPurchase(thing, getCost(player), null);
	}

	public boolean canDoOnFarm(Player player, DirPoint pos) {
		return getUseCount() < 1 && super.canDoOnFarm(player, pos);
	}
	
	public boolean isUsedEnough() {
		// optional
		return true;
	}

}
