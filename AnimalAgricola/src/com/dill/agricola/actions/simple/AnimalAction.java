package com.dill.agricola.actions.simple;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.support.Namer;
import com.dill.agricola.undo.SimpleEdit;

public abstract class AnimalAction extends AbstractAction {

	protected final Animal first;
	protected final Animal other;

	protected final Animals animals = new Animals();

	public AnimalAction(ActionType type, Animal first, Animal other) {
		super(type);
		this.first = first;
		this.other = other;
	}

	public void reset() {
		super.reset();
		animals.clear();
		setChanged();
	}

	public UndoableEdit init() {
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

	public boolean isQuickAction() {
		return false;
	}

	public boolean isPurchaseAction() {
		return false;
	}

	public boolean isResourceAction() {
		return true;
	}

	public boolean canDo(Player player, int doneSoFar) {
		return !animals.isEmpty();
	}

	public UndoableEdit doo(Player player, int doneSoFar) {
		if (canDo(player, doneSoFar)) {
			UndoableEdit edit = new TakeAnimals(player, new Animals(animals));
			player.purchaseAnimals(animals);
			animals.clear();
			setChanged();
			return edit;
		}
		return null;
	}

	public Animals getAccumulatedAnimals() {
		return animals;
	}

	public boolean canDo(Player player, DirPoint pos, int count) {
		return false;
	}

	public boolean canUndo(Player player, DirPoint pos, int count) {
		return false;
	}

	public UndoableEdit doo(Player player, DirPoint pos, int count) {
		return null;
	}

	public boolean undo(Player player, DirPoint pos, int count) {
		return false;
	}

	protected class TakeAnimals extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player player;
		private final Animals takenAnimals;

		public TakeAnimals(Player player, Animals animals) {
			this.player = player;
			this.takenAnimals = animals;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			player.unpurchaseAnimals(takenAnimals);
			animals.add(takenAnimals);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			animals.substract(takenAnimals);
			setChanged();
			player.purchaseAnimals(takenAnimals);
		}

		public String getPresentationName() {
			return Namer.getName(this);
		}

	}
	
	protected class RefillAnimals extends SimpleEdit {
		private static final long serialVersionUID = 1L;
		
		private final Animals added;
		
		public RefillAnimals(Animals added) {
			super(false);
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
