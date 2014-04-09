package com.dill.agricola.actions.farm;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.GeneralSupply.Supplyable;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.support.Namer;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

public class Expand extends PurchaseAction {

	public final static Materials REFILL = new Materials(Material.BORDER, 1);
	public final static Materials COST = new Materials();

	protected final Materials materials = new Materials();

	private boolean hadExp = false;

	public Expand() {
		super(ActionType.EXPAND, Purchasable.EXTENSION);
	}

	public void reset() {
		super.reset();
		materials.clear();
	}

	public UndoableFarmEdit init() {
		materials.add(REFILL);
		hadExp = false;
		return joinEdits(super.init(), new RefillMaterials(REFILL));
	}
	
	public boolean isPurchaseAction() {
		return hadExp || isAnyLeft();
	}
	
	public boolean isResourceAction() {
		return true;
	}
	
	public int getMinimalCount() {
		// first is fences, second is extension (that may not happen if not any left)
		return (hadExp || isAnyLeft()) ? 2 : 1; 
	}

	protected Materials getCost(int doneSoFar) {
		return COST;
	}

	protected boolean isAnyLeft() {
		return GeneralSupply.getLeft(Supplyable.EXTENSION) > 0;
	}

	public boolean canDo(Player player) {
		return !materials.isEmpty();
	}

	public boolean canDoOnFarm(Player player, DirPoint pos, int doneSoFar) {
		return doneSoFar == 1 && isAnyLeft() && player.canPurchase(thing, getCost(doneSoFar), pos);
	}

	public boolean canUndoOnFarm(Player player, DirPoint pos, int doneSoFar) {
		return hadExp && player.canUnpurchase(thing, pos, true);
	}

	public UndoableFarmEdit doo(Player player) {
		if (canDo(player)) {
			UndoableFarmEdit edit = new TakeMaterials(player, new Materials(materials));
			player.addMaterial(materials);
			materials.clear();
			player.setActiveType(thing);
			setChanged();
			return edit;
		}
		return null;
	}
	
	protected UndoableFarmEdit postActivate() {
		GeneralSupply.useExtension(true);
		hadExp = true;
		return new UseExtension();
	}

	public Materials getAccumulatedMaterials() {
		return materials;
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
		}
		
		public void redo() throws CannotRedoException {
			super.redo();
			materials.substract(takenMaterials);
			setChanged();
			player.addMaterial(takenMaterials);
		}
		
		public String getPresentationName() {
			return Namer.getName(this);
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
		
		public String getPresentationName() {
			return Namer.getName(this);
		}
	}
	
	protected class UseExtension extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		public void undo() throws CannotUndoException {
			super.undo();
			GeneralSupply.useExtension(false);
			hadExp = false;
			setChanged();
		}

		public void redo() throws CannotRedoException {
			super.redo();
			GeneralSupply.useExtension(true);
			hadExp = true;
			setChanged();
		}

	}

}
