package com.dill.agricola.model.buildings;

public enum BuildingType {

	STALL, STABLES,
	COTTAGE, HALF_TIMBERED_HOUSE, STORAGE_BUILDING, SHELTER, OPEN_STABLES,
	BUILDING;

	public boolean isHouse() {
		return this == BuildingType.COTTAGE || this == BuildingType.HALF_TIMBERED_HOUSE;
	}
}
