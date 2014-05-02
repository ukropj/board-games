package com.dill.agricola.model.buildings.more;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Farm;
import com.dill.agricola.model.Space;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class DogHouse extends Building implements ExtraCapacityProvider {

	public final static Materials COST = new Materials(Material.WOOD);
	public final static Animals EXTRA_CAP = new Animals(Animal.SHEEP);
	

	public DogHouse() {
		super(BuildingType.DOG_HOUSE, 0, 0);
	}

	public Animals getExtraCapacity(DirPoint pos, Space space) {
		return !space.isUsed() && pos.y != Farm.BY_FOREST ? EXTRA_CAP : null; 
	}

}
