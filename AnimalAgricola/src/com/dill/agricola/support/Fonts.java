package com.dill.agricola.support;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class Fonts {

	// TODO load fonts from file
	
	public static final Font TEXT_FONT = new Font("Calibri", Font.BOLD, 15);	
//	private static final Font DOMNICAN = new Font("Dominican", Font.BOLD, 15);	
	
	public static final Font FARM_FONT = TEXT_FONT.deriveFont(15f);
	public static final Font FARM_BUILDING = TEXT_FONT.deriveFont(10f);
	public static final Font FARM_MESSAGE = TEXT_FONT.deriveFont(13f);
	
	public static final Font TEXT_BIG = TEXT_FONT.deriveFont(18f);
	
	public static final Font ACTION_NUMBER = TEXT_FONT.deriveFont(18f);
	public static final Font ACTION_TEXT = TEXT_FONT.deriveFont(12f);
	public static final Font ACTION_TEXT_BIG = TEXT_FONT.deriveFont(16f);
	
//	public static final Font BUILDING_ICON = DOMNICAN.deriveFont(Font.PLAIN).deriveFont(20f);
	public static final Font BUILDING_ICON = TEXT_FONT.deriveFont(Font.PLAIN).deriveFont(20f);
	
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
			font = font.deriveFont((float)size);
			fm = g.getFontMetrics(font);
		}
		g.setFont(font);
	}
	
}
