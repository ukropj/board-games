package com.dill.agricola.actions;

import com.dill.agricola.model.enums.ActionType;
import com.dill.agricola.model.enums.Material;
import com.dill.agricola.model.enums.Materials;
import com.dill.agricola.model.enums.Purchasable;

public class Fences extends PurchaseAction {

	public final static Materials COST = new Materials(Material.BORDER, Material.WOOD);
	
	public Fences() {
		super(ActionType.FENCES, Purchasable.FENCE, COST, true);
	}
	
	public String toString() {
		return super.toString() + "<br>unlimited 1 for 1 WOOD";
	}

}
