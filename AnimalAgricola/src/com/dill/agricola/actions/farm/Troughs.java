package com.dill.agricola.actions.farm;

import com.dill.agricola.common.DirPoint;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.GeneralSupply.Supplyable;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.Purchasable;

public class Troughs extends PurchaseAction {

	public final static Materials FIRST_COST = new Materials();
	public final static Materials COST = new Materials(Material.WOOD, 3);

	public Troughs() {
		super(ActionType.TROUGHS, Purchasable.TROUGH);
	}

	public void reset() {
		super.reset();
		setChanged();  // to update available trough count
	}

	protected Materials getCost(int doneSoFar) {
		return doneSoFar == 0 ? FIRST_COST : COST;
	}
	
	protected boolean isAnyLeft() {
		return GeneralSupply.getLeft(Supplyable.TROUGH) > 0;
	}

	public boolean activate(Player player, DirPoint pos, int doneSoFar) {
		if (super.activate(player, pos, doneSoFar)) {
			GeneralSupply.useTrough(true);
			setChanged();
			return true;
		}
		return false;
	}

	public boolean undo(Player player, int doneSoFar) {
		if (super.undo(player, doneSoFar)) {
			GeneralSupply.useTrough(false);
			setChanged();
			return true;
		}
		return false;
	}

	public boolean undo(Player player, DirPoint pos, int doneSoFar) {
		if (super.undo(player, pos, doneSoFar)) {
			GeneralSupply.useTrough(false);
			setChanged();
			return true;
		}
		return false;
	}

}
