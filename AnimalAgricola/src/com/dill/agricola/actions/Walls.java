package com.dill.agricola.actions;

import com.dill.agricola.model.Player;
import com.dill.agricola.model.enums.ActionType;
import com.dill.agricola.model.enums.Material;
import com.dill.agricola.model.enums.Materials;
import com.dill.agricola.model.enums.Purchasable;

public class Walls extends PurchaseAction {

	public final static Materials COST = new Materials(Material.BORDER, Material.STONE, Material.STONE);
	private boolean hadSecondFreeFence = false;
	
	public Walls() {
		super(ActionType.WALLS, Purchasable.FENCE, COST, true);
	}
	
	public boolean canPerform(Player player) {
		return !used;
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
