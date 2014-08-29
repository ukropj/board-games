package com.dill.agricola.model.buildings.evenmore;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.simple.MaterialAction;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

public class TimberShop extends Building {

	public final static Materials COST = new Materials(Material.STONE, 2);
	public final static Action EXTRA_ACTION =
			new MaterialAction(ActionType.BUILDING_REWARD, new Materials(Material.WOOD, 3));
	private static final int TOP_COUNT = 4;
	
	private static int topActionsTaken = 0;
	private static Player owner;

	public TimberShop() {
		super(BuildingType.TIMBER_SHOP, 1, 0);
	}

	public static UndoableFarmEdit takeTopAction(boolean take) {
		topActionsTaken += take ? 1 : -1;
		return new TakeAction(take);
	}

	public static UndoableFarmEdit checkReward(boolean justBuilt) {
		// player's buildings list would not be yet updated, that's why justBuilt flag is used
		if (topActionsTaken == TOP_COUNT && owner != null && (justBuilt || owner.farm.hasBuilding(BuildingType.TIMBER_SHOP))) {
			return EXTRA_ACTION.doo(owner);
		}
		return null;
	}

	public void setOwner(Player player) {
		owner = player;
	}

	protected static class TakeAction extends SimpleEdit {
		private static final long serialVersionUID = 1L;
		private int d;

		public TakeAction(boolean take) {
			this.d = take ? 1 : -1;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			topActionsTaken -= d;
		}

		public void redo() throws CannotRedoException {
			super.redo();
			topActionsTaken += d;
		}

	}

}
