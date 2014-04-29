package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

public class NewGameDialog extends JDialog implements ActionListener, ItemListener {
	private static final long serialVersionUID = 1L;

	private JLabel startPlayerLabel;

	private PlayerColor startingPlayer = PlayerColor.BLUE;
	private boolean useMoreBuildings = true;
	private boolean useEvenMoreBuildings = false;
	private boolean done = false;

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
		JPanel main = UiFactory.createVerticalPanel();
		main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		getContentPane().add(main);

		main.add(buildStartPlayerPanel());
		if (Main.MORE_BUILDINGS || Main.EVEN_MORE_BUILDINGS) {
			main.add(buildExpansionPanel());
		}

		JButton submitButton = UiFactory.createTextButton(Msg.get("startGameBtn"), this);
		submitButton.setActionCommand(OptionCommand.SUBMIT.toString());
		JPanel submitP = UiFactory.createFlowPanel();
		submitP.add(submitButton);

		main.add(Box.createVerticalStrut(5));
		main.add(submitP);
	}

	private JPanel buildStartPlayerPanel() {
		JPanel startPlayerP = UiFactory.createBorderPanel(5, 0);
		startPlayerP.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(Msg.get("startPlayer")),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)
				));
		startPlayerLabel = UiFactory.createLabel(AgriImages.getFirstTokenIcon(0, ImgSize.BIG));
		startPlayerLabel.setOpaque(true);
		startPlayerLabel.setBackground(PlayerColor.BLUE.getRealColor());
		startPlayerP.add(startPlayerLabel, BorderLayout.LINE_START);

		JRadioButton blueStarting = new JRadioButton(Msg.get("player", Msg.get("blue")), startingPlayer == PlayerColor.BLUE);
		blueStarting.setActionCommand(OptionCommand.BLUE_STARTS.toString());
		blueStarting.addActionListener(this);
		JRadioButton redStarting = new JRadioButton(Msg.get("player", Msg.get("red")), startingPlayer == PlayerColor.RED);
		redStarting.setActionCommand(OptionCommand.RED_STARTS.toString());
		redStarting.addActionListener(this);
		ButtonGroup startButtons = new ButtonGroup();
		startButtons.add(blueStarting);
		startButtons.add(redStarting);

		JPanel b = UiFactory.createVerticalPanel();
		b.add(blueStarting);
		b.add(redStarting);
		startPlayerP.add(b, BorderLayout.CENTER);
		return startPlayerP;
	}

	private JPanel buildExpansionPanel() {
		JPanel expP = UiFactory.createBorderPanel(5, 0);
		expP.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(Msg.get("expansions")),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)
				));
		JCheckBox moreBuildings = new JCheckBox(Msg.get("moreBuildings"), useMoreBuildings);
		moreBuildings.setActionCommand(OptionCommand.MORE_BUILDINGS.toString());
		moreBuildings.addItemListener(this);
		moreBuildings.setEnabled(Main.MORE_BUILDINGS);
		JCheckBox evenMoreBuildings = new JCheckBox(Msg.get("evenMoreBuildings"), useEvenMoreBuildings);
		evenMoreBuildings.setActionCommand(OptionCommand.EVEN_MORE_BUILDINGS.toString());
		evenMoreBuildings.addItemListener(this);
		evenMoreBuildings.setEnabled(Main.EVEN_MORE_BUILDINGS);
		JPanel b = UiFactory.createVerticalPanel();
		b.add(moreBuildings);
		b.add(evenMoreBuildings);
		expP.add(b, BorderLayout.CENTER);
		return expP;
	}

	private static enum OptionCommand {
		BLUE_STARTS, RED_STARTS, MORE_BUILDINGS, EVEN_MORE_BUILDINGS, SUBMIT
	}

	public void actionPerformed(ActionEvent e) {
		OptionCommand command = OptionCommand.valueOf(e.getActionCommand());
		switch (command) {
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
		default:
			break;
		}
	}

	public void itemStateChanged(ItemEvent e) {
		JCheckBox source = (JCheckBox) e.getItemSelectable();
		OptionCommand command = OptionCommand.valueOf(source.getActionCommand());
		boolean selected = e.getStateChange() == ItemEvent.SELECTED;
		switch (command) {
		case MORE_BUILDINGS:
			useMoreBuildings = selected;
			break;
		case EVEN_MORE_BUILDINGS:
			useEvenMoreBuildings = selected;
			break;
		default:
			break;
		}
	}

	public boolean isDone() {
		return done;
	}

	public PlayerColor getStartingPlayer() {
		return startingPlayer;
	}

	public boolean getUseMoreBuildings() {
		return useMoreBuildings;
	}

	public boolean getUseEvenMoreBuildings() {
		return useEvenMoreBuildings;
	}

}
