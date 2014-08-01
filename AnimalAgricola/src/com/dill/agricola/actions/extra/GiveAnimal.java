package com.dill.agricola.actions.extra;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

public class GiveAnimal extends AbstractAction {

	public final Animals animals;

	public GiveAnimal(ActionType type, Animals animals) {
		super(type);
		this.animals = animals;
	}
	
	public boolean canDo(Player player) {
		return player.canUnpurchaseAnimals(animals);
	}

	public boolean canDoOnFarm(Player player, DirPoint pos) {
		return false;
	}

	public boolean canUndoOnFarm(Player player, DirPoint pos) {
		return false;
	}

	public UndoableFarmEdit doo(Player player) {
		UndoableFarmEdit edit = new GiveAnimals(player, new Animals(animals));
		player.unpurchaseAnimals(animals);
		return joinEdits(true, edit);
	}

	public UndoableFarmEdit doOnFarm(Player player, DirPoint pos) {
		return null;
	}

	private class GiveAnimals extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player player;
		private final Animals givenAnimals;

		public GiveAnimals(Player player, Animals animals) {
			super(true);
			this.player = player;
			this.givenAnimals = animals;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			player.purchaseAnimals(givenAnimals);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			player.unpurchaseAnimals(givenAnimals);
		}

	}

}
