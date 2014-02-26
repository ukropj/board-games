package com.dill.agricola.support;

public class Namer {

	private Namer() {
	}
	
	public static String getName(Object o) {
		return o.getClass().getSimpleName();
	}

	public static String getName(Object o, int chars) {
		return getName(o).substring(0, chars);
	}
	
	public static String getShortName(Object o) {
		return getName(o, 1);
	}
}
