package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.Game.Phase;
import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.extra.CarveStone;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class StoneCarversWorkshop extends Building {

	public final static Materials COST = new Materials(Material.WOOD, 5);
	public final static CarveStone CARVE_STONE = new CarveStone();
	public final static Action[] EXTRA_ACTIONS = { CARVE_STONE };

	public StoneCarversWorkshop() {
		super(BuildingType.STONE_CARVERS_WORKSHOP, 0, 0);
		CARVE_STONE.reset();
	}

	public Action[] getExtraActions(Phase phase, int round) {
		return phase == Phase.BEFORE_BREEDING ? EXTRA_ACTIONS : null;
	}

	public float getVictoryPoints(Player player) {
		int stones = CARVE_STONE.getCarvedStones();
		return stones * 2;
	}

}
