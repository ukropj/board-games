package com.dill.agricola.actions.extra;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.actions.FeatureAction;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.support.Msg;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.UiFactory;

public class TradeAnimals extends AbstractAction implements FeatureAction {

	public TradeAnimals() {
		super(ActionType.TRADE_ANIMALS);
	}

	public boolean canDo(Player player) {
		return player.farm.hasBuilding(BuildingType.ANIMAL_TRADER) && player.getAnimalTypes().size() >= 2;
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
			List<Animal> types = new ArrayList<Animal>(player.getAnimalTypes());
			// pick animals to sell
			List<Animals> sellCombinations = new ArrayList<Animals>();
			List<JComponent> opts = new ArrayList<JComponent>();
			for (int i = 0; i < types.size(); i++) {
				for (int j = i + 1; j < types.size(); j++) {
					Animals toSell = new Animals(types.get(i), types.get(j));
					sellCombinations.add(toSell);
					JComponent opt = UiFactory.createResourcesPanel(null, toSell, UiFactory.X_AXIS);
					opt.setPreferredSize(new Dimension(80, 30));
					opts.add(opt);
				}
			}
			Icon icon = AgriImages.getAnimalIcon(null, ImgSize.MEDIUM);
			int sellResult = UiFactory.showOptionDialog(null, Msg.get("chooseAnimalsToSell"), getType().shortDesc, icon, opts, 0);

			Animals animalsToSell = sellResult != UiFactory.NO_OPTION ? sellCombinations.get(sellResult) : null;
			if (animalsToSell == null) {
				return null;
			}
			// pick animal to buy
			List<Animals> buyCombinations = new ArrayList<Animals>();
			opts.clear();
			for (Animal a : Animal.values()) {
				if (animalsToSell.get(a) == 0) {
					// can buy only other than what is being sold
					Animals toBuy = new Animals(a);
					buyCombinations.add(toBuy);
					JComponent opt = UiFactory.createResourcesPanel(null, toBuy, UiFactory.X_AXIS);
					opt.setPreferredSize(new Dimension(40, 30));
					opts.add(opt);
				}
			}
			int buyResult = UiFactory.showOptionDialog(null, Msg.get("chooseAnimalsToBuy"), getType().shortDesc, icon, opts, 0);

			Animals animalsToBuy = buyResult != UiFactory.NO_OPTION ? buyCombinations.get(buyResult) : null;
			if (animalsToBuy == null) {
				return null;
			}

			player.unpurchaseAnimals(animalsToSell);
			player.purchaseAnimals(animalsToBuy);
			setChanged();
			return new Trade(player, animalsToSell, animalsToBuy);
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
		private final Animals toSell;
		private final Animals toBuy;

		public Trade(Player player, Animals toSell, Animals toBuy) {
			this.player = player;
			this.toSell = toSell;
			this.toBuy = toBuy;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			player.unpurchaseAnimals(toBuy);
			player.purchaseAnimals(toSell);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			player.unpurchaseAnimals(toSell);
			player.purchaseAnimals(toBuy);
		}

	}

	public boolean isQuickAction() {
		return true;
	}
	
	public boolean canDoDuringBreeding() {
		return false;
	}

	public String getButtonIconName() {
		return "trade_animals";
	}

}
