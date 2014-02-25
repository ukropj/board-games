package com.dill.agricola.actions;

import com.dill.agricola.model.enums.ActionType;
import com.dill.agricola.model.enums.Material;
import com.dill.agricola.model.enums.Materials;

public class ThreeWood extends RefillAction {

	public final static Materials REFILL = new Materials(Material.WOOD, 3);
	
	public ThreeWood() {
		super(ActionType.THREE_WOOD, REFILL);
	}

}
