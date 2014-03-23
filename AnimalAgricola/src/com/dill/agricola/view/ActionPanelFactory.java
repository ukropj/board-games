package com.dill.agricola.view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.GeneralSupply.Supplyable;
import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.ActionStateChangeListener;
import com.dill.agricola.actions.farm.Expand;
import com.dill.agricola.actions.farm.Fences;
import com.dill.agricola.actions.farm.Troughs;
import com.dill.agricola.actions.farm.Walls;
import com.dill.agricola.actions.simple.BuildingMaterial;
import com.dill.agricola.actions.simple.CowPigs;
import com.dill.agricola.actions.simple.HorseSheep;
import com.dill.agricola.actions.simple.Millpond;
import com.dill.agricola.actions.simple.OneStone;
import com.dill.agricola.actions.simple.PigSheep;
import com.dill.agricola.actions.simple.StartOneWood;
import com.dill.agricola.actions.simple.ThreeWood;
import com.dill.agricola.actions.simple.TwoStone;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Dir;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.buildings.Stables;
import com.dill.agricola.model.buildings.Stall;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.UiFactory;

public class ActionPanelFactory {

	// TODO this introduces state, rethink
	private static JLabel tmpStallSupplyL = null;
	private static JPanel tmpBuildingSupplyP = null;

	public static void createActionPanel(JPanel parent, Action action, JButton actionButton) {
		JComponent actionPanel = null;
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 3;
		c.ipadx = c.ipady = 3;
		c.insets = new Insets(2, 2, 2, 2);
		c.weighty = 0.5;
		switch (action.getType()) {
		case STARTING_ONE_WOOD:
			JLabel firstL = UiFactory.createLabel(AgriImages.getFirstTokenIcon(0, ImgSize.MEDIUM));
			firstL.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
			firstL.setOpaque(false);
			createRefillPanel(parent, 0, 0, action, actionButton, firstL, StartOneWood.REFILL);
			return;
		case THREE_WOOD:
			createRefillPanel(parent, 1, 0, action, actionButton, null, ThreeWood.REFILL);
			return;
		case ONE_STONE:
			createRefillPanel(parent, 0, 1, action, actionButton, null, OneStone.REFILL);
			return;
		case TWO_STONE:
			createRefillPanel(parent, 1, 1, action, actionButton, null, TwoStone.REFILL);
			return;
		case BUILDING_MATERIAL:
			JPanel bmP = UiFactory.createVerticalPanel();
			UiFactory.updateResourcePanel(bmP, BuildingMaterial.MATERIALS, null, true);
			actionButton.add(bmP);
			c.gridx = 0;
			c.gridy = 3;
			c.gridwidth = 1;
			c.gridheight = 2;
			break;
		case FENCES:
			JPanel fenP = UiFactory.createVerticalPanel();
			fenP.add(UiFactory.createLabel(Msg.get("unlimited")));
			Materials fcost = new Materials(Fences.COST);
			fcost.substract(Material.BORDER, 1);
			JPanel fen1P = UiFactory.createResourcesPanel(fcost, null, UiFactory.X_AXIS);
			fen1P.add(UiFactory.createArrowLabel(Dir.E, false));
			fen1P.add(UiFactory.createLabel(AgriImages.getPurchasableIcon(Purchasable.FENCE)));
			fenP.add(fen1P);
			actionButton.add(fenP);
			c.gridx = 1;
			c.gridy = 3;
			c.gridwidth = 2;
			break;
		case WALLS:
			JPanel walP = UiFactory.createVerticalPanel();
			JPanel freeP = UiFactory.createHorizontalPanel();
			freeP.add(UiFactory.createLabel(Msg.get("xTimes", 2)));
			freeP.add(UiFactory.createLabel(AgriImages.getPurchasableIcon(Purchasable.FENCE)));
			walP.add(freeP);
			walP.add(UiFactory.createLabel(Msg.get("alsoUnlimited")));
			Materials wcost = new Materials(Walls.COST);
			wcost.substract(Material.BORDER, 1);
			JPanel wal1P = UiFactory.createResourcesPanel(wcost, null, UiFactory.X_AXIS);
			wal1P.add(UiFactory.createArrowLabel(Dir.E, false));
			wal1P.add(UiFactory.createLabel(AgriImages.getPurchasableIcon(Purchasable.FENCE)));
			walP.add(wal1P);
			actionButton.add(walP);
			c.gridx = 1;
			c.gridy = 4;
			c.gridwidth = 2;
			break;
		case EXPAND:
			JPanel extP = UiFactory.createVerticalPanel();
			extP.add(UiFactory.createLabel("+", AgriImages.getPurchasableIcon(Purchasable.EXTENSION)));
			extP.add(createSupplyLabel(action, Supplyable.EXTENSION));
			createRefillPanel(parent, 1, 3, action, actionButton, extP, Expand.REFILL);
			return;
		case TROUGHS:
			JPanel troP = UiFactory.createVerticalPanel();
			troP.add(UiFactory.createLabel(Msg.get("xTimes", 1), AgriImages.getPurchasableIcon(Purchasable.TROUGH)));
			troP.add(UiFactory.createLabel(Msg.get("alsoUnlimited")));
			JPanel tro1P = UiFactory.createResourcesPanel(Troughs.COST, null, UiFactory.X_AXIS);
			tro1P.add(UiFactory.createArrowLabel(Dir.E, false));
			tro1P.add(UiFactory.createLabel(AgriImages.getPurchasableIcon(Purchasable.TROUGH)));
			troP.add(tro1P);
			troP.add(createSupplyLabel(action, Supplyable.TROUGH));
			actionButton.add(troP);
			c.gridx = 3;
			c.gridy = 4;
			break;
		case MILLPOND:
			createRefillPanel(parent, 0, 5, action, actionButton, null, Millpond.REFILL, null, Millpond.OTHER_ANIMAL);
			return;
		case PIG_AND_SHEEP:
			createRefillPanel(parent, 1, 5, action, actionButton, null, PigSheep.FIRST_ANIMAL, PigSheep.OTHER_ANIMAL);
			return;
		case COW_AND_PIGS:
			createRefillPanel(parent, 0, 6, action, actionButton, null, CowPigs.FIRST_ANIMAL, CowPigs.OTHER_ANIMAL);
			return;
		case HORSE_AND_SHEEP:
			createRefillPanel(parent, 1, 6, action, actionButton, null, HorseSheep.FIRST_ANIMAL, HorseSheep.OTHER_ANIMAL);
			return;
		case STALLS:
			JPanel stallP = UiFactory.createVerticalPanel();
			stallP.add(UiFactory.createLabel(Msg.get("once")));
			stallP.add(UiFactory.createResourcesPanel(Stall.COST, null, UiFactory.X_AXIS));
			stallP.add(UiFactory.createArrowLabel(Dir.S, false));
			stallP.add(UiFactory.createLabel(AgriImages.getBuildingIcon(BuildingType.STALL, ImgSize.MEDIUM)));
			tmpStallSupplyL = createSupplyLabel(action, Supplyable.STALL);
			stallP.add(tmpStallSupplyL);
			stallP.add(Box.createVerticalGlue());
			actionButton.add(stallP);
			c.gridx = 0;
			c.gridy = 7;
			c.gridwidth = 2;
			c.gridheight = 3;
			c.weightx = 1;
			break;
		case STABLES:
			JPanel staP = UiFactory.createVerticalPanel();
			staP.add(UiFactory.createLabel(Msg.get("unlimited")));
			JPanel costP = UiFactory.createHorizontalPanel();
			costP.add(UiFactory.createResourcesPanel(Stables.COST_WOOD, null, UiFactory.X_AXIS));
			costP.add(UiFactory.createLabel(" / "));
			costP.add(UiFactory.createResourcesPanel(Stables.COST_STONE, null, UiFactory.X_AXIS));
			staP.add(costP);
			staP.add(UiFactory.createArrowLabel(Dir.S, false));
			staP.add(UiFactory.createLabel(AgriImages.getBuildingIcon(BuildingType.STABLES, ImgSize.MEDIUM)));
			staP.add(Box.createVerticalGlue());
			actionButton.add(staP);
			c.gridx = 2;
			c.gridy = 7;
			c.gridwidth = 2;
			c.gridheight = 3;
			c.weightx = 1;
			break;
		case SPECIAL:
			c.gridx = 4;
			c.gridy = 8;
			c.gridwidth = 2;
			c.weighty = 0;
//			c.fill = GridBagConstraints.NONE;
//			c.anchor = GridBagConstraints.CENTER;
			tmpBuildingSupplyP = UiFactory.createFlowPanel(3, 0);
			action.addChangeListener(new BuildingChangeListener(tmpBuildingSupplyP));
			parent.add(tmpBuildingSupplyP, c);

			actionButton.add(UiFactory.createLabel(Msg.get("specBuildLabel")));
			if (tmpStallSupplyL != null) {
				action.addChangeListener(new SupplyChangeListener(Supplyable.STALL, tmpStallSupplyL));
			}
			c.fill = GridBagConstraints.BOTH;
			c.weighty = 0.5;
			c.gridy = 7;
			break;
		case SPECIAL2:
			if (tmpBuildingSupplyP != null) {
				action.addChangeListener(new BuildingChangeListener(tmpBuildingSupplyP));
			}
			if (tmpStallSupplyL != null) {
				action.addChangeListener(new SupplyChangeListener(Supplyable.STALL, tmpStallSupplyL));
			}
			actionButton.add(UiFactory.createLabel(Msg.get("specBuildLabel")));
			c.weighty = 0.5;
			c.gridx = 4;
			c.gridy = 9;
			c.gridwidth = 2;
			break;
		default:
			actionButton.setText(action.toString());
			return;
		}

		parent.add(actionPanel != null ? actionPanel : actionButton, c);
	}

	private static void createRefillPanel(JPanel parent, int x, int y, Action action, JButton button, JComponent extraP, Materials materials) {
		createRefillPanel(parent, x, y, action, button, extraP, materials, null, null);
	}

	private static void createRefillPanel(JPanel parent, int x, int y, Action action, JButton button, Materials materials, Animal animal, Animal otherAnimal) {
		createRefillPanel(parent, x, y, action, button, null, materials, animal, otherAnimal);
	}

	private static void createRefillPanel(JPanel parent, int x, int y, Action action, JButton button, JComponent extraP, //
			Materials materials, Animal animal, Animal otherAnimal) {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 3 * x;
		c.gridy = y;
		c.gridwidth = 2;
		c.ipadx = c.ipady = 3;
		c.insets = new Insets(2, 2, 2, 0);
		c.weightx = 0.6;
		c.weighty = 0.5;
		parent.add(createPrefixPanel(materials, animal, otherAnimal), c);
		c.gridx = 3 * x + 2;
		c.gridwidth = 1;
		c.insets = new Insets(2, 0, 2, 2);
		parent.add(createResourcesButton(action, button, extraP), c);
	}

	private static JPanel createPrefixPanel(Materials materials, Animal animal, Animal otherAnimal) {
		JPanel refillP = UiFactory.createHorizontalPanel();
		refillP.setOpaque(true);
		refillP.add(Box.createHorizontalGlue());
		refillP.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.GRAY));
		Animals animals = animal == null ? null : new Animals(animal, 1);
		JPanel main = UiFactory.createResourcesPanel(materials, animals, UiFactory.Y_AXIS);
		refillP.add(main);
		if (otherAnimal != null) {
			JPanel sub = UiFactory.createHorizontalPanel();
			sub.add(UiFactory.createLabel(" ("));
			sub.add(UiFactory.createResourcesPanel(null, new Animals(otherAnimal, 1), UiFactory.Y_AXIS));
			sub.add(UiFactory.createLabel(")"));
			main.add(sub);
		}
		refillP.add(UiFactory.createArrowLabel(Dir.E, true));
		return refillP;
	}

	private static JButton createResourcesButton(Action action, JButton button, JComponent extraP) {
		JPanel actionP = UiFactory.createHorizontalPanel();
		JPanel supplyP = UiFactory.createResourcesPanel(action.getAccumulatedMaterials(), action.getAccumulatedAnimals(), UiFactory.Y_AXIS);
		actionP.add(supplyP);
		if (extraP != null) {
			actionP.add(extraP);
		}
		button.add(actionP);
		action.addChangeListener(new ResourceChangeListener(supplyP));
		return button;
	}

	private static JLabel createSupplyLabel(Action action, Supplyable type) {
		JLabel l = UiFactory.createLabel(Msg.get("supplyableLeft", GeneralSupply.getLeft(type)));
//		l.setForeground(Color.GRAY);
		action.addChangeListener(new SupplyChangeListener(type, l));
		return l;
	}

	private static class ResourceChangeListener implements ActionStateChangeListener {

		private JPanel materialPanel;

		public ResourceChangeListener(JPanel materialPanel) {
			this.materialPanel = materialPanel;
		}

		public void stateChanges(Action action) {
			UiFactory.updateResourcePanel(materialPanel, action.getAccumulatedMaterials(), action.getAccumulatedAnimals(), true);
		}

	}

	private static class SupplyChangeListener implements ActionStateChangeListener {

		private final Supplyable type;
		private final JLabel label;

		public SupplyChangeListener(Supplyable type, JLabel label) {
			this.type = type;
			this.label = label;
		}

		public void stateChanges(Action action) {
			label.setText(Msg.get("supplyableLeft", GeneralSupply.getLeft(type)));
		}

	}

	private static class BuildingChangeListener implements ActionStateChangeListener {

		private JPanel buildingPanel;

		public BuildingChangeListener(JPanel buildingPanel) {
			this.buildingPanel = buildingPanel;
		}

		public void stateChanges(Action action) {
			buildingPanel.removeAll();
			for (BuildingType b : GeneralSupply.getBuildingsLeft()) {
				JLabel bl = new JLabel(AgriImages.getBuildingIcon(b, ImgSize.SMALL));
//				bl.setToolTipText(String.format(
//			            "<html><img src=\"%s\">",
//			            Images.getImageUrl(AgriImages.getBuildingImageName(b))));
				buildingPanel.add(bl);
			}
		}
	}

}
