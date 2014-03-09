package com.dill.agricola.actions.farm;

import com.dill.agricola.common.DirPoint;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Purchasable;

public abstract class BuildAction extends PurchaseAction {

	protected BuildingType toBuild;

	public BuildAction(ActionType type) {
		super(type, Purchasable.BUILDING);
		this.toBuild = null;
	}
	
	public BuildAction(ActionType type, BuildingType building) {
		super(type, Purchasable.BUILDING);
		this.toBuild = building;
	}

	abstract protected Building getBuildingInstance(BuildingType type); 
	
	public boolean canPerform(Player player, int doneSoFar) {
		return !isUsed() && isAnyLeft() && (toBuild == null || player.canPurchase(toBuild, getCost(doneSoFar), null));
	}

	public boolean canPerform(Player player, DirPoint pos, int doneSoFar) {
		return canPerform(player, doneSoFar) && toBuild != null && player.canPurchase(toBuild, getCost(doneSoFar), pos);
	}

	public boolean canUnperform(Player player, int doneSoFar) {
		return doneSoFar > 0 && toBuild != null;
	}

	public boolean canUnperform(Player player, DirPoint pos, int doneSoFar) {
		return canUnperform(player, doneSoFar) && player.canUnpurchase(toBuild, pos);
	}

	public boolean activate(Player player, DirPoint pos, int doneSoFar) {
		if (canPerform(player, pos, doneSoFar)) {
			Building b = getBuildingInstance(toBuild);
			player.purchase(b, getCost(doneSoFar), pos);
			postActivate(player, b);
			setChanged();
			return true;
		}
		return false;
	}

	public boolean undo(Player player, int doneSoFar) {
		if (canUnperform(player, doneSoFar)) {
			Building b = player.unpurchase(toBuild, null);
			postUndo(player, b);
			setChanged();
			return true;
		}
		return false;
	}

	public boolean undo(Player player, DirPoint pos, int doneSoFar) {
		if (canUnperform(player, pos, doneSoFar)) {
			Building b = player.unpurchase(toBuild, pos);
			postUndo(player, b);
			setChanged();
			return true;
		}
		return false;
	}

	protected void postActivate(Player player, Building b) {
	}
	
	protected void postUndo(Player player, Building b) {
	}

}
