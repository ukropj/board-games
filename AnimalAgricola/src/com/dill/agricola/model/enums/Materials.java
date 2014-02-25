package com.dill.agricola.model.enums;

import java.util.Map;

public class Materials extends Bag<Material>{

	public Materials() {
		super(Material.class);
	}

	public Materials(Material... materials) {
		super(Material.class, materials);
	}
	
	public Materials(Material type, int count) {
		super(Material.class, type, count);
	}

	public Materials(Map<Material, Integer> materials) {
		super(Material.class, materials);
	}

	public Materials(Materials materials) {
		super(Material.class, materials.map);
	}
}
