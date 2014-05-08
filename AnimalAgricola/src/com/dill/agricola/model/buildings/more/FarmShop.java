package com.dill.agricola.model.buildings.more;

import com.dill.agricola.actions.CompoundAction;
import com.dill.agricola.actions.simple.AnimalAction;
import com.dill.agricola.actions.simple.MaterialAction;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class FarmShop extends Building {

	public final static Materials COST = new Materials(Material.WOOD);
	public final static Materials[] REWARD1 = new Materials[] {
			new Materials(Material.WOOD),
			new Materials(Material.STONE),
			new Materials(Material.REED) };
	public final static Animals[] REWARD2 = new Animals[] {
			new Animals(Animal.SHEEP),
			new Animals(Animal.PIG),
			new Animals(Animal.COW),
			new Animals(Animal.HORSE) };

	public FarmShop() {
		super(BuildingType.FARM_SHOP, 0, 1, Animal.SHEEP, new CompoundAction(
				new MaterialAction(ActionType.BUILDING_REWARD, REWARD1), 
				new AnimalAction(ActionType.BUILDING_REWARD, REWARD2)));
	}

}
