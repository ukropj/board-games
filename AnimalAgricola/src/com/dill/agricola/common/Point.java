package com.dill.agricola.common;

import java.util.ArrayList;
import java.util.List;



public class Point {
	public final int x;
	public final int y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Point move(Dir d) {
		if (d == null) {
			return this;
		}
		switch (d) {
		case N:
			return new Point(x, y - 1);
		case E:
			return new Point(x + 1, y);
		case S:
			return new Point(x, y + 1);
		case W:
			return new Point(x - 1, y);
		default:
			throw new IllegalArgumentException();
		}
	}

	public static List<Point> createGridRange(int minX, int maxX, int minY, int maxY) {
		List<Point> range = new ArrayList<Point>();
		for (int i = minX; i < maxX; i++) {
			for (int j = minY; j < maxY; j++) {
				range.add(new Point(i, j));
			}
		}
		return range;
	}
	
	public static List<Point> createGridRange(int maxX, int maxY) {
		return createGridRange(0, maxX, 0, maxY);
	}

	public static boolean isInRange(Point pos, int minX, int maxX, int minY, int maxY) {
		return pos!= null && pos.x >= minX && pos.x < maxX && pos.y >= minY && pos.y < maxY;
	}

	public static boolean isInRange(Point pos, int maxX, int maxY) {
		return isInRange(pos, 0, maxX, 0, maxY);
	}
	
	public String toString() {
		return "[" + x + "," + y + "]";
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	
}
