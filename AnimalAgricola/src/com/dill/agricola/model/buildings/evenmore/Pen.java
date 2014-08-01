package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class Pen extends Building {

	public final static Materials COST = new Materials(Material.WOOD, 2);

	public Pen() {
		super(BuildingType.PEN, 0, 4);
	}
	
	// animals in the pen does not count for scorings
}
