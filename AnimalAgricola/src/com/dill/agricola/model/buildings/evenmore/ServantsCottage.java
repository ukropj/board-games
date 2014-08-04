package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.actions.CompoundAction;
import com.dill.agricola.actions.extra.FreeBorders;
import com.dill.agricola.actions.extra.OneTrough;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class ServantsCottage extends Building {

	public final static Materials COST = new Materials(Material.WOOD, 2);

	public ServantsCottage() {
		super(BuildingType.SERVANTS_COTTAGE, 0, 1,
				CompoundAction.withSubaction(
						new FreeBorders(2),
						new OneTrough(new Materials(Material.WOOD, 2)),
						false));
	}

}
