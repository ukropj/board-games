package com.dill.agricola.actions;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;

public class ThreeWood extends RefillAction {

	public final static Materials REFILL = new Materials(Material.WOOD, 3);
	
	public ThreeWood() {
		super(ActionType.THREE_WOOD, REFILL);
	}

}
