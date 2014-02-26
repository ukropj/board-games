package com.dill.agricola.actions;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;

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
