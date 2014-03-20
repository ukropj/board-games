package com.dill.agricola.actions;

import javax.swing.undo.UndoableEdit;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.PlayerColor;

public interface Action {

	ActionType getType();

	void reset();

	UndoableEdit init();

	void setUsed(PlayerColor playerColor);

	boolean isUsed();
	
	PlayerColor getUser();

	boolean isQuickAction();
	
	boolean isPurchaseAction();
	
	boolean isResourceAction();

	int getMinimalCount();
	
	
	boolean canDo(Player player, int count);

	UndoableEdit doo(Player player, int count);

	boolean canDo(Player player, DirPoint pos, int count);

	UndoableEdit doo(Player player, DirPoint pos, int count);


	boolean canUndo(Player player, int count);

	boolean undo(Player player, int count);

	boolean canUndo(Player player, DirPoint pos, int count);

	boolean undo(Player player, DirPoint pos, int count);

	
	Materials getAccumulatedMaterials();

	Animals getAccumulatedAnimals();

	void addChangeListener(ActionStateChangeListener changeListener);

}
