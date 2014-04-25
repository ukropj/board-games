package com.dill.agricola.model.buildings;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class Shelter extends Building {

	public final static Materials COST = new Materials(Material.WOOD, Material.WOOD, Material.STONE);

	public Shelter() {
		super(BuildingType.SHELTER, 0, 1, new Animals[] {
				new Animals(Animal.SHEEP), new Animals(Animal.PIG),
				new Animals(Animal.COW), new Animals(Animal.HORSE) }, null);
	}

}
