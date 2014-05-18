package com.dill.agricola.model.buildings.more;

import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.extra.OneTrough;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class Joinery extends Building {

	public final static Materials COST = new Materials(Material.STONE, Material.REED);
	private final static Action EXTRA_ACTION = new OneTrough(new Materials(Material.WOOD, 2));

	public Joinery() {
		super(BuildingType.JOINERY, 0, 0);
	}

	// grants 1 feeding trough for 2 wood before each breeding phase
	public Action getBeforeBreedingAction() {
		return EXTRA_ACTION;
	}
}
