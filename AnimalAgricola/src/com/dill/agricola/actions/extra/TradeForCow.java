package com.dill.agricola.actions.extra;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Animals;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.support.Msg;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.UiFactory;
import com.dill.agricola.view.utils.AgriImages.ImgSize;

public class TradeForCow extends AbstractAction {

	public TradeForCow() {
		super(ActionType.SWITCH_FOR_COW);
	}

	public boolean canDo(Player player) {
		return player.farm.hasBuilding(BuildingType.CATTLE_FARM) && player.getAnimals() - player.getAnimal(Animal.COW) > 0;
	}

	public boolean canDoOnFarm(Player player, DirPoint pos) {
		return false;
	}

	public boolean canUndoOnFarm(Player player, DirPoint pos) {
		return false;
	}

	public UndoableFarmEdit doo(Player player) {
		if (canDo(player)) {
			List<Animal> animalsToChange = new ArrayList<Animal>();
			for (Animal a : Animal.values()) {
				if (a != Animal.COW && player.getAnimal(a) > 0) {
					animalsToChange.add(a);
				}
			}
			List<JComponent> opts = new ArrayList<JComponent>();
			for (Animal a : animalsToChange) {
				JComponent opt = UiFactory.createResourcesPanel(null, new Animals(a), UiFactory.X_AXIS);
				opt.setPreferredSize(new Dimension(40, 30));
				opts.add(opt);
			}
			JComponent opt = UiFactory.createResourcesPanel(null, new Animals(), UiFactory.X_AXIS);
			opt.setPreferredSize(new Dimension(40, 30));
			opts.add(opt);
			Icon icon = AgriImages.getAnimalIcon(Animal.COW, ImgSize.MEDIUM);
			int result = UiFactory.showOptionDialog(null, Msg.get("chooseAnimal"), getType().shortDesc, icon, opts, 0);

			Animal chosenAnimal = result != UiFactory.NO_OPTION && result != animalsToChange.size() ? animalsToChange.get(result) : null;
			if (chosenAnimal != null) {
				player.unpurchaseAnimal(chosenAnimal, 1);
				player.purchaseAnimal(Animal.COW, 1);
				return new SwitchForCow(player, chosenAnimal);
			}
		}
		return null;
	}

	public UndoableFarmEdit doOnFarm(Player player, DirPoint pos) {
		return null;
	}

	private class SwitchForCow extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player player;
		private final Animal switchedType;

		public SwitchForCow(Player player, Animal switchedType) {
			this.player = player;
			this.switchedType = switchedType;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			player.unpurchaseAnimal(Animal.COW, 1);
			player.purchaseAnimal(switchedType, 1);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			player.unpurchaseAnimal(switchedType, 1);
			player.purchaseAnimal(Animal.COW, 1);
		}

	}

}
