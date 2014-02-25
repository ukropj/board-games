package com.dill.agricola.actions;

import com.dill.agricola.model.Player;
import com.dill.agricola.model.enums.ActionType;
import com.dill.agricola.model.enums.Materials;
import com.dill.agricola.model.enums.Purchasable;

public abstract class PurchaseAction extends AbstractAction {

	protected final Purchasable thing;	
	protected final Materials cost;	
	protected final boolean canPurchaseMore;
	
	public PurchaseAction(ActionType type, Purchasable thing, Materials cost, boolean canPurchaseMore) {
		super(type);
		this.thing = thing;
		this.cost = cost;
		this.canPurchaseMore = canPurchaseMore;
	}

	public boolean canPerform(Player player) {
		return super.canPerform(player) && player.canPay(cost);
	}
	
	public boolean canPerformMore(Player player, int doneSoFar) {
		return canPurchaseMore && player.canPay(cost);
	}
	
	public boolean doo(Player player) {
		boolean done = player.purchase(thing, cost);
		if (done) {
			player.setActiveType(thing);
		}
		return done;
	}

	public boolean undo(Player player) {
		return player.unpurchase(thing, cost);
	}

}
