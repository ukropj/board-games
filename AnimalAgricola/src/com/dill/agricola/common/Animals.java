package com.dill.agricola.common;

import java.util.Map;

import com.dill.agricola.model.types.Animal;

public class Animals extends Bag<Animal> {
	
	public static final Animals EMPTY = new UnmodifiableAnimals();

	public Animals() {
		super(Animal.class);
	}

	public Animals(Animal... animals) {
		super(Animal.class, animals);
	}
	
	public Animals(Animal type, int count) {
		super(Animal.class, type, count);
	}

	public Animals(Map<Animal, Integer> animals) {
		super(Animal.class, animals);
	}
	
	public Animals(Animals animals) {
		super(Animal.class, animals.map);
	}
	
	protected static class UnmodifiableAnimals extends Animals {

		public void add(Animal t, int count) {
			throw new UnsupportedOperationException("Cannot modify UnmodifiableAnimals");
		}

		public void substract(Animal t, int count) {
			throw new UnsupportedOperationException("Cannot modify UnmodifiableAnimals");
		}

		public void clear() {
			throw new UnsupportedOperationException("Cannot modify UnmodifiableAnimals");
		}

		public void set(Animal t, int count) {
			throw new UnsupportedOperationException("Cannot modify UnmodifiableAnimals");
		}
		
		public Bag<Animal> set(Bag<Animal> other) {
			throw new UnsupportedOperationException("Cannot modify UnmodifiableAnimals");
		}

		public Bag<Animal> add(Bag<Animal> other) {
			throw new UnsupportedOperationException("Cannot modify UnmodifiableAnimals");
		}

		public Bag<Animal> substract(Bag<Animal> other) {
			throw new UnsupportedOperationException("Cannot modify UnmodifiableAnimals");
		}

		public Bag<Animal> multiply(int multiplier) {
			throw new UnsupportedOperationException("Cannot modify UnmodifiableAnimals");
		}
	};

}
