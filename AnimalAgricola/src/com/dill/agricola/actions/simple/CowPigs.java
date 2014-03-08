package com.dill.agricola.actions.simple;

import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;

public class CowPigs extends AnimalAction {

	public static final Animal FIRST_ANIMAL = Animal.COW;
	public static final Animal OTHER_ANIMAL = Animal.PIG;
	
	public CowPigs() {
		super(ActionType.COW_AND_PIGS, FIRST_ANIMAL, OTHER_ANIMAL);
	}

}
