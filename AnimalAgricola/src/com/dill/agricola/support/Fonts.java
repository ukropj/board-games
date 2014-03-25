package com.dill.agricola.support;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class Fonts {

	// TODO load fonts from file
	
	private static final Font CALIBRI = new Font("Calibri", Font.BOLD, 15);	
//	private static final Font DOMNICAN = new Font("Dominican", Font.BOLD, 15);	
	
	public static final Font FARM_FONT = CALIBRI.deriveFont(15f);
	public static final Font FARM_BUILDING = CALIBRI.deriveFont(10f);
	public static final Font FARM_MESSAGE = CALIBRI.deriveFont(13f);
	
	public static final Font TEXT_BIG = CALIBRI.deriveFont(18f);
	
	public static final Font ACTION_NUMBER = CALIBRI.deriveFont(18f);
	public static final Font ACTION_TEXT = CALIBRI.deriveFont(12f);
	public static final Font ACTION_TEXT_BIG = CALIBRI.deriveFont(16f);
	
//	public static final Font BUILDING_ICON = DOMNICAN.deriveFont(Font.PLAIN).deriveFont(20f);
	public static final Font BUILDING_ICON = CALIBRI.deriveFont(Font.PLAIN).deriveFont(20f);
	
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
