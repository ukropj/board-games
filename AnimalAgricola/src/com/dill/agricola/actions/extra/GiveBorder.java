package com.dill.agricola.actions.extra;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.actions.farm.PurchaseAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

public class GiveBorder extends PurchaseAction {

	static private final Materials TWO_BORDER = new Materials(Material.BORDER, 2);
	static private final Materials ONE_BORDER = new Materials(Material.BORDER);

	private Player otherPlayer = null;

	public GiveBorder() {
		super(ActionType.GIVE_BORDERS, Purchasable.FENCE);
	}

	protected Materials getCost(Player player) {
		return ONE_BORDER;
	}

	public void setOtherPlayer(Player otherPlayer) {
		this.otherPlayer = otherPlayer;
	}

	public boolean canDo(Player player) {
		return player.farm.hasBuilding(BuildingType.ASSEMBLY_HALL)
				&& otherPlayer != null && player.canPay(TWO_BORDER)
				&& super.canDo(player);
	}

	public boolean canDoOnFarm(Player player, DirPoint pos) {
		return player.canPay(TWO_BORDER) && super.canDoOnFarm(player, pos);
	}

	protected UndoableFarmEdit postActivate(Player player) {
		player.removeMaterial(ONE_BORDER);
		otherPlayer.addMaterial(ONE_BORDER);
		return new Give(player, otherPlayer, ONE_BORDER);
	}

	public boolean isUsedEnough() {
		// optional
		return true;
	}

	private class Give extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player player;
		private final Player otherPlayer;
		private final Materials materials;

		public Give(Player player, Player otherPlayer, Materials materials) {
			this.player = player;
			this.otherPlayer = otherPlayer;
			this.materials = materials;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			otherPlayer.removeMaterial(materials);
			player.addMaterial(materials);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			player.removeMaterial(materials);
			otherPlayer.addMaterial(materials);
		}

	}

}
