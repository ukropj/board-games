package com.dill.agricola.model.buildings.more;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.common.PointUtils;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Space;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class Stud extends Building implements ExtraCapacityProvider {

	public final static Materials COST = new Materials(Material.WOOD,
			Material.STONE, Material.STONE, Material.STONE, Material.REED);
	public final static Animals EXTRA_CAP = new Animals(Animal.HORSE, 2);

	public Stud() {
		super(BuildingType.STUD, 3, 2, Animal.HORSE);
		setActive(true);
	}

	public Animals getExtraCapacity(DirPoint pos, Space space) {
		return !space.isUsed() && PointUtils.isNextTo(buildPos, pos) ? EXTRA_CAP : null;
	}

}
