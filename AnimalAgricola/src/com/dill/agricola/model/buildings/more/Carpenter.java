package com.dill.agricola.model.buildings.more;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.actions.FeatureAction;
import com.dill.agricola.actions.extra.MoveStallsAndTroughs;
import com.dill.agricola.actions.extra.MoveTroughs;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class Carpenter extends Building {

	public final static Materials COST = new Materials(Material.WOOD, Material.STONE);
	public final static FeatureAction MOVE_TROUGHS_AND_STALLS = (FeatureAction) AbstractAction.withSubaction(
			new MoveStallsAndTroughs(), new MoveTroughs(), false);

	public Carpenter() {
		super(BuildingType.CARPENTER, 1, 0);
		setActive(true);
	}

	// allows moving stalls and feeding troughs
	public FeatureAction getFeatureAction() {
		return MOVE_TROUGHS_AND_STALLS;
	}
}
