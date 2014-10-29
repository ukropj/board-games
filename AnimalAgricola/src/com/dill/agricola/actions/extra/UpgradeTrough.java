package com.dill.agricola.actions.extra;

import java.util.Set;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.actions.FeatureAction;
import com.dill.agricola.actions.farm.BuildStall;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

public class UpgradeTrough extends BuildStall implements FeatureAction {

	private final Materials COST = new Materials(Material.STONE, 2);
	private final Materials NO_COST = new Materials();

	public UpgradeTrough() {
		super(ActionType.UPGRADE_TROUGH, false);
	}

	protected Materials getCost(Player player) {
		return COST;
	}

	public boolean canDo(Player player) {
		if (!super.canDo(player)) {
			return false;
		}
		Set<DirPoint> troughSpots = player.farm.find(Purchasable.TROUGH);
		for (DirPoint pos : troughSpots) {
			if (super.canDoOnFarm(player, pos)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean canDoOnFarm(Player player, DirPoint pos) {
		return super.canDoOnFarm(player, pos) && player.canUnpurchase(Purchasable.TROUGH, pos, false);
	}
	
	protected UndoableFarmEdit postActivate(Player player, Building b, DirPoint pos) {
		UndoableFarmEdit edit = super.postActivate(player, b, pos);
		
		player.unpurchase(Purchasable.TROUGH, NO_COST, pos, false);		
		UndoableFarmEdit unpurchaseTrough = new UnpurchaseThing(player, pos, Purchasable.TROUGH);
		
		return joinEdits(edit, unpurchaseTrough);
	}
	
	public boolean isUsedEnough() {
		// optional
		return true;
	}

	protected class UnpurchaseThing extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		protected final Player player;
		protected final DirPoint pos;
		protected final Purchasable thing;
		private final DirPoint undoPos;

		public UnpurchaseThing(Player player, DirPoint pos, Purchasable thing) {
			super(player.getColor(), pos, thing);
			this.player = player;
			this.pos = pos;
			this.thing = thing;
			this.undoPos = pos;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			boolean done = player.purchase(thing, NO_COST, undoPos);
			if (!done) {
				throw new CannotUndoException();
			}
		}

		public void redo() throws CannotRedoException {
			super.redo();
			boolean done = player.unpurchase(thing, NO_COST, pos, false);
			if (!done) {
				throw new CannotRedoException();
			}
		}

	}

	public boolean canDoDuringBreeding() {
		return true;
	}

	public String getButtonIconName() {
		return "upgrade_trough";
	}
}
