package com.dill.agricola.actions;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.Purchasable;

public class BuildTrough extends PurchaseAction {

	public final static Materials COST = new Materials(Material.WOOD, 3);
	
	public BuildTrough() {
		super(ActionType.TROUGHS, Purchasable.TROUGH, COST, true);
	}
	
	public boolean canPerform(Player player) {
		return !isUsed() && GeneralSupply.getTroughsLeft() > 0;
	}

	public boolean doOnce(Player player) {
		if (GeneralSupply.getTroughsLeft() > 0) {
			player.purchase(Purchasable.TROUGH);
			player.setActiveType(Purchasable.TROUGH);
			GeneralSupply.useTrough(true);
			setChanged();
			return true;
		}
		return false;
	}
	
	public boolean undoOnce(Player player) {
		if (player.unpurchase(Purchasable.TROUGH)) {
			GeneralSupply.useTrough(false);
			setChanged();
			return true;
		}
		return false;
	}

	public String toString() {
		return "<html>BuildTrough" + " (" + GeneralSupply.getTroughsLeft() + " left)<br>1 free, unlimited 1 for " + COST;
	}

}
