package com.dill.agricola.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

@SuppressWarnings("serial")
public class LoggingUndoableEdit extends AbstractUndoableEdit {

	public void undo() throws CannotUndoException {
		System.out.println("# Atomic " + getUndoPresentationName());
		super.undo();
	}
	
	public void redo() throws CannotRedoException {
		System.out.println("# Atomic " + getRedoPresentationName());
		super.redo();
	}
	
}
