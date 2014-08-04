package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.Game.Phase;
import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.extra.TradeMaterials;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class TradingStation extends Building {

	public final static Materials COST = new Materials(
			Material.WOOD, Material.STONE, Material.STONE);
	private final static Action[] EXTRA_ACTIONS = { new TradeMaterials() };
	
	public TradingStation() {
		super(BuildingType.TRADING_STATION, 1, 1, Animal.COW);
	}

	// allows trading 2 different animal for 1 other animal
	public Action[] getExtraActions(Phase phase, int round) {
		return phase == Phase.BEFORE_BREEDING ? EXTRA_ACTIONS : null;
	}
}
