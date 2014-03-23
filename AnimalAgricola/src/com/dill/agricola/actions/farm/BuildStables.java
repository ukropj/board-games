package com.dill.agricola.actions.farm;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.buildings.Stables;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.undo.UndoableFarmEdit;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.UiFactory;

public class BuildStables extends BuildAction {

	private final Materials[] COSTS = new Materials[] { Stables.COST_WOOD, Stables.COST_STONE };
	private int costNo;

	public BuildStables() {
		super(ActionType.STABLES, BuildingType.STABLES);
	}

	protected Materials getCost(int doneSoFar) {
		return COSTS[costNo];
	}
	
	protected Building getBuildingInstance(BuildingType type) {
		return new Stables();
	}
	
	public boolean canDo(Player player) {
		return player.canPurchase(toBuild, COSTS[0], null) || player.canPurchase(toBuild, COSTS[1], null);
	}

	public boolean canDoOnFarm(Player player, DirPoint pos, int doneSoFar) {
		return player.canPurchase(toBuild, COSTS[0], pos) || player.canPurchase(toBuild, COSTS[1], pos);
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

	public UndoableFarmEdit doOnFarm(Player player, DirPoint pos, int doneSoFar) {
		costNo = chooseStablesCost(player);
		if (costNo == JOptionPane.CLOSED_OPTION) {
			return null;
		}
		return super.doOnFarm(player, pos, doneSoFar);
	}

}
