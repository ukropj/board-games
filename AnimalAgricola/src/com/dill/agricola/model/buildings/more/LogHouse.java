package com.dill.agricola.model.buildings.more;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class LogHouse extends Building {

	public final static Materials COST = new Materials(Material.WOOD, Material.WOOD, Material.REED);

	public LogHouse() {
		super(BuildingType.LOG_HOUSE, 0, 4);
	}
	
	public float getVictoryPoints(Player player) {
		return player.getMaterial(Material.WOOD) >= 4 ? 4 : 0;
	}

}
