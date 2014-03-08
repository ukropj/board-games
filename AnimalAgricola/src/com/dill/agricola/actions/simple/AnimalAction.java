package com.dill.agricola.actions.simple;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;

public abstract class AnimalAction extends AbstractAction {

	protected final Animal first;
	protected final Animal other;

	protected final Animals animals = new Animals();
	protected final Animals lastTakenAnimals = new Animals();	

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

	public void init() {
		super.init();
		if (animals.get(first) == 0) {
			animals.add(first, 1);
		} else {
			animals.add(other, 1);
		}
		lastTakenAnimals.clear();
		setChanged();
	}
	
	public boolean isFarmAction() {
		return false;
	}
	
	public boolean canPerform(Player player, int doneSoFar) {
		return !isUsed() && doneSoFar == 0;
	}

	public boolean canUnperform(Player player, int doneSoFar) {
		return doneSoFar > 0;
	}

	public boolean activate(Player player, int doneSoFar) {
		player.purchaseAnimals(animals);
		lastTakenAnimals.set(animals);
		animals.clear();
		setChanged();
		return true;
	}

	public boolean undo(Player player, int doneSoFar) {
		boolean done = player.unpurchaseAnimals(lastTakenAnimals);
		if (done) {
			animals.set(lastTakenAnimals);
			lastTakenAnimals.clear();
			setChanged();
		}
		return done;
	}
	
	public Animals getAccumulatedAnimals() {
		return animals;
	}

	public boolean canPerform(Player player, DirPoint pos, int count) {
		return false;
	}
	
	public boolean canUnperform(Player player, DirPoint pos, int count) {
		return false;
	}

	public boolean activate(Player player, DirPoint pos, int count) {
		return false;
	}

	public boolean undo(Player player, DirPoint pos, int count) {
		return false;
	}

}
