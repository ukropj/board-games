package com.dill.agricola.model.buildings.more;

import com.dill.agricola.actions.FeatureAction;
import com.dill.agricola.actions.extra.TradeAnimals;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class AnimalTrader extends Building {

	public final static Materials COST = new Materials(
			Material.WOOD, Material.WOOD, Material.REED, Material.REED);
	public final static FeatureAction[] TRADE_ANIMALS = new FeatureAction[] { new TradeAnimals() };

	public AnimalTrader() {
		super(BuildingType.ANIMAL_TRADER, 3, 2);
	}

	// allows trading 2 different animal for 1 other animal
	public FeatureAction[] getFeatureActions() {
		return TRADE_ANIMALS;
	}
}
