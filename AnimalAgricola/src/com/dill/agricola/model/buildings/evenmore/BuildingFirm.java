package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.extra.UpgradeTrough;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class BuildingFirm extends Building {

	public final static Materials COST = new Materials(
			Material.WOOD, Material.WOOD, Material.STONE);
	public final static Action UPGRADE_TROUGH = new UpgradeTrough();
	
	public BuildingFirm() {
		super(BuildingType.BUILDING_FIRM, 2, 0);
	}

	// allows upgrading trough to stall for 2 stone
}
