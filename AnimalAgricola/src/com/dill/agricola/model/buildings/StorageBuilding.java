package com.dill.agricola.model.buildings;

import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.enums.Material;
import com.dill.agricola.model.enums.Materials;

public class StorageBuilding extends Building {

	public final static Materials COST = new Materials(Material.WOOD, Material.WOOD, Material.REED);

	public StorageBuilding() {
		super(BuildingType.STORAGE_BUILDING, 0, 0);
	}

	public float getVictoryPoints(Player player) {
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
