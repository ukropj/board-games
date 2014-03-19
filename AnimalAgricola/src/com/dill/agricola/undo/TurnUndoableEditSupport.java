package com.dill.agricola.undo;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEditSupport;

import com.dill.agricola.model.types.PlayerColor;

public class TurnUndoableEditSupport extends UndoableEditSupport {

	/*public synchronized void postEdit(UndoableEdit e) {
		if (updateLevel == 0) {
			_postEdit(e);
		} else {
			// PENDING(rjrjr) Throw an exception if this fails?
			compoundEdit.addEdit(e);
		}
	}*/

	public synchronized void beginUpdate(int round, PlayerColor currentPlayer) {
		if (updateLevel == 0) {
			System.out.println("Turn Edit starts");
			compoundEdit = createCompoundEdit(round, currentPlayer);
			_postEdit(compoundEdit);
		}
		updateLevel++;
	}

	protected CompoundEdit createCompoundEdit(int round, PlayerColor currentPlayer) {
		return new TurnCompoundEdit(round, currentPlayer);
	}

	public synchronized void endUpdate() {
		updateLevel--;
		if (updateLevel == 0) {
			compoundEdit.end();
			System.out.println("Turn Edit ends");
			compoundEdit = null;
		}
	}

}
