package com.dill.agricola.actions;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.buildings.BuildingType;
import com.dill.agricola.model.buildings.Stables;
import com.dill.agricola.model.buildings.Stall;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.view.utils.Images;
import com.dill.agricola.view.utils.SwingUtils;

public class BuildStables extends AbstractAction {

	private final static int NONE = JOptionPane.CLOSED_OPTION;
	private final Materials[] COSTS = new Materials[] { Stables.COST_WOOD, Stables.COST_STONE };

	public BuildStables() {
		super(ActionType.STABLES);
	}

	private int getStalls(Player player) {
		int count = 0;
		for (Building b : player.getFarm().getBuiltBuildings()) {
			if (b instanceof Stall) {
				count++;
			}
		}
		return count;
	}

	public boolean canPerform(Player player) {
		return super.canPerform(player) && getStalls(player) > 0 && (player.canPay(COSTS[0]) || player.canPay(COSTS[1]));
	}

	public boolean canPerformMore(Player player, int doneSoFar) {
		return getStalls(player) > doneSoFar && (player.canPay(COSTS[0]) || player.canPay(COSTS[1]));
	}

	private int chooseStablesCost() {
		List<JComponent> opts = new ArrayList<JComponent>();
		for (Materials cost : COSTS) {
			opts.add(SwingUtils.createResourcesPanel(cost, null, SwingUtils.X_AXIS));
		}
		Icon icon = Images.getBuildingIcon(BuildingType.OPEN_STABLES, 40);
		return SwingUtils.showOptionDialog("Choose cost", "Stables", icon, opts);
	}

	public boolean doOnce(Player player) {
		int costNo = chooseStablesCost();
		if (costNo == NONE) {
			return false;
		}
		boolean done = player.purchaseBuilding(new Stables(), COSTS[costNo]);
		if (done) {
			player.setActiveType(Purchasable.BUILDING);
		}
		return done;
	}

	public boolean undoOnce(Player player) {
		return player.unpurchaseBuilding();
	}

	public boolean doo(Player player) {
		return doOnce(player);
	}

	public boolean undo(Player player) {
		return undoOnce(player);
	}

	public String toString() {
		return super.toString() + "<br>1 for " + Stables.COST_WOOD + " or " + Stables.COST_STONE;
	}

}
