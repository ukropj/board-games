package com.dill.agricola.model.buildings.more;

import com.dill.agricola.Game.Phase;
import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.extra.OneTrough;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class Joinery extends Building {

	public final static Materials COST = new Materials(Material.STONE, Material.REED);
	private final static Action[] EXTRA_ACTIONS = { new OneTrough(new Materials(Material.WOOD, 2)) };

	public Joinery() {
		super(BuildingType.JOINERY, 0, 0);
		setActive(true);
	}

	// grants 1 feeding trough for 2 wood before each breeding phase
	public Action[] getExtraActions(Phase phase, int round) {
		return phase == Phase.BEFORE_BREEDING ? EXTRA_ACTIONS : null;
	}
}
