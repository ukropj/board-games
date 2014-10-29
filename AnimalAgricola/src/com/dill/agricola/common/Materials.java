package com.dill.agricola.common;

import java.util.Map;

import com.dill.agricola.model.types.Material;

public class Materials extends Bag<Material> {

	public static final Materials EMPTY = new UnmodifiableMaterials();
	
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

	protected static class UnmodifiableMaterials extends Materials {

		public void add(Material t, int count) {
			throw new UnsupportedOperationException("Cannot modify UnmodifiableMaterials");
		}

		public void substract(Material t, int count) {
			throw new UnsupportedOperationException("Cannot modify UnmodifiableMaterials");
		}

		public void clear() {
			throw new UnsupportedOperationException("Cannot modify UnmodifiableMaterials");
		}

		public void set(Material t, int count) {
			throw new UnsupportedOperationException("Cannot modify UnmodifiableMaterials");
		}
		
		public Bag<Material> set(Bag<Material> other) {
			throw new UnsupportedOperationException("Cannot modify UnmodifiableMaterials");
		}

		public Bag<Material> add(Bag<Material> other) {
			throw new UnsupportedOperationException("Cannot modify UnmodifiableMaterials");
		}

		public Bag<Material> substract(Bag<Material> other) {
			throw new UnsupportedOperationException("Cannot modify UnmodifiableMaterials");
		}

		public Bag<Material> multiply(int multiplier) {
			throw new UnsupportedOperationException("Cannot modify UnmodifiableMaterials");
		}
	};

}
