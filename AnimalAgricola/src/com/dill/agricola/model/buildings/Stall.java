package com.dill.agricola.model.buildings;

import com.dill.agricola.model.Building;
import com.dill.agricola.model.enums.Material;
import com.dill.agricola.model.enums.Materials;

public class Stall extends Building {

	public static final Materials COST = new Materials(Material.STONE, Material.STONE, Material.STONE, Material.REED);
	
	public Stall() {
		super(BuildingType.STALL, 1, 3);
	}

	public String getShortName() {
		return "Sl";
	}
	
}
