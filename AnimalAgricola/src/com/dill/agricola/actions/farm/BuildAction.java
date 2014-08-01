package com.dill.agricola.actions.farm;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

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

	public boolean canDo(Player player) {
		return isAnyLeft() && (toBuild == null || player.canPurchase(toBuild, getCost(player), null));
	}

	public boolean canDoOnFarm(Player player, DirPoint pos) {
		return isAnyLeft() && toBuild != null && player.canPurchase(toBuild, getCost(player), pos);
	}

	public boolean canUndoOnFarm(Player player, DirPoint pos) {
		return player.canUnpurchase(toBuild, pos, true);
	}

	public UndoableFarmEdit doOnFarm(Player player, DirPoint pos) {
		if (canDoOnFarm(player, pos)) {
			Building b = getBuildingInstance(toBuild);
			Materials cost = getCost(player);
			UndoableFarmEdit edit = new PurchaseBuilding(player, b, cost, pos);
			player.purchase(b, cost, pos);
			UndoableFarmEdit postEdit = postActivate(player, b, pos);
			setChanged();
			return joinEdits(true, edit, postEdit);
		}
		return null;
	}

	protected UndoableFarmEdit postActivate(Player player, Building b, DirPoint pos) {
		return null;
	}

	protected class PurchaseBuilding extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player player;
		private final Building building;
		private final Materials cost;
		private final DirPoint pos;

		public PurchaseBuilding(Player player, Building building, Materials cost, DirPoint pos) {
			super(player.getColor(), pos, Purchasable.BUILDING);
			this.player = player;
			this.building = building;
			this.cost = cost;
			this.pos = pos;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			Object done = player.unpurchase(building.getType(), pos, false);
			if (done == null) {
				throw new CannotUndoException();
			}
		}

		public void redo() throws CannotRedoException {
			super.redo();
			boolean done = player.purchase(building, cost, pos);
			if (!done) {
				throw new CannotRedoException();
			}
		}

	}
	
}
