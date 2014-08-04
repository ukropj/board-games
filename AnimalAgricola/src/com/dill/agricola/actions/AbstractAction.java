package com.dill.agricola.actions;

import java.util.ArrayList;
import java.util.List;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.support.Namer;
import com.dill.agricola.undo.MultiEdit;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

public abstract class AbstractAction implements Action {

	private final ActionType type;
	protected PlayerColor user = null;
	private int subLevel = 0;
	protected int useCount = 0; // TODO make private
	protected final List<ActionStateChangeListener> changeListeners = new ArrayList<ActionStateChangeListener>();
	private Action subaction = null;
	protected boolean mustUndoSubactions = true;
	private boolean cancelled = false;

	protected AbstractAction(ActionType type) {
		this.type = type;
	}

	public ActionType getType() {
		return type;
	}

	public void reset() {
		user = null;
		useCount = 0;
		cancelled = false;
	}

	public UndoableFarmEdit init() {
		UndoableFarmEdit edit = isUsed() ? new ActionInit(user, useCount) : null;
		user = null;
		useCount = 0;
		cancelled = false;
		setChanged();
		return edit;
	}

	public void setUsed(PlayerColor user) {
		this.user = user;
		setChanged();
	}

	public boolean isUsed() {
		return user != null;
	}

	public PlayerColor getUser() {
		return user;
	}

	public void useAsSubaction(int level) {
		subLevel = level;
	}

	protected int getLevel() {
		return subLevel;
	}

	protected int getUseCount() {
		return useCount;
	}

	public boolean isUsedEnough() {
		return useCount > 0;
	}
	
	protected void setCancelled() {
		cancelled = true;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public boolean canDoOnFarm(Player player) {
		return canDoOnFarm(player, null);
	}

	public Materials getAccumulatedMaterials() {
		return null;
	}

	public Animals getAccumulatedAnimals() {
		return null;
	}
	
	public void setSubAction(Action subaction, boolean mustUndo) {
		this.subaction = subaction;
		this.mustUndoSubactions = mustUndo;
	}

	public Action getSubAction(Player player, boolean afterFarmAction) {
		return subaction;
	}
	
	public boolean mustUndoSubactions() {
		return mustUndoSubactions;
	}

	protected MultiEdit joinEdits(List<UndoableFarmEdit> edits) {
		return joinEdits(false, edits.toArray(new UndoableFarmEdit[0]));
	}

	protected MultiEdit joinEdits(UndoableFarmEdit... edits) {
		return joinEdits(false, edits);
	}

	protected MultiEdit joinEdits(boolean useAction, UndoableFarmEdit... edits) {
		MultiEdit edit = new MultiEdit();
		for (UndoableFarmEdit e : edits) {
			if (e != null) {
				edit.addEdit(e);
			}
		}
		if (edit.isEmpty()) {
			return null;
		} else {
			if (useAction) {
				useCount++;
				edit.addEdit(new ActionUse());
			}
			edit.end();
			return edit;
		}
	}

	public void addChangeListener(ActionStateChangeListener changeListener) {
		changeListeners.add(changeListener);
	}

	public void removeChangeListeners() {
		changeListeners.clear();
	}

	public void setChanged() {
		for (ActionStateChangeListener listener : changeListeners) {
			listener.stateChanges(this);
		}
	}

	public String toString() {
		return Namer.getName(this) + " user:" + user;
	}

	protected class ActionInit extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final PlayerColor lastUser;
		private final int lastUseCount;

		public ActionInit(PlayerColor lastUser, int lastUseCount) {
			this.lastUser = lastUser;
			this.lastUseCount = lastUseCount;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			setUsed(lastUser);
			useCount = lastUseCount;
		}

		public void redo() throws CannotRedoException {
			super.redo();
			setUsed(null);
			useCount = 0;
		}

		public String getPresentationName() {
			return Namer.getName(this);
		}
	}

	protected class ActionUse extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		public ActionUse() {
		}

		public void undo() throws CannotUndoException {
			super.undo();
			useCount--;
		}

		public void redo() throws CannotRedoException {
			super.redo();
			useCount++;
		}

		public String getPresentationName() {
			return Namer.getName(this);
		}
	}

}
