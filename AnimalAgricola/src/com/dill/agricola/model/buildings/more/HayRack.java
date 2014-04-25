package com.dill.agricola.model.buildings.more;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class HayRack extends Building {

	public final static Materials COST = new Materials(Material.WOOD, Material.WOOD, Material.WOOD, Material.REED);

	public HayRack() {
		super(BuildingType.HAY_RACK, 0, 0, new Animals[] { 
				new Animals(Animal.SHEEP, Animal.COW, Animal.HORSE) }, null);
	}

}
