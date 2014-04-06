package com.dill.agricola.model.types;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Animal {

	SHEEP(new Color(229, 229, 229), Color.BLACK, new int[] { 4, 8, 11 }),
	PIG(new Color(21,22,26), Color.WHITE, new int[] { 4, 7, 9 }),
	COW(new Color(67, 34, 30), Color.WHITE, new int[] { 4, 6, 8 }),
	HORSE(new Color(180, 125, 93), Color.BLACK, new int[] { 4, 5, 7 });

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
	
	public int getDoublePointsTreshold() {
		return scoringRanges[2] + 1;
	}

	public static Animal[] reversedValues() {
		List<Animal> list = Arrays.asList(Animal.values());
		Collections.reverse(list);
		return list.toArray(new Animal[4]);
	}

	/*public static void main(String[] args) {
		// test Animal scoring
		for (Animal a : Animal.values()) {
			System.out.println(a);
			for (int i = 0; i < 20; i++) {
				System.out.printf("%2d,", i + a.getBonusDirPoints(i));
			}
			System.out.println();
		}
	}*/
}
