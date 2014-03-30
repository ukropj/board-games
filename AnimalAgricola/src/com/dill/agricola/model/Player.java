package com.dill.agricola.model;

import java.util.Observer;

import com.dill.agricola.Main;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Dir;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.buildings.Cottage;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.model.types.Purchasable;

public class Player extends SimpleObservable {

	public static final int FARM_H = 3;
	public static final int FARM_W = 2;
	public static final int INIT_BORDERS = 8;

	public static final int USED_EXT_VP = 4;

	public static final int MAX_WORKERS = Main.DEBUG ? 1 : 3;

	private final PlayerColor color;
	public final Farm farm;

	private final Materials material = new Materials();
	private final Animals animals = new Animals();

	private boolean starting;
	private int workers;

	public Player(PlayerColor color) {
		this.color = color;
		this.farm = new Farm();
		init();
	}

	public void init() {
		workers = MAX_WORKERS;
		starting = false;
		material.clear();
		animals.clear();
		farm.init(FARM_W, FARM_H);
		farm.build(new Cottage(), new DirPoint(0, 2));
		addMaterial(Material.BORDER, INIT_BORDERS);

		if (Main.DEBUG) {
//			addMaterial(new Materials(Material.WOOD, 10));
			addMaterial(new Materials(Material.STONE, 20));
			addMaterial(new Materials(Material.REED, 20));

			DirPoint pos = new DirPoint(1, 1);
			farm.put(Purchasable.FENCE, new DirPoint(pos, Dir.N));
			farm.put(Purchasable.FENCE, new DirPoint(pos, Dir.W));
			farm.put(Purchasable.FENCE, new DirPoint(pos, Dir.S));
			farm.put(Purchasable.FENCE, new DirPoint(pos, Dir.E));
			Fencer.calculateFences(farm);
			purchaseAnimal(Animal.COW, 1);
			farm.putAnimals(pos, Animal.COW, 1);
			
//			DirPoint pos2 = new DirPoint(0, 1);
//			farm.build(new Stall(0), pos2);
//			farm.build(new Stables(), pos2);
//			farm.put(Purchasable.TROUGH, pos2);
//			purchaseAnimal(Animal.SHEEP, 10);
//			farm.putAnimals(pos2, Animal.SHEEP, 10);
		}
		Fencer.calculateFences(farm);
	}

	public String toString() {
		return getColor().toString();
	}

	public Farm getFarm() {
		return farm;
	}

	public PlayerColor getColor() {
		return color;
	}

	public void setStartingPlayer(boolean starting) {
		this.starting = starting;
		setChanged();
	}

	public boolean isStarting() {
		return starting;
	}

	public synchronized void addObserver(Observer o) {
		super.addObserver(o);
		farm.addObserver(o);
	}

	public void notifyObservers(Object o) {
		super.notifyObservers(o);
		farm.notifyObservers(o);
	}

	public void addMaterial(Materials m) {
		material.add(m);
		setChanged();
	}

	public void removeMaterial(Materials m) {
		material.substract(m);
		setChanged();
	}

	public void addMaterial(Material m, int count) {
		material.add(m, count);
		setChanged();
	}

	public void removeMaterial(Material m, int count) {
		material.substract(m, count);
		setChanged();
	}

	public int getMaterial(Material m) {
		return material.get(m);
	}

	public void setActiveType(Purchasable type) {
		farm.setActiveType(type);
		setChanged();
	}

	public Purchasable getActiveType() {
		return farm.getActiveType();
	}

	public boolean canPay(Materials cost) {
		return material.isSuperset(cost);
	}

	public void pay(Materials cost) {
		material.substract(cost);
	}

	public void unpay(Materials cost) {
		material.add(cost);
	}

	public boolean canPurchase(Purchasable type, Materials cost, DirPoint pos) {
		return canPay(cost) && (pos == null || !farm.has(type, pos, false));
	}

	public boolean canUnpurchase(Purchasable type, DirPoint pos, boolean activeOnly) {
		return pos == null || farm.has(type, pos, activeOnly);
	}

	public boolean purchase(Purchasable type, Materials cost, DirPoint pos) {
		if (canPurchase(type, cost, pos) && farm.put(type, pos)) {
			material.substract(cost);
			return true;
		}
		return false;
	}

	public boolean unpurchase(Purchasable type, Materials cost, DirPoint pos, boolean activeOnly) {
		if (canUnpurchase(type, pos, activeOnly) && farm.take(type, pos)) {
			material.add(cost);
			return true;
		}
		return false;
	}

	public boolean canPurchase(BuildingType type, Materials cost, DirPoint pos) {
		return canPay(cost) && farm.canBuild(type, pos);
	}

	public boolean canUnpurchase(BuildingType type, DirPoint pos, boolean activeOnly) {
		return pos == null || farm.hasBuilding(pos, type, activeOnly);
	}

	public boolean purchase(Building building, Materials cost, DirPoint pos) {
		if (canPurchase(building.getType(), cost, pos) && farm.build(building, pos)) {
			material.substract(cost);
			building.setPaidCost(cost);
			return true;
		}
		return false;
	}

	public Building unpurchase(BuildingType type, DirPoint pos, boolean activeOnly) {
		Building b = null;
		if (canUnpurchase(type, pos, activeOnly) && (b = farm.unbuild(pos)) != null) {
			material.add(b.getPaidCost());
			b.setPaidCost(null);
			return b;
		}
		return null;
	}

	public int getAnimal(Animal type) {
		return animals.get(type);
	}

	public boolean purchaseAnimal(Animal type, int count) {
		Main.asrtPositive(count, "Cannot purchase negative amount");
		animals.add(type, count);
		farm.addAnimals(type, count);
		return true;
	}

	public boolean purchaseAnimals(Animals newAnimals) {
		animals.add(newAnimals);
		// TODO refactor, add farm methods
		for (Animal a : Animal.values()) {
			int count = newAnimals.get(a);
			if (count > 0) {
				farm.addAnimals(a, count);
			}
		}
		return true;
	}

	public boolean unpurchaseAnimal(Animal type, int count) {
		Main.asrtPositive(count, "Cannot unpurchase negative amount");
		if (animals.get(type) >= count) {
			farm.removeAnimals(type, count);
			animals.substract(type, count);
			return true;
		}
		return false;
	}

	public boolean unpurchaseAnimals(Animals newAnimals) {
		if (animals.isSuperset(newAnimals)) {
			for (Animal a : Animal.values()) {
				int count = newAnimals.get(a);
				if (count > 0) {
					farm.removeAnimals(a, count);
				}
			}
			animals.substract(newAnimals);
			return true;
		}
		return false;
	}

	public int getBuildingCount(BuildingType type) {
		int count = 0;
		for (Building b : farm.getFarmBuildings()) {
			if (b.getType() == type) {
				count++;
			}
		}
		return count;
	}

	public boolean validate() {
		return farm.hasValidAnimals();
	}

	public Animals breedAnimals() {
		Animals newAnimals = new Animals();
		for (Animal type : Animal.values()) {
			if (animals.get(type) >= 2) {
				purchaseAnimal(type, 1);
				newAnimals.add(type, 1);
			}
		}
		return newAnimals;
	}

	public Animals releaseAnimals() {
		Animals lostAnimals = new Animals();
		for (Animal type : Animal.values()) {
			int loose = farm.getLooseAnimals(type);
			if (unpurchaseAnimal(type, loose)) {
				lostAnimals.add(type, loose);
			}
		}
		return lostAnimals;
	}

	public int getWorkers() {
		return workers;
	}

	public boolean hasWorkers() {
		return workers > 0;
	}

	public void spendWorker() {
		workers--;
		setChanged();
	}

	public void returnWorker() {
		workers++;
		setChanged();
	}

	public void spendAllWorkers() {
		workers = 0;
		setChanged();
	}
	
	public void returnAllWorkers() {
		workers = MAX_WORKERS;
		setChanged();
	}

	public int getAnimalScore(Animal type) {
		int count = getAnimal(type);
		return count + type.getBonusDirPoints(count);
	}

	public int getExtensionsScore() {
		return farm.getUsedExtensions() * USED_EXT_VP;
	}

	public float getBuildingScore() {
		float score = 0;
		for (Building b : farm.getFarmBuildings()) {
			score += b.getVictoryDirPoints(this);
		}
		return score;
	}

	public float getScore() {
		// score animals
		int animals = 0;
		for (Animal type : Animal.values()) {
			animals += getAnimalScore(type);
//			System.out.println("\t" + type + ": " + getAnimalScore(type));
		}
		// score extensions
		int usedExts = getExtensionsScore();
//		System.out.println("\tExtensions: " + usedExts);
		// score buildings
		float buildings = getBuildingScore();
//		System.out.println("\tBuildings: " + buildings);

		return animals + usedExts + buildings;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (color != other.color)
			return false;
		return true;
	}

}
