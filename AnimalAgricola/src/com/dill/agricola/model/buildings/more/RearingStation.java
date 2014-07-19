package com.dill.agricola.model.buildings.more;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Space;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class RearingStation extends Building implements ExtraCapacityProvider {

	// TODO unfinished
	
	public final static Materials COST = new Materials(Material.WOOD, Material.WOOD, Material.REED);
	public final static Animals EXTRA_CAP = new Animals(Animal.SHEEP, Animal.PIG, Animal.COW, Animal.HORSE);
	public final static Animals EXTRA_CAP2 = new Animals(
			Animal.SHEEP, Animal.SHEEP, Animal.PIG, Animal.PIG, 
			Animal.COW, Animal.COW, Animal.HORSE, Animal.HORSE);
	
	public RearingStation() {
		super(BuildingType.REARING_STATION, 1, 1);
	}

	// this space can contain one of each animal
	public Animals getExtraCapacity(DirPoint pos, Space space) {
		return buildPos.equals(pos) ? hasTrough() ? EXTRA_CAP2 : EXTRA_CAP : null;
	}
	
	public Set<Animal> getRequiredAnimals() {
		return new HashSet<Animal>(Arrays.asList(Animal.values()));
	}
	
	// TODO must be emptied before breeding

}
