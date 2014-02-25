package com.dill.agricola.model.buildings;

import com.dill.agricola.model.Building;
import com.dill.agricola.model.enums.Animal;
import com.dill.agricola.model.enums.Material;
import com.dill.agricola.model.enums.Materials;

public class Shelter extends Building {

	public final static Materials COST = new Materials(Material.WOOD, Material.WOOD, Material.STONE);

	public Shelter() {
		super(BuildingType.SHELTER, 0, 1, new Animal[]{Animal.SHEEP, Animal.PIG, Animal.COW, Animal.HORSE});
	}

}
