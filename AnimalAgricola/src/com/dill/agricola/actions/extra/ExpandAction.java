package com.dill.agricola.actions.extra;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.GeneralSupply.Supplyable;
import com.dill.agricola.actions.farm.PurchaseAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

public class ExpandAction extends PurchaseAction {

	public final static Materials COST = new Materials();

	public ExpandAction() {
		super(ActionType.BORDERS_EXPAND, Purchasable.EXTENSION);
	}

	public void reset() {
		super.reset();
		setChanged();  // to update available extension count
	}
	
	protected Materials getCost(Player player) {
		return COST;
	}

	protected boolean isAnyLeft() {
		return GeneralSupply.getLeft(Supplyable.EXTENSION) > 0;
	}
	
	public boolean canDoOnFarm(Player player, DirPoint pos) {
		return getUseCount() < 1 && super.canDoOnFarm(player, pos);
	}
	
	protected UndoableFarmEdit postActivate() {
		GeneralSupply.useExtension(true);
		return new UseExtension();
	}
	
	protected class UseExtension extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		public void undo() throws CannotUndoException {
			super.undo();
			GeneralSupply.useExtension(false);
			setChanged();
		}

		public void redo() throws CannotRedoException {
			super.redo();
			GeneralSupply.useExtension(true);
			setChanged();
		}

	}

}
