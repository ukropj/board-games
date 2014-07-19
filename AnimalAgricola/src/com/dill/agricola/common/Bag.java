package com.dill.agricola.common;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class Bag<T extends Enum<T>> {

	protected final Map<T, Integer> map;
	protected final Class<T> clazz;
	protected final T[] values;
	
	public Bag(Class<T> clazz) {
		this.clazz = clazz;
		this.values = clazz.getEnumConstants();
		map = new EnumMap<T, Integer>(clazz);
		for (T t : values) {
			map.put(t, 0);
		}
	}

	public Bag(Class<T> clazz, T... types) {
		this(clazz);
		for (T t : types) {
			map.put(t, map.get(t) + 1);
		}
	}
	
	public Bag(Class<T> clazz, T type, int count) {
		this(clazz);
		map.put(type, count);
	}

	public Bag(Class<T> clazz, Map<T, Integer> materials) {
		this(clazz);
		for (Entry<T, Integer> t : materials.entrySet()) {
			map.put(t.getKey(), t.getValue());
		}
	}

	public int get(T t) {
		return map.get(t);
	}

	public void set(T t, int count) {
		map.put(t, count);
	}

	public void add(T t, int count) {
		map.put(t, map.get(t) + count);
	}
	
	public void substract(T t, int count) {
		map.put(t, map.get(t) - count);
	}

	public boolean isEmpty() {
		for (T t : values) {
			if (map.get(t) > 0) {
				return false;
			}
		}
		return true;
	}
	
	public void clear() {
		for (T t : values) {
			map.put(t, 0);
		}
	}

	public boolean isSuperset(Bag<T> other) {
		// true if this Ts have at least as much of all materials the the
		// other Ts
		for (T t : values) {
			if (map.get(t) < other.get(t)) {
				return false;
			}
		}
		return true;
	}

	public Bag<T> set(Bag<T> other) {
		for (T t : values) {
			map.put(t, other.get(t));
		}
		return this;
	}
	
	public Bag<T> add(Bag<T> other) {
		for (T t : values) {
			map.put(t, map.get(t) + other.get(t));
		}
		return this;
	}

	public Bag<T> substract(Bag<T> other) {
		for (T t : values) {
			map.put(t, map.get(t) - other.get(t));
		}
		return this;
	}

	public Bag<T> multiply(int multiplier) {
		for (T t : values) {
			map.put(t, map.get(t) * multiplier);
		}
		return this;
	}

	public int size() {
		int s = 0;
		for (T t : values) {
			s += map.get(t);
		}
		return s;
	}
	
	public int subSize(Set<T> types) {
		int s = 0;
		for (T t : types) {
			s += map.get(t);
		}
		return s;
	}
	
	public Set<T> types() {
		Set<T> types = new TreeSet<T>();
		for (T t : values) {
			if (map.get(t) > 0) {
				types.add(t);
			}
		}
		return types;
	}
	
	public boolean hasSingleType() {
		boolean has = false;
		for (T t : values) {
			if (map.get(t) > 0) {
				if (has) {
					return false;
				} 
				has = true;
			}
		}
		return has;
	}

	public T getFirstType() {
		for (T t : values) {
			if (map.get(t) > 0) {
				return t;
			}
		}
		return null;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		for (Entry<T, Integer> t : map.entrySet()) {
			int v = t.getValue();
			if (v > 0) {
				sb.append(t.getKey()).append(":").append(v).append(", ");
			}
		}
		if (sb.length() > 1) {
			sb.setLength(sb.length() - 2);
		}
		sb.append("]");
		return sb.toString();
	}

	public static <K extends Enum<K>> int sumSize(Bag<K>[] bags) {
		int s = 0;
		for (int i = 0; i < bags.length; i++) {
			s += bags[i].size();
		}
		return s;
	}

}
