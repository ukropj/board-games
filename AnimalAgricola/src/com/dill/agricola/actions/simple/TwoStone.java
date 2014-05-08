package com.dill.agricola.actions.simple;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;

public class TwoStone extends MaterialRefillAction {

	public final static Materials REFILL = new Materials(Material.STONE, 2);
	
	public TwoStone() {
		super(ActionType.TWO_STONE, REFILL);
	}

}
