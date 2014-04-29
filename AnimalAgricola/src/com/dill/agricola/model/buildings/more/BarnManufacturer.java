package com.dill.agricola.model.buildings.more;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;


public class BarnManufacturer extends Building {

	public static final Materials COST = new Materials(Material.STONE, Material.REED);
	
	public BarnManufacturer() {
		super(BuildingType.BARN_MANUFACTURER, 0, 0, new Animals[]{
				new Animals(Animal.PIG), new Animals(Animal.COW)}, null);
	}

}
