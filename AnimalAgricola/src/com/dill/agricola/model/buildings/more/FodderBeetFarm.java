package com.dill.agricola.model.buildings.more;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;

public class FodderBeetFarm extends Building {

	// requires 2 animals of each kind
	public final static Materials COST = new Materials();

	public FodderBeetFarm() {
		super(BuildingType.FODDER_BEET_FARM, 0, 0, new Animals[] {
				new Animals(Animal.SHEEP, Animal.PIG, Animal.COW, Animal.HORSE) }, null);
	}

}
