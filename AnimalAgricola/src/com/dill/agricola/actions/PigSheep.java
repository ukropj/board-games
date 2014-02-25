package com.dill.agricola.actions;

import com.dill.agricola.model.enums.ActionType;
import com.dill.agricola.model.enums.Animal;

public class PigSheep extends AnimalAction {

	public static final Animal FIRST_ANIMAL = Animal.PIG;
	public static final Animal OTHER_ANIMAL = Animal.SHEEP;
	
	public PigSheep() {
		super(ActionType.PIG_AND_SHEEP, FIRST_ANIMAL, OTHER_ANIMAL);
	}

}
