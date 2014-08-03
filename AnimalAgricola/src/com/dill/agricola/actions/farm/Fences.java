package com.dill.agricola.actions.farm;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.Purchasable;

public class Fences extends PurchaseAction {

	public final static Materials COST = new Materials(Material.BORDER, Material.WOOD);
	
	public Fences() {
		super(ActionType.FENCES, Purchasable.FENCE);
	}

	protected Materials getCost(Player player) {
		return COST;
	}

	public boolean isUsedEnough() {
		// optional when as subaction 
		return getLevel() > 0 ? true : super.isUsedEnough();
	}
	
}
