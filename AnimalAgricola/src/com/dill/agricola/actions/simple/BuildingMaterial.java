package com.dill.agricola.actions.simple;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.undo.SimpleEdit;

public class BuildingMaterial extends AbstractAction {

	public static final Materials MATERIALS = new Materials(Material.WOOD, Material.STONE, Material.REED);

	public BuildingMaterial() {
		super(ActionType.BUILDING_MATERIAL);
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
		player.addMaterial(MATERIALS);
		return new TakeMaterials(player, MATERIALS);
	}
	
	public String toString() {
		return super.toString() + "<br>" + MATERIALS;
	}

	public boolean canDo(Player player, int count) {
		return true;
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
	
	private class TakeMaterials extends SimpleEdit {
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
		}
		
		public void redo() throws CannotRedoException {
			super.redo();
			player.addMaterial(takenMaterials);
		}
		
	}

}
