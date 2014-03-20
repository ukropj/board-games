package com.dill.agricola.actions.farm;

import javax.swing.undo.UndoableEdit;

import com.dill.agricola.common.DirPoint;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.undo.LoggingUndoableEdit;

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
	
	public boolean canDo(Player player, int doneSoFar) {
		return isAnyLeft() && (toBuild == null || player.canPurchase(toBuild, getCost(doneSoFar), null));
	}

	public boolean canDo(Player player, DirPoint pos, int doneSoFar) {
		return canDo(player, doneSoFar) && toBuild != null && player.canPurchase(toBuild, getCost(doneSoFar), pos);
	}

	public boolean canUndo(Player player, int doneSoFar) {
		return doneSoFar > 0 && toBuild != null;
	}

	public boolean canUndo(Player player, DirPoint pos, int doneSoFar) {
		return canUndo(player, doneSoFar) && player.canUnpurchase(toBuild, pos);
	}

	public UndoableEdit doo(Player player, DirPoint pos, int doneSoFar) {
		if (canDo(player, pos, doneSoFar)) {
			Building b = getBuildingInstance(toBuild);
			player.purchase(b, getCost(doneSoFar), pos);
			postActivate(player, b);
			setChanged();
			return new LoggingUndoableEdit();
		}
		return null;
	}

	public boolean undo(Player player, int doneSoFar) {
		if (canUndo(player, doneSoFar)) {
			Building b = player.unpurchase(toBuild, null);
			postUndo(player, b);
			setChanged();
			return true;
		}
		return false;
	}

	public boolean undo(Player player, DirPoint pos, int doneSoFar) {
		if (canUndo(player, pos, doneSoFar)) {
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
