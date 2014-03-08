package com.dill.agricola.actions.farm;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.DirPoint;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.GeneralSupply.Supplyable;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.buildings.Stall;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Purchasable;

public class BuildStalls extends AbstractAction {

	public BuildStalls() {
		super(ActionType.STALLS);
	}

	public void reset() {
		super.reset();
		setChanged();  // to update available stall count
	}

	public boolean isFarmAction() {
		return true;
	}

	public boolean canPerform(Player player, int doneSoFar) {
		return !isUsed() && doneSoFar < 1 && GeneralSupply.getLeft(Supplyable.STALL) > 0 && player.canPay(Stall.COST);
	}

	public boolean canPerform(Player player, DirPoint pos, int doneSoFar) {
		return canPerform(player, doneSoFar) && player.farm.canBuild(pos, BuildingType.STALL);
	}

	public boolean canUnperform(Player player, int doneSoFar) {
		return doneSoFar > 0;
	}

	public boolean canUnperform(Player player, DirPoint pos, int doneSoFar) {
		return canUnperform(player, doneSoFar) && player.farm.hasBuilding(pos, BuildingType.STALL, true);
	}

	public boolean activate(Player player, int doneSoFar) {
		if (canPerform(player, doneSoFar)) {
			player.setActiveType(Purchasable.BUILDING);
		}
		return false;
	}

	public boolean activate(Player player, DirPoint pos, int doneSoFar) {
		if (canPerform(player, pos, doneSoFar)) {
			Stall stall = GeneralSupply.useStall();
			boolean done = player.purchaseBuilding(stall, Stall.COST, pos);
			if (done) {
				setChanged();
			} else {
				GeneralSupply.unuseStall(stall);
			}
			return done;
		}
		return false;
	}

	public boolean undo(Player player, int doneSoFar) {
		if (canUnperform(player, doneSoFar)) {
			Stall stall = (Stall) player.unpurchaseBuilding();
			GeneralSupply.unuseStall(stall);
			setChanged();
			return true;
		}
		return false;
	}

	public boolean undo(Player player, DirPoint pos, int doneSoFar) {
		if (canUnperform(player, pos, doneSoFar)) {
			Stall stall = (Stall) player.farm.unbuild(pos, true);
			player.unpay(stall.getPaidCost());
			stall.setPaidCost(null);
			GeneralSupply.unuseStall(stall);
			return true;
		}
		return false;
	}

}
