package com.dill.agricola.model.buildings.more;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class RearingStation extends Building {

	// TODO unfinished
	
	public final static Materials COST = new Materials(Material.WOOD, Material.WOOD, Material.REED);
	public final static Animals EXTRA_CAP = new Animals(Animal.SHEEP, Animal.PIG, Animal.COW, Animal.HORSE);
	
	public RearingStation() {
		super(BuildingType.REARING_STATION, 1, 0);
	}
	
	// this space can contain one of each animal
//	public int getMaxCapacity() {
//		return hasTrough() ? EXTRA_CAP.size() * FEEDER_MULTI : EXTRA_CAP.size();
//	}
//
//	public Set<Animal> getRequiredAnimals() {
//		return new HashSet<Animal>(Arrays.asList(Animal.values()));
//	}
	
	// must be emptied before breednig

}
