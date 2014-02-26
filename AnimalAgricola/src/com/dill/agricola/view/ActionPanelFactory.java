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

import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.BuildTrough;
import com.dill.agricola.actions.BuildingMaterial;
import com.dill.agricola.actions.CowPigs;
import com.dill.agricola.actions.Expand;
import com.dill.agricola.actions.Fences;
import com.dill.agricola.actions.HorseSheep;
import com.dill.agricola.actions.Millpond;
import com.dill.agricola.actions.OneStone;
import com.dill.agricola.actions.PigSheep;
import com.dill.agricola.actions.StartOneWood;
import com.dill.agricola.actions.StateChangeListener;
import com.dill.agricola.actions.ThreeWood;
import com.dill.agricola.actions.TwoStone;
import com.dill.agricola.actions.Walls;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Dir;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.buildings.BuildingType;
import com.dill.agricola.model.buildings.Stables;
import com.dill.agricola.model.buildings.Stall;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.Images;
import com.dill.agricola.view.utils.SwingUtils;

public class ActionPanelFactory {
	
	public static void createActionPanel(JPanel parent, Action action, JButton actionButton) {
		JComponent actionPanel = null;
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 3;
		c.ipadx = c.ipady = 3;
		c.insets = new Insets(5,5,5,5);
		switch (action.getType()) {
		case STARTING_ONE_WOOD:
			JLabel firstL = SwingUtils.createLabel(Images.getFirstTokenIcon(0, 30));
			createRefillPanel(parent, 0, 0, action, actionButton, firstL, StartOneWood.REFILL);
			return;
		case THREE_WOOD:
			createRefillPanel(parent, 1, 0, action, actionButton, null, ThreeWood.REFILL);
			return;
		case TWO_STONE:
			createRefillPanel(parent, 0, 1, action, actionButton, null, TwoStone.REFILL);
			return;
		case ONE_STONE:
			createRefillPanel(parent, 1, 1, action, actionButton, null, OneStone.REFILL);
			return;
		case BUILDING_MATERIAL:
			JPanel bmP = SwingUtils.createVerticalPanel();
			SwingUtils.updateResourcePanel(bmP, BuildingMaterial.MATERIALS, null, true);
			actionButton.add(bmP);
			c.gridx = 0;
			c.gridy = 3;
			c.gridwidth = 1;
			c.gridheight = 2;
			break;
		case FENCES:
			JPanel fenP = SwingUtils.createVerticalPanel();
			fenP.add(SwingUtils.createLabel(Msg.get("unlimited")));
			Materials fcost = new Materials(Fences.COST);
			fcost.substract(Material.BORDER, 1);
			JPanel fen1P = SwingUtils.createResourcesPanel(fcost, null, SwingUtils.X_AXIS);
			fen1P.add(SwingUtils.createArrowLabel(Dir.E, false));
			fen1P.add(SwingUtils.createLabel(Images.toIcon(Images.getFenceImage(Dir.N), 5)));
			fenP.add(fen1P);
			actionButton.add(fenP);
			c.gridx = 1;
			c.gridy = 3;
			c.gridwidth = 2;
			break;
		case WALLS:
			JPanel walP = SwingUtils.createVerticalPanel();
			walP.add(SwingUtils.createLabel("2x", Images.toIcon(Images.getFenceImage(Dir.N), 5)), 0);
			walP.add(SwingUtils.createLabel(Msg.get("alsoUnlimited")));
			Materials wcost = new Materials(Walls.COST);
			wcost.substract(Material.BORDER, 1);
			JPanel wal1P = SwingUtils.createResourcesPanel(wcost, null, SwingUtils.X_AXIS);
			wal1P.add(SwingUtils.createArrowLabel(Dir.E, false));
			wal1P.add(SwingUtils.createLabel(Images.toIcon(Images.getFenceImage(Dir.N), 5)));
			walP.add(wal1P);
			actionButton.add(walP);
			c.gridx = 1;
			c.gridy = 4;
			c.gridwidth = 2;
			break;
		case EXPAND:
			JLabel extP = SwingUtils.createLabel("+", Images.toIcon(Images.getExtensionImage(), 30));
			createRefillPanel(parent, 1, 3, action, actionButton, extP, Expand.REFILL);
			return;
		case TROUGHS:
			JPanel troP = SwingUtils.createVerticalPanel();
			troP.add(SwingUtils.createLabel("+1", Images.toIcon(Images.getTroughImage(), 20)));
			troP.add(SwingUtils.createLabel(Msg.get("alsoUnlimited")));
			JPanel tro1P = SwingUtils.createResourcesPanel(BuildTrough.COST, null, SwingUtils.X_AXIS);
			tro1P.add(SwingUtils.createArrowLabel(Dir.E, false));
			tro1P.add(SwingUtils.createLabel(Images.toIcon(Images.getTroughImage(), 20)));
			troP.add(tro1P);
			actionButton.add(troP);
			c.gridx = 3;
			c.gridy = 4;
			break;
		case STALLS:
			JPanel stallP = SwingUtils.createVerticalPanel();
			stallP.add(SwingUtils.createLabel(Msg.get("once")));
			stallP.add(SwingUtils.createResourcesPanel(Stall.COST, null, SwingUtils.X_AXIS));
			stallP.add(SwingUtils.createArrowLabel(Dir.S, false));
			stallP.add(SwingUtils.createLabel(Images.getBuildingIcon(BuildingType.STALL, 40)));
			stallP.add(Box.createVerticalGlue());
			actionButton.add(stallP);
			c.gridx = 0;
			c.gridy = 7;
			c.gridwidth = 2;
			c.gridheight = 2;
			c.weightx = 0.5;
//			c.fill = GridBagConstraints.HORIZONTAL;
//			c.anchor = GridBagConstraints.PAGE_START;
			break;
		case STABLES:
			JPanel staP = SwingUtils.createVerticalPanel();
			staP.add(SwingUtils.createLabel(Msg.get("unlimited")));
			JPanel costP = SwingUtils.createHorizontalPanel();
			costP.add(SwingUtils.createResourcesPanel(Stables.COST_WOOD, null, SwingUtils.X_AXIS));
			costP.add(SwingUtils.createLabel("/"));
			costP.add(SwingUtils.createResourcesPanel(Stables.COST_STONE, null, SwingUtils.X_AXIS));
			staP.add(costP);
			staP.add(SwingUtils.createArrowLabel(Dir.S, false));
			staP.add(SwingUtils.createLabel(Images.getBuildingIcon(BuildingType.STABLES, 40)));
			staP.add(Box.createVerticalGlue());
			actionButton.add(staP);
			c.gridx = 2;
			c.gridy = 7;
			c.gridwidth = 2;
			c.gridheight = 2;
			c.weightx = 0.5;
//			c.fill = GridBagConstraints.HORIZONTAL;
//			c.anchor = GridBagConstraints.PAGE_START;
			break;
		case SPECIAL:
			actionButton.setText(Msg.get("specBuildLabel"));
//			JPanel sbP = SwingUtils.createFlowPanel();
//			for (BuildingType b : GeneralSupply.getBuildingsLeft()) {
//				sbP.add(new JLabel(Images.getBuildingIcon(b, 40)));
//			}
//			action.setChangeListener(new BuildingChangeListener(sbP));
//			actionPanel.add(sbP);
			c.gridx = 4;
			c.gridy = 7;
			c.gridwidth = 2;
			c.weighty = 0.5;
//			c.fill = GridBagConstraints.HORIZONTAL;
//			c.anchor = GridBagConstraints.PAGE_START;
			break;
		case SPECIAL2:
			actionButton.setText(Msg.get("specBuildLabel"));
			c.gridx = 4;
			c.gridy = 8;
			c.gridwidth = 2;
			c.weighty = 0.5;
//			c.fill = GridBagConstraints.HORIZONTAL;
//			c.anchor = GridBagConstraints.PAGE_START;
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
		c.gridx = 3*x;
		c.gridy = y;
		c.gridwidth = 2;
		c.ipadx = c.ipady = 3;
		c.insets = new Insets(5,5,5,0);
		c.weightx = 0.6;
//		c.weightx = 0.2;
		parent.add(createPrefixPanel(materials, animal, otherAnimal), c);
		c.gridx = 3*x + 2;
		c.gridwidth = 1;
		c.insets = new Insets(5,0,5,5);
		parent.add(addSupplyPanel(action, button, extraP), c);
	}
	
	private static JPanel createPrefixPanel(Materials materials, Animal animal, Animal otherAnimal) {
		JPanel refillP = SwingUtils.createHorizontalPanel();
		refillP.add(Box.createHorizontalGlue());
		refillP.setBorder(BorderFactory.createMatteBorder(1,1,1,0,Color.GRAY));
		Animals animals = animal == null ? null : new Animals(animal, 1);
		JPanel main = SwingUtils.createResourcesPanel(materials, animals, SwingUtils.Y_AXIS);
		refillP.add(main);
		if (otherAnimal != null) {
			JPanel sub = SwingUtils.createHorizontalPanel();
			sub.add(SwingUtils.createLabel(" ("));
			sub.add(SwingUtils.createResourcesPanel(null, new Animals(otherAnimal, 1), SwingUtils.Y_AXIS));
			sub.add(SwingUtils.createLabel(")"));
			main.add(sub);
		}
		refillP.add(SwingUtils.createArrowLabel(Dir.E, true));
		return refillP;
	}
	
	private static JButton addSupplyPanel(Action action, JButton button, JComponent extraP) {
		JPanel actionP = SwingUtils.createHorizontalPanel();
		JPanel supplyP = SwingUtils.createResourcesPanel(action.getAccumulatedMaterials(), action.getAccumulatedAnimals(), SwingUtils.Y_AXIS);
		actionP.add(supplyP);
		if (extraP != null) {
			actionP.add(extraP);
		}
		button.add(actionP);
		action.addChangeListener(new ResourceChangeListener(supplyP));
		return button;
	}

	private static class ResourceChangeListener implements StateChangeListener {

		private JPanel materialPanel;

		public ResourceChangeListener(JPanel materialPanel) {
			this.materialPanel = materialPanel;
		}

		public void stateChanges(Action action) {
			SwingUtils.updateResourcePanel(materialPanel, action.getAccumulatedMaterials(), action.getAccumulatedAnimals(), true);
		}

	}

	/*private static class BuildingChangeListener implements StateChangeListener {

		private JPanel buildingPanel;

		public BuildingChangeListener(JPanel buildingPanel) {
			this.buildingPanel = buildingPanel;
		}

		public void stateChanges(Action action) {
			buildingPanel.removeAll();
			for (BuildingType b : GeneralSupply.getBuildingsLeft()) {
				buildingPanel.add(new JLabel(Images.getBuildingIcon(b, 40)));
			}

		}

	}*/

}
