package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class Barn extends Building {

	public final static Materials COST = new Materials(Material.WOOD, 3);

	private boolean activated = false;
	
	public Barn() {
		super(BuildingType.BARN, 0, 1);
	}
	
	public boolean canActivate() {
		return !activated;
	}
	
	public void activate(boolean activate) {
		this.activated = activate;
	}
	
	// update next stall to stables for free
}
