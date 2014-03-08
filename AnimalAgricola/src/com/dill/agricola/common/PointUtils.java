package com.dill.agricola.common;

import java.util.ArrayList;
import java.util.List;

public class PointUtils {
	
	private PointUtils() {
		
	}

	public static DirPoint getNext(DirPoint pos) {
		if (pos.dir == null) {
			return pos;
		}
		DirPoint ret = new DirPoint(pos, pos.dir.opposite());
		switch (pos.dir) {
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

	public static List<DirPoint> createGridRange(int minX, int maxX, int minY, int maxY) {
		List<DirPoint> range = new ArrayList<DirPoint>();
		for (int i = minX; i < maxX; i++) {
			for (int j = minY; j < maxY; j++) {
				range.add(new DirPoint(i, j));
			}
		}
		return range;
	}
	
	public static List<DirPoint> createGridRange(int maxX, int maxY) {
		return createGridRange(0, maxX, 0, maxY);
	}

	public static boolean isInRange(DirPoint pos, int minX, int maxX, int minY, int maxY) {
		return pos!= null && pos.x >= minX && pos.x < maxX && pos.y >= minY && pos.y < maxY;
	}

	public static boolean isInRange(DirPoint pos, int maxX, int maxY) {
		return isInRange(pos, 0, maxX, 0, maxY);
	}
	
}
