package com.dill.agricola.actions.extra;

import com.dill.agricola.actions.farm.Troughs;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;

public class OneFreeTrough extends Troughs {

	public final static Materials COST = new Materials();

	public OneFreeTrough() {
		super(ActionType.ONE_FREE_TROUGH);
	}

	protected Materials getCost(Player player) {
		return COST;
	}

	public boolean canDo(Player player) {
		return isAnyLeft() && player.canPurchase(thing, getCost(player), null);
	}

	public boolean canDoOnFarm(Player player, DirPoint pos) {
		return getUseCount() < 1 && super.canDoOnFarm(player, pos);
	}

}
