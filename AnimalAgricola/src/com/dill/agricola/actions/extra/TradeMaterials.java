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

public class TradeMaterials extends AbstractAction {

	public TradeMaterials() {
		super(ActionType.TRADE_MATERIALS);
	}

	public boolean canDo(Player player) {
		return player.farm.hasBuilding(BuildingType.TRADING_STATION) && player.getMaterialTypes().size() >= 2;
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
			List<Material> types = new ArrayList<Material>(player.getMaterialTypes());
			types.remove(Material.BORDER);
			// pick materials to sell
			List<Materials> sellCombinations = new ArrayList<Materials>();
			List<JComponent> opts = new ArrayList<JComponent>();
			for (int i = 0; i < types.size(); i++) {
				for (int j = i + 1; j < types.size(); j++) {
					Materials toSell = new Materials(types.get(i), types.get(j));
					sellCombinations.add(toSell);
					JComponent opt = UiFactory.createResourcesPanel(toSell, null, UiFactory.X_AXIS);
					opt.setPreferredSize(new Dimension(80, 30));
					opts.add(opt);
				}
			}
			JComponent emptyOpt = UiFactory.createResourcesPanel(Materials.EMPTY, null, UiFactory.X_AXIS);
			emptyOpt.setPreferredSize(new Dimension(40, 30));
			opts.add(emptyOpt);
			Icon icon = AgriImages.getMaterialIcon(null);
			int sellResult = UiFactory.showOptionDialog(null, Msg.get("chooseMaterialsToSell"), getType().shortDesc, icon, opts, 0);

			Materials materialsToSell = sellResult != UiFactory.NO_OPTION && sellResult != sellCombinations.size() ? sellCombinations.get(sellResult) : null;
			if (materialsToSell == null) {
				return null;
			}
			// pick animal to buy
			List<Animals> buyCombinations = new ArrayList<Animals>();
			opts.clear();
			for (Animal a : Animal.values()) {
				Animals toBuy = new Animals(a);
				buyCombinations.add(toBuy);
				JComponent opt = UiFactory.createResourcesPanel(null, toBuy, UiFactory.X_AXIS);
				opt.setPreferredSize(new Dimension(40, 30));
				opts.add(opt);
			}
			int buyResult = UiFactory.showOptionDialog(null, Msg.get("chooseAnimalsToBuy"), getType().shortDesc, icon, opts, 0);

			Animals animalsToBuy = buyResult != UiFactory.NO_OPTION ? buyCombinations.get(buyResult) : null;
			if (animalsToBuy == null) {
				return null;
			}

			player.removeMaterial(materialsToSell);
			player.purchaseAnimals(animalsToBuy);
			setChanged();
			return new Trade(player, materialsToSell, animalsToBuy);
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

	private class Trade extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player player;
		private final Materials toSell;
		private final Animals toBuy;

		public Trade(Player player, Materials materialsToSell, Animals toBuy) {
			this.player = player;
			this.toSell = materialsToSell;
			this.toBuy = toBuy;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			player.unpurchaseAnimals(toBuy);
			player.addMaterial(toSell);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			player.removeMaterial(toSell);
			player.purchaseAnimals(toBuy);
		}

	}

}
