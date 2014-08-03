package com.dill.agricola.actions.farm;

import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.extra.Expand;
import com.dill.agricola.actions.extra.FreeBorders;
import com.dill.agricola.actions.simple.MaterialRefillAction;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.undo.UndoableFarmEdit;

public class BordersExpand extends MaterialRefillAction {

	public final static Materials REFILL = new Materials(Material.BORDER, 1);
	private final Action expandSubaction = new Expand();

	private int takenBorders = 0;
	
	public BordersExpand() {
		super(ActionType.BORDERS_EXPAND, REFILL);
	}
	
	@Override
	public UndoableFarmEdit doo(Player player) {
		takenBorders = materials.size();
		return super.doo(player);
	}
	
	public Action getSubAction(Player player, boolean afterFarmAction) {
		if (!afterFarmAction) {
			if (player.farm.hasBuilding(BuildingType.WOODWORKING_SHOP) && takenBorders > 0) {
				expandSubaction.setSubAction(new FreeBorders(takenBorders), true);
			} else {
				expandSubaction.setSubAction(null, true);
			}
			return expandSubaction;
		}
		return null;
	}

}
