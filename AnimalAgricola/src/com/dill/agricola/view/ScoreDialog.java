package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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

	private static final DecimalFormat scoreFormat = new DecimalFormat("###.#");
	private static final Color MORE_COLOR = new Color(23, 133, 23);
	private static final Color BORDER_COLOR = Color.LIGHT_GRAY;

	public ScoreDialog() {
		setTitle(Msg.get("scoringTitle"));
		setIconImage(Images.createIcon("application-certificate", ImgSize.SMALL).getImage());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setFont(Fonts.TEXT_FONT.deriveFont(15f));
	}

	public void update(Player[] players, PlayerColor initialStartPlayer, boolean isFinal) {
		getContentPane().removeAll();

		JPanel p = new JPanel(new GridLayout(0, 3, 0, 0));
		// heading
		JPanel emptyP = new JPanel();
		emptyP.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, BORDER_COLOR));
		p.add(emptyP);
		for (Player player : players) {
			JLabel playerL = UiFactory.createLabel(Msg.get("player", Msg.get(player.toString().toLowerCase())));
			playerL.setFont(Fonts.TEXT_FONT.deriveFont(15f));
			playerL.setForeground(Color.WHITE);
			playerL.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
			JPanel playerP = UiFactory.createBorderPanel();
			playerP.setBackground(player.getColor().getRealColor());
			playerP.setOpaque(true);
			playerP.add(playerL);
			p.add(addBorder(playerP));
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

		JPanel mainP = UiFactory.createVerticalPanel();
		mainP.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
		mainP.add(p);

		if (blueTotal == redTotal) {
			mainP.add(Box.createVerticalStrut(5));
			JLabel bottomLabel = UiFactory.createLabel(Msg.get("tieBreakerMsg",
					Msg.get(initialStartPlayer.other().toString().toLowerCase())));
			mainP.add(bottomLabel);
		}

		mainP.add(Box.createVerticalStrut(15));
		// winner
		PlayerColor winner = blueTotal > redTotal ? PlayerColor.BLUE
				: blueTotal < redTotal ? PlayerColor.RED : initialStartPlayer.other();
		JLabel winnerLabel = UiFactory.createLabel(Msg.get(isFinal ? "winnerMsg" : "wouldBeWinnerMsg",
				Msg.get(winner.toString().toLowerCase())));
		winnerLabel.setFont(Fonts.TEXT_FONT.deriveFont(18f));
		winnerLabel.setOpaque(true);
		winnerLabel.setForeground(Color.WHITE);
		winnerLabel.setBackground(winner.getRealColor());
		winnerLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		mainP.add(winnerLabel);

		mainP.add(Box.createVerticalStrut(10));
		// ok
		JButton okBtn = UiFactory.createTextButton(Msg.get("okBtn"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ScoreDialog.this.setVisible(false);
			}
		});
		mainP.add(okBtn);

		getContentPane().add(mainP, BorderLayout.CENTER);

		pack();
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
