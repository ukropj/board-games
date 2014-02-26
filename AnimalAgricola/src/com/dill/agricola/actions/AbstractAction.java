package com.dill.agricola.actions;

import java.util.ArrayList;
import java.util.List;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.support.Namer;

public abstract class AbstractAction implements Action {

	private final ActionType type;
	protected boolean used = false;
	protected final List<StateChangeListener> changeListeners = new ArrayList<StateChangeListener>();

	protected AbstractAction(ActionType type) {
		this.type = type;
	}

	public ActionType getType() {
		return type;
	}

	public void reset() {
		used = false;
	}

	public void init() {
		used = false;
	}

	public void setUsed() {
		used = true;
	}

	public boolean isUsed() {
		return used;
	}

	public boolean canPerform(Player player) {
		return !used;
	}

	public boolean canPerformMore(Player player, int doneSoFar) {
		return false;
	}

	public boolean doOnce(Player player) {
		return false;
	}

	public boolean undoOnce(Player player) {
		return false;
	}

	public boolean doo(Player player) {
		return false;
	}

	public boolean undo(Player player) {
		return false;
	}

	public String toString() {
		return "<html>" + Namer.getName(this);
	}

	public Materials getAccumulatedMaterials() {
		return null;
	}

	public Animals getAccumulatedAnimals() {
		return null;
	}
	
	public void addChangeListener(StateChangeListener changeListener) {
		this.changeListeners.add(changeListener);
	}
	
	protected void setChanged() {
		for (StateChangeListener listener : changeListeners) {
			listener.stateChanges(this);
		}
	}
}
