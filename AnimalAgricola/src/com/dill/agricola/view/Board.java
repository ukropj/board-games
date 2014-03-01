package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.dill.agricola.Game;
import com.dill.agricola.Main;
import com.dill.agricola.actions.ActionPerformer;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.ChangeType;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.UiFactory;
import com.dill.agricola.view.utils.AgriImages.ImgSize;

@SuppressWarnings("serial")
public class Board extends JFrame implements Observer{

	private final static boolean DEBUG_ON = true;

	private Game game;

	private final ActionPerformer ap = new ActionPerformer();

	private final Container mainPane;

	private JLabel statusL;
	private final PlayerBoard[] playerBoards;
	private ActionBoard actionBoard;
	private DebugPanel debugPanel;

	public Board() {
		playerBoards = new PlayerBoard[2];

		setTitle(Msg.get("gameTitle"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mainPane = getContentPane();
		mainPane.setLayout(new BorderLayout(5, 5));

		buildMenu();
	}

	private void buildMenu() {
		MenuListener bl = new MenuListener(this);
		JMenuBar mb = new JMenuBar();
		JMenu fileM = new JMenu(Msg.get("game"));
		JMenuItem newI = new JMenuItem(Msg.get("new"));
		newI.setActionCommand(MenuCommand.NEW.toString());
		newI.addActionListener(bl);
		fileM.add(newI);
		JMenuItem exitI = new JMenuItem(Msg.get("exit"));
		exitI.setActionCommand(MenuCommand.EXIT.toString());
		exitI.addActionListener(bl);
		exitI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
		fileM.add(exitI);
		mb.add(fileM);

		// JMenu devM = new JMenu("Dev");
		// JMenuItem debugI = new JCheckBoxMenuItem("Debug");
		// debugI.setSelected(DEBUG_ON);
		// debugI.setActionCommand(MenuCommand.DEBUG.toString());
		// debugI.addItemListener(bl);
		// devM.add(debugI);
		// mb.add(devM);

		setJMenuBar(mb);
	}

	public void init(Game game) {
		this.game = game;
		initStatusBar();
		initActionsBoard();
		initPlayerBoard(PlayerColor.BLUE, BorderLayout.WEST);
		initPlayerBoard(PlayerColor.RED, BorderLayout.EAST);
		if (Main.DEBUG) {
			initDebugPanel();
		}

		pack();
		setVisible(true);
		setLocationRelativeTo(null);
		// setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	private void initStatusBar() {
		statusL = UiFactory.createLabel(Msg.get("round", 0, Game.ROUNDS));
		mainPane.add(statusL, BorderLayout.NORTH);
	}

	private void initPlayerBoard(PlayerColor color, String direction) {
		Player player = game.getPlayer(color);
		player.addObserver(this);
		PlayerBoard playerBoard = new PlayerBoard(player);
		playerBoards[color.ordinal()] = playerBoard;
		mainPane.add(playerBoard, direction);
	}

	private void initActionsBoard() {
		actionBoard = new ActionBoard(game.getActions(), ap, game.getSubmitListener());
		mainPane.add(actionBoard, BorderLayout.CENTER);
	}

	private void initDebugPanel() {
		debugPanel = new DebugPanel();
		debugPanel.setVisible(DEBUG_ON);
		mainPane.add(debugPanel, BorderLayout.SOUTH);
	}

	public void start() {
		actionBoard.resetActions();
	}

	public void startRound(int roundNo) {
		statusL.setText(Msg.get("round", roundNo, Game.ROUNDS));
		actionBoard.initActions();
		if (Main.DEBUG && roundNo == 1) {
			actionBoard.initActions();
		}
	}

	public void startTurn(Player currentPlayer) {
		ap.setPlayer(currentPlayer);
		playerBoards[currentPlayer.getColor().ordinal()].setActive(true);
		playerBoards[currentPlayer.getColor().other().ordinal()].setActive(false);
		if (Main.DEBUG) {
			debugPanel.setCurrentPlayer(currentPlayer);			
		}
	}

	public void endRound() {
		playerBoards[0].setActive(true);
		playerBoards[1].setActive(true);
		actionBoard.clearActions();
		actionBoard.enableSubmitOnly();
	}

	public void showScoring(Player[] players, PlayerColor winner) {
		// TODO extract to separate class
		JDialog d = new JDialog(this, Msg.get("scoring"), true);
		JPanel p = new JPanel(new GridLayout(8, 3, 4, 4));
		// heading
		p.add(new JPanel());
		for (Player player : players) {
			JLabel playerL = UiFactory.createLabel(Msg.get("player", player.getColor().ordinal() + 1));
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
		p.add(addBorder(UiFactory.createLabel(AgriImages.getExtensionIcon(0))));
		p.add(addBorder(UiFactory.createLabel(String.valueOf(players[0].getExtensionsScore()))));
		p.add(addBorder(UiFactory.createLabel(String.valueOf(players[1].getExtensionsScore()))));
		// buildings
		p.add(addBorder(UiFactory.createLabel(AgriImages.getBuildingIcon(BuildingType.HALF_TIMBERED_HOUSE, ImgSize.SMALL))));
		p.add(addBorder(UiFactory.createLabel(String.valueOf(players[0].getBuildingScore()))));
		p.add(addBorder(UiFactory.createLabel(String.valueOf(players[1].getBuildingScore()))));
		// total
		p.add(addBorder(UiFactory.createLabel(Msg.get("sum"))));
		float blueTotal = players[0].getScore();
		float redTotal = players[1].getScore();
		p.add(addBorder(UiFactory.createLabel(String.valueOf(blueTotal))));
		p.add(addBorder(UiFactory.createLabel(String.valueOf(redTotal))));

		d.getContentPane().setLayout(new BorderLayout(5, 5));
		d.getContentPane().add(p, BorderLayout.CENTER);
		JLabel winnerLabel = UiFactory.createLabel(Msg.get("msgWinner", winner.ordinal() + 1));
		winnerLabel.setOpaque(true);
		winnerLabel.setBackground(winner.getRealColor());
		d.getContentPane().add(winnerLabel, BorderLayout.SOUTH);
		
		d.pack();
		d.setSize(300, 400);
		d.setVisible(true);
//		d.setLocation(getLocation());
		d.setLocationRelativeTo(null);
	}
	
	private JComponent addBorder(JComponent component) {
		component.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		return component;
	}

	public void startingPlayerChanged() {
		playerBoards[0].updatePlayer();
		playerBoards[1].updatePlayer();
	}
	
	private static enum MenuCommand {
		NEW, EXIT;
	}

	private static class MenuListener implements ActionListener, ItemListener {

		private final Board board;

		public MenuListener(Board board) {
			this.board = board;
		}

		public void actionPerformed(ActionEvent e) {
			MenuCommand command = MenuCommand.valueOf(e.getActionCommand());
			switch (command) {
			case NEW:
				board.game.start();
				break;
			case EXIT:
				System.exit(0);
				break;
			default:
				break;
			}
		}

		public void itemStateChanged(ItemEvent e) {
			JCheckBoxMenuItem mi = (JCheckBoxMenuItem) e.getItemSelectable();
			MenuCommand command = MenuCommand.valueOf(mi.getActionCommand());
			switch (command) {
			default:
				break;
			}
		}

	}

	public void update(Observable o, Object arg) {
		if (arg == ChangeType.FARM_RESIZE && ((getExtendedState() & JFrame.MAXIMIZED_BOTH) != JFrame.MAXIMIZED_BOTH)) {
			pack();
		}
	}


}
