package com.dill.agricola.actions.extra;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.undo.UndoableFarmEdit;

public class MoveTroughs extends AbstractAction {

	public final static Materials COST = new Materials();

	public MoveTroughs() {
		super(ActionType.MOVE_TROUGHS);
	}

	protected Materials getCost(Player player) {
		return COST;
	}

	public boolean canDo(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	public UndoableFarmEdit doo(Player player) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean canDoOnFarm(Player player, DirPoint pos) {
		// TODO Auto-generated method stub
		return false;
	}

	public UndoableFarmEdit doOnFarm(Player player, DirPoint pos) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean canUndoOnFarm(Player player, DirPoint pos) {
		// TODO Auto-generated method stub
		return false;
	}

}
