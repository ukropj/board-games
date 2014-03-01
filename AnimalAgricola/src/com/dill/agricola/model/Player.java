package com.dill.agricola.model;

import java.awt.Point;
import java.util.Observer;

import com.dill.agricola.Main;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Dir;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.buildings.Cottage;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.model.types.Purchasable;

public class Player extends SimpleObservable {

	public static final int FARM_H = 3;
	public static final int FARM_W = 2;
	public static final int INIT_BORDERS = 8;

	public static final int USED_EXT_VP = 4;

	public static final int MAX_WORKERS = Main.DEBUG ? 3 : 3;

	private final PlayerColor color;
	private final Farm farm;

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
		purchaseFreeBuilding(new Cottage());
		farm.build(new Point(0, 2));
		addMaterial(Material.BORDER, INIT_BORDERS);

		if (Main.DEBUG) {
			addMaterial(new Materials(Material.WOOD, 20));
			addMaterial(new Materials(Material.STONE, 20));
			addMaterial(new Materials(Material.REED, 20));
			
//			purchase(Purchasable.EXTENSION);
//			purchase(Purchasable.EXTENSION);
//			purchase(Purchasable.EXTENSION);
//			farm.extend(Dir.W);
//			farm.extend(Dir.E);
//			farm.extend(Dir.E);
			purchase(Purchasable.FENCE);
			purchase(Purchasable.FENCE);
			purchase(Purchasable.FENCE);
			purchase(Purchasable.FENCE);
			Point pos = new Point(1,1);
			farm.putFence(pos, Dir.N);
			farm.putFence(pos, Dir.W);
			farm.putFence(pos, Dir.S);
			farm.putFence(pos, Dir.E);
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
	}
	
	public Purchasable getActiveType() {
		return farm.getActiveType();
	}

	public boolean canPay(Materials cost) {
		if (material.isSuperset(cost)) {
			return true;
		} else {
			System.out.println(getColor() + ": cannot pay " + cost);
			return false;
		}
	}

	public boolean purchase(Purchasable type, Materials cost) {
		if (canPay(cost)) {
			material.substract(cost);
			return purchase(type);
		}
		return false;
	}

	public boolean purchase(Purchasable type) {
		farm.addUnused(type, 1);
		return true;
	}

	public boolean unpurchase(Purchasable type, Materials cost) {
		if (unpurchase(type)) {
			material.add(cost);
			return true;
		}
		return false;

	}

	public boolean unpurchase(Purchasable type) {
		if (farm.remove(type)) {
			return true;
		}
		return false;
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

	public boolean purchaseBuilding(Building building, Materials cost) {
		if (canPay(cost)) {
			material.substract(cost);
			building.setPaidCost(cost);
			farm.addBuilding(building);
			return true;
		} else {
			return false;
		}
	}

	public boolean purchaseFreeBuilding(Building building) {
		building.setPaidCost(new Materials());
		farm.addBuilding(building);
		return true;
	}

	public Building unpurchaseBuilding() {
		Building b = farm.removeBuilding();
		if (b != null) {
			material.add(b.getPaidCost());
			b.setPaidCost(null);
			return b;
		}
		return null;
	}

	public boolean validate() {
		return farm.getAllUnusedCount() == 0 && farm.getUnusedBuildings().size() == 0 && farm.hasValidAnimals();
	}

	public int breedAnimals() {
		int newAnimals = 0;
		for (Animal type : Animal.values()) {
			if (animals.get(type) >= 2) {
				purchaseAnimal(type, 1);
				newAnimals++;
			}
		}
		return newAnimals;
	}

	public int releaseAnimals() {
		int count = 0;
		for (Animal type : Animal.values()) {
			int loose = farm.getLooseAnimals(type);
			if (unpurchaseAnimal(type, loose)) {
				count += loose;
			}
		}
		return -count;
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

	public void returnAllWorkers() {
		workers = MAX_WORKERS;
		setChanged();
	}
	
	public int getAnimalScore(Animal type) {
		int count = getAnimal(type);
		return count + type.getBonusPoints(count);
	}
	
	public int getExtensionsScore() {
		return farm.getUsedExtensions() * USED_EXT_VP;
	}
	
	public float getBuildingScore() {
		float score = 0;
		for (Building b : farm.getBuiltBuildings()) {
			score += b.getVictoryPoints(this);
		}
		return score;
	}

	public float getScore() {
		// score animals
		int animals = 0;
		for (Animal type : Animal.values()) {
			animals += getAnimalScore(type);
			System.out.println("\t" + type + ": " + getAnimalScore(type));
		}
		// score extensions
		int usedExts = getExtensionsScore();
		System.out.println("\tExtensions: " + usedExts);
		// score buildings
		float buildings = getBuildingScore();
		System.out.println("\tBuildings: " + buildings);

		return animals + usedExts + buildings;
	}

}
