package com.dill.agricola.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import com.dill.agricola.model.types.PlayerColor;

@SuppressWarnings("serial")
public class TurnCompoundEdit extends CompoundEdit {
	// TODO class may not be needed
	
	boolean hasBeenDone;
	boolean alive;
	
	final int round;
	final PlayerColor currentPlayer;
	
	public TurnCompoundEdit(int round, PlayerColor currentPlayer) {
        super();
        this.round = round;
        this.currentPlayer = currentPlayer;

        hasBeenDone = true;
        alive = true;
    }
	
	@Override
	public boolean addEdit(UndoableEdit anEdit) {
		boolean retVal = super.addEdit(anEdit);
		if (retVal) {
			System.out.println("# Atomic Edit:" + anEdit.getPresentationName() + " into " + getPresentationName());			
		}
		return retVal;
	}
	
	public void undo() throws CannotUndoException {
        super.undo();
        hasBeenDone = false;
        if (isInProgress()) {
        	die();
        }
    }
	
	public boolean canUndo() {
        return /*!isInProgress() &&*/ alive && hasBeenDone;
    }

	public void redo() throws CannotRedoException {
		super.redo();
		hasBeenDone = true;
	}

    public boolean canRedo() {
        return !isInProgress() && alive && !hasBeenDone;
    }
    
    public void die() {
        super.die();
    	alive = false;
    }

    public boolean  isSignificant() {
        return true;
    }
	
    public String getPresentationName() {
    	return "R" + round + ": " + currentPlayer;
    }
    
    public String getUndoPresentationName() {
    	return "Undo " + getPresentationName();
    }
    
    public String getRedoPresentationName() {
    	return "Redo " + getPresentationName();
    }
}
