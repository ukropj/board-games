package com.dill.agricola.actions.simple;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.ChangeType;
import com.dill.agricola.support.Namer;
import com.dill.agricola.undo.LoggingUndoableEdit;

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

	private void addMaterials(Materials add) {
		materials.add(add);
		setChanged();
	}

	private void takeMaterials(Materials take) {
		materials.substract(take);
		setChanged();
	}

	public boolean canDo(Player player, int doneSoFar) {
		return !isUsed() && !materials.isEmpty();
	}

	public boolean canUndo(Player player, int doneSoFar) {
		return !lastTakenMaterials.isEmpty();
	}

	public boolean isPurchaseAction() {
		return false;
	}

	public boolean isResourceAction() {
		return true;
	}

	public UndoableEdit doo(Player player, int doneSoFar) {
		if (canDo(player, doneSoFar)) {
			UndoableEdit edit = new TakeResources(player, new Materials(materials));
			player.addMaterial(materials);
			lastTakenMaterials.set(materials);
			materials.clear();
			setChanged();
			return edit;
		}
		return null;
	}

	public boolean undo(Player player, int doneSoFar) {
		if (canUndo(player, doneSoFar)) {
			player.removeMaterial(lastTakenMaterials);
			materials.set(lastTakenMaterials);
			lastTakenMaterials.clear();
			setChanged();
			return true;
		}
		return false;
	}

	public Materials getAccumulatedMaterials() {
		return materials;
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

	@SuppressWarnings("serial")
	private class TakeResources extends LoggingUndoableEdit {

		private final Player player;
		private final Materials materials;
		
		public TakeResources(Player player, Materials materials) {
			this.player = player;
			this.materials = materials;
		}
		
		public void undo() throws CannotUndoException {
			super.undo();
			player.removeMaterial(materials);
			addMaterials(materials);
			player.notifyObservers(ChangeType.ACTION_UNDO);
		}
		
		public void redo() throws CannotRedoException {
			super.redo();
			takeMaterials(materials);
			player.addMaterial(materials);
			player.notifyObservers(ChangeType.ACTION_DO);
		}
		
		public String getPresentationName() {
			return Namer.getName(this);
		}
		
	}
}
