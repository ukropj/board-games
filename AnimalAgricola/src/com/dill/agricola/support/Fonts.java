package com.dill.agricola.support;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.net.URL;

import com.dill.agricola.Main;

public class Fonts {

	public static final Font TEXT_FONT = loadFont("FiraSans-Medium.ttf").deriveFont(Font.PLAIN, 15f);
	public static final Font TEXT_FONT_BOLD = loadFont("FiraSans-Bold.ttf").deriveFont(Font.PLAIN, 15f);
	
	private static Font loadFont(String name) {
		try {
			URL fontUrl = Main.class.getResource("resources/fonts/" + name);
			return Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
//	public static final Font TEXT_FONT = new Font("Calibri", Font.BOLD, 15);

	public static final Font TOOLBAR_BTN = TEXT_FONT.deriveFont(13f);
	public static final Font TOOLBAR_TEXT = TEXT_FONT_BOLD.deriveFont(17f);

	public static final Font FARM_NAME = TEXT_FONT.deriveFont(22f);
	public static final Font FARM_FONT = TEXT_FONT.deriveFont(15f);
	public static final Font FARM_BUILDING = TEXT_FONT.deriveFont(10f);
	public static final Font FARM_MESSAGE = TEXT_FONT.deriveFont(13f);

	public static final Font ACTION_NUMBER = TEXT_FONT_BOLD.deriveFont(16f);
	public static final Font ACTION_TEXT = TEXT_FONT.deriveFont(12f);
	public static final Font ACTION_TEXT_BIG = TEXT_FONT.deriveFont(14f);

	public static final Font BUILDING_ICON = TEXT_FONT.deriveFont(20f);

	public static void updateFontToFit(Graphics2D g, String text, int width) {
		Font font = g.getFont();
		FontMetrics fm = g.getFontMetrics();
		int size = font.getSize();
		String[] lines = text.split("[\r\n]+");
		// find longest line
		String longestLine = lines[0];
		for (String line : lines) {
			if (line.length() > longestLine.length()) {
				longestLine = line;
			}
		}
		// decrease font size until longest line is shorter than desired width
		while (fm.stringWidth(longestLine) > width) {
			size--;
			font = font.deriveFont((float) size);
			fm = g.getFontMetrics(font);
		}
		g.setFont(font);
	}
	
}
