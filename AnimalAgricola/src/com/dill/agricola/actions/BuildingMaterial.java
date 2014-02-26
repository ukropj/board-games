package com.dill.agricola.actions;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;

public class BuildingMaterial extends AbstractAction {

	public static final Materials MATERIALS = new Materials(Material.WOOD, Material.STONE, Material.REED);

	public BuildingMaterial() {
		super(ActionType.BUILDING_MATERIAL);
	}
	
	public boolean doOnce(Player player) {
		player.addMaterial(MATERIALS);
		return true;
	}

	public boolean undoOnce(Player player) {
		player.removeMaterial(MATERIALS);
		return true;
	}
	
	public String toString() {
		return super.toString() + "<br>" + MATERIALS;
	}

}
