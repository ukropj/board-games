package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Farm;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.Purchasable;

public class Office extends Building {

	public final static Materials COST = new Materials(
			Material.WOOD, Material.WOOD, Material.STONE, Material.STONE);

	public Office() {
		super(BuildingType.OFFICE, 6, 2, Animal.HORSE);
	}

	// lose 2 points for each building adjacent to the forest
	public float getVictoryPoints(Player player) {
		int count = 0;
		for (int x = 0; x < player.farm.getWidth(); x++) {
			if (player.farm.has(Purchasable.BUILDING, new DirPoint(x, Farm.BY_FOREST), false)) {
				count++;
			}
		}
		return super.getVictoryPoints(player) - 2 * count;
	}

}
