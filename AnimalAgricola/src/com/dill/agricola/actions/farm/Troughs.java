package com.dill.agricola.actions.farm;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.GeneralSupply.Supplyable;
import com.dill.agricola.common.Materials;
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
		return doneSoFar < 1 ? FIRST_COST : COST;
	}
	
	protected boolean isAnyLeft() {
		return GeneralSupply.getLeft(Supplyable.TROUGH) > 0;
	}
	
	protected void postActivate() {
		GeneralSupply.useTrough(true);
	}
	
	protected void postUndo() {
		GeneralSupply.useTrough(false);
	}


}
