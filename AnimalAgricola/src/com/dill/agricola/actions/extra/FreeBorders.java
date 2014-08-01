package com.dill.agricola.actions.extra;

import com.dill.agricola.actions.farm.PurchaseAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.Purchasable;

public class FreeBorders extends PurchaseAction {

	public final static Materials COST = new Materials(Material.BORDER);

	protected final int count;
	
	public FreeBorders(int count) {
		super(ActionType.FREE_BORDERS, Purchasable.FENCE);
		this.count = count;
	}

	protected Materials getCost(Player player) {
		return COST;
	}

	public boolean canDoOnFarm(Player player, DirPoint pos) {
		return getUseCount() < count && super.canDoOnFarm(player, pos);
	}

	public boolean isUsedEnough() {
		// optional
		return true;
	}

}
