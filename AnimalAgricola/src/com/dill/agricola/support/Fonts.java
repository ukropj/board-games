package com.dill.agricola.support;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class Fonts {

	// TODO load fonts from file
	
	private static final Font CALIBRI = new Font("Calibri", Font.BOLD, 15);	
	private static final Font DOMNICAN = new Font("Dominican", Font.BOLD, 15);	
	
	public static final Font FARM_FONT = CALIBRI.deriveFont(15f);
	public static final Font ACTION_NUMBER_FONT = CALIBRI.deriveFont(18f);
	public static final Font ACTION_TEXT_FONT = CALIBRI.deriveFont(12f);
	
//	public static final Font BUILDING_FONT = CALIBRI.deriveFont(Font.PLAIN).deriveFont(20f);
	public static final Font BUILDING_FONT = DOMNICAN.deriveFont(Font.PLAIN).deriveFont(20f);
	
	public static void updateFontToFit(Graphics2D g, String text, int width) {
		Font font = g.getFont();
		FontMetrics fm = g.getFontMetrics();
		int size = font.getSize();

		while (fm.stringWidth(text) > width) {
			size--;
			font = font.deriveFont((float)size);
			fm = g.getFontMetrics(font);
		}
		System.out.println(size);
		g.setFont(font);
	}
	
}
