package com.dill.agricola.actions.farm;

import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.extra.Expand;
import com.dill.agricola.actions.simple.MaterialRefillAction;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;

public class BordersExpand extends MaterialRefillAction {

	public final static Materials REFILL = new Materials(Material.BORDER, 1);
	private final Action expandSubaction = new Expand();

	public BordersExpand() {
		super(ActionType.BORDERS_EXPAND, REFILL);
	}
	
	public Action getSubAction(boolean afterFarmAction) {
		if (!afterFarmAction) {
			return expandSubaction;
		}
		return null;
	}

}
