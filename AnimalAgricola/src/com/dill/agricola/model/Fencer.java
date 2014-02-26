package com.dill.agricola.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dill.agricola.common.Dir;
import com.dill.agricola.common.Point;

public class Fencer {

	private Fencer() {
	}
	
	public static void calculateFences(Farm farm) {
		int w = farm.getWidth();
		int h = farm.getHeight();
		int[][] floodMap = new int[w][h];
		List<Point> range = Point.createGridRange(w, h);
		List<Dir> dirs = Arrays.asList(Dir.W, Dir.N, Dir.S, Dir.E);
		// init
		for (Point pos : range) {
			floodMap[pos.x][pos.y] = farm.getSpace(pos).isAlwaysEnclosed() ? 0 : -1;
		}
		// calculate pastures and feeders
		int counter = 0;
		int pastures = 0;
		boolean changed;
		do {
			changed = false;
			counter++;
			for (Point pos : range) {
				int current = floodMap[pos.x][pos.y];
				for (Dir d : dirs) {
					if (!farm.isClosed(pos, d)) {
						Point nextPos = pos.move(d);
						if (Point.isInRange(nextPos, w, h)) {
							int next = floodMap[nextPos.x][nextPos.y];
							if (current < 0) {
								current = next;
							} else if (next < 0) {
								next = current;
							} else {
								current = next = Math.min(current, next);
							}
							if (floodMap[nextPos.x][nextPos.y] != next) {
								floodMap[nextPos.x][nextPos.y] = next;
								changed = true;
							}
						} else {
							current = 0;
						}
					}
				}
				if (current < 0) {
					current = ++pastures;
				}
				if (floodMap[pos.x][pos.y] != current) {
					floodMap[pos.x][pos.y] = current;
					changed = true;
				}
			}
//			print(floodMap);
		} while (changed && counter < 10);
		
		// one more pass to fill maps
		Map<Integer, List<Space>> spaceMap = new HashMap<Integer, List<Space>>();
		for (Point pos : range) {
			int pastureNo = floodMap[pos.x][pos.y];
			if (pastureNo > 0) {
				Space space = farm.getSpace(pos);
				// collect spaces per each pasture
				if (!spaceMap.containsKey(pastureNo)) {
					spaceMap.put(pastureNo, new ArrayList<Space>());
				}
				spaceMap.get(pastureNo).add(space);
			}
		}

//		print(floodMap);
//		System.out.println("Fencer iterations: " + counter);
//		System.out.println("Feeders: " + feederMap);
//		System.out.println("----------------");
		
		// process results
		boolean farmValid = true;
		for (Point pos : range) {
			int pastureNo = floodMap[pos.x][pos.y];
			Space space = farm.getSpace(pos);
			space.setPasture(spaceMap.get(pastureNo));
			// check
			farmValid = farmValid && space.isValid();
		}
		farm.setValidAnimals(farmValid);
	}

	/*private static void print(int[][] map) {
		List<Point> range = Point.createGridRange(map[0].length, map.length);
		for (Point pos : range) {
			System.out.print(map[pos.y][pos.x]);
			if (pos.y == map.length - 1) {
				System.out.println();
			}
		}
		System.out.println("----------------");
	}*/

}
