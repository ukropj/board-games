package com.dill.agricola.undo;

import java.util.Stack;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import com.dill.agricola.support.Logger;

public class TurnUndoManager extends UndoManager {
	private static final long serialVersionUID = 1L;

	Stack<UndoRedoListener> listeners = new Stack<UndoRedoListener>();

	public synchronized boolean addEdit(UndoableEdit anEdit) {
		if (anEdit == null) {
			return false;
		}
		boolean retVal = super.addEdit(anEdit);
		if (retVal) {
			Logger.logUndo((anEdit.isSignificant() ? "" : "-") + "Edit: " + anEdit.getPresentationName());
		}
		return retVal;
	}

	public synchronized void undo() throws CannotUndoException {
		Logger.logUndo("Undo: " + getUndoPresentationName());
		super.undo();
		for (UndoRedoListener l : listeners) {
			l.undoOrRedoPerformed(true);
		}
	}
	
	public synchronized void redo() throws CannotRedoException {
		Logger.logUndo("Redo: " + getRedoPresentationName());
		super.redo();
		for (UndoRedoListener l : listeners) {
			l.undoOrRedoPerformed(false);
		}
	}

	public static interface UndoRedoListener {

		public void undoOrRedoPerformed(boolean isUndo);
	}

	public void addUndoRedoListener(UndoRedoListener listener) {
		listeners.add(listener);
	}
}
