package com.dill.agricola.actions;

import com.dill.agricola.model.enums.ActionType;
import com.dill.agricola.model.enums.Material;
import com.dill.agricola.model.enums.Materials;

public class OneStone extends RefillAction {
	
	public final static Materials REFILL = new Materials(Material.STONE, 1);

	public OneStone() {
		super(ActionType.ONE_STONE, REFILL);
	}

}
