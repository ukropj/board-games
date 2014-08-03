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
		translate(ret, pos.dir);
		return ret;
	}

	public static void translate(DirPoint pos, Dir dir) {
		switch (dir) {
		case N:
			pos.translate(0, -1);
			return;
		case E:
			pos.translate(1, 0);
			return;
		case S:
			pos.translate(0, 1);
			return;
		case W:
			pos.translate(-1, 0);
			return;
		default:
			throw new IllegalArgumentException();
		}
	}

	public static boolean isNextTo(DirPoint pos1, DirPoint pos2) {
		int dx = Math.abs(pos1.x - pos2.x);
		int dy = Math.abs(pos1.y - pos2.y);
		return dx + dy == 1;
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
		return pos != null && pos.x >= minX && pos.x < maxX && pos.y >= minY && pos.y < maxY;
	}

	public static boolean isInRange(DirPoint pos, int maxX, int maxY) {
		return isInRange(pos, 0, maxX, 0, maxY);
	}

}
