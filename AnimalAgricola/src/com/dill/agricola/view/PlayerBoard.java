package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dill.agricola.Game.Phase;
import com.dill.agricola.actions.ActionPerformer;
import com.dill.agricola.model.Farm;
import com.dill.agricola.model.Fencer;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ChangeType;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.support.Fonts;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.UiFactory;

public class PlayerBoard extends JPanel implements Observer {
	private static final long serialVersionUID = 1L;

	private final Player player;
	private final Farm farm;
	private final ActionPerformer ap;

	private JPanel mainPanel;

	private JLabel playerLabel;
	private FarmPanel farmPanel;

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

		updateFarm();
	}

	private void buildTopPanel() {
		PlayerColor color = player.getColor();
		playerLabel = UiFactory.createLabel(Msg.get("player", Msg.get(color.toString().toLowerCase())));
		playerLabel.setFont(Fonts.FARM_NAME);
		playerLabel.setPreferredSize(new Dimension(0, 30));
		playerLabel.setForeground(color.getRealColor());
		add(playerLabel, BorderLayout.NORTH);
	}

	private void buildFarmPanel(ActionListener submitListener) {
		farmPanel = new FarmPanel(player, ap, submitListener);

		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.insets = new Insets(0, 0, 0, 0);
		c.anchor = GridBagConstraints.CENTER;
		mainPanel.add(farmPanel, c);
	}

	public void activate(Phase phase) {
		farmPanel.setActive(true, phase);
	}
	
	public void deactivate() {
		farmPanel.setActive(false, null);
	}

	public void updateFarm() {
		Fencer.calculateFences(farm);
		farmPanel.updateControls();
		farmPanel.paintImmediately(farmPanel.getVisibleRect());
	}
	
	public void reset() {
		farmPanel.revalidate();
		farmPanel.updateComponentPosition();
		updateFarm();
	}

	public void update(Observable o, Object arg) {
		if (arg == ChangeType.FARM_RESIZE || arg == ChangeType.UNDO || arg == ChangeType.REDO) {
			farmPanel.revalidate();
			farmPanel.updateComponentPosition();
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
