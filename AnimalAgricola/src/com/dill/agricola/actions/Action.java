package com.dill.agricola.actions;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;

public interface Action {

	ActionType getType();

	void reset();

	void init();

	void setUsed();

	boolean isUsed();

	boolean isFarmAction();

	int getMinimalCount();
	
	boolean canPerform(Player player, int count);

	boolean canUnperform(Player player, int count);

	boolean canPerform(Player player, DirPoint pos, int count);

	boolean canUnperform(Player player, DirPoint pos, int count);

	boolean activate(Player player, int count);

	boolean activate(Player player, DirPoint pos, int count);

	boolean undo(Player player, int count);

	boolean undo(Player player, DirPoint pos, int count);

	
	Materials getAccumulatedMaterials();

	Animals getAccumulatedAnimals();

	void addChangeListener(StateChangeListener changeListener);


}
