package com.dill.agricola.actions.extra;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.actions.farm.PurchaseAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

public abstract class MovePurchasable extends PurchaseAction {

	private final Materials NO_COST = new Materials();
	
	protected DirPoint movedFrom = null;

	public MovePurchasable(ActionType type, Purchasable thing) {
		super(type, thing);
	}

	protected Materials getCost(Player player) {
		return NO_COST;
	}

	public boolean canDo(Player player) {
		return player.farm.count(thing) > 0;
	}

	public boolean canDoOnFarm(Player player, DirPoint pos) {
		return movedFrom == null
				? player.canUnpurchase(thing, pos, false)
				: player.canPurchase(thing, NO_COST, pos);
	}

	public boolean canUndoOnFarm(Player player, DirPoint pos) {
		return false;
	}

	public UndoableFarmEdit doOnFarm(Player player, DirPoint pos) {
		if (canDoOnFarm(player, pos)) {
			if (movedFrom == null) {
				UndoableFarmEdit edit = new MoveThing(player, new DirPoint(pos), true);
				player.unpurchase(thing, getCost(player), pos, false);
				movedFrom = pos;
				setChanged();
				return edit;
			} else {
				UndoableFarmEdit edit = new MoveThing(player, new DirPoint(pos), false);
				player.purchase(thing, getCost(player), pos);
				movedFrom = null;
				setChanged();
				return edit;
			}
		}
		return null;
	}

	public boolean isUsedEnough() {
		// optional, but must be in consistent state
		return movedFrom == null;
	}

	protected class MoveThing extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player player;
		private final DirPoint pos;
		private final boolean take;

		public MoveThing(Player player, DirPoint pos, boolean add) {
			super(true);
			this.player = player;
			this.pos = pos;
			this.take = add;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			boolean done = take
					? player.purchase(thing, getCost(player), pos)
					: player.unpurchase(thing, getCost(player), pos, false);
			if (!done) {
				throw new CannotRedoException();
			}
			movedFrom = take ? null : pos;
		}

		public void redo() throws CannotRedoException {
			super.redo();
			boolean done = take
					? player.unpurchase(thing, getCost(player), pos, false)
					: player.purchase(thing, getCost(player), pos);
			if (!done) {
				throw new CannotUndoException();
			}
			movedFrom = take ? pos : null;
		}
		
		public String getPresentationName() {
			return MovePurchasable.this.getType().shortDesc;
		}

	}

}
