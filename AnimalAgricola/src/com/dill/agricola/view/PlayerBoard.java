package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.EnumMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dill.agricola.actions.ActionPerformer;
import com.dill.agricola.model.Farm;
import com.dill.agricola.model.Fencer;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.ChangeType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.support.Fonts;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.UiFactory;

public class PlayerBoard extends JPanel implements Observer {
	private static final long serialVersionUID = 1L;

	private final Player player;
	private final Farm farm;
	private final ActionPerformer ap;

	private JPanel mainPanel;

	private JLabel playerLabel;
	private FarmPanel farmPanel;
	private JLabel firstLabel;
	private final Map<Material, JLabel> supply = new EnumMap<Material, JLabel>(Material.class);
	private final Map<Animal, JLabel> animalSupply = new EnumMap<Animal, JLabel>(Animal.class);
	private final JLabel[] workerLabels = new JLabel[Player.MAX_WORKERS];

	public PlayerBoard(Player player, ActionPerformer ap, ActionListener submitListener) {
		this.player = player;
		this.farm = player.getFarm();
		this.ap = ap;
		player.addObserver(this);

		setLayout(new BorderLayout());

		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		add(mainPanel, BorderLayout.CENTER);

		buildTopPanel();
		buildFarmPanel(submitListener);
		buildFirstPanel();
		buildMaterialPanel();
		buildAnimalPanel();
		buildWorkerPanel();

		updatePlayer();
		updateFarm();
	}

	private void buildTopPanel() {
		PlayerColor color = player.getColor();
		playerLabel = UiFactory.createLabel(Msg.get("player", Msg.get(color.toString().toLowerCase())));
		playerLabel.setFont(Fonts.TEXT_BIG);
		playerLabel.setOpaque(true);
		playerLabel.setForeground(color.getRealColor());
		add(playerLabel, BorderLayout.NORTH);
	}

	private void buildFarmPanel(ActionListener submitListener) {
		farmPanel = new FarmPanel(player, ap, submitListener);

		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		c.gridwidth = 3;
		c.weightx = 1.0;
		c.insets = new Insets(0, 0, 10, 0);
		c.anchor = GridBagConstraints.PAGE_START;
		mainPanel.add(farmPanel, c);
	}

	private void buildFirstPanel() {
		JPanel first = new JPanel();
		first.setPreferredSize(new Dimension(50, 0));
		firstLabel = UiFactory.createLabel(AgriImages.getFirstTokenIcon(player.getColor().ordinal(), ImgSize.BIG));
		first.add(firstLabel);
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 0);
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 2;
		c.weightx = c.weighty = 1.0;
		c.anchor = GridBagConstraints.LINE_END;
		c.fill = GridBagConstraints.VERTICAL;
//		first.setOpaque(true);
//		first.setBackground(Color.GREEN);
		mainPanel.add(first, c);
	}

	private void buildWorkerPanel() {
		JPanel workers = UiFactory.createVerticalPanel();
		workers.setPreferredSize(new Dimension(50, 0));
		for (int i = 0; i < workerLabels.length; i++) {
			workerLabels[i] = UiFactory.createLabel(AgriImages.getWorkerIcon(player.getColor()));
			workers.add(workerLabels[i]);
		}
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 0, 5, 5);
		c.gridx = 2;
		c.gridy = 2;
		c.gridheight = 2;
		c.weightx = c.weighty = 1.0;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.VERTICAL;
		mainPanel.add(workers, c);
	}

	private void buildMaterialPanel() {
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
		mainPanel.add(materials, c);
	}

	private void buildAnimalPanel() {
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
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0;
		mainPanel.add(animals, c);
	}

	public void setActive(boolean active, boolean breeding) {
		Color c = player.getColor().getRealColor();
		if (active) {
			playerLabel.setForeground(Color.WHITE);
			playerLabel.setBackground(c);
		} else {
			playerLabel.setForeground(c);
			playerLabel.setBackground(null);
		}
		farmPanel.setActive(active, breeding);
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
		farmPanel.updateButtons();
	}

	public void update(Observable o, Object arg) {
		updatePlayer();
		if (arg == ChangeType.FARM_RESIZE) {
			farmPanel.revalidate();
		}
		updateFarm();

//		if (arg == ChangeType.FARM_CLICK || arg == ChangeType.FARM_ANIMALS) {
//			updateControls();
//		}
//		if (arg == ChangeType.FARM_ANIMALS) {
//			updateFinishLabel();
//		}
//		if (arg == ChangeType.ACTION_DONE) {
//			updateActions();
//			submitListener.actionPerformed(null);
//		}
	}

}
