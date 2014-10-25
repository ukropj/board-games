package com.dill.agricola.actions.simple;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.buildings.evenmore.TimberShop;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

public abstract class MaterialRefillAction extends AbstractAction {

	protected final Materials refill;

	protected final Materials materials = new Materials();
	
	protected final boolean triggerTimberShop;
	
	public MaterialRefillAction(ActionType type, Materials refill) {
		this(type, refill, false);
	}

	public MaterialRefillAction(ActionType type, Materials refill, boolean triggerTimberShop) {
		super(type);
		this.refill = refill;
		this.triggerTimberShop = triggerTimberShop;
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
	
	public UndoableFarmEdit initUsed() {
		UndoableFarmEdit ts = triggerTimberShop && isUsed() ? TimberShop.takeTopAction(false) : null;
		return joinEdits(super.initUsed(), new RefillMaterials(refill), ts);
	}

	public boolean canDo(Player player) {
		return !materials.isEmpty();
	}

	public UndoableFarmEdit doo(Player player) {
		if (canDo(player)) {
			UndoableFarmEdit edit = new TakeMaterials(player, new Materials(materials));
			player.addMaterial(materials);
			materials.clear();
			
			if (triggerTimberShop) {
				UndoableFarmEdit takeActionEdit = TimberShop.takeTopAction(true);
				UndoableFarmEdit timberReward = TimberShop.checkReward(false);
				edit = joinEdits(edit, takeActionEdit, timberReward);				
			}
			
			setChanged();
			return joinEdits(true, edit);
		}
		return null;
	}

	public Materials getAccumulatedMaterials() {
		return materials;
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
