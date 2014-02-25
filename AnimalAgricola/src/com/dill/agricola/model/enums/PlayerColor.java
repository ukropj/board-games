package com.dill.agricola.model.enums;

import java.awt.Color;

public enum PlayerColor {
	BLUE (new Color(116, 116, 245)), RED(new Color(255, 100, 71));

	private final Color color;

	private PlayerColor(Color color) {
		this.color = color;
	}
	
	public PlayerColor other() {
		return this == BLUE ? RED : BLUE;
	}
	
	public Color getRealColor() {
		return color;
	}
}
