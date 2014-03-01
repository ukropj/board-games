package com.dill.agricola.view;

import java.awt.Color;
import java.awt.Dimension;
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
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.UiFactory;

@SuppressWarnings("serial")
public class PlayerBoard extends JPanel implements Observer {

	private final Player player;
	private final Farm farm;

	private JLabel playerLabel;
	private FarmPanel farmPanel;
	private JLabel firstLabel;
	private Map<Material, JLabel> supply = new EnumMap<Material, JLabel>(Material.class);
	private Map<Animal, JLabel> animalSupply = new EnumMap<Animal, JLabel>(Animal.class);
	private JLabel[] workerLabels = new JLabel[Player.MAX_WORKERS];

	public PlayerBoard(Player player) {
		this.player = player;
		this.farm = player.getFarm();
		player.addObserver(this);

		//		setLayout(new BorderLayout());
		setLayout(new GridBagLayout());
		setBorder(new LineBorder(Color.GRAY, 1));

		initTopPanel();
		initFarmPanel();
		initFirstPanel();
		initMaterialPanel();
		initAnimalPanel();
		initWorkerPanel();

		updatePlayer();
		updateFarm();
	}

	private void initTopPanel() {
		PlayerColor color = player.getColor();
		playerLabel = UiFactory.createLabel(Msg.get("player", color.ordinal() + 1));
		playerLabel.setFont(playerLabel.getFont().deriveFont(20.0f));
		playerLabel.setOpaque(true);

		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.ipady = 10;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 3;
		add(playerLabel, c);
	}
	
	private void initFarmPanel() {
		farmPanel = new FarmPanel(player);
		// farmPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		//		addBorder(farmPanel);
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		c.gridwidth = 3;
		add(farmPanel, c);
	}

	private void initFirstPanel() {
		JPanel p = new JPanel();
		p.setPreferredSize(new Dimension(50, 0));
		firstLabel = UiFactory.createLabel(AgriImages.getFirstTokenIcon(player.getColor().ordinal(), ImgSize.BIG));
		p.add(firstLabel);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 2;
		c.insets = new Insets(5, 5, 5, 0);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		add(p, c);
	}

	private void initMaterialPanel() {
		JPanel materials = UiFactory.createFlowPanel(15, 0);
		for (Material m : Material.values()) {
			JLabel l = UiFactory.createMaterialLabel(m, 0, UiFactory.ICON_FIRST);
			materials.add(l);
			supply.put(m, l);
		}

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 0, 5, 0);
		c.gridx = 1;
		c.gridy = 2;
		c.fill = GridBagConstraints.BOTH;
		//		c.weightx = 0;
		add(materials, c);
	}

	private void initAnimalPanel() {
		JPanel animals = UiFactory.createFlowPanel(15, 0);
		for (Animal a : Animal.values()) {
			JLabel l = UiFactory.createAnimalLabel(a, 0, UiFactory.ICON_FIRST);
			animals.add(l);
			animalSupply.put(a, l);
		}
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 0, 5, 0);
		c.gridx = 1;
		c.gridy = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.PAGE_START;
		add(animals, c);
	}

	private void initWorkerPanel() {
		JPanel workers = UiFactory.createVerticalPanel();
		workers.setPreferredSize(new Dimension(50, 0));
		for (int i = 0; i < workerLabels.length; i++) {
			workerLabels[i] = UiFactory.createLabel(AgriImages.getWorkerIcon(player.getColor().ordinal()));
			workers.add(workerLabels[i]);
		}
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 0, 5, 5);
		c.gridx = 2;
		c.gridy = 2;
		c.gridheight = 2;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;
		add(workers, c);
	}

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
		firstLabel.setVisible(player.isStarting());
		for (Material m : Material.values()) {
			supply.get(m).setText(String.valueOf(player.getMaterial(m)));
		}
		for (Animal a : Animal.values()) {
			animalSupply.get(a).setText(String.valueOf(player.getAnimal(a)));
		}

		for (int i = 0; i < workerLabels.length; i++) {
			workerLabels[i].setVisible(player.getWorkers() > i);
		}
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
