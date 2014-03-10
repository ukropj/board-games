package com.dill.agricola.model.types;

import com.dill.agricola.support.Msg;

public enum BuildingType {

	EMPTY(""),

	STALL(Msg.get("stall")),
	STABLES(Msg.get("stables")),

	COTTAGE(Msg.get("cottage")),
	HALF_TIMBERED_HOUSE(Msg.get("halfHouse")),
	STORAGE_BUILDING(Msg.get("storage"), Msg.get("storageText"), 0.39f, 0.48f, 0.42f),
	SHELTER(Msg.get("shelter"), Msg.get("immediately"), 		 0.12f, 0.57f, 0.38f),
	OPEN_STABLES(Msg.get("openStables"), Msg.get("immediately"), 0.14f, 0.535f, 0.38f);

//	BUILDING();

	public final String name;
	public final String text;

	public final float x;
	public final float y;
	public final float textWidth;

	private BuildingType(String name) {
		this(name, null, 0f, 0f, 0f);
	}

	private BuildingType(String name, String text, float x, float y, float textWidth) {
		this.name = name;
		this.text = text;
		this.x = x;
		this.y = y;
		this.textWidth = textWidth;
	}

	public boolean canBuildAt(BuildingType placeType) {
		switch (this) {
		case HALF_TIMBERED_HOUSE:
			return placeType == BuildingType.COTTAGE;
		case STABLES:
		case OPEN_STABLES:
			return placeType == BuildingType.STALL;
		default:
			return placeType == EMPTY;
		}
	}

	//	public boolean isHouse() {
	//		return this == BuildingType.COTTAGE || this == BuildingType.HALF_TIMBERED_HOUSE;
	//	}
}
