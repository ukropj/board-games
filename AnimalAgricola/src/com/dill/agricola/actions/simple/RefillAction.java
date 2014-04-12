package com.dill.agricola.actions.simple;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

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

	public UndoableFarmEdit init() {
		materials.add(refill);
		setChanged();
		return joinEdits(super.init(), new RefillMaterials(refill));
	}

	public boolean canDo(Player player) {
		return !materials.isEmpty();
	}
	
	public boolean isPurchaseAction() {
		return false;
	}

	public boolean isResourceAction() {
		return true;
	}

	public UndoableFarmEdit doo(Player player) {
		if (canDo(player)) {
			UndoableFarmEdit edit = new TakeMaterials(player, new Materials(materials));
			player.addMaterial(materials);
			materials.clear();
			setChanged();
			return edit;
		}
		return null;
	}

	public Materials getAccumulatedMaterials() {
		return materials;
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

	protected class TakeMaterials extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player player;
		private final Materials takenMaterials;
		
		public TakeMaterials(Player player, Materials materials) {
			super(true);
			this.player = player;
			this.takenMaterials = materials;
		}
		
		public void undo() throws CannotUndoException {
			super.undo();
			player.removeMaterial(takenMaterials);
			materials.add(takenMaterials);
			setChanged();
		}
		
		public void redo() throws CannotRedoException {
			super.redo();
			materials.substract(takenMaterials);
			setChanged();
			player.addMaterial(takenMaterials);
		}
		
	}
	
	protected class RefillMaterials extends SimpleEdit {
		private static final long serialVersionUID = 1L;
		
		private final Materials added;
		
		public RefillMaterials(Materials added) {
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
