package com.dill.agricola.undo;

import java.util.Stack;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

@SuppressWarnings("serial")
public class TurnUndoManager extends UndoManager {

	Stack<UndoRedoListener> listeners = new Stack<UndoRedoListener>();
	
	public synchronized boolean addEdit(UndoableEdit anEdit) {
		boolean retVal = super.addEdit(anEdit);
		if (retVal) {
			System.out.println("# Edit: " + anEdit.getPresentationName());
		}
		return retVal;
	}
	
	public synchronized void undo() throws CannotUndoException {
		System.out.println("# Undo: " + getUndoPresentationName());
		super.undo();
		for (UndoRedoListener l : listeners) {
			l.undoOrRedoPerformed(true);
		}
		System.out.println(canRedo());
	}
	
	public synchronized void redo() throws CannotRedoException {
		System.out.println("# Undo: " + getRedoPresentationName());
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
