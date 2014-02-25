package com.dill.agricola.view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.EnumMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.dill.agricola.model.Farm;
import com.dill.agricola.model.Fencer;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.enums.Animal;
import com.dill.agricola.model.enums.Material;
import com.dill.agricola.model.enums.PlayerColor;
import com.dill.agricola.model.enums.Purchasable;

@SuppressWarnings("serial")
public class PlayerBoard extends JPanel implements Observer {

	private final Player player;
	private final Farm farm;

	private JLabel playerLabel;
	private JLabel firstLabel;
	private FarmPanel farmPanel;
	private Map<Material, JLabel> supply = new EnumMap<Material, JLabel>(Material.class);
	private Map<Animal, JLabel> animalSupply = new EnumMap<Animal, JLabel>(Animal.class);
	private Map<Purchasable, JLabel> unused = new EnumMap<Purchasable, JLabel>(Purchasable.class);

	public PlayerBoard(Player player) {
		this.player = player;
		this.farm = player.getFarm();
		player.addObserver(this);

//		setLayout(new BorderLayout());
		setLayout(new GridBagLayout());
		setBorder(new LineBorder(Color.GRAY, 1));

		initFarmPanel();
		initSupplyPanel();
		initPurchasePanel();

		updatePlayer();
		updateFarm();
	}

	private void initFarmPanel() {
		farmPanel = new FarmPanel(player);
		// farmPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//		addBorder(farmPanel);
		GridBagConstraints c=  new GridBagConstraints();
		c.gridy = 1;
//		c.fill = GridBagConstraints.BOTH;
		add(farmPanel, c);
//		add(farmPanel, BorderLayout.EAST);
	}

	private void initPurchasePanel() {
		JPanel panel = SwingUtils.createFlowPanel(5, 0);
//		addBorder(panel);
		PlayerColor color = player.getColor();
		playerLabel = new JLabel("P" + (color.ordinal() + 1));
		playerLabel.setOpaque(true);
		panel.add(playerLabel);

		for (Purchasable p : Purchasable.values()) {
			JLabel l = new JLabel(p.toString());
			panel.add(l);
			unused.put(p, l);
		}
		
		firstLabel = SwingUtils.createLabel(Images.getFirstTokenIcon(player.getColor().ordinal(), 20));
		firstLabel.setVisible(false);
		panel.add(firstLabel, 0);
		
		GridBagConstraints c=  new GridBagConstraints();
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);
//		c.ipady = c.ipadx = 10;
		c.fill = GridBagConstraints.BOTH;
		
//		add(panel, BorderLayout.NORTH);
		add(panel, c);
	}

	private void initSupplyPanel() {
//		JPanel panel = SwingUtils.createBorderPanel(0, 3);
//		addBorder(panel);
		GridBagConstraints c=  new GridBagConstraints();

		JPanel materials = SwingUtils.createFlowPanel(20, 0);
		for (Material m : Material.values()) {
			JLabel l = SwingUtils.createMaterialLabel(m, 0, SwingUtils.ICON_FIRST);
			materials.add(l);
			supply.put(m, l);
		}
		c.gridy = 2;
		c.insets = new Insets(5, 5, 5, 5);
//		c.ipady = c.ipadx = 10;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(materials, c);
//		panel.add(materials, BorderLayout.NORTH);
		JPanel animals = SwingUtils.createFlowPanel(20, 0);
		for (Animal a : Animal.values()) {
			JLabel l = SwingUtils.createAnimalLabel(a, 0, SwingUtils.ICON_FIRST);
			animals.add(l);
			animalSupply.put(a, l);
		}
		c.anchor = GridBagConstraints.PAGE_START;
		c.weighty = 1;
		c.gridy = 3;
		add(animals, c);
//		panel.add(animals, BorderLayout.SOUTH);
		
//		add(panel, BorderLayout.SOUTH);
	}

//	private void addBorder(JPanel panel) {
//		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 1), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
//	}

	public void setActive(boolean active) {
		Color c = player.getColor().getRealColor();
		if (active) {
			playerLabel.setForeground(Color.WHITE);
			playerLabel.setBackground(c);
		} else {
			playerLabel.setForeground(c);
			playerLabel.setBackground(null);
		}
		farmPanel.setActive(active);
		updatePlayer();
	}

	public void updatePlayer() {
		for (Material m : Material.values()) {
			supply.get(m).setText(String.valueOf(player.getMaterial(m)));
		}
		for (Animal a : Animal.values()) {
			animalSupply.get(a).setText(String.valueOf(player.getAnimal(a)));
		}
		for (Purchasable p : Purchasable.values()) {
			int count = farm.getUnused(p);
			JLabel l = unused.get(p);
			l.setText(p + ": " + count);
			l.setForeground(count > 0 ? Color.RED : Color.GRAY);
		}
		firstLabel.setVisible(player.isStarting());
	}

	private void updateFarm() {
		Fencer.calculateFences(farm);
		farmPanel.paintImmediately(farmPanel.getVisibleRect());
	}

	public void update(Observable o, Object arg) {
		updatePlayer();
		updateFarm();
	}

}
