package com.dill.agricola.model.buildings;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;

public class Cottage extends Building {

	public final static Materials COST = Materials.EMPTY;

	public Cottage() {
		super(BuildingType.COTTAGE, 0, 1);
	}

}
