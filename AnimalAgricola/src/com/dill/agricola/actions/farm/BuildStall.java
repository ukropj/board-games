package com.dill.agricola.actions.farm;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.GeneralSupply.Supplyable;
import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.extra.FreeBorders;
import com.dill.agricola.actions.extra.FreeStables;
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
	private final Action stablesAction = new FreeStables();

	protected final boolean allowOne;  
	
	public BuildStall() {
		this(ActionType.STALLS, true);
	}
	
	public BuildStall(ActionType type, boolean allowOne) {
		super(type, BuildingType.STALL);
		this.allowOne = allowOne;
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
		return allowOne ? getUseCount() < 1 : true && super.canDoOnFarm(player, pos);
	}
	
	public boolean canUndoOnFarm(Player player, DirPoint pos) {
		return super.canUndoOnFarm(player, pos) 
				// for barn
				|| (player.farm.hasBuilding(BuildingType.BARN) && player.canUnpurchase(BuildingType.STABLES, pos, true)); 
	}

	protected UndoableFarmEdit postActivate(Player player, Building b, DirPoint pos) {
		UndoableFarmEdit edit = new UseStall((Stall) b);
		if (player.farm.hasBuilding(BuildingType.BARN)) {
			Barn barn = (Barn) player.farm.getBuilding(BuildingType.BARN);
			if (barn.canUse()) {
				barn.use(true);
				return joinEdits(edit, new UseBarn(barn), stablesAction.doOnFarm(player, pos));
			}
		}
		return edit;
	}

	public Action getSubAction(Player player, boolean afterFarmAction) {
		if (afterFarmAction && player.farm.hasBuilding(BuildingType.CARPENTERS_WORKSHOP)) {
			return fencesAction;
		}
		return null;
	}

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

	protected class UseBarn extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private Barn barn;

		public UseBarn(Barn barn) {
			this.barn = barn;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			barn.use(false);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			barn.use(true);
		}

	}
}
