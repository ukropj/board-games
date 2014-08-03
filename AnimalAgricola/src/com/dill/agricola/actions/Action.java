package com.dill.agricola.actions;

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

	UndoableFarmEdit init();

	void setUsed(PlayerColor playerColor);

	boolean isUsed();
	
	PlayerColor getUser();
	
	void useAsSubaction(int level);
	
	boolean isCancelled();
	
	boolean isUsedEnough();
	
	boolean canDo(Player player);

	UndoableFarmEdit doo(Player player);

	boolean canDoOnFarm(Player player);

	boolean canDoOnFarm(Player player, DirPoint pos);

	UndoableFarmEdit doOnFarm(Player player, DirPoint pos);

	boolean canUndoOnFarm(Player player, DirPoint pos);

	Materials getAccumulatedMaterials();

	Animals getAccumulatedAnimals();

	void setSubAction(Action subaction, boolean mustUndo);
	
	Action getSubAction(Player player, boolean afterFarmAction);

	boolean mustUndoSubactions();
	
	void setChanged();
	
	void addChangeListener(ActionStateChangeListener changeListener);

	void removeChangeListeners();


}
