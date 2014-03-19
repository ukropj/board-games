package com.dill.agricola.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

@SuppressWarnings("serial")
public class TurnUndoManager extends UndoManager {

	public synchronized boolean addEdit(UndoableEdit anEdit) {
		boolean retVal = super.addEdit(anEdit);
		if (retVal) {
			System.out.println("# Edit: " + anEdit.getPresentationName());
		}
		return retVal;
	}
	
	@Override
	public synchronized void undo() throws CannotUndoException {
		System.out.println("# Undo: " + getUndoPresentationName());
		super.undo();
	}
	
	@Override
	public synchronized void redo() throws CannotRedoException {
		System.out.println("# Undo: " + getRedoPresentationName());
		super.redo();
	}
}
