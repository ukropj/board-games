package com.dill.agricola.common;

import java.util.Map;

import com.dill.agricola.model.types.Animal;

public class Animals extends Bag<Animal>{

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

}
