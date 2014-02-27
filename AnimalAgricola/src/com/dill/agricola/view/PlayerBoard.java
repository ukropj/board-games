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
import com.dill.agricola.view.utils.SwingUtils;

@SuppressWarnings("serial")
public class PlayerBoard extends JPanel implements Observer {

	private final Player player;
	private final Farm farm;

	private JLabel playerLabel;
	private JLabel firstLabel;
	private FarmPanel farmPanel;
	private Map<Material, JLabel> supply = new EnumMap<Material, JLabel>(Material.class);
	private Map<Animal, JLabel> animalSupply = new EnumMap<Animal, JLabel>(Animal.class);

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
		initAbcPanel();

		updatePlayer();
		updateFarm();
	}

	private void initFarmPanel() {
		farmPanel = new FarmPanel(player);
		// farmPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		//		addBorder(farmPanel);
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		c.gridwidth = 3;
		//		c.fill = GridBagConstraints.BOTH;
		add(farmPanel, c);
		//		add(farmPanel, BorderLayout.EAST);
	}

	private void initTopPanel() {
		PlayerColor color = player.getColor();
		playerLabel = SwingUtils.createLabel(Msg.get("player", color.ordinal() + 1));
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

	private void initFirstPanel() {
		JPanel p = new JPanel();
		p.setPreferredSize(new Dimension(50, 0));
		firstLabel = SwingUtils.createLabel(AgriImages.getFirstTokenIcon(player.getColor().ordinal(), ImgSize.BIG));
		firstLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		firstLabel.setVisible(false);
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
		JPanel materials = SwingUtils.createFlowPanel(15, 0);
		for (Material m : Material.values()) {
			JLabel l = SwingUtils.createMaterialLabel(m, 0, SwingUtils.ICON_FIRST);
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
		JPanel animals = SwingUtils.createFlowPanel(15, 0);
		for (Animal a : Animal.values()) {
			JLabel l = SwingUtils.createAnimalLabel(a, 0, SwingUtils.ICON_FIRST);
			animals.add(l);
			animalSupply.put(a, l);
		}
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 0, 5, 0);
		c.gridx = 1;
		c.gridy = 3;
		c.fill = GridBagConstraints.BOTH;
		//		c.anchor = GridBagConstraints.PAGE_START;
		add(animals, c);
	}

	private void initAbcPanel() {
		JLabel l = new JLabel();
		l.setPreferredSize(new Dimension(50, 0));
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 0, 5, 0);
		c.gridx = 2;
		c.gridy = 2;
		c.gridheight = 2;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		add(l, c);
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
		for (Material m : Material.values()) {
			supply.get(m).setText(String.valueOf(player.getMaterial(m)));
		}
		for (Animal a : Animal.values()) {
			animalSupply.get(a).setText(String.valueOf(player.getAnimal(a)));
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
