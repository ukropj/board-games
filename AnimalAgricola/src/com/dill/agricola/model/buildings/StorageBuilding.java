package com.dill.agricola.model.buildings;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class StorageBuilding extends Building {

	public final static Materials COST = new Materials(Material.WOOD, Material.WOOD, Material.REED);

	public StorageBuilding() {
		super(BuildingType.STORAGE_BUILDING, 0, 0);
	}

	public float getVictoryDirPoints(Player player) {
		int material = 0;
		for (Material type : Material.values()) {
			if (type != Material.BORDER) {
				// borders are not considered building material
				material += player.getMaterial(type);
			}
		}
		return ((float)material)/2;
	}
	
	public String getShortName() {
		return "Sg";
	}
}
