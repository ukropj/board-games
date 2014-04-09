package com.dill.agricola.actions.farm;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.GeneralSupply.Supplyable;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.buildings.HalfTimberedHouse;
import com.dill.agricola.model.buildings.OpenStables;
import com.dill.agricola.model.buildings.Shelter;
import com.dill.agricola.model.buildings.StorageBuilding;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.support.Msg;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.UiFactory;

public class BuildSpecial extends BuildAction {

	private static int counter = 0;

	private final static Map<BuildingType, Materials> COSTS = new EnumMap<BuildingType, Materials>(BuildingType.class);
	static {
		COSTS.put(BuildingType.HALF_TIMBERED_HOUSE, HalfTimberedHouse.COST);
		COSTS.put(BuildingType.STORAGE_BUILDING, StorageBuilding.COST);
		COSTS.put(BuildingType.SHELTER, Shelter.COST);
		COSTS.put(BuildingType.OPEN_STABLES, null);
	}
	private final Materials[] OS_COSTS = new Materials[] { OpenStables.COST_WOOD, OpenStables.COST_STONE };
	private int osCostNo;

	private Animals toGive = null;

	public BuildSpecial() {
		super(counter++ % 2 == 0 ? ActionType.SPECIAL : ActionType.SPECIAL2);
	}

	public UndoableFarmEdit init() {
		toBuild = null;
		toGive = null;
		return super.init();
	}

	protected Materials getCost(int doneSoFar) {
		return toBuild == null ? null : toBuild == BuildingType.OPEN_STABLES ? OS_COSTS[osCostNo] : COSTS.get(toBuild);
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

	public boolean canDoOnFarm(Player player, DirPoint pos, int doneSoFar) {
		// there does not need to be "any left", since building was already chosen
		return doneSoFar < 1 && toBuild != null && canPurchase(player, toBuild, pos);
	}

	private boolean canPurchase(Player player, BuildingType type, DirPoint pos) {
		if (type == BuildingType.OPEN_STABLES) {
			return player.canPurchase(type, OS_COSTS[0], pos) || player.canPurchase(type, OS_COSTS[1], pos);
		} else {
			return player.canPurchase(type, COSTS.get(type), pos);
		}
	}

	private BuildingType chooseBuilding(Player player) {
		List<BuildingType> types = new ArrayList<BuildingType>(GeneralSupply.getBuildingsLeft());
		if (types.size() == 0) {
			System.out.println("No special buildings available");
			return null;
		}
		List<JComponent> opts = new ArrayList<JComponent>();
		for (BuildingType type : types) {
			JComponent opt = UiFactory.createLabel(AgriImages.getBuildingIcon(type, ImgSize.BIG));
			opt.setEnabled(canPurchase(player, type, null));
			opts.add(opt);
		}
		int result = UiFactory.showOptionDialog(null, Msg.get("chooseBuilding"), Msg.get("specialBuildings"), null, opts);
		return result != JOptionPane.CLOSED_OPTION ? types.get(result) : null;
	}

	private int chooseOpenStablesCost(Player player) {
		List<JComponent> opts = new ArrayList<JComponent>();
		for (Materials cost : OS_COSTS) {
			JComponent opt = UiFactory.createResourcesPanel(cost, null, UiFactory.X_AXIS);
			opt.setEnabled(player.canPay(cost));
			opts.add(opt);
		}
		Icon icon = AgriImages.getBuildingIcon(BuildingType.OPEN_STABLES, ImgSize.MEDIUM);
		return UiFactory.showOptionDialog(null, Msg.get("chooseCost"), Msg.get("openStables"), icon, opts);
	}

	private Animals chooseReward(Building building) {
		Animals[] animalRewards = building.getAnimalRewards();
		if (animalRewards == null) {
			return null;
		}
		List<JComponent> opts = new ArrayList<JComponent>();
		for (int i = 0; i < animalRewards.length; i++) {
			JComponent opt = UiFactory.createResourcesPanel(null, animalRewards[i], UiFactory.X_AXIS);
			opt.setPreferredSize(new Dimension(40, 30));
			opts.add(opt);
		}
		Icon icon = AgriImages.getBuildingIcon(building.getType(), ImgSize.MEDIUM);
		int result = UiFactory.showOptionDialog(null, Msg.get("chooseReward"), building.getType().name, icon, opts);
		return result != JOptionPane.CLOSED_OPTION ? animalRewards[result] : animalRewards[0];
	}

	public boolean isCancelled() {
		return toBuild == null;
	}

	public UndoableFarmEdit doo(Player player) {
		if (canDo(player)) {
			toBuild = chooseBuilding(player);
			if (toBuild != null) {
				player.setActiveType(thing);
				setChanged();
			}
		}
		return null;
	}

	public UndoableFarmEdit doOnFarm(Player player, DirPoint pos, int doneSoFar) {
		if (toBuild == BuildingType.OPEN_STABLES) {
			osCostNo = JOptionPane.CLOSED_OPTION;
			if (!player.canPay(OS_COSTS[0])) {
				osCostNo = 1;
			} else if (!player.canPay(OS_COSTS[1])) {
				osCostNo = 0;
			}
			if (osCostNo == JOptionPane.CLOSED_OPTION) {
				// ask only if player can pay any
				osCostNo = chooseOpenStablesCost(player);
			}
			if (osCostNo == JOptionPane.CLOSED_OPTION) {
				return null;
			}
		}
		return super.doOnFarm(player, pos, doneSoFar);
	}

	protected UndoableFarmEdit postActivate(Player player, Building b) {
		GeneralSupply.useBuilding(toBuild, true);
		toGive = chooseReward(b);
		if (toGive != null) {
			player.purchaseAnimals(toGive);
			return new UseBuilding(toBuild, player, toGive);
		} else {
			return new UseBuilding(toBuild, player);
		}
	}

	protected class UseBuilding extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final BuildingType built;
		private final Player player;
		private final Animals takenAnimals;

		public UseBuilding(BuildingType built, Player player) {
			this(built, player, null);
		}

		public UseBuilding(BuildingType built, Player player, Animals animals) {
			this.built = built;
			this.player = player;
			this.takenAnimals = animals;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			if (takenAnimals != null) {
				player.unpurchaseAnimals(takenAnimals);
			}
			GeneralSupply.useBuilding(built, false);
			setChanged();
		}

		public void redo() throws CannotRedoException {
			super.redo();
			GeneralSupply.useBuilding(built, true);
			if (takenAnimals != null) {
				player.purchaseAnimals(takenAnimals);
			}
			setChanged();
		}

		public boolean isAnimalEdit() {
			return takenAnimals != null && takenAnimals.size() > 0;
		}
	}
}
