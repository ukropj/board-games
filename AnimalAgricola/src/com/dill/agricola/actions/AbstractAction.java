package com.dill.agricola.actions;

import java.util.ArrayList;
import java.util.List;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.support.Namer;
import com.dill.agricola.undo.LoggingUndoableEdit;

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

	public UndoableEdit init() {
		UndoableEdit edit = isUsed() ? new ActionInit(user) : null;
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

	public Materials getAccumulatedMaterials() {
		return null;
	}

	public Animals getAccumulatedAnimals() {
		return null;
	}
	
	protected UndoableEdit joinEdits(UndoableEdit... edits) {
		CompoundEdit edit = new CompoundEdit();
		for (UndoableEdit e : edits) {
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
	
	protected void setChanged() {
		for (ActionStateChangeListener listener : changeListeners) {
			listener.stateChanges(this);
		}
	}
	
	@SuppressWarnings("serial")
	protected class ActionInit extends LoggingUndoableEdit {
		
		private final PlayerColor lastUser;
		
		public ActionInit(PlayerColor lastUser) {
			super(false);
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
