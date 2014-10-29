package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.actions.FeatureAction;
import com.dill.agricola.actions.extra.TradeReed;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class CattleMarket extends Building {

	public final static Materials COST = new Materials(
			Material.WOOD, Material.WOOD, Material.WOOD, Material.WOOD, Material.STONE);
	public final static FeatureAction TRADE_REED = new TradeReed();

	public CattleMarket() {
		super(BuildingType.CATTLE_MARKET, 2, 2);
	}

	// allows trading 2 different animal for 1 other animal
	public FeatureAction getFeatureAction() {
		return TRADE_REED;
	}
}
