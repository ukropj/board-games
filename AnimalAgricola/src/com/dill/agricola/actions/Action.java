package com.dill.agricola.actions;

import javax.swing.undo.UndoableEdit;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.undo.UndoableFarmEdit;

public interface Action {

	ActionType getType();

	void reset();

	UndoableEdit init();

	void setUsed(PlayerColor playerColor);

	boolean isUsed();
	
	PlayerColor getUser();

	boolean isPurchaseAction();
	
	boolean isResourceAction();

	int getMinimalCount();
	
	boolean isCancelled();
	
	boolean canDo(Player player);

	UndoableFarmEdit doo(Player player);

	boolean canDoOnFarm(Player player, int doneSoFar);

	boolean canDoOnFarm(Player player, DirPoint pos, int doneSoFar);

	UndoableFarmEdit doOnFarm(Player player, DirPoint pos, int doneSoFar);

	boolean canUndoOnFarm(Player player, DirPoint pos, int doneSoFar);

	Materials getAccumulatedMaterials();

	Animals getAccumulatedAnimals();

	boolean isSubAction();
	
	Action getSubAction();
	
	void addChangeListener(ActionStateChangeListener changeListener);

}
