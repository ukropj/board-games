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
	
	public boolean isFarmAction() {
		return false;
	}
	
	public boolean activate(Player player, int doneSoFar) {
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

	public boolean canPerform(Player player, int count) {
		return !isUsed();
	}

	public boolean canUnperform(Player player, int count) {
		return count != 0;
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