package com.dill.agricola.actions;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.buildings.BuildingType;
import com.dill.agricola.model.buildings.HalfTimberedHouse;
import com.dill.agricola.model.buildings.OpenStables;
import com.dill.agricola.model.buildings.Shelter;
import com.dill.agricola.model.buildings.StorageBuilding;
import com.dill.agricola.model.enums.ActionType;
import com.dill.agricola.model.enums.Animal;
import com.dill.agricola.model.enums.Materials;
import com.dill.agricola.model.enums.Purchasable;
import com.dill.agricola.view.Images;
import com.dill.agricola.view.SwingUtils;
import com.dill.agricola.view.Images.IconSize;

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

	public void init() {
		super.init();
		toBuild = null;
		toGive = null;
		setChanged();
	}

	public boolean canPerform(Player player) {
		return super.canPerform(player) && GeneralSupply.getBuildingsLeft().size() > 0;
	}

	private BuildingType chooseBuilding() {
		List<JComponent> opts = new ArrayList<JComponent>();
		List<BuildingType> types = new ArrayList<BuildingType>(GeneralSupply.getBuildingsLeft());
		if (types.size() == 0) {
			System.out.println("No special buildings available");
			return null;
		}
		for (BuildingType b : types) {
			opts.add(new JLabel(Images.getBuildingIcon(b, 100)));			
		}
		int optNo = SwingUtils.showOptionDialog("Choose building", "Special buildings", null, opts);
		if (optNo == NONE) {
			return null;
		} else {
			return types.get(optNo);
		}
	}

	private int chooseOpenStablesCost() {
		List<JComponent> opts = new ArrayList<JComponent>();
		for (Materials cost : OS_COSTS) {
			opts.add(SwingUtils.createResourcesPanel(cost, null, SwingUtils.X_AXIS));
		}
		Icon icon = Images.getBuildingIcon(BuildingType.OPEN_STABLES, 40);
		return SwingUtils.showOptionDialog("Choose cost", "Open Stables", icon, opts);
	}

	private Animal chooseReward(Building building) {
		Animal[] animalRewards = building.getAnimalRewards();
		if (animalRewards == null) {
			return null;
		}
		Icon[] icons = new ImageIcon[animalRewards.length];
		for (int i = 0; i < animalRewards.length; i++) {
			icons[i] = Images.getAnimalIcon(animalRewards[i], IconSize.BIG);
		}
		int result = JOptionPane.showOptionDialog(null, "Choose free animal", "Reward", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
				Images.getBuildingIcon(building.getType(), 40), icons, icons[0]);
		return result != NONE ? animalRewards[result] : null;
	}

	public boolean doOnce(Player player) {
		toBuild = chooseBuilding();
		if (toBuild == null) {
			return false;
		} else {
			boolean done;
			Building building = GeneralSupply.getSpecialBuilding(toBuild);
			if (toBuild == BuildingType.OPEN_STABLES) {
				int toPay = chooseOpenStablesCost();
				if (toPay == NONE) {
					return false;
				} else {
					done = player.purchaseBuilding(building, OS_COSTS[toPay]);
				}
			} else {
				done = player.purchaseBuilding(building, COSTS.get(toBuild));
			}
			if (done) {
				toGive = chooseReward(building);
				if (toGive != null) {
					player.purchaseAnimal(toGive, 1);
				}
				player.setActiveType(Purchasable.BUILDING);
				GeneralSupply.useBuilding(toBuild, true);
				setChanged();
			} else {
				toBuild = null;
			}
			return done;
		}
	}

	public boolean undoOnce(Player player) {
		if (toBuild != null) {
			boolean done = player.unpurchaseBuilding();
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

	public String toString() {
		return super.toString() + " (" + GeneralSupply.getBuildingsLeft().size() + " left)";
	}
}
