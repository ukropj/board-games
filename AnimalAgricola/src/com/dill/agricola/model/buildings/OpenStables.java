package com.dill.agricola.model.buildings;

import com.dill.agricola.model.Building;
import com.dill.agricola.model.Space;
import com.dill.agricola.model.enums.Animal;
import com.dill.agricola.model.enums.Material;
import com.dill.agricola.model.enums.Materials;


public class OpenStables extends Building {

	public static final Materials COST_WOOD = new Materials(Material.WOOD, 3);
	public static final Materials COST_STONE = new Materials(Material.STONE, 3);
	
	public OpenStables() {
		super(BuildingType.OPEN_STABLES, 2, 5, new Animal[]{Animal.COW, Animal.HORSE});
	}

	public boolean canBuildAt(Space building) {
		return building instanceof Stall;
	}
}
