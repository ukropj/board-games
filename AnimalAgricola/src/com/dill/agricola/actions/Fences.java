package com.dill.agricola.actions;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.Purchasable;

public class Fences extends PurchaseAction {

	public final static Materials COST = new Materials(Material.BORDER, Material.WOOD);
	
	public Fences() {
		super(ActionType.FENCES, Purchasable.FENCE, COST, true);
	}
	
	public String toString() {
		return super.toString() + "<br>unlimited 1 for 1 WOOD";
	}

}
