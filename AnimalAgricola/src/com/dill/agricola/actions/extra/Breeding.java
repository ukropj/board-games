package com.dill.agricola.actions.extra;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.support.Namer;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

public class Breeding extends AbstractAction {

	public Breeding() {
		super(null);
	}

	public boolean isPurchaseAction() {
		return false;
	}

	public boolean isResourceAction() {
		return false;
	}

	public boolean canDo(Player player) {
		return true;
	}

	public UndoableFarmEdit doo(Player player) {
		Animals newAnimals = new Animals();
		boolean hasInseminationCenter = player.farm.hasBuilding(BuildingType.INSEMINATION_CENTER);

		for (Animal type : Animal.values()) {
			int count = player.getAnimal(type);
			if (count >= 2 || (hasInseminationCenter && count == 1)) {
				newAnimals.add(type, 1);
			}
		}
		if (newAnimals.size() > 0) {
			UndoableFarmEdit edit = new BreedAnimals(player, new Animals(newAnimals));
			player.purchaseAnimals(newAnimals);
			player.setLastBornAnimals(newAnimals);
			return edit;
		}
		return null;
	}

	public boolean canDoOnFarm(Player player, DirPoint pos, int count) {
		return false;
	}

	public boolean canUndoOnFarm(Player player, DirPoint pos, int count) {
		return false;
	}

	public UndoableFarmEdit doOnFarm(Player player, DirPoint pos, int count) {
		return null;
	}

	protected class BreedAnimals extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player player;
		private final Animals newAnimals;

		public BreedAnimals(Player player, Animals newAnimals) {
			super(true);
			this.player = player;
			this.newAnimals = newAnimals;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			boolean done = player.unpurchaseAnimals(newAnimals);
			player.setLastBornAnimals(new Animals());
			if (!done) {
				throw new CannotUndoException();
			}
		}

		public void redo() throws CannotRedoException {
			super.redo();
			boolean done = player.purchaseAnimals(newAnimals);
			player.setLastBornAnimals(newAnimals);
			if (!done) {
				throw new CannotRedoException();
			}
		}

		public boolean isAnimalEdit() {
			return newAnimals.size() > 0;
		}

		public String getPresentationName() {
			return Namer.getName(this);
		}

	}

}
