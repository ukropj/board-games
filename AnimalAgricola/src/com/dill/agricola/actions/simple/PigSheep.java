package com.dill.agricola.actions.simple;

import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;

public class PigSheep extends AnimalAction {

	public static final Animal FIRST_ANIMAL = Animal.PIG;
	public static final Animal OTHER_ANIMAL = Animal.SHEEP;
	
	public PigSheep() {
		super(ActionType.PIG_AND_SHEEP, FIRST_ANIMAL, OTHER_ANIMAL);
	}

}
