package com.dill.agricola.actions;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.buildings.Stables;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.UiFactory;

public class BuildStables extends AbstractAction {

	private final static int NONE = JOptionPane.CLOSED_OPTION;
	private final Materials[] COSTS = new Materials[] { Stables.COST_WOOD, Stables.COST_STONE };

	public BuildStables() {
		super(ActionType.STABLES);
	}

	public boolean canPerform(Player player) {
		return super.canPerform(player) && player.getBuildingCount(BuildingType.STALL) > 0 && (player.canPay(COSTS[0]) || player.canPay(COSTS[1]));
	}

	public boolean canPerformMore(Player player, int doneSoFar) {
		return player.getBuildingCount(BuildingType.STALL) > doneSoFar && (player.canPay(COSTS[0]) || player.canPay(COSTS[1]));
	}

	private int chooseStablesCost(Player player) {
		List<JComponent> opts = new ArrayList<JComponent>();
		for (Materials cost : COSTS) {
			JComponent opt = UiFactory.createResourcesPanel(cost, null, UiFactory.X_AXIS);
			opt.setEnabled(player.canPay(cost));
			opts.add(opt);
		}
		Icon icon = AgriImages.getBuildingIcon(BuildingType.OPEN_STABLES, ImgSize.MEDIUM);
		return UiFactory.showOptionDialog("Choose cost", "Stables", icon, opts);
	}

	public boolean doOnce(Player player) {
		int costNo = chooseStablesCost(player);
		if (costNo == NONE) {
			return false; // TODO this will not cancel action becousa still "canDoMore"
		}
		boolean done = player.purchaseBuilding(new Stables(), COSTS[costNo]);
		if (done) {
			player.setActiveType(Purchasable.BUILDING);
		}
		return done;
	}

	public boolean undoOnce(Player player) {
		return player.unpurchaseBuilding() != null;
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
