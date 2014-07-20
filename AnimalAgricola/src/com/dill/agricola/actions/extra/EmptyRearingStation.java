package com.dill.agricola.actions.extra;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.model.Fencer;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.buildings.more.RearingStation;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

public class EmptyRearingStation extends AbstractAction {

	private final boolean state;

	public EmptyRearingStation(boolean state) {
		super(ActionType.EMPTY_REARING_STATION);
		this.state = state;
	}

	public boolean canDo(Player player) {
		return player.farm.hasBuilding(BuildingType.REARING_STATION)
				&& RearingStation.mustBeEmpty != state
				&& (!state || player.farm.getBuilding(BuildingType.REARING_STATION).getAnimals() > 0);
	}

	public boolean canDoOnFarm(Player player, DirPoint pos) {
		return false;
	}

	public boolean canUndoOnFarm(Player player, DirPoint pos) {
		return false;
	}

	public UndoableFarmEdit doo(Player player) {
		if (canDo(player)) {
			UndoableFarmEdit edit = new MustBeEmpty(player, RearingStation.mustBeEmpty, state);
			RearingStation.mustBeEmpty = state;
			Fencer.calculateFences(player.farm);
			player.setChanged();
			return edit;
		}
		return null;
	}

	public UndoableFarmEdit doOnFarm(Player player, DirPoint pos) {
		return null;
	}

	public boolean isUsedEnough() {
		// optional
		return true;
	}

	private class MustBeEmpty extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player player;
		private final boolean old;
		private final boolean current;

		public MustBeEmpty(Player player, boolean old, boolean current) {
			this.player = player;
			this.old = old;
			this.current = current;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			RearingStation.mustBeEmpty = old;
			Fencer.calculateFences(player.farm);
			player.setChanged();
		}

		public void redo() throws CannotRedoException {
			super.redo();
			RearingStation.mustBeEmpty = current;
			Fencer.calculateFences(player.farm);
			player.setChanged();
		}

	}

	public String toString() {
		return super.toString() + " state:" + state;
	}

}
