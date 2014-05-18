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
import com.dill.agricola.model.buildings.Stall;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.support.Namer;

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

	public static void reset(boolean useMoreBuildings, boolean useEvenMoreBuildings) {
		GeneralSupply.useMoreBuildings = useMoreBuildings;
		GeneralSupply.useEvenMoreBuildings = useEvenMoreBuildings;
		stallsLeft.clear();
		stallsLeft.addAll(Arrays.asList(STALLS));
		if (!useMoreBuildings) {
			stallsLeft.remove(stallsLeft.size() - 1);
		}
		if (Main.DEBUG) {
			stallsLeft.clear();
			stallsLeft.add(STALLS[0]);
			stallsLeft.add(STALLS[1]);
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
		SPECIAL_BUILDINGS.clear(); // clear building instances
		randomizeBuildings();
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
	
	public static void randomizeBuildings() {
		buildingsAll.clear();
		buildingsAll.addAll(generateRandomBuildings());
		buildingsLeft.clear();
		buildingsLeft.addAll(buildingsAll);
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
		if (SPECIAL_BUILDINGS.containsKey(type)) {
			return SPECIAL_BUILDINGS.get(type);				
		} else {
			try {
				String pkg = "com.dill.agricola.model.buildings." + (type.set == 1 ? "more." : "");
				Class<?> bldgClass = Class.forName(pkg + Namer.toCamelCase(type.toString()));
				Building b = (Building) bldgClass.getConstructor().newInstance();
				SPECIAL_BUILDINGS.put(type, b);
				return b;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
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
