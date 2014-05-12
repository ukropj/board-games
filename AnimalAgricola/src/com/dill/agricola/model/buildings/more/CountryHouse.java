package com.dill.agricola.model.buildings.more;

import com.dill.agricola.actions.CompoundAction;
import com.dill.agricola.actions.extra.ExpandAction;
import com.dill.agricola.actions.simple.AnimalAction;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class CountryHouse extends Building {

	public final static Materials COST = new Materials(Material.WOOD, Material.WOOD, Material.WOOD,
			Material.STONE, Material.STONE, Material.STONE);
	public final static Animals REWARD = new Animals(Animal.COW);

	public CountryHouse() {
		super(BuildingType.COUNTRY_HOUSE, 1, 3, new CompoundAction(
				new AnimalAction(ActionType.BUILDING_REWARD, REWARD),
				new ExpandAction()
				));
	}
}
