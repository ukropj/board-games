package com.dill.agricola.undo;

import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

import com.dill.agricola.common.DirPoint;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.support.Logger;

public class TurnUndoableEditSupport extends UndoableEditSupport {

	protected MultiEdit compoundEdit;
	protected boolean compoundEditPosted;

	public void reset() {
		updateLevel = 0;
		compoundEdit = null;
		compoundEditPosted = false;
	}
	
	public synchronized void postEdit(UndoableEdit e) {
		if (updateLevel == 0) {
			System.err.println("Level zero edit!");
			_postEdit(e);
		} else {
			if (!compoundEditPosted && compoundEdit.addEdit(e)) {
				Logger.logUndo("Start: " + compoundEdit.getPresentationName());
				compoundEditPosted = true;
				_postEdit(compoundEdit);
			} else {
				compoundEdit.addEdit(e);
			}
		}
	}

	public synchronized void beginUpdate(PlayerColor currentPlayer) {
		beginUpdate(currentPlayer, null);
	}
	
	public synchronized void beginUpdate(PlayerColor currentPlayer, ActionType actionType) {
		if (updateLevel > 0) {
			endUpdate();
		}
		if (updateLevel == 0) {
			compoundEdit = createCompoundEdit(currentPlayer, actionType != null ? actionType.shortDesc : null);
			Logger.logUndo("Start unposted: " + compoundEdit.getPresentationName());
			compoundEditPosted = false;
		}
		updateLevel++;
	}

	protected MultiEdit createCompoundEdit(PlayerColor currentPlayer, String name) {
		return new MultiEdit(currentPlayer, name);
	}

	public synchronized void endUpdate() {
		if (updateLevel > 0) {
			updateLevel = 0;
			compoundEdit.end();
			Logger.logUndo("End" + (compoundEditPosted ? " unposted" : "") + ": " + compoundEdit.getPresentationName());
			compoundEdit = null;
			compoundEditPosted = false;
		} else {
			System.err.println("Cannot end update, already ended!");
		}
	}

	public synchronized void invalidateUpdated() throws CannotUndoException {
		if (compoundEdit != null) {
			compoundEdit.die();
		}
	}

	protected boolean undoSpecificAction(Player player, DirPoint pos, Purchasable thing, boolean undoAllAfter) throws CannotUndoException {
		return compoundEdit != null && compoundEditPosted && compoundEdit.undoFarmAction(player.getColor(), pos, thing, undoAllAfter);
	}

}
