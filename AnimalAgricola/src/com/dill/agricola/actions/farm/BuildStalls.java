package com.dill.agricola.actions.farm;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.GeneralSupply.Supplyable;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.buildings.Stall;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

public class BuildStalls extends BuildAction {

	public BuildStalls() {
		super(ActionType.STALLS, BuildingType.STALL);
	}

	protected boolean isAnyLeft() {
		return GeneralSupply.getLeft(Supplyable.STALL) > 0;
	}
	
	protected Materials getCost(int doneSoFar) {
		return Stall.COST;
	}
	
	protected Building getBuildingInstance(BuildingType type) {
		return GeneralSupply.useStall();
	}
	
	public boolean canDoOnFarm(Player player, DirPoint pos, int doneSoFar) {
		return doneSoFar < 1 && super.canDoOnFarm(player, pos, doneSoFar);
	}
	
	protected UndoableFarmEdit postActivate(Player player, Building b) {
		return new UseStall((Stall) b);
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
}
