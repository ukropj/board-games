package com.dill.agricola.model.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
	ANIMAL_TRADER(1, Msg.get("animalTrader"), new BuildingText(Msg.get("animalTraderText"), 0.13f, 0.52f, 0.74f)),
	BARN_MANUFACTURER(1, Msg.get("barnManufacturer"), new BuildingText(Msg.get("immediately"), 0.13f, 0.56f, 0.38f)),
	BREEDING_STATION(1, Msg.get("breedingStation"), new BuildingText(Msg.get("breedingStationText"), 0.13f, 0.52f, 0.72f)),
	CARPENTER(1, Msg.get("carpenter"), new BuildingText(Msg.get("carpenterText"), 0.13f, 0.52f, 0.74f)),
	CATTLE_FARM(1, Msg.get("cattleFarm"), new BuildingText(Msg.get("cattleFarmText"), 0.12f, 0.50f, 0.76f)),
	COUNTRY_HOUSE(1, Msg.get("countryHouse"), new BuildingText(Msg.get("immediately"), 0.33f, 0.49f, 0.4f)),
	COW_STALL(1, Msg.get("cowStall"), new BuildingText(Msg.get("immediately"), 0.4f, 0.5f, 0.4f),
			new BuildingText(Msg.get("cowStallText"), 0.33f, 0.78f, 0.11f)),
	DOG_HOUSE(1, Msg.get("dogHouse"), new BuildingText(Msg.get("dogHouseText"), 0.13f, 0.52f, 0.74f)),
	DUCK_POND(1, Msg.get("duckPond"), new BuildingText(Msg.get("immediately"), 0.13f, 0.51f, 0.37f),
			new BuildingText(Msg.get("duckPondText"), 0.30f, 0.18f, 0.6f)),
	FARM_SHOP(1, Msg.get("farmShop"), new BuildingText(Msg.get("immediately"), 0.33f, 0.49f, 0.4f),
			new BuildingText(Msg.get("nextToRoad"), 0.33f, 0.18f, 0.58f)),
	FARM_WELL(1, Msg.get("farmWell"), new BuildingText(Msg.get("farmWellText"), 0.13f, 0.52f, 0.74f)),
	FENCE_MANUFACTURER(1, Msg.get("fenceManufacturer"), new BuildingText(Msg.get("fenceManufacturerText"), 0.12f, 0.52f, 0.76f)),
	FEED_STOREHOUSE(1, Msg.get("feedStorehouse"), new BuildingText(Msg.get("immediately"), 0.13f, 0.54f, 0.38f),
			new BuildingText(Msg.get("feedStorehouseText"), 0.33f, 0.78f, 0.11f)),
	FODDER_BEET_FARM(1, Msg.get("fodderBeetFarm"), new BuildingText(Msg.get("immediately"), 0.33f, 0.495f, 0.4f),
			new BuildingText(Msg.get("haveOfEach"), 0.1f, 0.18f, 0.78f)),
	HAY_RACK(1, Msg.get("hayRack"), new BuildingText(Msg.get("immediately"), 0.33f, 0.51f, 0.4f)),
	HOME_WORKSHOP(1, Msg.get("homeWorkshop"), new BuildingText(Msg.get("homeWorkshopText"), 0.13f, 0.52f, 0.74f)),
	INSEMINATION_CENTER(1, Msg.get("inseminationCenter"), new BuildingText(Msg.get("inseminationCenterText"), 0.13f, 0.52f, 0.74f)),
	JOINERY(1, Msg.get("joinery"), new BuildingText(Msg.get("joineryText"), 0.13f, 0.52f, 0.74f)),
	LARGE_EXTENSION(1, Msg.get("largeExtension"), new BuildingText(Msg.get("perAdjacentBuilding"), 0.34f, 0.5f, 0.55f)),
	LOG_HOUSE(1, Msg.get("logHouse"), new BuildingText(Msg.get("logHouseText"), 0.35f, 0.5f, 0.46f)),
	PIG_STALL(1, Msg.get("pigStall"), new BuildingText(Msg.get("immediately"), 0.4f, 0.5f, 0.4f),
			new BuildingText(Msg.get("pigStallText"), 0.33f, 0.78f, 0.11f)),
	RANCH(1, Msg.get("ranch"), new BuildingText(Msg.get("ranchText"), 0.14f, 0.49f, 0.73f)),
	REARING_STATION(1, Msg.get("rearingStation"), new BuildingText(Msg.get("rearingStationText"), 0.12f, 0.50f, 0.76f)),
	SAWMILL(1, Msg.get("sawmill"), new BuildingText(Msg.get("sawmillText"), 0.12f, 0.52f, 0.76f)),
	SMALL_EXTENSION(1, Msg.get("smallExtension"), new BuildingText(Msg.get("perAdjacentBuilding"), 0.34f, 0.5f, 0.55f)),
	STUD(1, Msg.get("stud"), new BuildingText(Msg.get("studText"), 0.12f, 0.52f, 0.76f)),
	WILD_BOAR_PEN(1, Msg.get("wildBoarPen"), new BuildingText(Msg.get("wildBoarPenText"), 0.13f, 0.52f, 0.74f)),

	// even more special buildings
	BARN(2, Msg.get("barn"), new BuildingText(Msg.get("barnText"), 0.12f, 0.50f, 0.74f)),
	BYRE_DWELLING(2, Msg.get("byreDwelling")),
	CARPENTERS_WORKSHOP(2, Msg.get("carpentersWorkshop"), new BuildingText(Msg.get("carpentersWorkshopText"), 0.12f, 0.49f, 0.74f)),
	CATTLE_MARKET(2, Msg.get("cattleMarket"), new BuildingText(Msg.get("cattleMarketText"), 0.27f, 0.47f, 0.39f)),
	CORNER_HOUSES(2, Msg.get("cornerHouses"), new BuildingText(Msg.get("cornerHousesText"), 0.14f, 0.53f, 0.70f),
			new BuildingText(Msg.get("cornerHousesCondition"), 0.17f, 0.7f, 0.68f)),
	DAIRY_FARM(2, Msg.get("dairyFarm"), new BuildingText(Msg.get("dairyFarmText"), 0.12f, 0.52f, 0.76f)),
	ESTATE(2, Msg.get("estate"), new BuildingText(Msg.get("immediately"), 0.4f, 0.5f, 0.4f),
			new BuildingText(Msg.get("estateText"), 0.34f, 0.72f, 0.53f)),
	INN(2, Msg.get("inn"), new BuildingText(Msg.get("immediately"), 0.26f, 0.52f, 0.39f)),
	MANOR(2, Msg.get("manor"), new BuildingText(Msg.get("manorText"), 0.13f, 0.49f, 0.74f)),
	MATERIALS_OUTLET(2, Msg.get("materialsOutlet"), new BuildingText(Msg.get("immediately"), 0.16f, 0.52f, 0.39f)),
	OFFICE(2, Msg.get("office"), new BuildingText(Msg.get("officeText"), 0.13f, 0.48f, 0.74f)),
	ORGANIC_FARM(2, Msg.get("organicFarm"), new BuildingText(Msg.get("organicFarmText"), 0.13f, 0.49f, 0.76f)),
	PEN(2, Msg.get("pen"), new BuildingText(Msg.get("penText"), 0.17f, 0.5f, 0.71f)),
	REED_HUT(2, Msg.get("reedHut"), new BuildingText(Msg.get("immediately"), 0.12f, 0.85f, 0.37f)),
	SERVANTS_COTTAGE(2, Msg.get("servantsCottage"), new BuildingText(Msg.get("servantsCottageText"), 0.13f, 0.51f, 0.76f)),
	STEWARDS_OFFICE(2, Msg.get("stewardsOffice"), new BuildingText(Msg.get("stewardsOfficeText"), 0.12f, 0.51f, 0.76f)),
	TRADING_STATION(2, Msg.get("tradingStation"), new BuildingText(Msg.get("tradingStationText"), 0.12f, 0.49f, 0.76f)),
	WOODWORKING_SHOP(2, Msg.get("woodworkingShop"), new BuildingText(Msg.get("woodworkingShopText"), 0.12f, 0.51f, 0.68f));

	public final static List<BuildingType> SPECIAL_BUILDINGS_TYPES =
			Collections.unmodifiableList(
					Arrays.asList(new BuildingType[] {
							HALF_TIMBERED_HOUSE,
							STORAGE_BUILDING,
							SHELTER,
							OPEN_STABLES
					}));

	public final static List<BuildingType> MORE_SPECIAL_BUILDINGS_TYPES =
			Collections.unmodifiableList(
					Arrays.asList(new BuildingType[] {
							ANIMAL_TRADER,
							BARN_MANUFACTURER,
							BREEDING_STATION,
							CARPENTER,
							CATTLE_FARM,
							COW_STALL,
							COUNTRY_HOUSE,
							DOG_HOUSE,
							DUCK_POND,
							FARM_SHOP,
							FARM_WELL,
							FEED_STOREHOUSE,
							FENCE_MANUFACTURER,
							FODDER_BEET_FARM,
							HAY_RACK,
							HOME_WORKSHOP,
							INSEMINATION_CENTER,
							JOINERY,
							LARGE_EXTENSION,
							LOG_HOUSE,
							PIG_STALL,
							REARING_STATION,
							RANCH,
							SAWMILL,
							SMALL_EXTENSION,
							STUD,
							WILD_BOAR_PEN
					}));

	public final static List<BuildingType> EVEN_MORE_SPECIAL_BUILDINGS_TYPES =
			Collections.unmodifiableList(
					Arrays.asList(new BuildingType[] {
							BARN,
							BYRE_DWELLING,
							CARPENTERS_WORKSHOP,
							CATTLE_MARKET,
							CORNER_HOUSES,
							DAIRY_FARM,
							ESTATE,
							INN,
							MANOR,
							MATERIALS_OUTLET,
							OFFICE,
							ORGANIC_FARM,
							PEN,
							REED_HUT,
							SERVANTS_COTTAGE,
							STEWARDS_OFFICE,
							TRADING_STATION,
							WOODWORKING_SHOP
					}));

	public static Collection<BuildingType> set(int set) {
		switch (set) {
		case 0:
			return SPECIAL_BUILDINGS_TYPES;
		case 1:
			return MORE_SPECIAL_BUILDINGS_TYPES;
		case 2:
			return EVEN_MORE_SPECIAL_BUILDINGS_TYPES;
		}
		;
		throw new IllegalArgumentException("Unsupported set " + set);
	}

	public static Comparator<BuildingType> COMPARATOR = new Comparator<BuildingType>() {
		public int compare(BuildingType o1, BuildingType o2) {
			return o1.set != o1.set ? o1.set - o2.set : o1.compareTo(o2);
		}
	};

	public final String name;
	public final int set;
	public final BuildingText[] texts;

	private BuildingType(int set, String name, BuildingText... texts) {
		this.set = set;
		this.name = name;
		this.texts = texts;
	}

	public boolean canBuildAt(BuildingType placeType, DirPoint pos, Farm farm) {
		switch (this) {
		case HALF_TIMBERED_HOUSE:
		case LOG_HOUSE:
		case BYRE_DWELLING:
			return placeType == COTTAGE;
		case STABLES:
		case OPEN_STABLES:
			return placeType == STALL || placeType == COW_STALL || placeType == PIG_STALL;
		case MATERIALS_OUTLET:
			return placeType == STABLES; // cannot build on Open Stales
		case FARM_SHOP:
			return placeType == EMPTY && pos.y == Farm.BY_ROAD;
		case CORNER_HOUSES:
			return placeType == EMPTY
					&& (pos.x == 0 || pos.x == farm.getWidth() - 1)
					&& (pos.y == Farm.BY_ROAD || pos.y == Farm.BY_FOREST);
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
