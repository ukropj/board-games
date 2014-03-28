package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.support.Fonts;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.Images;
import com.dill.agricola.view.utils.UiFactory;


public class ScoreDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private final DecimalFormat scoreFormat = new DecimalFormat("###.#");
	private final Color MORE_COLOR = new Color(23, 133, 23);
	private final Color BORDER_COLOR = Color.LIGHT_GRAY;

	public ScoreDialog(Player[] players, PlayerColor initialStartingPlayer) {
		setTitle(Msg.get("scoring"));
		setIconImage(Images.createIcon("application-certificate", ImgSize.SMALL).getImage());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);

		buildScoringGrid(players, initialStartingPlayer);

		pack();
		setSize(300, 400);
	}

	private void buildScoringGrid(Player[] players, PlayerColor initialStartPlayer) {
		setFont(Fonts.TEXT_FONT.deriveFont(15f));
		JPanel p = new JPanel(new GridLayout(0, 3, 0, 0));
		// heading
		JPanel emptyP = new JPanel();
		emptyP.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, BORDER_COLOR));
		p.add(emptyP);
		for (Player player : players) {
			JLabel playerL = UiFactory.createLabel(Msg.get("player", Msg.get(player.toString().toLowerCase())));
			playerL.setFont(Fonts.TEXT_FONT.deriveFont(15f));
			playerL.setOpaque(true);
			playerL.setForeground(Color.WHITE);
			playerL.setBackground(player.getColor().getRealColor());
			p.add(addBorder(playerL));
		}
		// animals
		for (Animal a : Animal.values()) {
			addLine(p, UiFactory.createAnimalLabel(a, 0, UiFactory.NO_NUMBER), players[0].getAnimalScore(a), players[1].getAnimalScore(a), 
					String.valueOf(players[0].getAnimal(a)), String.valueOf(players[1].getAnimal(a)));
		}
		// extensions
		addLine(p, "", AgriImages.getPurchasableIcon(Purchasable.EXTENSION), players[0].getExtensionsScore(), players[1].getExtensionsScore());
		// buildings
		addLine(p, "", AgriImages.getPurchasableIcon(Purchasable.BUILDING), players[0].getBuildingScore(), players[1].getBuildingScore());
		// total
		float blueTotal = players[0].getScore();
		float redTotal = players[1].getScore();
		addLine(p, Msg.get("sum"), null, blueTotal, redTotal);

		JPanel mainP = UiFactory.createBorderPanel(5, 5);
		mainP.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
		mainP.add(p, BorderLayout.CENTER);
		
		if (blueTotal == redTotal) {
			JLabel bottomLabel = UiFactory.createLabel(Msg.get("msgTieBreaker", 
					Msg.get(initialStartPlayer.other().toString().toLowerCase())));
			mainP.add(bottomLabel, BorderLayout.SOUTH);
		}
		
		// winner
		PlayerColor winner = blueTotal > redTotal ? PlayerColor.BLUE
				: blueTotal < redTotal ? PlayerColor.RED : initialStartPlayer.other();
		JLabel winnerLabel = UiFactory.createLabel(Msg.get("msgWinner", 
				Msg.get(winner.toString().toLowerCase())));
		winnerLabel.setFont(Fonts.TEXT_FONT.deriveFont(18f));
		winnerLabel.setOpaque(true);
		winnerLabel.setForeground(Color.WHITE);
		winnerLabel.setBackground(winner.getRealColor());
		winnerLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));


		getContentPane().add(mainP, BorderLayout.CENTER);
		getContentPane().add(winnerLabel, BorderLayout.SOUTH);
	}

	private void addLine(JPanel p, String text, ImageIcon icon, float scoreBlue, float scoreRed) {
		addLine(p, UiFactory.createLabel(text, icon), scoreBlue, scoreRed, null, null);
	}

	private void addLine(JPanel p, JLabel label, float scoreBlue, float scoreRed, String extraBlue, String extraRed) {
		p.add(addBorder(label));
		JLabel blueL = UiFactory.createLabel(scoreFormat.format(scoreBlue) + (extraBlue != null ? " [" + extraBlue + "]" : ""));
		if (scoreBlue > scoreRed) {
			blueL.setForeground(MORE_COLOR);
		}
		p.add(addBorder(blueL));
		JLabel redL = UiFactory.createLabel(scoreFormat.format(scoreRed) + (extraRed != null ? " [" + extraRed + "]" : ""));
		if (scoreRed > scoreBlue) {
			redL.setForeground(MORE_COLOR);
		}
		p.add(addBorder(redL));
	}

	private JComponent addBorder(JComponent component) {
		component.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		return component;
	}
}
