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

	public synchronized void postEdit(UndoableEdit e) {
		if (updateLevel == 0) {
			_postEdit(e);
		} else {
			if (compoundEdit.isEmpty() && compoundEdit.addEdit(e)) {
				Logger.logUndo("Start: " + compoundEdit.getPresentationName());
				_postEdit(compoundEdit);
			} else {
				compoundEdit.addEdit(e);
			}
		}
	}

	public synchronized void beginUpdate(PlayerColor currentPlayer, ActionType actionType) {
		beginUpdate(currentPlayer, actionType.shortDesc);
	}

	public synchronized void beginUpdate(PlayerColor currentPlayer, String name) {
		if (updateLevel > 0) {
			endUpdate();
		}
		if (updateLevel == 0) {
			compoundEdit = createCompoundEdit(currentPlayer, name);
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
			if (!compoundEdit.isEmpty()) {
				Logger.logUndo("End: " + compoundEdit.getPresentationName());
			}
			compoundEdit = null;
		}
	}

	public synchronized void invalidateUpdated() throws CannotUndoException {
		if (compoundEdit != null) {
			compoundEdit.die();
		}
	}

	protected boolean undoSpecificAction(Player player, DirPoint pos, Purchasable thing, boolean undoAllAfter) throws CannotUndoException {
		return compoundEdit != null && compoundEdit.undoFarmAction(player.getColor(), pos, thing, undoAllAfter);
	}

}
