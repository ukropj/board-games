package com.dill.agricola.actions.farm;

//import com.dill.agricola.common.DirPoint;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.GeneralSupply.Supplyable;
import com.dill.agricola.actions.AbstractAction;
import com.dill.agricola.common.DirPoint;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.buildings.HalfTimberedHouse;
import com.dill.agricola.model.buildings.OpenStables;
import com.dill.agricola.model.buildings.Shelter;
import com.dill.agricola.model.buildings.StorageBuilding;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.UiFactory;

public class BuildSpecial extends AbstractAction {

	private static int counter = 0;
	
	private final static int NONE = JOptionPane.CLOSED_OPTION;

	private final static Map<BuildingType, Materials> COSTS = new EnumMap<BuildingType, Materials>(BuildingType.class);
	static {
		COSTS.put(BuildingType.HALF_TIMBERED_HOUSE, HalfTimberedHouse.COST);
		COSTS.put(BuildingType.STORAGE_BUILDING, StorageBuilding.COST);
		COSTS.put(BuildingType.SHELTER, Shelter.COST);
		COSTS.put(BuildingType.OPEN_STABLES, null);
	}
	private final Materials[] OS_COSTS = new Materials[] { OpenStables.COST_WOOD, OpenStables.COST_STONE };

	private BuildingType toBuild = null;
	private Animal toGive = null;

	public BuildSpecial() {
		super(counter++ % 2 == 0 ? ActionType.SPECIAL : ActionType.SPECIAL2);
	}
	
	public void reset() {
		super.reset();
		setChanged();  // to update available building count
	}

	public void init() {
		super.init();
		toBuild = null;
		toGive = null;
		setChanged();
	}

	public boolean isFarmAction() {
		return true;
	}
	
	public boolean canPerform(Player player, int doneSoFar) {
		return !isUsed() && GeneralSupply.getLeft(Supplyable.SPECIAL_BUILDING) > 0 && doneSoFar < 1;
	}
	
	public boolean canPerform(Player player, DirPoint pos, int doneSoFar) {
		return canPerform(player, doneSoFar) && toBuild != null && player.farm.canBuild(pos, toBuild);
	}
	
	public boolean canUnperform(Player player, int doneSoFar) {
		return toBuild!= null && doneSoFar > 0;
	}
	
	public boolean canUnperform(Player player, DirPoint pos, int doneSoFar) {
		return canUnperform(player, doneSoFar) && player.farm.hasBuilding(pos, toBuild, true);
	}

	private BuildingType chooseBuilding(Player player) {
		List<JComponent> opts = new ArrayList<JComponent>();
		List<BuildingType> types = new ArrayList<BuildingType>(GeneralSupply.getBuildingsLeft());
		if (types.size() == 0) {
			System.out.println("No special buildings available");
			return null;
		}
		for (BuildingType type : types) {
			JComponent opt = UiFactory.createLabel(AgriImages.getBuildingIcon(type, ImgSize.BIG));
			if (type != BuildingType.OPEN_STABLES) {
				opt.setEnabled(player.canPay(COSTS.get(type)));				
			} else {
				opt.setEnabled(player.getBuildingCount(BuildingType.STALL) > 0 && (player.canPay(OS_COSTS[0]) || player.canPay(OS_COSTS[1])));								
			}
			opts.add(opt);
		}
		int optNo = UiFactory.showOptionDialog("Choose building", "Special buildings", null, opts);
		if (optNo == NONE) {
			return null;
		} else {
			return types.get(optNo);
		}
	}

	private int chooseOpenStablesCost(Player player) {
		List<JComponent> opts = new ArrayList<JComponent>();
		for (Materials cost : OS_COSTS) {
			JComponent opt = UiFactory.createResourcesPanel(cost, null, UiFactory.X_AXIS);
			opt.setEnabled(player.canPay(cost));
			opts.add(opt);
		}
		Icon icon = AgriImages.getBuildingIcon(BuildingType.OPEN_STABLES, ImgSize.MEDIUM);
		return UiFactory.showOptionDialog("Choose cost", "Open Stables", icon, opts);
	}

	private Animal chooseReward(Building building) {
		Animal[] animalRewards = building.getAnimalRewards();
		if (animalRewards == null) {
			return null;
		}
		Icon[] icons = new ImageIcon[animalRewards.length];
		for (int i = 0; i < animalRewards.length; i++) {
			icons[i] = AgriImages.getAnimalIcon(animalRewards[i], ImgSize.BIG);
		}
		int result = JOptionPane.showOptionDialog(null, "Choose free animal", "Reward", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
				AgriImages.getBuildingIcon(building.getType(), ImgSize.MEDIUM), icons, icons[0]);
		return result != NONE ? animalRewards[result] : null;
	}

	public boolean activate(Player player, int doneSoFar) {
		if (canPerform(player, doneSoFar)) {
			toBuild = chooseBuilding(player);
			if (toBuild != null) {
				player.setActiveType(Purchasable.BUILDING);
				setChanged();
			}
		}
		return false;
	}
	
	public boolean activate(Player player, DirPoint pos, int doneSoFar) {
		if (canPerform(player, pos, doneSoFar)) {
			boolean done;
			Building building = GeneralSupply.getSpecialBuilding(toBuild);
			if (toBuild == BuildingType.OPEN_STABLES) {
				int toPay = chooseOpenStablesCost(player);
				if (toPay == NONE) {
					return false;
				} else {
					done = player.purchaseBuilding(building, OS_COSTS[toPay], pos);
				}
			} else {
				done = player.purchaseBuilding(building, COSTS.get(toBuild), pos);
			}
			if (done) {
				toGive = chooseReward(building);
				if (toGive != null) {
					player.purchaseAnimal(toGive, 1);
				}
				GeneralSupply.useBuilding(toBuild, true);
				setChanged();
			} else {
				toBuild = null;
			}
			return done;
		}
		return false;
	}

	public boolean undo(Player player, int doneSoFar) {
		if (canUnperform(player, doneSoFar)) {
			boolean done = player.unpurchaseBuilding() != null;
			if (toGive != null) {
				done = player.unpurchaseAnimal(toGive, 1) && done;
			}
			if (done) {
				GeneralSupply.useBuilding(toBuild, false);
				toBuild = null;
				toGive = null;
				setChanged();
			}
			return done;
		}
		return false;
	}
	
	public boolean undo(Player player, DirPoint pos, int doneSoFar) {
		if (canUnperform(player, pos, doneSoFar)) {
			Building b = player.farm.unbuild(pos, true);
			player.unpay(b.getPaidCost());
			b.setPaidCost(null);
			
			GeneralSupply.useBuilding(toBuild, false);
			toBuild = null;
			toGive = null;
			setChanged();
			return true;
		}
		return false;
	}

	public String toString() {
		return super.toString() + " (" + GeneralSupply.getBuildingsLeft().size() + " left)";
	}
}
