package com.dill.agricola.model.buildings;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class Stall extends Building {

	public static final Materials COST = new Materials(Material.STONE, Material.STONE, Material.STONE, Material.REED);
	
	public Stall() {
		super(BuildingType.STALL, 1, 3);
	}

	public String getShortName() {
		return "Sl";
	}
	
}
