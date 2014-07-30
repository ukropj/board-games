package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Farm;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.Purchasable;

public class Manor extends Building {

	public final static Materials COST = new Materials(
			Material.WOOD, Material.STONE, Material.STONE, Material.STONE);

	public Manor() {
		super(BuildingType.MANOR, 7, 2);
	}

	// lose 2 points for each building adjacent to the road
	public float getVictoryPoints(Player player) {
		int count = 0;
		for (int x = 0; x < player.farm.getWidth(); x++) {
			if (player.farm.has(Purchasable.BUILDING, new DirPoint(x, Farm.BY_ROAD), false)) {
				count++;
			}
		}
		return super.getVictoryPoints(player) - 2 * count;
	}

}
