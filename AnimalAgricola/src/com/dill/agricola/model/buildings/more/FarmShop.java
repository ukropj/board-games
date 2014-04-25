package com.dill.agricola.model.buildings.more;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class FarmShop extends Building {

	public final static Materials COST = new Materials(Material.WOOD);

	public FarmShop() {
		super(BuildingType.FARM_SHOP, 0, 1, Animal.SHEEP,
				new Animals[] {
						new Animals(Animal.SHEEP),
						new Animals(Animal.PIG),
						new Animals(Animal.COW),
						new Animals(Animal.HORSE) },
				new Materials[] {
						new Materials(Material.WOOD),
						new Materials(Material.STONE),
						new Materials(Material.REED) });
	}

}
