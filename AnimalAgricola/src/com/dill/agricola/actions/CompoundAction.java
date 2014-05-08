package com.dill.agricola.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dill.agricola.common.DirPoint;
import com.dill.agricola.model.Player;
import com.dill.agricola.undo.UndoableFarmEdit;

public class CompoundAction extends AbstractAction {

	private final List<Action> actions;

	public CompoundAction(Action... actions) {
		super(null);
		this.actions = Arrays.asList(actions);
	}

	public void reset() {
		for (Action a : actions) {
			a.reset();
		}
	}

	public UndoableFarmEdit init() {
		List<UndoableFarmEdit> edits = new ArrayList<UndoableFarmEdit>();
		for (Action a : actions) {
			UndoableFarmEdit e = a.init();
			if (e != null) {
				edits.add(e);				
			}
		}
		return !edits.isEmpty() ? joinEdits(edits) : null;
	}

	public int getMinimalCount() {
		return 1;
	}
	
	public boolean canDo(Player player) {
		for (Action a : actions) {
			if (a.canDo(player)) {
				return true;
			}
		}
		return false;
	}

	public UndoableFarmEdit doo(Player player) {
		List<UndoableFarmEdit> edits = new ArrayList<UndoableFarmEdit>();
		for (Action a : actions) {
			if (a.canDo(player)) {
				UndoableFarmEdit e = a.doo(player);
				if (e != null) {
					edits.add(e);				
				}
			}
		}
		return !edits.isEmpty() ? joinEdits(edits) : null;
	}

	public boolean canDoOnFarm(Player player, DirPoint pos, int doneSoFar) {
		return false; // TODO maybe enable
	}

	public UndoableFarmEdit doOnFarm(Player player, DirPoint pos, int doneSoFar) {
		return null;
	}

	public boolean canUndoOnFarm(Player player, DirPoint pos, int doneSoFar) {
		return false;
	}

	public boolean isPurchaseAction() {
		for (Action a : actions) {
			if (a.isPurchaseAction()) {
				return true;
			}
		}
		return false;
	}

	public boolean isResourceAction() {
		for (Action a : actions) {
			if (a.isResourceAction()) {
				return true;
			}
		}
		return false;
	}

//	public String toString() {
//		return Namer.getName(this) + " user:" + user;
//	}

}
