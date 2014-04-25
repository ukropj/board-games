package com.dill.agricola.model.types;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.dill.agricola.common.DirPoint;
import com.dill.agricola.support.Msg;

public enum BuildingType {

	EMPTY(0, ""),

	STALL(0, Msg.get("stall")),
	STABLES(0, Msg.get("stables")),
	// special buildings
	COTTAGE(0, Msg.get("cottage")),
	HALF_TIMBERED_HOUSE(0, Msg.get("halfHouse")),
	STORAGE_BUILDING(0, Msg.get("storage"), new BuildingText(Msg.get("storageText"), 0.39f, 0.48f, 0.42f)),
	SHELTER(0, Msg.get("shelter"), new BuildingText(Msg.get("immediately"), 0.12f, 0.57f, 0.37f)),
	OPEN_STABLES(0, Msg.get("openStables"), new BuildingText(Msg.get("immediately"), 0.14f, 0.535f, 0.37f)),
	// more special buildings
	FARM_SHOP(1, Msg.get("farmShop"), new BuildingText(Msg.get("immediately"), 0.33f, 0.49f, 0.4f), 
			new BuildingText(Msg.get("nextToRoad"), 0.33f, 0.18f, 0.56f)),
	FODDER_BEET_FARM(1, Msg.get("fodderBeetFarm"), new BuildingText(Msg.get("immediately"), 0.33f, 0.49f, 0.4f), 
			new BuildingText(Msg.get("haveOfEach"), 0.14f, 0.18f, 0.76f)),
	HAY_RACK(1, Msg.get("hayRack"), new BuildingText(Msg.get("immediately"), 0.33f, 0.51f, 0.4f)),
	INSEMINATION_CENTER(1, Msg.get("inseminationCenter"), new BuildingText(Msg.get("inseminationCenterText"), 0.13f, 0.52f, 0.75f));

	public final static List<BuildingType> SPECIAL_BUILDINGS_TYPES =
			Collections.unmodifiableList(
					Arrays.asList(new BuildingType[] {
							BuildingType.HALF_TIMBERED_HOUSE, BuildingType.STORAGE_BUILDING,
							BuildingType.SHELTER, BuildingType.OPEN_STABLES }));

	public final static List<BuildingType> MORE_SPECIAL_BUILDINGS_TYPES =
			Collections.unmodifiableList(
					Arrays.asList(new BuildingType[] {
							BuildingType.FARM_SHOP, BuildingType.FODDER_BEET_FARM,
							BuildingType.HAY_RACK, BuildingType.INSEMINATION_CENTER }));

//	private final int BY_FOREST = 0;
	private final int BY_ROAD = 2;
	
	public final String name;
	public final int set;
	public final BuildingText[] texts;

	private BuildingType(int set, String name, BuildingText... texts) {
		this.set = set;
		this.name = name;
		this.texts = texts;
	}

	public boolean canBuildAt(BuildingType placeType, DirPoint pos) {
		switch (this) {
		case HALF_TIMBERED_HOUSE:
			return placeType == BuildingType.COTTAGE;
		case STABLES:
		case OPEN_STABLES:
			return placeType == BuildingType.STALL;
		case FARM_SHOP:
			return placeType == EMPTY && pos.y == BY_ROAD;			
		default:
			return placeType == EMPTY;
		}
	}

	public static class BuildingText {

		public String text;
		public float x;
		public float y;
		public float textWidth;

		public BuildingText(String text, float x, float y, float textWidth) {
			this.text = text;
			this.x = x;
			this.y = y;
			this.textWidth = textWidth;
		}
	}

}
