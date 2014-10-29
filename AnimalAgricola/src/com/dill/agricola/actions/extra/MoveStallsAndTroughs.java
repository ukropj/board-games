package com.dill.agricola.actions.extra;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.actions.FeatureAction;
import com.dill.agricola.actions.farm.PurchaseAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

public class MoveStallsAndTroughs extends PurchaseAction implements FeatureAction {

	private Building movedBuilding = null;

	private final Materials NO_COST = new Materials();

	public MoveStallsAndTroughs() {
		super(ActionType.MOVE_STALLS_AND_TROUGHS, Purchasable.BUILDING);
		setSubAction(new MoveTroughs(), false);
	}

	protected Materials getCost(Player player) {
		return NO_COST;
	}

	public boolean canDo(Player player) {
		return player.farm.hasBuilding(BuildingType.STALL)
				|| player.farm.hasBuilding(BuildingType.COW_STALL)
				|| player.farm.hasBuilding(BuildingType.PIG_STALL);
	}

	public boolean canDoOnFarm(Player player, DirPoint pos) {
		return movedBuilding == null
				? player.canUnpurchase(BuildingType.STALL, pos, false)
						|| player.canUnpurchase(BuildingType.COW_STALL, pos, false)
						|| player.canUnpurchase(BuildingType.PIG_STALL, pos, false)
				: player.canPurchase(movedBuilding.getType(), getCost(player), pos);
	}

	public boolean canUndoOnFarm(Player player, DirPoint pos) {
		return false;
	}

	public UndoableFarmEdit doOnFarm(Player player, DirPoint pos) {
		if (canDoOnFarm(player, pos)) {
			if (movedBuilding == null) {
				Building b = player.farm.getBuilding(pos);
				Materials cost = b.getPaidCost();
				movedBuilding = player.unpurchase(b.getType(), pos, false);
				movedBuilding.setPaidCost(cost);
				player.removeMaterial(cost);
				UndoableFarmEdit edit = new MoveBuilding(player, new DirPoint(pos), movedBuilding, true);
				return edit;
			} else {
				Materials cost = movedBuilding.getPaidCost();
				player.purchase(movedBuilding, cost, pos);
				player.addMaterial(cost);
				UndoableFarmEdit edit = new MoveBuilding(player, new DirPoint(pos), movedBuilding, false);
				movedBuilding = null;
				return edit;
			}
		}
		return null;
	}

	public boolean isUsedEnough() {
		// optional, but must be in consistent state
		return movedBuilding == null;
	}

	protected class MoveBuilding extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player player;
		private final DirPoint pos;
		private final Building building;
		private final boolean take;

		public MoveBuilding(Player player, DirPoint pos, Building building, boolean add) {
			super(true);
			this.player = player;
			this.pos = pos;
			this.building = building;
			this.take = add;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			boolean done;
			Materials cost = building.getPaidCost();
			if (take) {
				done = player.purchase(building, cost, pos);
				player.addMaterial(cost);
				movedBuilding = null;
			} else {
				done = player.unpurchase(building.getType(), pos, false) == building;
				building.setPaidCost(cost);
				player.removeMaterial(cost);
				movedBuilding = building;
			}
			if (!done) {
				throw new CannotRedoException();
			}
		}

		public void redo() throws CannotRedoException {
			super.redo();
			boolean done;
			Materials cost = building.getPaidCost();
			if (take) {
				done = player.unpurchase(building.getType(), pos, false) == building;
				building.setPaidCost(cost);
				player.removeMaterial(cost);
				movedBuilding = building;
			} else {
				done = player.purchase(building, cost, pos);
				player.addMaterial(cost);
				movedBuilding = null;
			}
			if (!done) {
				throw new CannotUndoException();
			}
		}

		public String getPresentationName() {
			return MoveStallsAndTroughs.this.getType().shortDesc;
		}

	}

	public boolean isQuickAction() {
		return false;
	}

	public boolean canDoDuringBreeding() {
		return true;
	}

	public String getButtonIconName() {
		return "move_stalls";
	}

}
