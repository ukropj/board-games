package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.extra.FreeBorders;
import com.dill.agricola.actions.extra.OneTrough;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class ServantsCottage extends Building {

	public final static Materials COST = new Materials(Material.WOOD, 2);

	private final static Action subaction = new OneTrough(new Materials(Material.WOOD, 2));
	static {
		subaction.setSubAction(new FreeBorders(2), false);		
	}

	public ServantsCottage() {
		super(BuildingType.SERVANTS_COTTAGE, 0, 1, subaction);
	}
	
}
