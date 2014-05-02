package com.dill.agricola.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dill.agricola.common.Dir;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.PointUtils;
import com.dill.agricola.model.buildings.more.ExtraCapacityProvider;

public class Fencer {

	private Fencer() {
	}
	
	public static void calculateFences(Farm farm) {
		int w = farm.getWidth();
		int h = farm.getHeight();
		int[][] floodMap = new int[w][h];
		List<DirPoint> range = PointUtils.createGridRange(w, h);
		List<Dir> dirs = Arrays.asList(Dir.W, Dir.N, Dir.S, Dir.E);
		// init
		for (DirPoint pos : range) {
			floodMap[pos.x][pos.y] = farm.getSpace(pos).isAlwaysEnclosed() ? 0 : -1;
		}
		// calculate pastures and feeders
		int counter = 0;
		int pastures = 0;
		boolean changed;
		do {
			changed = false;
			counter++;
			for (DirPoint pos : range) {
				int current = floodMap[pos.x][pos.y];
				for (Dir d : dirs) {
					DirPoint dpos = new DirPoint(pos, d);
					if (!farm.isClosed(dpos)) {
						DirPoint nextPos = PointUtils.getNext(dpos);
						if (PointUtils.isInRange(nextPos, w, h)) {
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
		List<Building> buildings = new ArrayList<Building>();
		List<ExtraCapacityProvider> ecps = new ArrayList<ExtraCapacityProvider>();
		Map<Integer, List<Space>> spaceMap = new HashMap<Integer, List<Space>>();
		for (DirPoint pos : range) {
			int pastureNo = floodMap[pos.x][pos.y];
			if (pastureNo > 0) {
				Space space = farm.getSpace(pos);
				// collect spaces per each pasture
				if (!spaceMap.containsKey(pastureNo)) {
					spaceMap.put(pastureNo, new ArrayList<Space>());
				}
				spaceMap.get(pastureNo).add(space);
			}
			Building b = farm.getBuilding(pos);
			if (b!= null) {
				buildings.add(b);
				if (b instanceof ExtraCapacityProvider) {
					ecps.add((ExtraCapacityProvider)b);
				}
			}
		}
		
		// process results
		boolean farmValid = true;
		for (DirPoint pos : range) {
			int pastureNo = floodMap[pos.x][pos.y];
			Space space = farm.getSpace(pos);
			space.setPasture(spaceMap.get(pastureNo));
			space.clearExtraCapacity();
			for (ExtraCapacityProvider p : ecps) {
				space.addExtraCapacity(p.getExtraCapacity(pos, space));
			}
			// check
			farmValid = farmValid && space.isValid();
		}
		farm.setBuildingList(buildings);
		farm.setValidAnimals(farmValid);
	}

	/*private static void print(int[][] map) {
		List<DirPoint> range = DirPoint.createGridRange(map[0].length, map.length);
		for (DirPoint pos : range) {
			System.out.print(map[pos.y][pos.x]);
			if (pos.y == map.length - 1) {
				System.out.println();
			}
		}
		System.out.println("----------------");
	}*/

}
