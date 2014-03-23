package com.dill.agricola.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import com.dill.agricola.common.DirPoint;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.model.types.Purchasable;

public class MultiEdit extends CompoundEdit implements UndoableFarmEdit {
	private static final long serialVersionUID = 1L;

	boolean hasBeenDone;
	boolean alive;

	final PlayerColor currentPlayer;
	final ActionType actionType;

//	final boolean significant;

	public MultiEdit() {
		this(null, null, false);
	}

	public MultiEdit(PlayerColor currentPlayer, ActionType actionType, boolean significant) {
		super();
		this.currentPlayer = currentPlayer;
		this.actionType = actionType;
//		this.significant = significant;

		hasBeenDone = true;
		alive = true;
	}

	public boolean isFarmEdit() {
		if (!edits.isEmpty()) {
			// compound edit is farm edit only when first child is // TODO rethink..
			UndoableFarmEdit edit = (UndoableFarmEdit) edits.firstElement();
			return edit.isFarmEdit();
		}
		return false;
	}

	public boolean addEdit(UndoableEdit anEdit) {
		if (anEdit == null) {
			return false;
		}
		boolean retVal = super.addEdit(anEdit);
		if (retVal && anEdit.isSignificant() && isSignificant()) {
			System.out.println("# Atomic Edit: " + anEdit.getPresentationName() + " into " + getPresentationName());
		}
		return retVal;
	}

	public void undo() throws CannotUndoException {
		System.out.println("#" + (isSignificant() ? " " : "-") + getUndoPresentationName());
		super.undo();
		hasBeenDone = false;
		if (isInProgress()) {
			die();
			end();
		}
	}

	public boolean canUndo() {
		return /*!isInProgress() &&*/alive && hasBeenDone;
	}

	public void redo() throws CannotRedoException {
		System.out.println("#" + (isSignificant() ? " " : "-") + getRedoPresentationName());
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

	/*public boolean isSignificant() {
		return significant && super.isSignificant();
	}*/

	public String getPresentationName() {
		return !isSignificant() ? "Cleanup" : currentPlayer != null
				? "[" + currentPlayer.toString().substring(0, 1) + "] " + actionType.name
				: super.getPresentationName();
	}

	public String getUndoPresentationName() {
		return !isSignificant() ? super.getUndoPresentationName() + "..." : "Undo " + getPresentationName();
	}

	public String getRedoPresentationName() {
		return !isSignificant() ? super.getRedoPresentationName() + "..." : "Redo " + getPresentationName();
	}

	public boolean matchesFarmAction(PlayerColor player, DirPoint pos, Purchasable thing) {
		if (currentPlayer != null && !player.equals(currentPlayer)) {
			return false;
		}
		int i = edits.size();
		while (i-- > 0) {
			UndoableFarmEdit e = (UndoableFarmEdit) edits.elementAt(i);
			if (e.matchesFarmAction(player, pos, thing)) {
				return true;
			}
		}
		return false;
	}

	public boolean undoFarmAction(PlayerColor player, DirPoint pos, Purchasable thing) {
		// TODO refactor ugly
		if (matchesFarmAction(player, pos, thing)) {
			int i = edits.size();
			while (i-- > 0) {
				try {
					UndoableFarmEdit e = (UndoableFarmEdit) edits.elementAt(i);
					if (e.canUndo() && e.matchesFarmAction(player, pos, thing)) {
						e.undo();
						e.die();
						edits.removeElementAt(i);
						System.out.println("# Unedit " + e.getPresentationName());
						return true;
					}
				} catch (ClassCastException e) {
					e.printStackTrace(); //TODO remove
					return false;
				}
			}
		}
		return false;
	}

	public String toString()
	{
		return getPresentationName()
				+ " hasBeenDone: " + hasBeenDone
				+ " alive: " + alive
				+ edits.toString();
	}
}
