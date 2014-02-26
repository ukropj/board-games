package com.dill.agricola;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.dill.agricola.model.Building;
import com.dill.agricola.model.buildings.HalfTimberedHouse;
import com.dill.agricola.model.buildings.OpenStables;
import com.dill.agricola.model.buildings.Shelter;
import com.dill.agricola.model.buildings.StorageBuilding;
import com.dill.agricola.model.types.BuildingType;

public class GeneralSupply {

	public final static int MAX_STALLS = 4;
	public final static int MAX_FEEDERS = 10;
	public final static Integer[] EXTS = {0,1,2,3};
	
	private final static List<BuildingType> SPECIAL_BUILDINGS_TYPES = Arrays.asList(new BuildingType[]{BuildingType.HALF_TIMBERED_HOUSE, 
			BuildingType.STORAGE_BUILDING, BuildingType.SHELTER, BuildingType.OPEN_STABLES});
	private final static Map<BuildingType, Building> SPECIAL_BUILDINGS = new EnumMap<BuildingType, Building>(BuildingType.class);

	private static int stallsLeft;
	private static int troughsLeft;
	private static final Stack<Integer> extsLeft = new Stack<Integer>();
	private static int lastUsedExt = 0;
	private static final List<BuildingType> buildingsLeft = new ArrayList<BuildingType>();
	
	static {
		SPECIAL_BUILDINGS.put(BuildingType.HALF_TIMBERED_HOUSE, new HalfTimberedHouse());
		SPECIAL_BUILDINGS.put(BuildingType.STORAGE_BUILDING, new StorageBuilding());
		SPECIAL_BUILDINGS.put(BuildingType.SHELTER, new Shelter());
		SPECIAL_BUILDINGS.put(BuildingType.OPEN_STABLES, new OpenStables());
	}

	public static void reset() {
		stallsLeft = MAX_STALLS;
		troughsLeft = MAX_FEEDERS;
		extsLeft.addAll(Arrays.asList(EXTS));
		buildingsLeft.clear();
		buildingsLeft.addAll(SPECIAL_BUILDINGS_TYPES);
	}

	public static int getStallsLeft() {
		return stallsLeft;
	}

	public static int getTroughsLeft() {
		return troughsLeft;
	}

	public static int getExpansionsLeft() {
		return extsLeft.size();
	}

	public static List<BuildingType> getBuildingsLeft() {
		return buildingsLeft;
	}

	public static void useStall(boolean use) {
		stallsLeft += use ? 1 : -1;
	}

	public static void useTrough(boolean use) {
		troughsLeft += use ? 1 : -1;
	}

	public static void useExtension(boolean use) {
		if (use) {
			lastUsedExt = extsLeft.pop();
		} else {
			extsLeft.push(lastUsedExt);			
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

	public static int getLastExtensionId() {
		return lastUsedExt;
	}

}
