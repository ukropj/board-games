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
	protected final List<ActionStateChangeListener> changeListeners = new ArrayList<ActionStateChangeListener>();

	protected AbstractAction(ActionType type) {
		this.type = type;
	}

	public ActionType getType() {
		return type;
	}

	public void reset() {
		user = null;
	}

	public UndoableFarmEdit init() {
		UndoableFarmEdit edit = isUsed() ? new ActionInit(user) : null;
		user = null;
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

	public int getMinimalCount() {
		return 1;
	}
	
	public boolean isCancelled() {
		return false;
	}
	
	public boolean canDoOnFarm(Player player, int doneSoFar) {
		return canDoOnFarm(player, null, doneSoFar);
	}

	public Materials getAccumulatedMaterials() {
		return null;
	}

	public Animals getAccumulatedAnimals() {
		return null;
	}
	
	public boolean isSubAction() {
		return false;
	}
	
	public Action getSubAction() {
		return null;
	}
	
	protected MultiEdit joinEdits(List<UndoableFarmEdit> edits) {
		return joinEdits(edits.toArray(new UndoableFarmEdit[0]));
	}

	protected MultiEdit joinEdits(UndoableFarmEdit... edits) {
		MultiEdit edit = new MultiEdit();
		for (UndoableFarmEdit e : edits) {
			if (e != null) {
				edit.addEdit(e);
			}
		}
		edit.end();
		return edit;
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

		public ActionInit(PlayerColor lastUser) {
			this.lastUser = lastUser;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			setUsed(lastUser);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			setUsed(null);
		}

		public String getPresentationName() {
			return Namer.getName(this);
		}
	}

}
