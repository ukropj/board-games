package com.dill.agricola.model.buildings.more;

import com.dill.agricola.common.Dir;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.common.PointUtils;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class SmallExtension extends Building {

	public final static Materials COST = new Materials(Material.STONE);

	public SmallExtension() {
		super(BuildingType.SMALL_EXTENSION, 0, 1);
	}

	public float getVictoryPoints(Player player) {
		int count = 0;
		for (Dir d : Dir.values()) {
			DirPoint neighbor = PointUtils.getNext(new DirPoint(buildPos, d));
			Building b = player.getFarm().getBuilding(neighbor);
			if (b != null) {
				count++;
			}
		}
		return count * 2;
	}
	
}
