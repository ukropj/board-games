package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.actions.extra.GiveBorder;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class AssemblyHall extends Building {

	public final static Materials COST = new Materials(
			Material.WOOD, Material.WOOD, Material.WOOD, Material.REED);
	public final static GiveBorder GIVE_BORDER = new GiveBorder();
	
	public AssemblyHall() {
		super(BuildingType.ASSEMBLY_HALL, 2, 0);
	}

	// you can give 1 border to opponent and build 1 free fence
}
