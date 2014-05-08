package com.dill.agricola.actions.simple;

import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;

public class HorseSheep extends AnimalRefillAction {

	public static final Animal FIRST_ANIMAL = Animal.HORSE;
	public static final Animal OTHER_ANIMAL = Animal.SHEEP;
	
	public HorseSheep() {
		super(ActionType.HORSE_AND_SHEEP, FIRST_ANIMAL, OTHER_ANIMAL);
	}

}
