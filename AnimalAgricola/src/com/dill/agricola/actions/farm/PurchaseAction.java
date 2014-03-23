package com.dill.agricola.actions.farm;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

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

	public boolean canDo(Player player) {
		return isAnyLeft() && player.canPurchase(thing, getCost(0), null);
	}

	public boolean canDoOnFarm(Player player, DirPoint pos, int doneSoFar) {
		return isAnyLeft() && player.canPurchase(thing, getCost(doneSoFar), pos);
	}

	public boolean canUndoOnFarm(Player player, DirPoint pos, int doneSoFar) {
		return player.canUnpurchase(thing, pos, true);
	}

	public UndoableFarmEdit doo(Player player) {
		if (canDo(player)) {
			player.setActiveType(thing);
		}
		return null;
	}

	public UndoableFarmEdit doOnFarm(Player player, DirPoint pos, int doneSoFar) {
		if (canDoOnFarm(player, pos, doneSoFar)) {
			Materials cost = getCost(doneSoFar);
			UndoableFarmEdit edit = new PurchaseThing(player, cost, pos);
			player.purchase(thing, cost, pos);
			UndoableFarmEdit postEdit = postActivate();
			setChanged();
			return joinEdits(edit, postEdit);
		}
		return null;
	}

	public boolean undoOnFarm(Player player, DirPoint pos, int doneSoFar) {
		if (canUndoOnFarm(player, pos, doneSoFar)) {
			player.unpurchase(thing, getCost(doneSoFar - 1), pos, true);
			postUndo();
			return true;
		}
		return false;
	}

	protected UndoableFarmEdit postActivate() {
		return null;
	}

	protected void postUndo() {
	}
	
	protected class PurchaseThing extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player player;
		private final Materials cost;
		private final DirPoint pos;
		
		public PurchaseThing(Player player,  Materials cost, DirPoint pos) {
			super(pos, thing);
			this.player = player;
			this.cost = cost;
			this.pos = pos;
		}
		
		public void undo() throws CannotUndoException {
			super.undo();
			player.unpurchase(thing, cost, pos, false);
//			postUndo();
		}
		
		public void redo() throws CannotRedoException {
			super.redo();
			player.purchase(thing, cost, pos);
//			postActivate();
		}
		
	}

}
