package com.dill.agricola.actions;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.GeneralSupply.Supplyable;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.buildings.Stall;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Purchasable;

public class BuildStalls extends AbstractAction {
	
	public BuildStalls() {
		super(ActionType.STALLS);
	}
	
	public boolean canPerform(Player player) {
		return super.canPerform(player) && GeneralSupply.getLeft(Supplyable.STALL) > 0 && player.canPay(Stall.COST);
	}

	public boolean doOnce(Player player) {
		if (GeneralSupply.getLeft(Supplyable.STALL) > 0) {
			Stall stall = GeneralSupply.useStall();
			boolean done = player.purchaseBuilding(stall, Stall.COST);
			if (done) {
				player.setActiveType(Purchasable.BUILDING);
				setChanged();
			} else {
				GeneralSupply.unuseStall(stall);				
			}
			return done;
		}
		return false;
	}
	
	public boolean undoOnce(Player player) {
		Stall stall = (Stall)player.unpurchaseBuilding();
		if (stall != null) {
			GeneralSupply.unuseStall(stall);
			setChanged();
			return true;
		}
		return false;
	}

	public String toString() {
		return super.toString() + " (" + GeneralSupply.getLeft(Supplyable.STALL) + " left)<br>max 1 for " + Stall.COST;
	}
}
