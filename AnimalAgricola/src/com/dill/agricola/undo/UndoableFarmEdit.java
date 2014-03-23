package com.dill.agricola.undo;

import javax.swing.undo.UndoableEdit;

import com.dill.agricola.common.DirPoint;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.model.types.Purchasable;

public interface UndoableFarmEdit extends UndoableEdit {

	public boolean isFarmEdit();
	
	public boolean isAnimalEdit();
	
	public boolean matchesFarmAction(PlayerColor player, DirPoint pos, Purchasable thing);
	
}
