package com.dill.agricola.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

public class Msg {

	private static Locale locale;
	private static ResourceBundle messages;

	private Msg() {
	}

	public static void load(Locale locale) {
		Msg.locale = locale;
		try {
			messages = ResourceBundle.getBundle("messages", locale,
					new UnicodeControl());
		} catch (MissingResourceException e) {
			e.printStackTrace();
		}
	}

	public static String get(String code) {
		try {
			return messages.getString(code);
		} catch (MissingResourceException e) {
			System.err.println("Message not found: " + code);
			return code;
		}
	}

	public static String get(String code, Object... params) {
		try {
			String msg = messages.getString(code);
			for (int i = 0; i < params.length; i++) {
				msg = msg.replaceAll("\\$\\{" + i + "\\}", params[i].toString());
			}
			return msg;
		} catch (MissingResourceException e) {
			System.err.println("Message not found: " + code);
			return code;
		}
	}

	public static String getNum(int number, String code, Object... params) {
		number = Math.abs(number);
		if (number == 0 || number >= 5) {
			code += "Many";
		} else if (number == 1) {
			code += "One";
		} else if (number < 5) {
			code += "Few";
		}
		return get(code, params);
	}

	private static class UnicodeControl extends Control {
		public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader,
				boolean reload) throws IllegalAccessException, InstantiationException, IOException {
			// The below is a copy of the default implementation.
			String bundleName = toBundleName(baseName, locale);
			String resourceName = toResourceName(bundleName, "properties");
			ResourceBundle bundle = null;
			InputStream stream = null;
			if (reload) {
				URL url = loader.getResource(resourceName);
				if (url != null) {
					URLConnection connection = url.openConnection();
					if (connection != null) {
						connection.setUseCaches(false);
						stream = connection.getInputStream();
					}
				}
			} else {
				stream = loader.getResourceAsStream(resourceName);
			}
			if (stream != null) {
				try {
					// Only this line is changed to make it to read properties
					// files as UTF-8.
					bundle = new PropertyResourceBundle(new InputStreamReader(
							stream, "UTF-8"));
				} finally {
					stream.close();
				}
			}
			return bundle;
		}
	}

	public static Object getLocaleName() {
		return locale.getLanguage();
	}

}
