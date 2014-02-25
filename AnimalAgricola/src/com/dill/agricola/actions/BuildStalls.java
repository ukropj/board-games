package com.dill.agricola.actions;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.buildings.Stall;
import com.dill.agricola.model.enums.ActionType;
import com.dill.agricola.model.enums.Purchasable;

public class BuildStalls extends AbstractAction {
	
	public BuildStalls() {
		super(ActionType.STALLS);
	}
	
	public boolean canPerform(Player player) {
		return super.canPerform(player) && GeneralSupply.getStallsLeft() > 0 && player.canPay(Stall.COST);
	}

	public boolean doOnce(Player player) {
		if (GeneralSupply.getStallsLeft() > 0) {
			boolean done = player.purchaseBuilding(new Stall(), Stall.COST);
			if (done) {
				player.setActiveType(Purchasable.BUILDING);
				GeneralSupply.useStall(true);
				setChanged();
			}
			return done;
		}
		return false;
	}
	
	public boolean undoOnce(Player player) {
		if (player.unpurchaseBuilding()) {
			GeneralSupply.useStall(false);
			setChanged();
			return true;
		}
		return false;
	}

	public String toString() {
		return super.toString() + " (" + GeneralSupply.getStallsLeft() + " left)<br>max 1 for " + Stall.COST;
	}
}
