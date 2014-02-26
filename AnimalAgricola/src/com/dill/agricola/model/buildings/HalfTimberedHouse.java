package com.dill.agricola.model.buildings;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Space;
import com.dill.agricola.model.types.Material;

public class HalfTimberedHouse extends Building {

	public final static Materials COST = new Materials(Material.WOOD, Material.WOOD, Material.WOOD, Material.STONE, Material.STONE, Material.REED);

	public HalfTimberedHouse() {
		super(BuildingType.HALF_TIMBERED_HOUSE, 5, 2);
	}

	public boolean canBuildAt(Space building) {
		return building instanceof Cottage;
	}
}
