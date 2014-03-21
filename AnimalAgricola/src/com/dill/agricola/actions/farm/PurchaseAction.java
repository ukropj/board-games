package com.dill.agricola.actions.farm;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.support.Namer;
import com.dill.agricola.undo.LoggingUndoableEdit;

public abstract class PurchaseAction extends AbstractAction {

	protected final Purchasable thing;

	public PurchaseAction(ActionType type, Purchasable thing) {
		super(type);
		this.thing = thing;
	}

	abstract protected Materials getCost(int doneSoFar);

	protected boolean isAnyLeft() {
		return true;
	}
	
	public boolean isQuickAction() {
		return false;
	}

	public boolean isPurchaseAction() {
		return true;
	}
	
	public boolean isResourceAction() {
		return false;
	}

	public boolean canDo(Player player, int doneSoFar) {
		return isAnyLeft() && player.canPurchase(thing, getCost(doneSoFar), null);
	}

	public boolean canDo(Player player, DirPoint pos, int doneSoFar) {
		return canDo(player, doneSoFar) && player.canPurchase(thing, getCost(doneSoFar), pos);
	}

	public boolean canUndo(Player player, int doneSoFar) {
		return false; //doneSoFar > 0;
	}

	public boolean canUndo(Player player, DirPoint pos, int doneSoFar) {
		return canUndo(player, doneSoFar) && player.canUnpurchase(thing, pos, true);
	}

	public UndoableEdit doo(Player player, int doneSoFar) {
		if (canDo(player, doneSoFar)) {
			player.setActiveType(thing);
		}
		return null;
	}

	public UndoableEdit doo(Player player, DirPoint pos, int doneSoFar) {
		if (canDo(player, pos, doneSoFar)) {
			Materials cost = getCost(doneSoFar);
			UndoableEdit edit = new PurchaseThing(player, cost, pos);
			player.purchase(thing, cost, pos);
			postActivate();
			setChanged();
			return edit;
		}
		return null;
	}

	public boolean undo(Player player, int doneSoFar) {
		/*if (canUndo(player, doneSoFar)) {
			player.unpurchase(thing, getCost(doneSoFar - 1), null, false);
			postUndo();
			setChanged();
			return true;
		}*/
		return false;
	}

	public boolean undo(Player player, DirPoint pos, int doneSoFar) {
		if (canUndo(player, pos, doneSoFar)) {
			player.unpurchase(thing, getCost(doneSoFar - 1), pos, true);
			postUndo();
			return true;
		}
		return false;
	}

	protected void postActivate() {
	}

	protected void postUndo() {
	}
	
	@SuppressWarnings("serial")
	protected class PurchaseThing extends LoggingUndoableEdit {

		private final Player player;
		private final Materials cost;
		private final DirPoint pos;
		
		public PurchaseThing(Player player,  Materials cost, DirPoint pos) {
			this.player = player;
			this.cost = cost;
			this.pos = pos;
		}
		
		public void undo() throws CannotUndoException {
			super.undo();
			player.unpurchase(thing, cost, pos, false);
			postUndo();
		}
		
		public void redo() throws CannotRedoException {
			super.redo();
			player.purchase(thing, cost, pos);
			postActivate();
		}
		
		public String getPresentationName() {
			return Namer.getName(this);
		}
		
	}

}
