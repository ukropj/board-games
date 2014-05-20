package com.dill.agricola.actions.extra;

import com.dill.agricola.actions.farm.PurchaseAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.Purchasable;

public class OneBorder extends PurchaseAction {

	public final static Materials COST = new Materials(Material.BORDER);

	public OneBorder() {
		super(ActionType.ONE_BORDER, Purchasable.FENCE);
	}

	protected Materials getCost(Player player) {
		return COST;
	}

	public boolean canDo(Player player) {
		return isAnyLeft() && player.getAnimal(Animal.HORSE) >= 2 && player.canPurchase(thing, getCost(player), null);
	}

	public boolean canDoOnFarm(Player player, DirPoint pos) {
		// must have at least 2 horses
		return getUseCount() < 1 && player.getAnimal(Animal.HORSE) >= 2 && super.canDoOnFarm(player, pos);
	}

	public boolean isUsedEnough() {
		// optional
		return true;
	}

}
