package com.dill.agricola.actions.farm;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.support.Namer;
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
		return false; //doneSoFar > 0 && toBuild != null;
	}

	public boolean canUndo(Player player, DirPoint pos, int doneSoFar) {
		return canUndo(player, doneSoFar) && player.canUnpurchase(toBuild, pos, true);
	}

	public UndoableEdit doo(Player player, DirPoint pos, int doneSoFar) {
		if (canDo(player, pos, doneSoFar)) {
			Building b = getBuildingInstance(toBuild);
			Materials cost = getCost(doneSoFar);
			UndoableEdit edit = new PurchaseBuilding(player, b, cost, pos);
			player.purchase(b, cost, pos);
			postActivate(player, b);
			setChanged();
			return edit;
		}
		return null;
	}

	public boolean undo(Player player, int doneSoFar) {
		/*if (canUndo(player, doneSoFar)) {
			Building b = player.unpurchase(toBuild, null);
			postUndo(player, b);
			setChanged();
			return true;
		}*/
		// TODO remove
		return false;
	}

	public boolean undo(Player player, DirPoint pos, int doneSoFar) {
		if (canUndo(player, pos, doneSoFar)) {
			Building b = player.unpurchase(toBuild, pos, true);
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

	@SuppressWarnings("serial")
	protected class PurchaseBuilding extends LoggingUndoableEdit {

		private final Player player;
		private final Building building;
		private final Materials cost;
		private final DirPoint pos;

		public PurchaseBuilding(Player player, Building building, Materials cost, DirPoint pos) {
			this.player = player;
			this.building = building;
			this.cost = cost;
			this.pos = pos;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			player.unpurchase(building.getType(), pos, false);
			postUndo(player, building);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			player.purchase(building, cost, pos);
			postActivate(player, building);
		}

		public String getPresentationName() {
			return Namer.getName(this);
		}

	}
}
