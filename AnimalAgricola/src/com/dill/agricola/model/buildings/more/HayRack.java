package com.dill.agricola.model.buildings.more;

import com.dill.agricola.actions.simple.AnimalAction;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class HayRack extends Building {

	public final static Materials COST = new Materials(Material.WOOD, Material.WOOD, Material.WOOD, Material.REED);
	private final static Animals REWARD = new Animals(Animal.SHEEP, Animal.COW, Animal.HORSE);
	
	public HayRack() {
		super(BuildingType.HAY_RACK, 0, 0, new AnimalAction(ActionType.BUILDING_REWARD, REWARD));
	}

}
