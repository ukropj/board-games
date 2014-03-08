package com.dill.agricola.actions.farm;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.Purchasable;

public class Walls extends PurchaseAction {

	public final static Materials FIRST_COST = new Materials(Material.BORDER);
	public final static Materials COST = new Materials(Material.BORDER, Material.STONE, Material.STONE);
	
	public Walls() {
		super(ActionType.WALLS, Purchasable.FENCE);
	}
	
	protected Materials getCost(int doneSoFar) {
		return doneSoFar < 2 ? FIRST_COST : COST;
	}

}
