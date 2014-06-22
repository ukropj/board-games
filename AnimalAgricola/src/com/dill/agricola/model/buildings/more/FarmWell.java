package com.dill.agricola.model.buildings.more;

import com.dill.agricola.Game;
import com.dill.agricola.Game.Phase;
import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.extra.OneTrough;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class FarmWell extends Building {

	public final static Materials COST = new Materials(Material.WOOD, Material.STONE, Material.STONE);
	private final static Action EXTRA_ACTION = new OneTrough(new Materials());

	public FarmWell() {
		super(BuildingType.FARM_WELL, 0, 0);
	}

	// grants free feeding trough before last 3 rounds
	public Action getExtraAction(Phase phase, int round) {
		return phase == Phase.BEFORE_WORK && round > Game.ROUNDS - 3 ? EXTRA_ACTION : null;
	}
}
