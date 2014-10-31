package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.actions.FeatureAction;
import com.dill.agricola.actions.extra.UpgradeTrough;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class BuildingFirm extends Building {

	public final static Materials COST = new Materials(
			Material.WOOD, Material.WOOD, Material.STONE);
	public final static FeatureAction UPGRADE_TROUGH = new UpgradeTrough();

	public BuildingFirm() {
		super(BuildingType.BUILDING_FIRM, 2, 0);
		setActive(true);
	}

	// allows upgrading trough to stall for 2 stone
	public FeatureAction getFeatureAction() {
		return UPGRADE_TROUGH;
	}
}
