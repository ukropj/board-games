package com.dill.agricola.model.buildings;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class Stall extends Building implements MultiImaged {

	public static final Materials COST = new Materials(Material.STONE, Material.STONE, Material.STONE, Material.REED);
	
	private static int COUNTER = 0;
	private int id;
	
	public Stall() {
		super(BuildingType.STALL, 1, 3);
		id = (COUNTER++ % GeneralSupply.MAX_STALLS);
	}
	
	public int getId() {
		return id;
	}

	public String getShortName() {
		return "Sl";
	}
	
}
