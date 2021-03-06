package com.dill.agricola.model.buildings.more;

import com.dill.agricola.Game.Phase;
import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.farm.BuildStall;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class HomeWorkshop extends Building {

	public final static Materials COST = new Materials(Material.WOOD, Material.WOOD, Material.REED);
	private final static Action[] EXTRA_ACTIONS = { new BuildStall() {
		public boolean isUsedEnough() {
			// optional
			return true;
		};
	} };

	public HomeWorkshop() {
		super(BuildingType.HOME_WORKSHOP, 1, 0);
		setActive(true);
	}

	// grants Stall action before each breeding phase
	public Action[] getExtraActions(Phase phase, int round) {
		return phase == Phase.BEFORE_BREEDING ? EXTRA_ACTIONS : null;
	}
}
