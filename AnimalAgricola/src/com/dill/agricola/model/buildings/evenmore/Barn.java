package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class Barn extends Building {

	public final static Materials COST = new Materials(Material.WOOD, 3);

	private boolean used = false;

	public Barn() {
		super(BuildingType.BARN, 0, 1);
		setActive(true);
	}

	// next stall will be updated to stables for free
	
	public boolean canUse() {
		return !used;
	}

	public void use(boolean use) {
		used = use;
		setActive(!used);
	}

}
