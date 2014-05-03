package com.dill.agricola.actions.farm;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;

import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.buildings.Stables;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.undo.UndoableFarmEdit;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.UiFactory;

public class BuildStables extends BuildAction {

	private final static Materials[] COSTS = new Materials[] { Stables.COST_WOOD, Stables.COST_STONE };
	private int costNo;

	public BuildStables() {
		super(ActionType.STABLES, BuildingType.STABLES);
	}

	public static Materials getBuildingCost(Player player, int variant) {
		Materials cost = COSTS[variant];
		if (cost.get(Material.WOOD) > 0 && player.getFarm().hasBuilding(BuildingType.SAWMILL)) {
			cost = new Materials(cost);
			cost.substract(Material.WOOD, 1);
		}
		return cost;
	}

	protected Materials getCost(Player player, int doneSoFar) {
		return getBuildingCost(player, costNo);
	}

	protected Building getBuildingInstance(BuildingType type) {
		return new Stables();
	}

	public boolean canDo(Player player) {
		return player.canPurchase(toBuild, getBuildingCost(player, 0), null) || player.canPurchase(toBuild, getBuildingCost(player, 1), null);
	}

	public boolean canDoOnFarm(Player player, DirPoint pos, int doneSoFar) {
		return player.canPurchase(toBuild, getBuildingCost(player, 0), pos) || player.canPurchase(toBuild, getBuildingCost(player, 1), pos);
	}

	private int chooseStablesCost(Player player) {
		List<JComponent> opts = new ArrayList<JComponent>();
		for (int i = 0; i < COSTS.length; i++) {
			Materials cost = getBuildingCost(player, i);
			JComponent opt = UiFactory.createResourcesPanel(cost, null, UiFactory.X_AXIS);
			opt.setEnabled(player.canPay(cost));
			opts.add(opt);
		}
		Icon icon = AgriImages.getBuildingIcon(BuildingType.OPEN_STABLES, ImgSize.MEDIUM);
		return UiFactory.showOptionDialog(null, "Choose cost", "Stables", icon, opts, 0);
	}

	public UndoableFarmEdit doOnFarm(Player player, DirPoint pos, int doneSoFar) {
		costNo = UiFactory.NO_OPTION;
		if (!player.canPay(getBuildingCost(player, 0))) {
			costNo = 1;
		} else if (!player.canPay(getBuildingCost(player, 1))) {
			costNo = 0;
		}
		if (costNo == UiFactory.NO_OPTION) {
			// ask only if player can pay any
			costNo = chooseStablesCost(player);
		}
		if (costNo == UiFactory.NO_OPTION) {
			return null;
		}
		return super.doOnFarm(player, pos, doneSoFar);
	}

}
