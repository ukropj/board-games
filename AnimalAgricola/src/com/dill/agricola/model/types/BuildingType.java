package com.dill.agricola.model.types;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.dill.agricola.common.DirPoint;
import com.dill.agricola.model.Farm;
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
	BARN_MANUFACTURER(1, Msg.get("barnManufacturer"), new BuildingText(Msg.get("immediately"), 0.13f, 0.56f, 0.38f)),
	BREEDING_STATION(1, Msg.get("breedingStation"), new BuildingText(Msg.get("breedingStationText"), 0.13f, 0.52f, 0.72f)),
	COW_STALL(1, Msg.get("cowStall"), new BuildingText(Msg.get("immediately"), 0.4f, 0.5f, 0.4f),
			new BuildingText(Msg.get("cowStallText"), 0.33f, 0.78f, 0.11f)),
	DOG_HOUSE(1, Msg.get("dogHouse"), new BuildingText(Msg.get("dogHouseText"), 0.13f, 0.52f, 0.74f)),
	DUCK_POND(1, Msg.get("duckPond"), new BuildingText(Msg.get("immediately"), 0.13f, 0.51f, 0.37f),
			new BuildingText(Msg.get("duckPondText"), 0.30f, 0.18f, 0.6f)),
	FARM_SHOP(1, Msg.get("farmShop"), new BuildingText(Msg.get("immediately"), 0.33f, 0.49f, 0.4f),
			new BuildingText(Msg.get("nextToRoad"), 0.33f, 0.18f, 0.58f)),
	FODDER_BEET_FARM(1, Msg.get("fodderBeetFarm"), new BuildingText(Msg.get("immediately"), 0.33f, 0.495f, 0.4f),
			new BuildingText(Msg.get("haveOfEach"), 0.1f, 0.18f, 0.78f)),
	HAY_RACK(1, Msg.get("hayRack"), new BuildingText(Msg.get("immediately"), 0.33f, 0.51f, 0.4f)),
	INSEMINATION_CENTER(1, Msg.get("inseminationCenter"), new BuildingText(Msg.get("inseminationCenterText"), 0.13f, 0.52f, 0.74f)),
	LARGE_EXTENSION(1, Msg.get("largeExtension"), new BuildingText(Msg.get("perAdjacentBuilding"), 0.34f, 0.5f, 0.55f)),
	LOG_HOUSE(1, Msg.get("logHouse"), new BuildingText(Msg.get("logHouseText"), 0.35f, 0.5f, 0.46f)),
	PIG_STALL(1, Msg.get("pigStall"), new BuildingText(Msg.get("immediately"), 0.4f, 0.5f, 0.4f),
			new BuildingText(Msg.get("pigStallText"), 0.33f, 0.78f, 0.11f)),
			SMALL_EXTENSION(1, Msg.get("smallExtension"), new BuildingText(Msg.get("perAdjacentBuilding"), 0.34f, 0.5f, 0.55f)),
			STUD(1, Msg.get("stud"), new BuildingText(Msg.get("studText"), 0.12f, 0.52f, 0.76f)),
	WILD_BOAR_PEN(1, Msg.get("wildBoarPen"), new BuildingText(Msg.get("wildBoarPenText"), 0.13f, 0.52f, 0.74f)),;


	public final static List<BuildingType> SPECIAL_BUILDINGS_TYPES =
			Collections.unmodifiableList(
					Arrays.asList(new BuildingType[] {
							BuildingType.HALF_TIMBERED_HOUSE, BuildingType.STORAGE_BUILDING,
							BuildingType.SHELTER, BuildingType.OPEN_STABLES }));

	public final static List<BuildingType> MORE_SPECIAL_BUILDINGS_TYPES =
			Collections.unmodifiableList(
					Arrays.asList(new BuildingType[] {
							BuildingType.BARN_MANUFACTURER, BuildingType.BREEDING_STATION,
							BuildingType.DOG_HOUSE,
							BuildingType.COW_STALL, BuildingType.DUCK_POND,
							BuildingType.FARM_SHOP, BuildingType.FODDER_BEET_FARM,
							BuildingType.HAY_RACK, BuildingType.INSEMINATION_CENTER,
							BuildingType.LARGE_EXTENSION,
							BuildingType.LOG_HOUSE, BuildingType.PIG_STALL,
							BuildingType.SMALL_EXTENSION, BuildingType.STUD,
							BuildingType.WILD_BOAR_PEN }));

	public final static List<BuildingType> EVEN_MORE_SPECIAL_BUILDINGS_TYPES =
			Collections.unmodifiableList(
					Arrays.asList(new BuildingType[] {}));

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
		case LOG_HOUSE:
			return placeType == BuildingType.COTTAGE;
		case STABLES:
		case OPEN_STABLES:
			return placeType == BuildingType.STALL;
		case FARM_SHOP:
			return placeType == EMPTY && pos.y == Farm.BY_ROAD;
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
