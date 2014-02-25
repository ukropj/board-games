package com.dill.agricola.actions;

import com.dill.agricola.model.Player;
import com.dill.agricola.model.enums.ActionType;
import com.dill.agricola.model.enums.Material;
import com.dill.agricola.model.enums.Materials;

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
