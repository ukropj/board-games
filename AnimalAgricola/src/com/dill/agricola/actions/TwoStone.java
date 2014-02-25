package com.dill.agricola.actions;

import com.dill.agricola.model.enums.ActionType;
import com.dill.agricola.model.enums.Material;
import com.dill.agricola.model.enums.Materials;

public class TwoStone extends RefillAction {

	public final static Materials REFILL = new Materials(Material.STONE, 2);
	
	public TwoStone() {
		super(ActionType.TWO_STONE, REFILL);
	}

}
