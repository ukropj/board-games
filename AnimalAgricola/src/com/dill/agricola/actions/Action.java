package com.dill.agricola.actions;

import com.dill.agricola.model.Player;
import com.dill.agricola.model.enums.ActionType;
import com.dill.agricola.model.enums.Animals;
import com.dill.agricola.model.enums.Materials;

public interface Action {
	
	ActionType getType();

	void reset();
	
	void init();
	
	void setUsed();
	
	boolean isUsed();

	boolean canPerform(Player player); 
	
	boolean canPerformMore(Player player, int doneSoFar); 	
	
	boolean doOnce(Player player);

	boolean undoOnce(Player player);

	boolean doo(Player player);
	
	boolean undo(Player player);
	
	Materials getAccumulatedMaterials();

	Animals getAccumulatedAnimals();

	void addChangeListener(StateChangeListener changeListener);

}
