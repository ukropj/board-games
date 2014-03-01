package com.dill.agricola.model.types;

import com.dill.agricola.support.Msg;

public enum BuildingType {

	STALL(Msg.get("stall")), STABLES(Msg.get("stables")),
	COTTAGE(Msg.get("cottage")), HALF_TIMBERED_HOUSE(Msg.get("halfHouse")),
	STORAGE_BUILDING(Msg.get("storage")), SHELTER(Msg.get("shelter")),
	OPEN_STABLES(Msg.get("openStables"));
	
//	BUILDING();

	public final String name;

	private BuildingType(String name) {
		this.name = name;
	}

	//	public boolean isHouse() {
	//		return this == BuildingType.COTTAGE || this == BuildingType.HALF_TIMBERED_HOUSE;
	//	}
}
