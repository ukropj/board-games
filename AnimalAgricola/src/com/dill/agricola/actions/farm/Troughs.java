package com.dill.agricola.actions.farm;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.GeneralSupply.Supplyable;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

public class Troughs extends PurchaseAction {

	public final static Materials FIRST_COST = new Materials();
	public final static Materials COST = new Materials(Material.WOOD, 3);

	public Troughs() {
		super(ActionType.TROUGHS, Purchasable.TROUGH);
	}

	public void reset() {
		super.reset();
		setChanged();  // to update available trough count
	}

	protected Materials getCost(int doneSoFar) {
		return doneSoFar < 1 ? FIRST_COST : COST;
	}

	protected boolean isAnyLeft() {
		return GeneralSupply.getLeft(Supplyable.TROUGH) > 0;
	}

	protected UndoableFarmEdit postActivate() {
		GeneralSupply.useTrough(true);
		return new UseTrough();
	}

	protected void postUndo() {
		GeneralSupply.useTrough(false);
	}

	protected class UseTrough extends SimpleEdit {
		private static final long serialVersionUID = 1L;
		
		public void undo() throws CannotUndoException {
			super.undo();
			GeneralSupply.useTrough(false);
			setChanged();
		}

		public void redo() throws CannotRedoException {
			super.redo();
			GeneralSupply.useTrough(true);
			setChanged();
		}

	}

}
