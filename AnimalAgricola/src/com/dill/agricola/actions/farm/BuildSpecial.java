package com.dill.agricola.actions.farm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.Game.Phase;
import com.dill.agricola.GeneralSupply.Supplyable;
import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.ActionStateChangeListener;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.buildings.OpenStables;
import com.dill.agricola.model.buildings.evenmore.Inn;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.support.Msg;
import com.dill.agricola.support.Namer;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.UiFactory;

public class BuildSpecial extends BuildAction {

	private static int counter = 0;

	private final static Map<BuildingType, Materials> COSTS = new EnumMap<BuildingType, Materials>(BuildingType.class);
	private final static Materials[] OS_COSTS = new Materials[] { OpenStables.COST_WOOD, OpenStables.COST_STONE };
	private int osCostNo;

	public BuildSpecial() {
		super(counter++ % 2 == 0 ? ActionType.SPECIAL : ActionType.SPECIAL2);
	}

	private static Materials getBuildingCost(Player player, BuildingType type) {
		return getBuildingCost(player, type, 0);
	}

	private static Materials getBuildingCost(Player player, BuildingType type, int variant) {
		Materials cost = null;
		if (type == BuildingType.OPEN_STABLES) {
			cost = OS_COSTS[variant];
		} else {
			if (COSTS.containsKey(type)) {
				cost = COSTS.get(type);
			} else {
				try {
					String pkg = "com.dill.agricola.model.buildings." + (type.set == 1 ? "more." : type.set == 2 ? "evenmore." : "");
					Class<?> bldgClass = Class.forName(pkg + Namer.toCamelCase(type.toString()));
					cost = (Materials) bldgClass.getField("COST").get(null);
					COSTS.put(type, cost);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		if (cost.get(Material.WOOD) > 0 && player.getFarm().hasBuilding(BuildingType.SAWMILL)) {
			cost = new Materials(cost);
			cost.substract(Material.WOOD, 1);
		}
		return cost;
	}

	public UndoableFarmEdit init() {
		toBuild = null;
		return super.init();
	}

	protected Materials getCost(Player player) {
		return toBuild == null ? null : getBuildingCost(player, toBuild, osCostNo);
	}

	protected boolean isAnyLeft() {
		return GeneralSupply.getLeft(Supplyable.SPECIAL_BUILDING) > 0;
	}

	protected Building getBuildingInstance(BuildingType type) {
		return GeneralSupply.getSpecialBuilding(type);
	}

	public boolean canDo(Player player) {
		return isAnyLeft() && canPurchaseAny(player);
	}

	private boolean canPurchaseAny(Player player) {
		for (BuildingType b : GeneralSupply.getBuildingsLeft()) {
			if (canPurchase(player, b, null)) {
				return true;
			}
		}
		return false;
	}

	public boolean canDoOnFarm(Player player, DirPoint pos) {
		// there does not need to be "any left", since building was already chosen
		return getUseCount() < 1 && toBuild != null && canPurchase(player, toBuild, pos);
	}

	private boolean canPurchase(Player player, BuildingType type, DirPoint pos) {
		if (type == BuildingType.OPEN_STABLES) {
			return player.canPurchase(type, getBuildingCost(player, type, 0), pos)
					|| player.canPurchase(type, getBuildingCost(player, type, 1), pos);
		} else {
			return passesCondition(player, type, pos) && player.canPurchase(type, getBuildingCost(player, type), pos);
		}
	}

	private boolean passesCondition(Player player, BuildingType type, DirPoint pos) {
		// special conditions for some buildings
		if (type == BuildingType.DUCK_POND) {
			return player.farm.getUnusedSpaces().size() >= 6;
		}
		if (type == BuildingType.FODDER_BEET_FARM) {
			for (Animal a : Animal.values()) {
				if (player.getAnimal(a) < 2) {
					return false;
				}
			}
			return true;
		}
		if (type == BuildingType.INN) {
			for (Animal a : Inn.ANIMAL_COST.types()) {
				if (player.getAnimal(a) <= 0) {
					return false;
				}
			}
			return true;
		}
		return true;
	}

	private BuildingType chooseBuilding(Player player) {
		List<BuildingType> types = new ArrayList<BuildingType>(GeneralSupply.getBuildingsAll());
		List<BuildingType> left = new ArrayList<BuildingType>(GeneralSupply.getBuildingsLeft());
		if (left.size() == 0) {
			return null;
		}
		List<JComponent> opts = new ArrayList<JComponent>();
		for (BuildingType type : types) {
			if (left.contains(type)) {
				boolean canPurchase = canPurchase(player, type, null);
				JComponent opt = UiFactory.createLabel(AgriImages.getBuildingIcon(type, ImgSize.BIG, canPurchase));
				opt.setEnabled(canPurchase);
				opts.add(opt);
			} else {
				opts.add(null);
			}
		}
		int result = UiFactory.showOptionDialog(null, Msg.get("chooseBuilding"), Msg.get("specialBuildings"), null, opts, 4);
		return result != UiFactory.NO_OPTION ? types.get(result) : null;
	}

	private int chooseOpenStablesCost(Player player) {
		List<JComponent> opts = new ArrayList<JComponent>();
		for (int i = 0; i < OS_COSTS.length; i++) {
			Materials cost = getBuildingCost(player, BuildingType.OPEN_STABLES, i);
			JComponent opt = UiFactory.createResourcesPanel(cost, null, UiFactory.X_AXIS);
			opt.setEnabled(player.canPay(cost));
			opts.add(opt);
		}
		Icon icon = AgriImages.getBuildingIcon(BuildingType.OPEN_STABLES, ImgSize.MEDIUM);
		return UiFactory.showOptionDialog(null, Msg.get("chooseCost"), Msg.get("openStables"), icon, opts, 0);
	}

	public UndoableFarmEdit doo(Player player) {
		if (canDo(player)) {
			toBuild = chooseBuilding(player);
			if (toBuild != null) {
				setChanged();
				return setPlayerActive(player);
			} else {
				setCancelled();
			}
		}
		return null;
	}

	public UndoableFarmEdit doOnFarm(Player player, DirPoint pos) {
		if (toBuild == BuildingType.OPEN_STABLES) {
			osCostNo = UiFactory.NO_OPTION;
			// TODO move logic to chooseOpenStablesCost
			if (!player.canPay(getBuildingCost(player, toBuild, 0))) {
				osCostNo = 1;
			} else if (!player.canPay(getBuildingCost(player, toBuild, 1))) {
				osCostNo = 0;
			}
			if (osCostNo == UiFactory.NO_OPTION) {
				// ask only if player can pay any
				osCostNo = chooseOpenStablesCost(player);
			}
			if (osCostNo == UiFactory.NO_OPTION) {
				return null;
			}
		}
		return super.doOnFarm(player, pos);
	}

	protected UndoableFarmEdit postActivate(Player player, Building b, DirPoint pos) {
		GeneralSupply.useBuilding(toBuild, true);
		List<Action> actions = new ArrayList<Action>();
		Action[] preActions = getBuildingInstance(toBuild).getExtraActions(Phase.BEFORE_WORK, 8/*TODO ugly hack*/);
		if (preActions != null) {
			actions.addAll(Arrays.asList(preActions));
		}
		Action[] postActions = getBuildingInstance(toBuild).getExtraActions(Phase.BEFORE_BREEDING, 8);
		if (postActions != null) {
			actions.addAll(Arrays.asList(postActions));
		}
		for (Action a : actions) {
			a.addChangeListener(new ExtraActionStateChangeListener());
		}
		return new UseBuilding(toBuild, actions);
	}

	public Action getSubAction(Player player, boolean afterFarmAction) {
		return afterFarmAction && toBuild != null ? getBuildingInstance(toBuild).getSubAction() : null;
	}

	protected class UseBuilding extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final BuildingType built;
		private final List<Action> actions;

		public UseBuilding(BuildingType built, List<Action> actions) {
			this.built = built;
			this.actions = actions;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			GeneralSupply.useBuilding(built, false);
			for (Action a : actions) {
				a.removeChangeListeners();
			}
			setChanged();
		}

		public void redo() throws CannotRedoException {
			super.redo();
			GeneralSupply.useBuilding(built, true);
			for (Action a : actions) {
				a.addChangeListener(new ExtraActionStateChangeListener());
			}
			setChanged();
		}

	}

	private class ExtraActionStateChangeListener implements ActionStateChangeListener {
		public void stateChanges(Action subAction) {
			BuildSpecial.this.setChanged();
		}
	}
}
