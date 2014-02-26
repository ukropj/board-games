package com.dill.agricola.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.dill.agricola.Main;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Dir;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Point;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.Purchasable;

public class Farm extends SimpleObservable {

	private int width;
	private int height;

	private List<List<Space>> spaces;

	private final Map<Dir, Integer> extensions = new EnumMap<Dir, Integer>(Dir.class);
	private final Animals looseAnimals = new Animals();
	//	private final Map<Animal, Integer> animals = new EnumMap<Animal, Integer>(Animal.class);

	private final Map<Purchasable, Integer> unusedStuff = new EnumMap<Purchasable, Integer>(Purchasable.class);
	private final Stack<Building> unusedBuildings = new Stack<Building>();

	private Purchasable activeType = null;
	private final Set<Point> activeSpots = new HashSet<Point>();
	private final Set<DirPoint> activeFenceSpots = new HashSet<DirPoint>();

	private boolean animalsValid = true;

	public Farm() {
	}

	public void init(int w, int h) {
		initSpaces(w, h);
		extensions.put(Dir.E, 0);
		extensions.put(Dir.W, 0);
		for (Purchasable p : Purchasable.values()) {
			unusedStuff.put(p, 0);
		}
		unusedBuildings.clear();
		looseAnimals.clear();
		activeType = null;
		activeSpots.clear();
		activeFenceSpots.clear();
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
			activeFenceSpots.clear();
			setChanged();
		}
	}

	public Purchasable getActiveType() {
		return activeType;
	}

	public boolean isActiveSpot(Point pos, Purchasable forType) {
		return activeType == forType && activeSpots.contains(pos);
	}

	public boolean isActiveSpotForFence(Point pos, Dir dir) {
		return activeType == Purchasable.FENCE && activeFenceSpots.contains(new DirPoint(pos, dir));
		// TODO optimize new
	}

	private boolean addActiveSpot(Point pos) {
		return activeSpots.add(pos);
	}

	private boolean addActiveSpot(Point pos, Dir d) {
		return activeFenceSpots.add(new DirPoint(pos, d));
	}

	private boolean removeActiveSpot(Point pos) {
		return activeSpots.remove(pos);
	}

	private boolean removeActiveSpot(Point pos, Dir d) {
		return activeFenceSpots.remove(new DirPoint(pos, d));
	}

	public int getExtensions(Dir d) {
		return extensions.get(d);
	}

	public int getUsedExtensions() {
		int used = 0;
		List<List<Space>> extSpaces = new ArrayList<List<Space>>();
		for (int i = 0; i < extensions.get(Dir.W); i++) {
			extSpaces.add(spaces.get(i));
		}
		for (int i = 0; i < extensions.get(Dir.E); i++) {
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

	public boolean moveExtension(Dir target) {
		Dir source = target.opposite();
		boolean canUnextend = unextend(source, true);
		if (canUnextend) {
			extend(target);
			return true;
		} else {
			return false;
		}
	}

	public boolean extend(Dir d) {
		if (getUnused(Purchasable.EXTENSION) < 1) {
			// no extensions available - cannot unextend
			return false;
		}
		int targetCol;
		if (d == Dir.E) {
			// add to right
			targetCol = width;
		} else if (d == Dir.W) {
			// add to left
			targetCol = 0;
		} else {
			throw new IllegalArgumentException("Cannot extend farm in direction" + d);
		}

		spaces.add(targetCol, initCol(height));
		extensions.put(d, extensions.get(d) + 1);
		addUnused(Purchasable.EXTENSION, -1);
		addActiveSpot(new Point(targetCol, 0));
		width++;
		// some half fences may need adding
		/*
		 * for (Point pos : Point.createGridRange(targetCol, targetCol + 1, 0, height)) { if (hasFence(pos.move(d.opposite()), d)) { putFence(pos,
		 * d.opposite()); // put again to add new half } }
		 */
		setChanged();
		return true;
	}

	private boolean unextend(Dir d, boolean activeOnly) {
		int present = extensions.get(d);
		if (present < 1) {
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
		if (activeOnly && !isActiveSpot(new Point(targetCol, 0), Purchasable.EXTENSION)) {
			return false;
		}

		// retrieve fences & animals from removed extension
		for (Point pos : Point.createGridRange(targetCol, targetCol + 1, 0, height)) {
			takeAnimals(pos);
			for (Dir dir : Dir.values()) {
				if (dir != d.opposite()) { // keep fences on border
					takeFence(pos, dir, false);
				}
			}
		}
		spaces.remove(targetCol);
		extensions.put(d, present - 1);
		addUnused(Purchasable.EXTENSION, 1);
		removeActiveSpot(new Point(targetCol, 0));
		width--;

		setChanged();
		return true;
	}

	public boolean hasFence(Point pos, Dir d, boolean activeOnly) {
		if (activeOnly && !isActiveSpotForFence(pos, d)) {
			return false;
		}
		Space first = getSpace(pos);
		Space second = getSpace(pos.move(d));
		return first != null && first.hasBorder(d) || second != null && second.hasBorder(d.opposite());
	}

	public boolean isClosed(Point pos, Dir d) {
		Space first = getSpace(pos);
		Space second = getSpace(pos.move(d));
		return first != null && (first.isAlwaysEnclosed() || first.hasBorder(d)) //
				|| second != null && (second.isAlwaysEnclosed() || second.hasBorder(d.opposite()));
	}

	public boolean putFence(Point pos, Dir d) {
		if (getUnused(Purchasable.FENCE) < 1) {
			return false;
		}
		boolean done = false;
		Space first = getSpace(pos);
		if (first != null) {
			done = first.setBorder(d, true);
		}
		Space second = getSpace(pos.move(d));
		if (second != null) {
			done = second.setBorder(d.opposite(), true) || done;
		}
		if (done) {
			addUnused(Purchasable.FENCE, -1);
			addActiveSpot(pos, d);
			setChanged();
		}
		return done;
	}

	public boolean takeFence(Point pos, Dir d, boolean activeOnly) {
		if (activeOnly && !isActiveSpotForFence(pos, d)) {
			return false;
		}
		boolean done = false;
		Space first = getSpace(pos);
		if (first != null) {
			done = first.setBorder(d, false);
		}
		Space second = getSpace(pos.move(d));
		if (second != null) {
			done = second.setBorder(d.opposite(), false) || done;
		}
		if (done) {
			addUnused(Purchasable.FENCE, 1);
			removeActiveSpot(pos, d);
			setChanged();
		}
		return done;
	}

	public void toggleFence(Point pos, Dir d) {
		if (hasFence(pos, d, true)) {
			takeFence(pos, d, false); // active already checked
		} else {
			putFence(pos, d);
		}
	}

	public boolean hasTrough(Point pos, boolean activeOnly) {
		if (activeOnly && !isActiveSpot(pos, Purchasable.TROUGH)) {
			return false;
		}
		Space space = getSpace(pos);
		return space != null && space.hasTrough();
	}

	public boolean putTrough(Point pos) {
		if (getUnused(Purchasable.TROUGH) < 1) {
			return false;
		}
		Space space = getSpace(pos);
		if (space != null && !space.hasTrough()) {
			space.setTrough(true);
			addUnused(Purchasable.TROUGH, -1);
			addActiveSpot(pos);
			setChanged();
			return true;
		}
		return false;
	}

	public boolean takeTrough(Point pos, boolean activeOnly) {
		if (activeOnly && !isActiveSpot(pos, Purchasable.TROUGH)) {
			return false;
		}
		Space space = getSpace(pos);
		if (space != null && space.hasTrough()) {
			space.setTrough(false);
			addUnused(Purchasable.TROUGH, 1);
			removeActiveSpot(pos);
			setChanged();
			return true;
		}
		return false;
	}

	public void toggleTrough(Point pos) {
		if (hasTrough(pos, true)) {
			takeTrough(pos, false); // active already checked
		} else {
			putTrough(pos);
		}
	}

	public void addBuilding(Building b) {
		unusedBuildings.push(b);
		setChanged();
	}

	public Building removeBuilding() {
		if (!unusedBuildings.isEmpty()) {
			Building b = unusedBuildings.pop();
			setChanged();
			return b;
		}
		int active = activeType == Purchasable.BUILDING ? activeSpots.size() : 0;
		if (active > 0) {
			if (takeLastActive()) {
				return unusedBuildings.pop();
			}
		}
		return null;
	}

	public List<Building> getUnusedBuildings() {
		return unusedBuildings;
	}

	public List<Building> getBuiltBuildings() {
		// TODO keep list
		List<Building> buildings = new ArrayList<Building>();
		List<Point> range = Point.createGridRange(width, height);
		for (Point pos : range) {
			Building b = getBuilding(pos);
			if (b != null) {
				buildings.add(b);
			}
		}
		return buildings;
	}

	public boolean hasBuilding(Point pos, boolean activeOnly) {
		if (activeOnly && !isActiveSpot(pos, Purchasable.BUILDING)) {
			return false;
		}
		Space space = getSpace(pos);
		return space != null && space instanceof Building;
	}

	public boolean build(Point pos) {
		// builds purchased building
		if (unusedBuildings.isEmpty()) {
			return false;
		}
		Space space = getSpace(pos);
		if (space != null && unusedBuildings.peek().canBuildAt(space)) {
			Building building = unusedBuildings.pop();
			building.buildAt(space);
			putSpace(pos, building);
			addActiveSpot(pos);
			setChanged();
			return true;
		}
		return false;
	}

	public boolean unbuild(Point pos, boolean activeOnly) {
		if (activeOnly && !isActiveSpot(pos, Purchasable.BUILDING)) {
			return false;
		}
		Building building = getBuilding(pos);
		if (building != null) {
			Space original = building.unbuild();
			putSpace(pos, original);
			unusedBuildings.push(building);
			removeActiveSpot(pos);
			setChanged();
			return true;
		}
		return false;
	}

	public void toggleBuilding(Point pos) {
		if (hasBuilding(pos, true)) {
			unbuild(pos, false); // active already checked
		} else {
			build(pos);
		}
	}

	public Building getBuilding(Point pos) {
		Space space = getSpace(pos);
		return space != null && space instanceof Building ? (Building) space : null;
	}

	private void putSpace(Point pos, Space space) {
		if (Point.isInRange(pos, width, height)) {
			spaces.get(pos.x).set(pos.y, space);
		}
	}

	public Space getSpace(Point pos) {
		Main.asrtNotNull(pos, "Cannot handle null position");
		if (Point.isInRange(pos, width, height)) {
			return spaces.get(pos.x).get(pos.y);
		} else {
			return null;
		}
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

	/*
	 * public int moveAnimals(Point from, Point to, int count) { return putAnimals(to, type, takeAnimals(from, count)); }
	 */

	public int putAnimals(Point to, Animal type, int count) {
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

	public int takeAnimals(Point from) {
		// take all animals of one type
		return takeAnimals(from, Integer.MAX_VALUE);
	}

	public int takeAnimals(Point from, int count) {
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
		List<Point> range = Point.createGridRange(width, height);
		for (Point pos : range) {
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

	public List<Animal> guessAnimalTypesToPut(Point pos) {
		List<Animal> types = new ArrayList<Animal>();
		Space space = getSpace(pos);
		if (space != null) {
			if (space.getAnimals() > 0) {
				// if animals are present on space use their type
				types.add(space.getAnimalType());
			} else {
				// if animals are present on pasture use their types
				Set<Animal> pastureTypes = space.getAnimalTypesPerPasture();
				if (!pastureTypes.isEmpty()) {
					types.addAll(pastureTypes);
				} else {
					// if farm has unused animals use their types
					for (Animal type : Animal.values()) {
						if (getLooseAnimals(type) > 0) {
							types.add(type);
						}
					}
				}
			}
		}
		return types;
	}

	public int addUnused(Purchasable type, int count) {
		int present = unusedStuff.get(type);
		if (count < 0) {
			count = Math.max(count, -present);
		}
		unusedStuff.put(type, present + count);
		setChanged();
		return count;
	}

	public int getUnused(Purchasable type) {
		if (type == Purchasable.BUILDING) {
			return unusedBuildings.size();
		}
		return unusedStuff.get(type);
	}

	public int getAllUnusedCount() {
		int count = 0;
		for (int c : unusedStuff.values()) {
			count += c;
		}
		return count;
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
		if (activeType != Purchasable.FENCE) {
			if (!activeSpots.isEmpty()) {
				Point pos = new ArrayList<Point>(activeSpots).get(0);
				switch (activeType) {
				case EXTENSION:
					return unextend(pos.x == 0 ? Dir.W : Dir.E, false);
				case TROUGH:
					return takeTrough(pos, false);
				case BUILDING:
					return unbuild(pos, false);
				default:
					return false;
				}
			}
		} else {
			if (!activeFenceSpots.isEmpty()) {
				DirPoint dpos = new ArrayList<DirPoint>(activeFenceSpots).get(0);
				return takeFence(dpos.point, dpos.dir, false);
			}
		}
		return false;
	}

	public boolean remove(Purchasable type) {
		int unused = getUnused(type);
		if (unused > 0) {
			addUnused(type, -1);
			return true;
		}
		int active = activeType == type //
		? type == Purchasable.FENCE ? activeFenceSpots.size() : activeSpots.size()
				: 0;
		if (active > 0) {
			if (takeLastActive()) {
				addUnused(type, -1);
				return true;
			}
		}
		return false;
	}

}
