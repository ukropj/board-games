package com.dill.agricola.model.buildings.more;

import com.dill.agricola.actions.extra.OneTrough;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class FeedStorehouse extends Building {

	public final static Materials COST = new Materials(Material.WOOD, Material.REED);

	public FeedStorehouse() {
		super(BuildingType.FEED_STOREHOUSE, 0, 0, new OneTrough(new Materials()));
	}
	
	public float getVictoryPoints(Player player) {
		return player.farm.getTroughs() >= 5 ? 3 : 0;
	}

}
