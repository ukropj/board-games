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

	abstract protected Materials getCost(Player player);

	protected boolean isAnyLeft() {
		return true;
	}
	
	public boolean isPurchaseAction() {
		return true;
	}
	
	public boolean isResourceAction() {
		return false;
	}

	public boolean canDo(Player player) {
		return isAnyLeft() && player.canPurchase(thing, getCost(player), null);
	}

	public boolean canDoOnFarm(Player player, DirPoint pos) {
		return isAnyLeft() && player.canPurchase(thing, getCost(player), pos);
	}

	public boolean canUndoOnFarm(Player player, DirPoint pos) {
		return player.canUnpurchase(thing, pos, true);
	}

	public UndoableFarmEdit doo(Player player) {
		if (canDo(player)) {
			setPlayerActive(player);
		}
		return null;
	}
	
	protected void setPlayerActive(Player player) {
		if (!isAsSubAction()) {
			player.getFarm().setActiveType(thing);				
		} else {
			player.getFarm().setActiveSubType(thing);								
		}
	}

	public UndoableFarmEdit doOnFarm(Player player, DirPoint pos) {
		if (canDoOnFarm(player, pos)) {
			UndoableFarmEdit edit = new PurchaseThing(player, new DirPoint(pos));
			player.purchase(thing, getCost(player), pos);
			UndoableFarmEdit postEdit = postActivate();
			setChanged();
			return joinEdits(true, edit, postEdit);
		}
		return null;
	}

	protected UndoableFarmEdit postActivate() {
		return null;
	}

	protected class PurchaseThing extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player player;
		private final DirPoint pos;
		private final DirPoint undoPos;
		
		public PurchaseThing(Player player, DirPoint pos) {
			super(thing == Purchasable.EXTENSION && pos.x < 0 ? new DirPoint(0, 0) : pos, thing);
			this.player = player;
			this.pos = pos;
			this.undoPos = thing == Purchasable.EXTENSION && pos.x < 0 ? new DirPoint(0, 0) : pos;
		}
		
		public void undo() throws CannotUndoException {
			super.undo();
			boolean done = player.unpurchase(thing, getCost(player), undoPos, false);
			if (!done) {
				throw new CannotUndoException();
			}
		}
		
		public void redo() throws CannotRedoException {
			super.redo();
			boolean done = player.purchase(thing, getCost(player), pos);
			if (!done) {
				throw new CannotRedoException();
			}
		}
		
	}

}
