package com.dill.agricola.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.xml.internal.ws.util.StringUtils;

public class Namer {
	
	private static final Pattern NON_CAMEL = Pattern.compile("([^-_]+)[-_]*");

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

	public static String toCamelCase(String str) {
		Matcher m = NON_CAMEL.matcher(str);
		StringBuilder sb = new StringBuilder();
		while (m.find()) {
			sb.append(StringUtils.capitalize(m.group(1).toLowerCase()));
		}
		return sb.toString();
	}
}
