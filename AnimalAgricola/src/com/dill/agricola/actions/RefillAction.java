package com.dill.agricola.actions;

import com.dill.agricola.model.Player;
import com.dill.agricola.model.enums.ActionType;
import com.dill.agricola.model.enums.Materials;

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
	
	public boolean canPerform(Player player) {
		return super.canPerform(player);
	}

	public boolean doOnce(Player player) {
		player.addMaterial(materials);
		lastTakenMaterials.set(materials);
		materials.clear();
		setChanged();
		return true;
	}
	
	public boolean undoOnce(Player player) {
		player.removeMaterial(lastTakenMaterials);
		materials.set(lastTakenMaterials);
		lastTakenMaterials.clear();
		setChanged();
		// TODO check if removed?
		return true;
	}
	
	public Materials getAccumulatedMaterials() {
		return materials;
	}
	
	public String toString() {
		return super.toString() + "<br>+" + materials;
	}

}
