package com.dill.agricola.actions.simple;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

public class BuildingMaterial extends AbstractAction {

	public static final Materials MATERIALS = new Materials(Material.WOOD, Material.STONE, Material.REED);

	public BuildingMaterial() {
		super(ActionType.BUILDING_MATERIAL);
	}
	
	public boolean isPurchaseAction() {
		return false;
	}
	
	public boolean isResourceAction() {
		return true;
	}

	public boolean canDo(Player player) {
		return true;
	}

	public boolean canDoOnFarm(Player player, DirPoint pos, int count) {
		return false;
	}

	public boolean canUndoOnFarm(Player player, DirPoint pos, int count) {
		return false;
	}

	public UndoableFarmEdit doo(Player player) {
		player.addMaterial(MATERIALS);
		return new TakeMaterials(player, MATERIALS);
	}
	
	public UndoableFarmEdit doOnFarm(Player player, DirPoint pos, int count) {
		return null;
	}

	public boolean undoOnFarm(Player player, DirPoint pos, int count) {
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
