package com.dill.agricola.actions;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.Purchasable;

public class Walls extends PurchaseAction {

	public final static Materials COST = new Materials(Material.BORDER, Material.STONE, Material.STONE);
	private boolean hadSecondFreeFence = false;
	
	public Walls() {
		super(ActionType.WALLS, Purchasable.FENCE, COST, true);
	}
	
	public boolean canPerform(Player player) {
		// player needs at least one border to do this
		return !used && player.canPay(new Materials(Material.BORDER)); 
	}

	public boolean doOnce(Player player) {
		boolean done = player.purchase(thing, new Materials(Material.BORDER));
		if (done) { // at least one taken
			hadSecondFreeFence = player.purchase(thing, new Materials(Material.BORDER));
			player.setActiveType(thing);
		}
		return done;
	}
	
	public boolean undoOnce(Player player) {
		boolean done = player.unpurchase(thing, new Materials(Material.BORDER));
		if (hadSecondFreeFence) {
			player.unpurchase(thing, new Materials(Material.BORDER));			
		}
		return done;
	}

	public String toString() {
		return super.toString() + "<br>2 free, unlimited 1 for 2 STONE";
	}

}
