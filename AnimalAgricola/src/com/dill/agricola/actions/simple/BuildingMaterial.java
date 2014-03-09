package com.dill.agricola.actions.simple;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;

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
	
	public boolean doo(Player player, int doneSoFar) {
		player.addMaterial(MATERIALS);
		return true;
	}

	public boolean undo(Player player, int doneSoFar) {
		player.removeMaterial(MATERIALS);
		return true;
	}
	
	public String toString() {
		return super.toString() + "<br>" + MATERIALS;
	}

	public boolean canDo(Player player, int count) {
		return !isUsed();
	}

	public boolean canUndo(Player player, int count) {
		return count != 0;
	}

	public boolean canDo(Player player, DirPoint pos, int count) {
		return false;
	}

	public boolean canUndo(Player player, DirPoint pos, int count) {
		return false;
	}

	public boolean doo(Player player, DirPoint pos, int count) {
		return false;
	}

	public boolean undo(Player player, DirPoint pos, int count) {
		return false;
	}

}
