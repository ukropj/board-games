package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.dill.agricola.Game;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.support.Fonts;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.UiFactory;

public class ScorePanel extends JScrollPane {
	private static final long serialVersionUID = 1L;

	private static final DecimalFormat scoreFormat = new DecimalFormat("###.#");
	private static final Color MORE_COLOR = new Color(23, 133, 23);
	private static final Color BORDER_COLOR = Color.LIGHT_GRAY;
	private static final Font SMALLER_FONT = Fonts.TEXT_FONT.deriveFont(14f);
	private static final Font BIGGER_FONT = Fonts.TEXT_FONT.deriveFont(16f);
	public static final Font BIGGER_NUMBER = Fonts.TEXT_FONT_BOLD.deriveFont(20f);

	private final JPanel scoringP;

	private final Game game;

	public ScorePanel(Game game) {
		this.game = game;
		setFont(SMALLER_FONT);
		setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
		setBorder(null);
		JPanel p = new JPanel();
		setViewportView(p);
		p.setLayout(new BorderLayout(0, 10));
		p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		scoringP = UiFactory.createBorderPanel();
		p.add(scoringP, BorderLayout.NORTH);
	}

	public void updateScoring() {
		scoringP.removeAll();

		Player[] players = game.getPlayers();
		JPanel p = new JPanel(new GridLayout(0, 3, 0, 0));
//		p.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		// heading
		JPanel emptyP = new JPanel();
		emptyP.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, BORDER_COLOR));
		p.add(emptyP);
		for (Player player : players) {
			JLabel playerL = UiFactory.createLabel(Msg.get("player", Msg.get(player.toString().toLowerCase())));
			playerL.setFont(BIGGER_FONT);
			playerL.setForeground(player.getColor().getRealColor());
			playerL.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
			JPanel playerP = UiFactory.createBorderPanel();
			playerP.add(playerL);
			p.add(addBorder(playerP));
		}
		// animals
		for (Animal a : Animal.values()) {
			String animalName = a.getName(true);
			if (!"de".equals(Msg.getLocaleName())) {
				animalName = animalName.toLowerCase();
			}
			addLine(p, UiFactory.createAnimalLabel(a, 0, UiFactory.NO_NUMBER), Msg.get("pointsFor", animalName),
					players[0].getAnimalScore(a), players[1].getAnimalScore(a),
					String.valueOf(players[0].getAnimal(a)), String.valueOf(players[1].getAnimal(a)));
		}
		// extensions
		addLine(p, "", AgriImages.getPurchasableIcon(Purchasable.EXTENSION), Msg.get("pointsForExts"), players[0].getExtensionsScore(),
				players[1].getExtensionsScore());
		// buildings
		addLine(p, "", AgriImages.getPurchasableIcon(Purchasable.BUILDING), Msg.get("pointsForBuildings"), players[0].getBuildingScore(),
				players[1].getBuildingScore());
		// total
		float blueTotal = players[0].getScore();
		float redTotal = players[1].getScore();
		addLine(p, Msg.get("sum"), null, Msg.get("pointsFinal"), blueTotal, redTotal);
		for (int i = p.getComponentCount() - 1, j = 0; j < players.length + 1; i--, j++) {
			p.getComponent(i).setFont(BIGGER_NUMBER);
		}

		JPanel mainP = UiFactory.createVerticalPanel();
		mainP.add(p);
		// tie breaker
		if (blueTotal == redTotal) {
			mainP.add(Box.createVerticalStrut(5));
			JLabel bottomLabel = UiFactory.createLabel(Msg.get("tieBreakerMsg",
					Msg.get(game.getInitialStartPlayer().other().toString().toLowerCase())));
			mainP.add(bottomLabel);
		}
		mainP.add(Box.createVerticalStrut(15));
		// winner
		PlayerColor winner = blueTotal > redTotal ? PlayerColor.BLUE
				: blueTotal < redTotal ? PlayerColor.RED : game.getInitialStartPlayer().other();
		JLabel winnerLabel = UiFactory.createLabel(Msg.get(game.isEnded() ? "winnerMsg" : "wouldBeWinnerMsg",
				Msg.get(winner.toString().toLowerCase())));
		winnerLabel.setFont(BIGGER_FONT);
		winnerLabel.setForeground(winner.getRealColor());
		mainP.add(winnerLabel);

		scoringP.add(mainP, BorderLayout.CENTER);
	}

	private void addLine(JPanel p, String text, ImageIcon icon, String tooltip, float scoreBlue, float scoreRed) {
		addLine(p, UiFactory.createLabel(text, icon), tooltip, scoreBlue, scoreRed, null, null);
	}

	private void addLine(JPanel p, JLabel lineLabel, String tooltip, float scoreBlue, float scoreRed, String extraBlue, String extraRed) {
		lineLabel.setToolTipText(tooltip);
		p.add(addBorder(lineLabel));
		JLabel blueL = UiFactory.createLabel(scoreFormat.format(scoreBlue) + (extraBlue != null ? " [" + extraBlue + "]" : ""));
		blueL.setToolTipText(tooltip);
		if (scoreBlue > scoreRed) {
			blueL.setForeground(MORE_COLOR);
		}
		p.add(addBorder(blueL));

		JLabel redL = UiFactory.createLabel(scoreFormat.format(scoreRed) + (extraRed != null ? " [" + extraRed + "]" : ""));
		redL.setToolTipText(tooltip);
		if (scoreRed > scoreBlue) {
			redL.setForeground(MORE_COLOR);
		}
		p.add(addBorder(redL));
	}

	private <T extends JComponent> T addBorder(T component) {
		component.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1),
				BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		return component;
	}

}
