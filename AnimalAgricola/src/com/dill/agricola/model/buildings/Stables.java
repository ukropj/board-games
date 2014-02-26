package com.dill.agricola.model.buildings;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Space;
import com.dill.agricola.model.types.Material;


public class Stables extends Building {

	public static final Materials COST_WOOD = new Materials(Material.WOOD, 5);
	public static final Materials COST_STONE = new Materials(Material.STONE, 5);
	
	public Stables() {
		super(BuildingType.STABLES, 4, 5);
	}

	public boolean canBuildAt(Space building) {
		return building instanceof Stall;
	}
}
