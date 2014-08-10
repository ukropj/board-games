package com.dill.agricola.actions.extra;

import javax.swing.Icon;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.support.Msg;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.UiFactory;

public class CarveStone extends AbstractAction {

	static private final Materials ONE_STONE = new Materials(Material.STONE);
	private int carvedStones = 0;

	public CarveStone() {
		super(ActionType.CARVE_STONE);
	}

	public void reset() {
		super.reset();
		carvedStones = 0;
	}

	public boolean canDo(Player player) {
		return player.farm.hasBuilding(BuildingType.STONE_CARVERS_WORKSHOP) && player.canPay(ONE_STONE);
	}

	public boolean canDoOnFarm(Player player, DirPoint pos) {
		return false;
	}

	public boolean canUndoOnFarm(Player player, DirPoint pos) {
		return false;
	}

	public UndoableFarmEdit doo(Player player) {
		if (canDo(player)) {
			Icon icon = AgriImages.getMaterialIcon(Material.STONE);
			boolean result = UiFactory.showQuestionDialog(null, Msg.get("carveStoneQuestion"), getType().shortDesc, icon);
			if (result) {
				player.removeMaterial(ONE_STONE);
				carvedStones++;
				setChanged();
				return new Carve(player);
			}
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

	public int getCarvedStones() {
		return carvedStones;
	}

	private class Carve extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player player;

		public Carve(Player player) {
			this.player = player;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			carvedStones--;
			player.addMaterial(ONE_STONE);
			setChanged();
		}

		public void redo() throws CannotRedoException {
			super.redo();
			carvedStones++;
			player.removeMaterial(ONE_STONE);
			setChanged();
		}

	}

}
