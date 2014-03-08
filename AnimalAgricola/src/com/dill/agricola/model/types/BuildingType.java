package com.dill.agricola.model.types;

import com.dill.agricola.common.DirPoint;

import com.dill.agricola.support.Msg;

public enum BuildingType {

	EMPTY(""),
	
	STALL(Msg.get("stall")), 
	STABLES(Msg.get("stables")),
	
	COTTAGE(Msg.get("cottage")), 
	HALF_TIMBERED_HOUSE(Msg.get("halfHouse")),
	STORAGE_BUILDING(Msg.get("storage"), Msg.get("storageText"), new DirPoint(90, 110), 90),
	SHELTER(Msg.get("shelter"), Msg.get("immediately"), new DirPoint(26, 128), 85),
	OPEN_STABLES(Msg.get("openStables"), Msg.get("immediately"), new DirPoint(35, 120), 85);
	
//	BUILDING();

	public final String name;
	public final String text;
	public final DirPoint textPos;
	public final int textWidth;

	private BuildingType(String name) {
		this(name, null, null, 0);
	}

	private BuildingType(String name, String text, DirPoint textPos, int textWidth) {
		this.name = name;
		this.text = text;
		this.textPos = textPos;
		this.textWidth = textWidth;
	}

	public boolean canBuildAt(BuildingType placeType) {
		switch(this) {
		case HALF_TIMBERED_HOUSE:
			return placeType == BuildingType.COTTAGE;
		case STABLES:
		case OPEN_STABLES:
			return placeType == BuildingType.STALL;
		default :
			return placeType == EMPTY;
		}
	}
	
	//	public boolean isHouse() {
	//		return this == BuildingType.COTTAGE || this == BuildingType.HALF_TIMBERED_HOUSE;
	//	}
}
