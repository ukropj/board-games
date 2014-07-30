package com.dill.agricola.model.buildings.evenmore;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.common.PointUtils;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Space;
import com.dill.agricola.model.buildings.more.ExtraCapacityProvider;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class DairyFarm extends Building implements ExtraCapacityProvider {

	public final static Materials COST = new Materials(
			Material.STONE, Material.STONE, Material.STONE, Material.REED);
	public final static Animals EXTRA_CAP = new Animals(Animal.COW, 2);

	public DairyFarm() {
		super(BuildingType.DAIRY_FARM, 3, 2, Animal.COW);
	}

	public Animals getExtraCapacity(DirPoint pos, Space space) {
		return !space.isUsed() && PointUtils.isNextTo(buildPos, pos) ? EXTRA_CAP : null;
	}

}
