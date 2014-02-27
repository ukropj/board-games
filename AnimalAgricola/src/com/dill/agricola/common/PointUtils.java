package com.dill.agricola.common;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;



public class PointUtils {
	
	private PointUtils() {
		
	}

	public static Point getNext(Point point, Dir dir) {
		if (dir == null) {
			return point;
		}
		Point ret = new Point(point);
		switch (dir) {
		case N:
			ret.translate(0, -1);
			break;
		case E:
			ret.translate(1, 0);
			break;
		case S:
			ret.translate(0, 1);
			break;
		case W:
			ret.translate(-1, 0);
			break;
		default:
			throw new IllegalArgumentException();
		}
		return ret;
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
	
}
