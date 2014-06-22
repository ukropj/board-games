package com.dill.agricola.actions.simple;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.support.Namer;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

public abstract class AnimalRefillAction extends AbstractAction {

	protected final Animal first;
	protected final Animal other;

	protected final Animals animals = new Animals();

	public AnimalRefillAction(ActionType type, Animal first, Animal other) {
		super(type);
		this.first = first;
		this.other = other;
	}

	public void reset() {
		super.reset();
		animals.clear();
		setChanged();
	}

	public UndoableFarmEdit init() {
		Animal added = null;
		if (animals.get(first) == 0) {
			animals.add(first, 1);
			added = first;
		} else {
			animals.add(other, 1);
			added = other;
		}
		return joinEdits(super.init(), new RefillAnimals(new Animals(added)));
	}

	public boolean canDo(Player player) {
		return !animals.isEmpty();
	}

	public UndoableFarmEdit doo(Player player) {
		if (canDo(player)) {
			UndoableFarmEdit edit = new TakeAnimals(player, new Animals(animals));
			player.purchaseAnimals(animals);
			animals.clear();
			setChanged();
			return joinEdits(true, edit);
		}
		return null;
	}

	public Animals getAccumulatedAnimals() {
		return animals;
	}

	public boolean canDoOnFarm(Player player, DirPoint pos) {
		return false;
	}

	public boolean canUndoOnFarm(Player player, DirPoint pos) {
		return false;
	}

	public UndoableFarmEdit doOnFarm(Player player, DirPoint pos) {
		return null;
	}

	protected class TakeAnimals extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player player;
		private final Animals takenAnimals;

		public TakeAnimals(Player player, Animals animals) {
			super(true);
			this.player = player;
			this.takenAnimals = animals;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			boolean done = player.unpurchaseAnimals(takenAnimals);
			if (!done) {
				throw new CannotUndoException();
			}
			animals.add(takenAnimals);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			animals.substract(takenAnimals);
			setChanged();
			boolean done = player.purchaseAnimals(takenAnimals);
			if (!done) {
				throw new CannotRedoException();
			}
		}

		public String getPresentationName() {
			return Namer.getName(this);
		}

	}
	
	protected class RefillAnimals extends SimpleEdit {
		private static final long serialVersionUID = 1L;
		
		private final Animals added;
		
		public RefillAnimals(Animals added) {
			this.added = added;
		}
		
		public void undo() throws CannotUndoException {
			super.undo();
			animals.substract(added);
			setChanged();
		}
		
		public void redo() throws CannotRedoException {
			super.redo();
			animals.add(added);
			setChanged();
		}
		
		public String getPresentationName() {
			return Namer.getName(this);
		}
	}

}
