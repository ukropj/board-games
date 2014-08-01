package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.actions.CompoundAction;
import com.dill.agricola.actions.extra.GiveAnimal;
import com.dill.agricola.actions.simple.AnimalAction;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class Inn extends Building {

	public final static Materials COST = new Materials(Material.STONE, 2);
	public final static Animals ANIMAL_COST = new Animals(Animal.PIG, Animal.COW);
	private final static Animals REWARD = new Animals(Animal.HORSE);

	public Inn() {
		super(BuildingType.INN, 4, 2, Animal.PIG, new CompoundAction(
				ActionType.BUILDING_REWARD,
				new GiveAnimal(ActionType.BUILDING_REWARD, ANIMAL_COST),
				new AnimalAction(ActionType.BUILDING_REWARD, REWARD)));
	}
}
