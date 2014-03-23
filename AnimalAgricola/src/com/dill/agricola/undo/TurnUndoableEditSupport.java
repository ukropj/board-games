package com.dill.agricola.undo;

import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

import com.dill.agricola.common.DirPoint;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.model.types.Purchasable;

public class TurnUndoableEditSupport extends UndoableEditSupport {

	protected MultiEdit compoundEdit;
	
    public synchronized void postEdit(UndoableEdit e) {
        if (updateLevel == 0) {
            _postEdit(e);
        } else {
            compoundEdit.addEdit(e);
        }
    }
	
    public synchronized void beginUpdate() {
    	beginUpdate(null, null);
    }
    
	public synchronized void beginUpdate(PlayerColor currentPlayer, ActionType actionType) {
		if (updateLevel > 0) {
			endUpdate();
		}
		if (updateLevel == 0) {
			compoundEdit = createCompoundEdit(currentPlayer, actionType);
			System.out.println("# Start: " + compoundEdit.getPresentationName());
			_postEdit(compoundEdit);
		}
		updateLevel++;
	}
	
	protected MultiEdit createCompoundEdit(PlayerColor currentPlayer, ActionType actionType) {
		return new MultiEdit(currentPlayer, actionType, true);
	}

	public synchronized void endUpdate() {
		if (updateLevel > 0) {
			updateLevel = 0;
			compoundEdit.end();
			System.out.println("# End: " + compoundEdit.getPresentationName());
			compoundEdit = null;
		}
	}
	
	public synchronized void invalidateUpdated() {
		if (compoundEdit != null) {
			compoundEdit.die();
		}
	}
	
	protected boolean undoFarmAction(Player player, DirPoint pos, Purchasable thing) {
		return compoundEdit != null && compoundEdit.undoFarmAction(player.getColor(), pos, thing);
	}

}
