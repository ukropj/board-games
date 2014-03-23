package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.Images;
import com.dill.agricola.view.utils.UiFactory;


public class ScoreDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public ScoreDialog(Player[] players, PlayerColor initialStartingPlayer) {
		// TODO make reusable
		setTitle(Msg.get("scoring"));
		setIconImage(Images.createImage("icons/small/application-certificate"));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		buildScoringGrid(players, initialStartingPlayer);

		pack();
		setSize(300, 400);
	}

	private void buildScoringGrid(Player[] players, PlayerColor initialStartPlayer) {
		JPanel p = new JPanel(new GridLayout(8, 3, 4, 4));
		// heading
		p.add(new JPanel());
		for (Player player : players) {
			JLabel playerL = UiFactory.createLabel(Msg.get("player", Msg.get(player.toString().toLowerCase())));
			playerL.setOpaque(true);
			playerL.setBackground(player.getColor().getRealColor());
			p.add(addBorder(playerL));
		}
		// animals
		for (Animal a : Animal.values()) {
			p.add(addBorder(UiFactory.createAnimalLabel(a, 0, UiFactory.NO_NUMBER)));
			p.add(addBorder(UiFactory.createLabel(players[0].getAnimalScore(a) + " [" + players[0].getAnimal(a) + "]")));
			p.add(addBorder(UiFactory.createLabel(players[1].getAnimalScore(a) + " [" + players[1].getAnimal(a) + "]")));
		}
		// extensions
		p.add(addBorder(UiFactory.createLabel(AgriImages.getPurchasableIcon(Purchasable.EXTENSION))));
		p.add(addBorder(UiFactory.createLabel(String.valueOf(players[0].getExtensionsScore()))));
		p.add(addBorder(UiFactory.createLabel(String.valueOf(players[1].getExtensionsScore()))));
		// buildings
		p.add(addBorder(UiFactory.createLabel(AgriImages.getPurchasableIcon(Purchasable.BUILDING))));
		p.add(addBorder(UiFactory.createLabel(String.valueOf(players[0].getBuildingScore()))));
		p.add(addBorder(UiFactory.createLabel(String.valueOf(players[1].getBuildingScore()))));
		// total
		p.add(addBorder(UiFactory.createLabel(Msg.get("sum"))));
		float blueTotal = players[0].getScore();
		float redTotal = players[1].getScore();
		p.add(addBorder(UiFactory.createLabel(String.valueOf(blueTotal))));
		p.add(addBorder(UiFactory.createLabel(String.valueOf(redTotal))));

		// winner
		PlayerColor winner = blueTotal > redTotal ? PlayerColor.BLUE
				: blueTotal < redTotal ? PlayerColor.RED : initialStartPlayer.other();
		JLabel winnerLabel = UiFactory.createLabel(Msg.get("msgWinner", Msg.get(winner.toString().toLowerCase())));
		winnerLabel.setOpaque(true);
		winnerLabel.setBackground(winner.getRealColor());

		JPanel mainP = UiFactory.createBorderPanel(5, 5);
		mainP.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
		mainP.add(p, BorderLayout.CENTER);
		mainP.add(winnerLabel, BorderLayout.SOUTH);

		getContentPane().add(mainP, BorderLayout.CENTER);
	}

	private JComponent addBorder(JComponent component) {
		component.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		return component;
	}
}
