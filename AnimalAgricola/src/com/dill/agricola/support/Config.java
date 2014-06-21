package com.dill.agricola.support;

import java.lang.reflect.Method;
import java.util.prefs.Preferences;

import com.dill.agricola.Main;

public class Config {

	public static enum ConfigKey {
		LANG,
		LAST_STARTING_PLAYER,
		MORE_BUILDINGS,
		EVEN_MORE_BUILDINGS, 
		CONDENSED_LAYOUT;
	}
	
	private final static Preferences prefs = Preferences.userNodeForPackage(Main.class);
	
	private Config() {
	}
	
	public static boolean getBoolean(ConfigKey key, boolean def) {
		return prefs.getBoolean(key.toString(), def);
	}
	
	public static void putBoolean(ConfigKey key, boolean value) {
		prefs.putBoolean(key.toString(), value);
	}
	
	public static <E extends Enum<?>> void putEnum(ConfigKey key, E value) {
		prefs.put(key.toString(), value.toString());
	}
	
	@SuppressWarnings("unchecked")
	public static <E extends Enum<?>> E getEnum(ConfigKey key, Class<E> enumClass, E def) {
		String val = prefs.get(key.toString(), null);
		if (val == null) {
			return def;
		}
		try {
			Method valueOfMethod = enumClass.getMethod("valueOf", String.class);
			valueOfMethod.setAccessible(true);
			return (E) valueOfMethod.invoke(null, val.toUpperCase());
		} catch(Exception e) {
			// not an enum value
			return def;
		}
	}

}
