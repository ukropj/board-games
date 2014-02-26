package com.dill.agricola.model.types;

import java.awt.Color;

public enum Animal {

	SHEEP(Color.WHITE, Color.BLACK, new int[] { 4, 8, 11 }), //
	PIG(Color.BLACK, Color.WHITE, new int[] { 4, 7, 9 }), //
	COW(new Color(165, 42, 42), Color.WHITE, new int[] { 4, 6, 8 }), //
	HORSE(Color.GRAY, Color.BLACK, new int[] { 4, 5, 7 });

	private final Color color;
	private final Color color2;
	private final int[] scoringRanges;

	private Animal(Color color, Color color2, int[] scoringRanges) {
		this.color = color;
		this.color2 = color2;
		this.scoringRanges = scoringRanges;
	}

	public Color getColor() {
		return color;
	}

	public Color getContrastingColor() {
		return color2;
	}

	public int getBonusPoints(int count) {
		if (count < scoringRanges[0]) {
			return -3;
		}
		if (count < scoringRanges[1]) {
			return 0;
		}
		if (count < scoringRanges[2]) {
			return 1;
		}
		if (count == scoringRanges[2]) {
			return 2;
		}
		return count - (scoringRanges[2] - 1);
	}

	/*public static void main(String[] args) {
		// test Animal scoring
		for (Animal a : Animal.values()) {
			System.out.println(a);
			for (int i = 0; i < 20; i++) {
				System.out.printf("%2d,", i + a.getBonusPoints(i));
			}
			System.out.println();
		}
	}*/
}
