package com.dill.agricola.actions.extra;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.support.Msg;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.UiFactory;

public class TradeReed extends AbstractAction {

	static private final Materials ONE_REED = new Materials(Material.REED);
	
	public TradeReed() {
		super(ActionType.TRADE_REED);
	}

	public boolean canDo(Player player) {
		return player.farm.hasBuilding(BuildingType.CATTLE_MARKET) && player.canPay(ONE_REED);
	}

	public boolean canDoOnFarm(Player player, DirPoint pos) {
		return false;
	}

	public boolean canUndoOnFarm(Player player, DirPoint pos) {
		return false;
	}

	public UndoableFarmEdit doo(Player player) {
		// TODO refactor, together with TradeForCow
		if (canDo(player)) {
			// pick animal to buy
			List<JComponent> opts = new ArrayList<JComponent>();
			for (Animal a : Animal.values()) {
				JComponent opt = UiFactory.createResourcesPanel(null, new Animals(a), UiFactory.X_AXIS);
				opt.setPreferredSize(new Dimension(40, 30));
				opts.add(opt);
			}
			Icon icon = AgriImages.getMaterialIcon(Material.REED);
			int result = UiFactory.showOptionDialog(null, Msg.get("chooseAnimalsToBuy"), getType().shortDesc, icon, opts, 0);

			Animal chosenAnimal = result != UiFactory.NO_OPTION ? Animal.values()[result] : null;
			if (chosenAnimal == null) {
				return null;
			}

			player.removeMaterial(ONE_REED);
			player.purchaseAnimal(chosenAnimal, 1);
			setChanged();
			return new TradeForReed(player, chosenAnimal);
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

	private class TradeForReed extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player player;
		private final Animal toBuy;

		public TradeForReed(Player player, Animal toBuy) {
			this.player = player;
			this.toBuy = toBuy;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			player.unpurchaseAnimal(toBuy, 1);
			player.addMaterial(ONE_REED);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			player.removeMaterial(ONE_REED);
			player.purchaseAnimal(toBuy, 1);
		}

	}

}
