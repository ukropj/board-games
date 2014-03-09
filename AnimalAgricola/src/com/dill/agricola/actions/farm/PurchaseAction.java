package com.dill.agricola.actions.farm;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Purchasable;

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

	public boolean isFarmAction() {
		return true;
	}

	public boolean canPerform(Player player, int doneSoFar) {
		return !isUsed() && isAnyLeft() && player.canPurchase(thing, getCost(doneSoFar), null);
	}

	public boolean canPerform(Player player, DirPoint pos, int doneSoFar) {
		return canPerform(player, doneSoFar) && player.canPurchase(thing, getCost(doneSoFar), pos);
	}

	public boolean canUnperform(Player player, int doneSoFar) {
		return doneSoFar > 0;
	}

	public boolean canUnperform(Player player, DirPoint pos, int doneSoFar) {
		return canUnperform(player, doneSoFar) && player.canUnpurchase(thing, pos);
	}

	public boolean activate(Player player, int doneSoFar) {
		if (canPerform(player, doneSoFar)) {
			player.setActiveType(thing);
		}
		return false;
	}

	public boolean activate(Player player, DirPoint pos, int doneSoFar) {
		if (canPerform(player, pos, doneSoFar)) {
			player.purchase(thing, getCost(doneSoFar), pos);
			postActivate();
			setChanged();
			return true;
		}
		return false;
	}

	public boolean undo(Player player, int doneSoFar) {
		if (canUnperform(player, doneSoFar)) {
			player.unpurchase(thing, getCost(doneSoFar - 1), null);
			postUndo();
			setChanged();
			return true;
		}
		return false;
	}

	public boolean undo(Player player, DirPoint pos, int doneSoFar) {
		if (canUnperform(player, pos, doneSoFar)) {
			player.unpurchase(thing, getCost(doneSoFar - 1), pos);
			postUndo();
			return true;
		}
		return false;
	}

	protected void postActivate() {
	}

	protected void postUndo() {
	}

}
