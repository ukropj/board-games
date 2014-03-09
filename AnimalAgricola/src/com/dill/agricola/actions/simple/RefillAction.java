package com.dill.agricola.actions.simple;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.DirPoint;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;

public abstract class RefillAction extends AbstractAction {

	protected final Materials refill;

	protected final Materials materials = new Materials();
	protected final Materials lastTakenMaterials = new Materials();

	public RefillAction(ActionType type, Materials refill) {
		super(type);
		this.refill = refill;
	}

	public void reset() {
		super.reset();
		materials.clear();
	}

	public void init() {
		super.init();
		materials.add(refill);
		lastTakenMaterials.clear();
		setChanged();
	}

	public boolean canPerform(Player player, int doneSoFar) {
		return !isUsed() && !materials.isEmpty();
	}

	public boolean canUnperform(Player player, int doneSoFar) {
		return !lastTakenMaterials.isEmpty();
	}

	public boolean isFarmAction() {
		return false;
	}

	public boolean activate(Player player, int doneSoFar) {
		if (canPerform(player, doneSoFar)) {
			player.addMaterial(materials);
			lastTakenMaterials.set(materials);
			materials.clear();
			setChanged();
			return true;
		}
		return false;
	}

	public boolean undo(Player player, int doneSoFar) {
		if (canUnperform(player, doneSoFar)) {
			player.removeMaterial(lastTakenMaterials);
			materials.set(lastTakenMaterials);
			lastTakenMaterials.clear();
			setChanged();
			// TODO check if removed?
			return true;
		}
		return false;
	}

	public Materials getAccumulatedMaterials() {
		return materials;
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