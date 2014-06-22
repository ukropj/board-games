package com.dill.agricola.model.buildings.more;

import com.dill.agricola.Game.Phase;
import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.extra.OneBorder;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class Ranch extends Building {

	public final static Materials COST = new Materials(Material.WOOD, Material.WOOD, 
			Material.STONE, Material.STONE, Material.STONE);
	private final static Action EXTRA_ACTION = new OneBorder();
	
	public Ranch() {
		super(BuildingType.RANCH, 2, 2, Animal.HORSE);
	}

	// grants one free fence action during each breeding phase when horse is born
	public Action getExtraAction(Phase phase, int round) {
		return phase == Phase.BEFORE_BREEDING ? EXTRA_ACTION : null;
	}
}
