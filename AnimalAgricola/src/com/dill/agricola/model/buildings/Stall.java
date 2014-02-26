package com.dill.agricola.model.buildings;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class Stall extends Building implements MultiImaged {

	public static final Materials COST = new Materials(Material.STONE, Material.STONE, Material.STONE, Material.REED);
	
	private int id;
	
	public Stall(int id) {
		super(BuildingType.STALL, 1, 3);
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public String getShortName() {
		return "Sl";
	}
	
}
