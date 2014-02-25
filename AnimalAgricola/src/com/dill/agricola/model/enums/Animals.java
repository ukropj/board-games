package com.dill.agricola.model.enums;

import java.util.Map;

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
