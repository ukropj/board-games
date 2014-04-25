package com.dill.agricola.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.Main;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Dir;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.PointUtils;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Purchasable;

public class Farm extends SimpleObservable {

	private int width;
	private int height;

	private List<List<Space>> spaces;

	private final Map<Dir, Stack<Integer>> extensions = new EnumMap<Dir, Stack<Integer>>(Dir.class);
	private final Animals looseAnimals = new Animals();

	private Purchasable activeType = null;
	private final Set<DirPoint> activeSpots = new HashSet<DirPoint>();

	private boolean animalsValid = true;

	public Farm() {
	}

	public void init(int w, int h) {
		initSpaces(w, h);
		extensions.put(Dir.E, new Stack<Integer>());
		extensions.put(Dir.W, new Stack<Integer>());
		looseAnimals.clear();
		activeType = null;
		activeSpots.clear();
		animalsValid = true;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	private void initSpaces(int w, int h) {
		height = h;
		width = w;
		spaces = new ArrayList<List<Space>>(w);

		for (int i = 0; i < w; i++) {
			spaces.add(initCol(h));
		}
	}

	private List<Space> initCol(int h) {
		List<Space> col = new ArrayList<Space>(h);
		for (int j = 0; j < h; j++) {
			col.add(new Pasture());
		}
		return col;
	}

	public void setActiveType(Purchasable activeType) {
		if (this.activeType != activeType) {
			this.activeType = activeType;
			activeSpots.clear();
			setChanged();
		}
	}

	public Purchasable getActiveType() {
		return activeType;
	}

	public boolean isActiveSpot(DirPoint pos, Purchasable forType) {
		return activeType == forType && activeSpots.contains(pos);
	}

	private void addActiveSpot(DirPoint pos) {
		activeSpots.add(pos);
		if (pos.dir != null) {
			activeSpots.add(PointUtils.getNext(pos));
		}
	}

	private void removeActiveSpot(DirPoint pos) {
		activeSpots.remove(pos);
		if (pos.dir != null) {
			activeSpots.remove(PointUtils.getNext(pos));
		}
	}

	public boolean has(Purchasable type, DirPoint pos, boolean activeOnly) {
		if (activeOnly && !isActiveSpot(pos, type)) {
			return false;
		}
		Space space = getSpace(pos);

		switch (type) {
		case TROUGH:
			return space != null && space.hasTrough();
		case FENCE:
			DirPoint next = PointUtils.getNext(pos);
			Space second = getSpace(next);
			return space != null && space.hasBorder(pos.dir) ||
					second != null && second.hasBorder(next.dir);
		case EXTENSION:
			return space != null; // not null space is considered farm space i.e. extension
		case BUILDING:
			return hasBuilding(pos, null, false); // active already checked
		}
		return false;
	}

	public boolean put(Purchasable type, DirPoint pos) {
		Space space = getSpace(pos);

		switch (type) {
		case TROUGH:
			if (space != null && !has(type, pos, false)) {
				space.setTrough(true);
			} else {
				return false;
			}
			break;
		case FENCE:
			boolean any = false;
			if (!has(type, pos, false)) {
				if (space != null) {
					space.setBorder(pos.dir, true);
					any = true;
				}
				DirPoint next = PointUtils.getNext(pos);
				Space second = getSpace(next);
				if (second != null) {
					second.setBorder(next.dir, true);
					any = true;
				}
			}
			if (!any) {
				return false;
			}
			break;
		case EXTENSION:
			if (!has(type, pos, false)) {
				if (!extend(pos.x < 0 ? Dir.W : Dir.E)) {
					return false;
				}
				if (pos.x < 0) {
					pos = new DirPoint(pos);
					pos.translate(1, 0);
				}
			}
			break;
		case BUILDING:
//			return hasBuilding(pos, null, false); // active already checked
		default:
			return false;
		}
		addActiveSpot(pos);
		setChanged();
		return true;
	}

	public boolean take(Purchasable type, DirPoint pos) {
		if (pos == null) {
			if (activeType == type) {
				return takeLastActive();
			}
			return false;
		}
		Space space = getSpace(pos);

		switch (type) {
		case TROUGH:
			if (has(type, pos, false)) {
				space.setTrough(false);
			} else {
				return false;
			}
			break;
		case FENCE:
			if (has(type, pos, false)) {
				if (space != null) {
					space.setBorder(pos.dir, false);
				}
				DirPoint next = PointUtils.getNext(pos);
				Space second = getSpace(next);
				if (second != null) {
					second.setBorder(next.dir, false);
				}
			} else {
				return false;
			}
			break;
		case EXTENSION:
			if (has(type, pos, false)) {
				if (!unextend(pos.x == 0 ? Dir.W : Dir.E)) {
					return false;
				}
			}
			break;
		case BUILDING:
//			return hasBuilding(pos, null, false); // active already checked
		default:
			return false;
		}

		removeActiveSpot(pos);
		setChanged();
		return true;
	}

	public List<Integer> getExtensions(Dir d) {
		return extensions.get(d);
	}

	public int getUsedExtensions() {
		int used = 0;
		List<List<Space>> extSpaces = new ArrayList<List<Space>>();
		for (int i = 0; i < extensions.get(Dir.W).size(); i++) {
			extSpaces.add(spaces.get(i));
		}
		for (int i = 0; i < extensions.get(Dir.E).size(); i++) {
			extSpaces.add(spaces.get(spaces.size() - 1 - i));
		}
		for (List<Space> ext : extSpaces) {
			boolean allUsed = true;
			for (Space space : ext) {
				allUsed = allUsed && space.isUsed();
			}
			if (allUsed) {
				used++;
			}
		}
		return used;
	}

//	public boolean moveExtension(Dir target) {
//		Dir source = target.opposite();
//		boolean canUnextend = unextend(source, true);
//		if (canUnextend) {
//			extend(target);
//			return true;
//		} else {
//			return false;
//		}
//	}

	private boolean extend(Dir d) {
		int targetCol;
		if (d == Dir.E) {
			// add to right
			targetCol = width;
		} else if (d == Dir.W) {
			// add to left
			targetCol = 0;
		} else {
			throw new IllegalArgumentException("Cannot extend farm in direction " + d);
		}

		spaces.add(targetCol, initCol(height));
		extensions.get(d).push(GeneralSupply.getNextExtensionId());
		width++;
		return true;
	}

	private boolean unextend(Dir d) {
		if (extensions.get(d).isEmpty()) {
			// no extensions in this direction - cannot unextend
			return false;
		}
		int targetCol;
		if (d == Dir.E) {
			// remove from right
			targetCol = width - 1;
		} else if (d == Dir.W) {
			// remove form left
			targetCol = 0;
		} else {
			throw new IllegalArgumentException("Cannot unextend farm in direction" + d);
		}

		// retrieve fences & animals from removed extension
		for (DirPoint pos : PointUtils.createGridRange(targetCol, targetCol + 1, 0, height)) {
			takeAnimals(pos);
			for (Dir dir : Dir.values()) {
				if (dir != d.opposite()) { // keep fences on border
					take(Purchasable.FENCE, new DirPoint(pos, dir));
				}
			}
		}
		spaces.remove(targetCol);
		extensions.get(d).pop();
		width--;
		return true;
	}

	public boolean isClosed(DirPoint pos) {
		Space first = getSpace(pos);
		Space second = getSpace(PointUtils.getNext(pos));
		return first != null && (first.isAlwaysEnclosed() || first.hasBorder(pos.dir)) //
				|| second != null && (second.isAlwaysEnclosed() || second.hasBorder(pos.dir.opposite()));
	}

	public List<Building> getFarmBuildings() {
		// TODO let fencer precompute this
		List<Building> buildings = new ArrayList<Building>();
		List<DirPoint> range = PointUtils.createGridRange(width, height);
		for (DirPoint pos : range) {
			Building b = getBuilding(pos);
			if (b != null) {
				buildings.add(b);
			}
		}
		return buildings;
	}
	
	public boolean hasBuilding(BuildingType type) {
		for (Building b : getFarmBuildings()) {
			if (b.getType() == type) {
				return true;
			}
		}
		return false;
	}

	public boolean hasBuilding(DirPoint pos, BuildingType type, boolean activeOnly) {
		if (activeOnly && !isActiveSpot(pos, Purchasable.BUILDING)) {
			return false;
		}
		Space space = getSpace(pos);
		return space != null && space instanceof Building && (type == null || ((Building) space).getType() == type);
	}

	public boolean canBuild(BuildingType type, DirPoint pos) {
		if (pos == null) {
			return canBuildAnywhere(type);
		}
		Space space = getSpace(pos);
		return space != null && type.canBuildAt(space.getType(), pos);
	}

	private boolean canBuildAnywhere(BuildingType type) {
		List<DirPoint> range = PointUtils.createGridRange(width, height);
		for (DirPoint pos : range) {
			Space space = getSpace(pos);
			if (space != null && type.canBuildAt(space.getType(), pos)) {
				return true;
			}
		}
		return false;
	}

	public boolean build(Building b, DirPoint pos) {
		Main.asrtNotNull(b, "Cannot build null building");
		Space space = getSpace(pos);
		if (space != null && b.getType().canBuildAt(space.getType(), pos)) {
			b.buildAt(space);
			putSpace(pos, b);
			addActiveSpot(pos);
			setChanged();
			return true;
		}
		return false;
	}

	private Building takenBuilding = null;

	public Building unbuild(DirPoint pos) {
		if (pos == null) {
			if (activeType == Purchasable.BUILDING) {
				if (takeLastActive()) {
					return takenBuilding;
				}
			}
			return null;
		}
		Building building = getBuilding(pos);
		if (building != null) {
			Space original = building.unbuild();
			putSpace(pos, original);
			removeActiveSpot(pos);
			setChanged();
			return building;
		}
		return null;
	}

	public Building getBuilding(DirPoint pos) {
		Space space = getSpace(pos);
		return space != null && space instanceof Building ? (Building) space : null;
	}

	private void putSpace(DirPoint pos, Space space) {
		if (PointUtils.isInRange(pos, width, height)) {
			spaces.get(pos.x).set(pos.y, space);
		}
	}

	public Space getSpace(DirPoint pos) {
		Main.asrtNotNull(pos, "Cannot handle null position");
		if (PointUtils.isInRange(pos, width, height)) {
			return spaces.get(pos.x).get(pos.y);
		} else {
			return null;
		}
	}

	public int getAnimals(DirPoint pos) {
		Space space = getSpace(pos);
		return space != null ? space.getAnimals() : 0;
	}

	public void addAnimals(Animal type, int count) {
		Main.asrtPositive(count, "Cannot add negative amount of animals");
		addLooseAnimals(type, count);
	}

	public boolean removeAnimals(Animal type, int count) {
		int unused = getLooseAnimals(type);
		if (unused >= count) {
			addLooseAnimals(type, -count);
			return true;
		}
		addLooseAnimals(type, -unused);
		if (takeRandomAnimals(type, count - unused)) {
			addLooseAnimals(type, -(count - unused));
			return true;
		}
		return false;
	}

	public int getLooseAnimals(Animal type) {
		return looseAnimals.get(type);
	}

	public Animals getLooseAnimals() {
		return looseAnimals;
	}

	private int addLooseAnimals(Animal type, int count) {
		if (count == 0) {
			return count;
		}
		int present = looseAnimals.get(type);
		if (count < 0) {
			count = Math.max(count, -present);
		}
		looseAnimals.add(type, count);
		setChanged();
		return count;
	}

	public int putAnimals(DirPoint to, Animal type, int count) {
		Space target = getSpace(to);
		if (target != null) {
			return putAnimals(target, type, count, false);
		}
		return 0;
	}

	private int putAnimals(Space target, Animal type, int count, boolean ignoreRestOfPasture) {
		Main.asrtPositive(count, "Cannot move negative amount of animals");
		count = Math.min(looseAnimals.get(type), count);
		int moving = Math.min(count, target.getActualCapacity(type));
		if (moving > 0) {
			addLooseAnimals(type, -moving);
			target.setAnimalType(type);
			target.addAnimals(moving);
			setChanged();
		}
		if (!ignoreRestOfPasture && count > moving) {
			moving += putToPasture(target, type, count - moving);
		}
		return moving;
	}

	private int putToPasture(Space origSpace, Animal type, int count) {
		int moved = 0;
		int left = count;
		for (Space space : origSpace.getPastureSpaces()) {
			if (space == origSpace) {
				continue;
			}
			moved = putAnimals(space, type, left, true);
			left -= moved;
			if (left == 0) {
				setChanged();
				break;
			}
		}
		return moved;
	}

	public int takeAnimals(DirPoint from) {
		// take all animals of one type
		return takeAnimals(from, Integer.MAX_VALUE);
	}

	public int takeAnimals(DirPoint from, int count) {
		Space source = getSpace(from);
		if (source != null) {
			return takeAnimals(source, count, false);
		}
		return 0;
	}

	private int takeAnimals(Space source, int count, boolean ignoreRestOfPasture) {
		Main.asrtPositive(count, "Cannot move negative amount of animals");
		int moving = Math.min(source.getAnimals(), count);
		if (moving > 0) {
			Animal type = source.getAnimalType();
			source.addAnimals(-moving);
			addLooseAnimals(type, moving);
			setChanged();
		}
		if (!ignoreRestOfPasture && source.getAnimalTypesPerPasture().size() <= 1 && count > moving) {
			// take form pasture only when it not mixed
			moving += takeFromPasture(source, count - moving);
		}
		return moving;
	}

	private int takeFromPasture(Space origSpace, int count) {
		int moved = 0;
		int left = count;
		for (Space space : origSpace.getPastureSpaces()) {
			if (space == origSpace) {
				continue;
			}
			moved = takeAnimals(space, left, true);
			left -= moved;
			if (left == 0) {
				setChanged();
				break;
			}
		}
		return moved;
	}

	private boolean takeRandomAnimals(Animal type, int count) {
		int moved = 0;
		List<DirPoint> range = PointUtils.createGridRange(width, height);
		for (DirPoint pos : range) {
			Space space = getSpace(pos);
			if (space.getAnimalType() == type) {
				moved += takeAnimals(space, count - moved, false);
				if (moved == count) {
					return true;
				}
			}
		}
		return false;
	}

	public List<Animal> guessAnimalTypesToPut(DirPoint pos, boolean onlyWhenUnused) {
		List<Animal> types = new ArrayList<Animal>();
		Space space = getSpace(pos);
		if (space != null) {
			Animal requiredType = space.getRequiredAnimal();
			boolean unusedOk = !onlyWhenUnused || (requiredType == null ? looseAnimals.size() > 0 : looseAnimals.get(requiredType) > 0);
			
			if (space.getMaxCapacity() > 0 && unusedOk) {
				if (requiredType != null) {
					// if there is a required type use it
					types.add(requiredType);
				} else if (space.getAnimals() > 0) {
					// if animals are present on space use their type
					types.add(space.getAnimalType());
				} else {
					// if animals are present on pasture use their types
					Set<Animal> pastureTypes = space.getAnimalTypesPerPasture();
					if (!pastureTypes.isEmpty()) {
						types.addAll(pastureTypes);
					} else {
						// if no animals anywhere use all types 
						types.addAll(Arrays.asList(Animal.values()));
					}
				}
				if (onlyWhenUnused) {
					// use only types of present unused animals
					for (Animal type : Animal.values()) {
						if (getLooseAnimals(type) == 0) {
							types.remove(type);
						}
					}
				}
			}
		}
		return types;
	}

	public void setValidAnimals(boolean valid) {
		this.animalsValid = valid;
	}

	public boolean hasValidAnimals() {
		return animalsValid;
	}

	private boolean takeLastActive() {
		if (activeType == null) {
			return false;
		}
		if (!activeSpots.isEmpty()) {
			DirPoint pos = new ArrayList<DirPoint>(activeSpots).get(0);
			switch (activeType) {
			case FENCE:
			case EXTENSION:
			case TROUGH:
				return take(activeType, pos);
			case BUILDING:
				takenBuilding = unbuild(pos);
				return takenBuilding != null;
			default:
				return false;
			}
		}
		return false;
	}

}
