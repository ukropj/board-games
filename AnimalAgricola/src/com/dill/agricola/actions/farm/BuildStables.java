package com.dill.agricola.actions.farm;

import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.DirPoint;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
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

	public boolean isFarmAction() {
		return true;
	}

	public boolean canPerform(Player player, int doneSoFar) {
		return !isUsed() && player.getBuildingCount(BuildingType.STALL) > 0 && (player.canPay(COSTS[0]) || player.canPay(COSTS[1]));
	}

	public boolean canPerform(Player player, DirPoint pos, int doneSoFar) {
		return canPerform(player, doneSoFar) && player.farm.canBuild(pos, BuildingType.STABLES);
	}

	public boolean canUnperform(Player player, int doneSoFar) {
		return doneSoFar > 0;
	}

	public boolean canUnperform(Player player, DirPoint pos, int doneSoFar) {
		return canUnperform(player, doneSoFar) && player.farm.hasBuilding(pos, BuildingType.STABLES, true);
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

	public boolean activate(Player player, int doneSoFar) {
		if (canPerform(player, doneSoFar)) {
			player.setActiveType(Purchasable.BUILDING);
		}
		return false;
	}

	public boolean activate(Player player, DirPoint pos, int doneSoFar) {
		int costNo = chooseStablesCost(player);
		if (costNo == NONE) {
			return false; // TODO this will not cancel action becousa still "canDoMore"
		}
		boolean done = player.purchaseBuilding(new Stables(), COSTS[costNo], pos);
		setChanged();
		return done;
	}

	public boolean undo(Player player, int doneSoFar) {
		if (canUnperform(player, doneSoFar)) {
			player.unpurchaseBuilding();
			setChanged();
			return true;
		}
		return false;
	}

	public boolean undo(Player player, DirPoint pos, int doneSoFar) {
		if (canUnperform(player, pos, doneSoFar)) {
			Building b = player.farm.unbuild(pos, true);
			player.unpay(b.getPaidCost());
			b.setPaidCost(null);
			setChanged();
			return true;
		}
		return false;
	}

}
