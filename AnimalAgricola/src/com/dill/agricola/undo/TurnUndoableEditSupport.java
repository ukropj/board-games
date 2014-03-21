package com.dill.agricola.undo;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEditSupport;

import com.dill.agricola.model.types.PlayerColor;

public class TurnUndoableEditSupport extends UndoableEditSupport {

	public synchronized void beginUpdate(PlayerColor currentPlayer) {
		if (updateLevel > 0) {
			endUpdate();
		}
		if (updateLevel == 0) {
			compoundEdit = createCompoundEdit(currentPlayer);
			System.out.println("# Start: " + compoundEdit.getPresentationName());
			_postEdit(compoundEdit);
		}
		updateLevel++;
	}

	protected CompoundEdit createCompoundEdit(PlayerColor currentPlayer) {
		return new TurnCompoundEdit(currentPlayer);
	}

	public synchronized void endUpdate() {
		if (updateLevel > 0) {
			updateLevel = 0;
			compoundEdit.end();
			System.out.println("# End: " + compoundEdit.getPresentationName());
			compoundEdit = null;
		}
	}

}
