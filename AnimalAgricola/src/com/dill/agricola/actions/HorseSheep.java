package com.dill.agricola.actions;

import com.dill.agricola.model.enums.ActionType;
import com.dill.agricola.model.enums.Animal;

public class HorseSheep extends AnimalAction {

	public static final Animal FIRST_ANIMAL = Animal.HORSE;
	public static final Animal OTHER_ANIMAL = Animal.SHEEP;
	
	public HorseSheep() {
		super(ActionType.HORSE_AND_SHEEP, FIRST_ANIMAL, OTHER_ANIMAL);
	}

}
