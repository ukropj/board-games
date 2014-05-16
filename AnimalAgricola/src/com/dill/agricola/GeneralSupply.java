package com.dill.agricola;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.dill.agricola.model.Building;
import com.dill.agricola.model.buildings.HalfTimberedHouse;
import com.dill.agricola.model.buildings.OpenStables;
import com.dill.agricola.model.buildings.Shelter;
import com.dill.agricola.model.buildings.Stall;
import com.dill.agricola.model.buildings.StorageBuilding;
import com.dill.agricola.model.buildings.more.BarnManufacturer;
import com.dill.agricola.model.buildings.more.BreedingStation;
import com.dill.agricola.model.buildings.more.CattleFarm;
import com.dill.agricola.model.buildings.more.CountryHouse;
import com.dill.agricola.model.buildings.more.CowStall;
import com.dill.agricola.model.buildings.more.DogHouse;
import com.dill.agricola.model.buildings.more.DuckPond;
import com.dill.agricola.model.buildings.more.FarmShop;
import com.dill.agricola.model.buildings.more.FeedStorehouse;
import com.dill.agricola.model.buildings.more.FenceManufacturer;
import com.dill.agricola.model.buildings.more.FodderBeetFarm;
import com.dill.agricola.model.buildings.more.HayRack;
import com.dill.agricola.model.buildings.more.InseminationCenter;
import com.dill.agricola.model.buildings.more.LargeExtension;
import com.dill.agricola.model.buildings.more.LogHouse;
import com.dill.agricola.model.buildings.more.PigStall;
import com.dill.agricola.model.buildings.more.Sawmill;
import com.dill.agricola.model.buildings.more.SmallExtension;
import com.dill.agricola.model.buildings.more.Stud;
import com.dill.agricola.model.buildings.more.WildBoarPen;
import com.dill.agricola.model.types.BuildingType;

public class GeneralSupply {

	public static enum Supplyable {
		TROUGH, EXTENSION, STALL, SPECIAL_BUILDING;
	}

	public final static int MAX_TROUGHS = 10;
	public final static int MORE_BUILDINGS = Main.DEBUG ? 27 : 4;
	public final static int EVEN_MORE_BUILDINGS = Main.DEBUG ? 4 : 4;
	public final static Integer[] EXTS = { 0, 1, 2, 3, 4 };
	public final static Stall[] STALLS = { new Stall(0), new Stall(1), new Stall(2), new Stall(3), new Stall(4) };

	private final static Map<BuildingType, Building> SPECIAL_BUILDINGS = new EnumMap<BuildingType, Building>(BuildingType.class);

	private static final Stack<Stall> stallsLeft = new Stack<Stall>();
	private static int troughsLeft;
	private static final Stack<Integer> extsLeft = new Stack<Integer>();
	private static final Stack<Integer> extsUsed = new Stack<Integer>();
	private static final List<BuildingType> buildingsAll = new ArrayList<BuildingType>();
	private static final List<BuildingType> buildingsLeft = new ArrayList<BuildingType>();
	
	private static boolean useMoreBuildings;
	private static boolean useEvenMoreBuildings;

	static {
		SPECIAL_BUILDINGS.put(BuildingType.HALF_TIMBERED_HOUSE, new HalfTimberedHouse());
		SPECIAL_BUILDINGS.put(BuildingType.STORAGE_BUILDING, new StorageBuilding());
		SPECIAL_BUILDINGS.put(BuildingType.SHELTER, new Shelter());
		SPECIAL_BUILDINGS.put(BuildingType.OPEN_STABLES, new OpenStables());
		// more
		SPECIAL_BUILDINGS.put(BuildingType.BARN_MANUFACTURER, new BarnManufacturer());
		SPECIAL_BUILDINGS.put(BuildingType.BREEDING_STATION, new BreedingStation());
		SPECIAL_BUILDINGS.put(BuildingType.CATTLE_FARM, new CattleFarm());
		SPECIAL_BUILDINGS.put(BuildingType.COUNTRY_HOUSE, new CountryHouse());
		SPECIAL_BUILDINGS.put(BuildingType.COW_STALL, new CowStall());
		SPECIAL_BUILDINGS.put(BuildingType.DOG_HOUSE, new DogHouse());
		SPECIAL_BUILDINGS.put(BuildingType.DUCK_POND, new DuckPond());
		SPECIAL_BUILDINGS.put(BuildingType.FARM_SHOP, new FarmShop());
		SPECIAL_BUILDINGS.put(BuildingType.FENCE_MANUFACTURER, new FenceManufacturer());
		SPECIAL_BUILDINGS.put(BuildingType.FEED_STOREHOUSE, new FeedStorehouse());
		SPECIAL_BUILDINGS.put(BuildingType.FODDER_BEET_FARM, new FodderBeetFarm());
		SPECIAL_BUILDINGS.put(BuildingType.HAY_RACK, new HayRack());
		SPECIAL_BUILDINGS.put(BuildingType.INSEMINATION_CENTER, new InseminationCenter());
		SPECIAL_BUILDINGS.put(BuildingType.LOG_HOUSE, new LogHouse());
		SPECIAL_BUILDINGS.put(BuildingType.LARGE_EXTENSION, new LargeExtension());
		SPECIAL_BUILDINGS.put(BuildingType.PIG_STALL, new PigStall());
		SPECIAL_BUILDINGS.put(BuildingType.SAWMILL, new Sawmill());
		SPECIAL_BUILDINGS.put(BuildingType.SMALL_EXTENSION, new SmallExtension());
		SPECIAL_BUILDINGS.put(BuildingType.STUD, new Stud());
		SPECIAL_BUILDINGS.put(BuildingType.WILD_BOAR_PEN, new WildBoarPen());
	}

	public static void reset(boolean useMoreBuildings, boolean useEvenMoreBuildings) {
		GeneralSupply.useMoreBuildings = useMoreBuildings;
		GeneralSupply.useEvenMoreBuildings = useEvenMoreBuildings;
		stallsLeft.clear();
		stallsLeft.addAll(Arrays.asList(STALLS));
		if (!useMoreBuildings) {
			stallsLeft.remove(stallsLeft.size() - 1);
		}
		Collections.shuffle(stallsLeft);
		troughsLeft = MAX_TROUGHS;
		extsLeft.clear();
		extsLeft.addAll(Arrays.asList(EXTS));
		if (!useMoreBuildings) {
			extsLeft.remove(extsLeft.size() - 1);
		}
		Collections.shuffle(extsLeft);
		extsUsed.clear();
		buildingsAll.clear();
		buildingsAll.addAll(generateRandomBuildings());
		buildingsLeft.clear();
		buildingsLeft.addAll(buildingsAll);
	}

	public static int getLeft(Supplyable type) {
		switch (type) {
		case EXTENSION:
			return extsLeft.size();
		case TROUGH:
			return troughsLeft;
		case STALL:
			return stallsLeft.size();
		case SPECIAL_BUILDING:
			return buildingsLeft.size();
		default:
			throw new IllegalArgumentException();
		}
	}

	public static List<BuildingType> getBuildingsLeft() {
		return buildingsLeft;
	}

	public static List<BuildingType> getBuildingsAll() {
		return buildingsAll;
	}

	private static List<BuildingType> generateRandomBuildings() {
		List<BuildingType> types = new ArrayList<BuildingType>(BuildingType.SPECIAL_BUILDINGS_TYPES);
		if (useMoreBuildings) {
			List<BuildingType> moreTypes = new ArrayList<BuildingType>(BuildingType.MORE_SPECIAL_BUILDINGS_TYPES);
			Collections.shuffle(moreTypes);
			for (int i = 0; i < MORE_BUILDINGS && i < moreTypes.size(); i++) {
				types.add(moreTypes.get(i));
			}
		}
		if (useEvenMoreBuildings) {
			List<BuildingType> moreTypes = new ArrayList<BuildingType>(BuildingType.EVEN_MORE_SPECIAL_BUILDINGS_TYPES);
			Collections.shuffle(moreTypes);
			for (int i = 0; i < EVEN_MORE_BUILDINGS && i < moreTypes.size(); i++) {
				types.add(moreTypes.get(i));
			}
		}
		Collections.sort(types, new Comparator<BuildingType>() {
			public int compare(BuildingType o1, BuildingType o2) {
				return o1.set != o1.set ? o1.set - o2.set : o1.compareTo(o2);
			}
		});
		return types;
	}

	public static void useTrough(boolean use) {
		troughsLeft += use ? -1 : 1;
	}

	public static Stall useStall() {
		return stallsLeft.pop();
	}

	public static void unuseStall(Stall stall) {
		stallsLeft.push(stall);
	}

	public static void useExtension(boolean use) {
		if (use) {
			extsUsed.push(extsLeft.pop());
		} else {
			extsLeft.push(extsUsed.pop());
		}
	}

	public static void useBuilding(BuildingType buildingType, boolean use) {
		if (use) {
			buildingsLeft.remove(buildingType);
		} else {
			buildingsLeft.add(buildingType);
		}
	}

	public static Building getSpecialBuilding(BuildingType type) {
		return SPECIAL_BUILDINGS.get(type);
	}

	public static int getNextExtensionId() {
		return extsLeft.peek();
	}
	
	public static boolean getUseMoreBuildings() {
		return useMoreBuildings;
	}
	
	public static boolean getUseEvenMoreBuildings() {
		return useEvenMoreBuildings;
	}

}
