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
import com.dill.agricola.model.buildings.Stall;
import com.dill.agricola.model.buildings.StorageBuilding;
import com.dill.agricola.model.types.BuildingType;

public class GeneralSupply {

	public final static int MAX_TROUGHS = 10;
	public final static Integer[] EXTS = {0,1,2,3};
	public final static Stall[] STALLS = {new Stall(0), new Stall(1), new Stall(2), new Stall(3)};
	
	private final static List<BuildingType> SPECIAL_BUILDINGS_TYPES = Arrays.asList(new BuildingType[]{BuildingType.HALF_TIMBERED_HOUSE, 
			BuildingType.STORAGE_BUILDING, BuildingType.SHELTER, BuildingType.OPEN_STABLES});
	private final static Map<BuildingType, Building> SPECIAL_BUILDINGS = new EnumMap<BuildingType, Building>(BuildingType.class);

	private static final Stack<Stall> stallsLeft = new Stack<Stall>();
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
		stallsLeft.clear();
		stallsLeft.addAll(Arrays.asList(STALLS));
		troughsLeft = MAX_TROUGHS;
		extsLeft.clear();
		extsLeft.addAll(Arrays.asList(EXTS));
		buildingsLeft.clear();
		buildingsLeft.addAll(SPECIAL_BUILDINGS_TYPES);
	}

	public static int getTroughsLeft() {
		return troughsLeft;
	}
	
	public static int getStallsLeft() {
		return stallsLeft.size();
	}

	public static int getExpansionsLeft() {
		return extsLeft.size();
	}

	public static List<BuildingType> getBuildingsLeft() {
		return buildingsLeft;
	}

	public static void useTrough(boolean use) {
		troughsLeft += use ? 1 : -1;
	}
	
	public static Stall useStall() {
		return stallsLeft.pop();
	}

	public static void unuseStall(Stall stall) {
		stallsLeft.push(stall);
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
			// TODO building open stables returns one stall to supply!
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
