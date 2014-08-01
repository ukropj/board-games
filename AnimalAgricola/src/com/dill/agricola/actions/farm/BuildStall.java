package com.dill.agricola.actions.farm;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.GeneralSupply.Supplyable;
import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.extra.FreeBorders;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.buildings.Stall;
import com.dill.agricola.model.buildings.evenmore.Barn;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

public class BuildStall extends BuildAction {

	private final Action fencesAction = new FreeBorders(2);

//	private final Action stablesSubaction = new FreeStables();

	public BuildStall() {
		super(ActionType.STALLS, BuildingType.STALL);
	}

	protected boolean isAnyLeft() {
		return GeneralSupply.getLeft(Supplyable.STALL) > 0;
	}

	protected Materials getCost(Player player) {
		return Stall.COST;
	}

	protected Building getBuildingInstance(BuildingType type) {
		return GeneralSupply.useStall();
	}

	public boolean canDoOnFarm(Player player, DirPoint pos) {
		return getUseCount() < 1 && super.canDoOnFarm(player, pos);
	}

	protected UndoableFarmEdit postActivate(Player player, Building b, DirPoint pos) {
		return new UseStall((Stall) b);
	}

	public Action getSubAction(Player player, boolean afterFarmAction) {
		if (afterFarmAction && player.farm.hasBuilding(BuildingType.CARPENTERS_WORKSHOP)) {
			return fencesAction;
		}
		return null;
	}

	/*public Action getSubAction(Player player, boolean afterFarmAction) {
		if (afterFarmAction && player.farm.hasBuilding(BuildingType.BARN)) {
			Barn barn = (Barn) player.farm.getBuilding(BuildingType.BARN);
			if (barn.canActivate()) {
				UndoableFarmEdit stablesEdit = stablesSubaction.doOnFarm(player, pos);
				UndoableFarmEdit activateBarn = new ActivateBarn(barn);
				return joinEdits(edit, stablesEdit, activateBarn);
			}
		}
	}*/

	protected class UseStall extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private Stall stall;

		public UseStall(Stall stall) {
			this.stall = stall;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			GeneralSupply.unuseStall(stall);
			setChanged();
		}

		public void redo() throws CannotRedoException {
			super.redo();
			stall = GeneralSupply.useStall();
			setChanged();
		}

	}

	protected class ActivateBarn extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private Barn barn;

		public ActivateBarn(Barn barn) {
			this.barn = barn;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			barn.activate(false);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			barn.activate(true);
		}

	}
}
