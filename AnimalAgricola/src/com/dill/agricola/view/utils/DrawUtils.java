package com.dill.agricola.view.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class DrawUtils {

	public static final Color BORDER_COLOR = Color.BLACK;
	
	private DrawUtils() {
	}

	private static int TL(float width) {
		return Math.round(width) / 2;
	}
	
	private static int BD(float width) {
		return Math.round(width) / 2 + Math.round(width) % 2;
	}
	
	public static void drawFillRect(Graphics2D g, int x, int y, int w, int h, Color fillColor) {
		drawFillRect(g, x, y, w, h, fillColor, BORDER_COLOR);
	}

	public static void drawFillRect(Graphics2D g, int x, int y, int w, int h, Color fillColor, Color borderColor) {
		float width = ((BasicStroke) g.getStroke()).getLineWidth();
		int xy = TL(width), wh = TL(width) + BD(width);
		g.setColor(fillColor);
		g.fillRect(x, y, w, h);
		g.setColor(borderColor);
		g.drawRect(x + xy, y + xy, w -wh, h - wh);
	}
	
	public static void drawFillRRect(Graphics2D g, int x, int y, int w, int h, int r, Color fillColor) {
		drawFillRRect(g, x, y, w, h, r, fillColor, BORDER_COLOR);
	}
	
	public static void drawFillRRect(Graphics2D g, int x, int y, int w, int h, int r, Color fillColor, Color borderColor) {
		g.setColor(fillColor);
		g.fillRoundRect(x, y, w, h, r, r);
		g.setColor(borderColor);
		g.drawRoundRect(x, y, w, h, r,r);
	}
	
	public static void drawFillCircle(Graphics2D g, int x, int y, int r, Color fillColor) {
		drawFillCircle(g, x, y, r, fillColor, BORDER_COLOR);
	}
	
	public static void drawFillCircle(Graphics2D g, int x, int y, int r, Color fillColor, Color borderColor) {
		g.setColor(fillColor);
		g.fillOval(x, y, r, r);
		g.setColor(borderColor);
		g.drawOval(x, y, r, r);
	}
	
}
