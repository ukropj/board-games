package com.dill.agricola.actions;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.support.Namer;

public class Expand extends RefillAction {

	public final static Materials REFILL = new Materials(Material.BORDER, 1);
	private boolean hadExp = false;

	public Expand() {
		super(ActionType.EXPAND, REFILL);
	}

	public boolean doOnce(Player player) {
		super.doOnce(player);
		if (GeneralSupply.getExpansionsLeft() > 0) {
			player.purchase(Purchasable.EXTENSION);
			player.setActiveType(Purchasable.EXTENSION);
			GeneralSupply.useExtension(true);
			hadExp = true;
		} else {
			hadExp = false;
		}
		return true;
	}

	public boolean undoOnce(Player player) {
		if (hadExp) {
			if (player.unpurchase(Purchasable.EXTENSION)) {
				GeneralSupply.useExtension(false);
				hadExp = false;
			} else {
				return false;				
			}
		}
		super.undoOnce(player);
		return true;
	}

	public String toString() {
		return "<html>" + Namer.getName(this) + " (" + GeneralSupply.getExpansionsLeft() + " left)" + "<br>+" + materials;
	}

}