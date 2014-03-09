package com.dill.agricola.actions.farm;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.GeneralSupply.Supplyable;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.Purchasable;

public class Expand extends PurchaseAction {

	public final static Materials REFILL = new Materials(Material.BORDER, 1);
	public final static Materials COST = new Materials();

	protected final Materials materials = new Materials();
	protected final Materials lastTakenMaterials = new Materials();

	private boolean hadExp = false;

	public Expand() {
		super(ActionType.EXPAND, Purchasable.EXTENSION);
	}

	public void reset() {
		super.reset();
		materials.clear();
	}

	public void init() {
		super.init();
		materials.add(REFILL);
		lastTakenMaterials.clear();
		hadExp = false;
		setChanged();
	}
	
	public boolean isPurchaseAction() {
		return hadExp || isAnyLeft();
	}
	
	public boolean isResourceAction() {
		return true;
	}
	
	public int getMinimalCount() {
		return (hadExp || isAnyLeft()) ? 2 : 1; 
		// first is fences, second is extension (that may not happen if not any left)
	}

	protected Materials getCost(int doneSoFar) {
		return COST;
	}

	protected boolean isAnyLeft() {
		return GeneralSupply.getLeft(Supplyable.EXTENSION) > 0;
	}

	public boolean canDo(Player player, int doneSoFar) {
		return !isUsed() && !materials.isEmpty();
	}

	public boolean canDo(Player player, DirPoint pos, int doneSoFar) {
		return !isUsed() && !hadExp && isAnyLeft() && player.canPurchase(thing, getCost(doneSoFar), pos);
	}

	public boolean canUndo(Player player, int doneSoFar) {
		return !lastTakenMaterials.isEmpty();
	}

	public boolean canUndo(Player player, DirPoint pos, int doneSoFar) {
		return hadExp && player.canUnpurchase(thing, pos);
	}

	public boolean doo(Player player, int doneSoFar) {
		if (canDo(player, doneSoFar)) {
			super.doo(player, doneSoFar);
			player.addMaterial(materials);
			lastTakenMaterials.set(materials);
			materials.clear();
			setChanged();
			return true;
		}
		return false;
	}

	public boolean undo(Player player, int doneSoFar) {
		if (hadExp) {
			if (super.undo(player, doneSoFar)) {
				postUndo();
				return true;
			} else {
				return false;
			}
		} else {
			player.removeMaterial(lastTakenMaterials);
			materials.set(lastTakenMaterials);
			lastTakenMaterials.clear();
			setChanged();
			return true;			
		}
	}

	public boolean undo(Player player, DirPoint pos, int doneSoFar) {
		if (hadExp) {
			if (super.undo(player, pos, doneSoFar)) {
				postUndo();
			} else {
				return false;
			}
		}
		return true;
	}
	
	protected void postActivate() {
		GeneralSupply.useExtension(true);
		hadExp = true;
	}
	
	protected void postUndo() {
		GeneralSupply.useExtension(false);
		hadExp = false;
	}

	public Materials getAccumulatedMaterials() {
		return materials;
	}

}
