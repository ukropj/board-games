package com.dill.agricola.model.buildings;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.actions.simple.AnimalAction;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Space;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;


public class OpenStables extends Building {

	public static final Materials COST_WOOD = new Materials(Material.WOOD, 3);
	public static final Materials COST_STONE = new Materials(Material.STONE, 3);
	public final static Animals[] REWARD = new Animals[] {
		new Animals(Animal.COW),
		new Animals(Animal.HORSE) };
	
	public OpenStables() {
		super(BuildingType.OPEN_STABLES, 2, 5, new AnimalAction(ActionType.BUILDING_REWARD, REWARD));
	}
	
	public void buildAt(Space space, DirPoint pos) {
		super.buildAt(space, pos);
		
		Stall stall = (Stall)removeIntermediary();
		if (stall != null) {
			// original stall "under" Open Stables will be returned to supply
			GeneralSupply.unuseStall(stall);			
		}
	}
	
	public Space unbuild() {
		// original stall "under" Open Stables will be taken from supply
		insertIntermediary(GeneralSupply.useStall());
		
		return super.unbuild();
	}

}
