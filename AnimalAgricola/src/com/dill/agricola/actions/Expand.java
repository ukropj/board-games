package com.dill.agricola.actions;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.Namer;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.enums.ActionType;
import com.dill.agricola.model.enums.Material;
import com.dill.agricola.model.enums.Materials;
import com.dill.agricola.model.enums.Purchasable;

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
			GeneralSupply.useExpansion(true);
			hadExp = true;
		} else {
			hadExp = false;
		}
		return true;
	}

	public boolean undoOnce(Player player) {
		if (hadExp) {
			if (player.unpurchase(Purchasable.EXTENSION)) {
				GeneralSupply.useExpansion(false);
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
