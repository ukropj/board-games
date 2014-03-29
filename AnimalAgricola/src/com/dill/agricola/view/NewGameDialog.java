package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.dill.agricola.Main;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.Images;
import com.dill.agricola.view.utils.UiFactory;

public class NewGameDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;

	private JLabel startPlayerLabel;

	PlayerColor startingPlayer = PlayerColor.BLUE;
	boolean done = false;

	public NewGameDialog(JFrame parent) {
		super(parent);
		setTitle(Msg.get("newGameTitle"));
		setIconImage(Images.createIcon("document-new", ImgSize.SMALL).getImage());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);

		buildOptions();
		pack();
		setLocationRelativeTo(parent);

		if (!Main.DEBUG) {
			setVisible(true);
		} else {
			done = true;
			dispose();
		}
	}

	private void buildOptions() {
		JPanel main = UiFactory.createBorderPanel(5, 5);
		main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		getContentPane().add(main);

		JPanel startPlayerP = UiFactory.createBorderPanel(5, 0);
		startPlayerP.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(Msg.get("startPlayer")),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)
				));
		startPlayerLabel = UiFactory.createLabel(AgriImages.getFirstTokenIcon(0, ImgSize.BIG));
		startPlayerLabel.setOpaque(true);
		startPlayerLabel.setBackground(PlayerColor.BLUE.getRealColor());
		startPlayerP.add(startPlayerLabel, BorderLayout.LINE_START);

		JRadioButton blueStarting = new JRadioButton(Msg.get("player", Msg.get("blue")), true);
		blueStarting.setActionCommand(OptionCommand.BLUE_STARTS.toString());
		blueStarting.addActionListener(this);
		JRadioButton redStarting = new JRadioButton(Msg.get("player", Msg.get("red")));
		redStarting.setActionCommand(OptionCommand.RED_STARTS.toString());
		redStarting.addActionListener(this);
		ButtonGroup startButtons = new ButtonGroup();
		startButtons.add(blueStarting);
		startButtons.add(redStarting);

		JPanel b = UiFactory.createVerticalPanel();
		b.add(blueStarting);
		b.add(redStarting);
		startPlayerP.add(b, BorderLayout.CENTER);
		main.add(startPlayerP, BorderLayout.CENTER);

		JButton submitButton = new JButton(Msg.get("startGameBtn"));
		submitButton.setActionCommand(OptionCommand.SUBMIT.toString());
		submitButton.addActionListener(this);
		JPanel submitP = UiFactory.createFlowPanel();
		submitP.add(submitButton);

		main.add(submitP, BorderLayout.SOUTH);
	}

	private static enum OptionCommand {
		BLUE_STARTS, RED_STARTS, SUBMIT
	}

	public void actionPerformed(ActionEvent e) {
		OptionCommand o = OptionCommand.valueOf(e.getActionCommand());
		switch (o) {
		case BLUE_STARTS:
			startingPlayer = PlayerColor.BLUE;
			startPlayerLabel.setBackground(startingPlayer.getRealColor());
			startPlayerLabel.setIcon(AgriImages.getFirstTokenIcon(startingPlayer.ordinal(), ImgSize.BIG));
			break;
		case RED_STARTS:
			startingPlayer = PlayerColor.RED;
			startPlayerLabel.setBackground(startingPlayer.getRealColor());
			startPlayerLabel.setIcon(AgriImages.getFirstTokenIcon(startingPlayer.ordinal(), ImgSize.BIG));
			break;
		case SUBMIT:
			done = true;
			setVisible(false);
			dispose();
			break;
		}
	}

	public boolean isDone() {
		return done;
	}

	public PlayerColor getStartingPlayer() {
		return startingPlayer;
	}

}
