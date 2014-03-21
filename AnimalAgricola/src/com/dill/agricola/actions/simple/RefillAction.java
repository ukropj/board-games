package com.dill.agricola.actions.simple;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.undo.SimpleEdit;

public abstract class RefillAction extends AbstractAction {

	protected final Materials refill;

	protected final Materials materials = new Materials();

	public RefillAction(ActionType type, Materials refill) {
		super(type);
		this.refill = refill;
	}

	public void reset() {
		super.reset();
		materials.clear();
		setChanged();
	}

	public UndoableEdit init() {
		materials.add(refill);
		setChanged();
		return joinEdits(super.init(), new RefillMaterials(refill));
	}

	public boolean canDo(Player player, int doneSoFar) {
		return !materials.isEmpty();
	}

	public boolean canUndo(Player player, int doneSoFar) {
		return false;
	}

	public boolean isQuickAction() {
		return true;
	}
	
	public boolean isPurchaseAction() {
		return false;
	}

	public boolean isResourceAction() {
		return true;
	}

	public UndoableEdit doo(Player player, int doneSoFar) {
		if (canDo(player, doneSoFar)) {
			UndoableEdit edit = new TakeMaterials(player, new Materials(materials));
			player.addMaterial(materials);
			materials.clear();
			setChanged();
			return edit;
		}
		return null;
	}

	public boolean undo(Player player, int doneSoFar) {
		// TODO remove
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
	protected class TakeMaterials extends SimpleEdit {

		private final Player player;
		private final Materials takenMaterials;
		
		public TakeMaterials(Player player, Materials materials) {
			this.player = player;
			this.takenMaterials = materials;
		}
		
		public void undo() throws CannotUndoException {
			super.undo();
			player.removeMaterial(takenMaterials);
			materials.add(takenMaterials);
		}
		
		public void redo() throws CannotRedoException {
			super.redo();
			materials.substract(takenMaterials);
			setChanged();
			player.addMaterial(takenMaterials);
		}
		
	}
	
	@SuppressWarnings("serial")
	protected class RefillMaterials extends SimpleEdit {
		
		private final Materials added;
		
		public RefillMaterials(Materials added) {
			super(false);
			this.added = added;
		}
		
		public void undo() throws CannotUndoException {
			super.undo();
			materials.substract(added);
			setChanged();
		}
		
		public void redo() throws CannotRedoException {
			super.redo();
			materials.add(added);
			setChanged();
		}
		
	}
}
