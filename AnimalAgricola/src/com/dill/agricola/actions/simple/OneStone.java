package com.dill.agricola.actions.simple;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;

public class OneStone extends MaterialRefillAction {
	
	public final static Materials REFILL = new Materials(Material.STONE, 1);

	public OneStone() {
		super(ActionType.ONE_STONE, REFILL, true);
	}

}
