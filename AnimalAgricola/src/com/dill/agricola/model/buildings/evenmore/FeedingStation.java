package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class FeedingStation extends Building {

	public final static Materials COST = new Materials(Material.WOOD, 2);

	public FeedingStation() {
		super(BuildingType.FEEDING_STATION, 0, 1);
		setActive(true);
	}

	// can one more animal on each pasture without feeding troughs
}
