package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.dill.agricola.ActionPerformer;
import com.dill.agricola.Game;
import com.dill.agricola.Main;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.enums.ChangeType;
import com.dill.agricola.model.enums.PlayerColor;

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

		setTitle("Agricola: All Creatures Big and Small");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mainPane = getContentPane();
		mainPane.setLayout(new BorderLayout(5, 5));

		buildMenu();
	}

	private void buildMenu() {
		MenuListener bl = new MenuListener(this);
		JMenuBar mb = new JMenuBar();
		JMenu fileM = new JMenu("Game");
		JMenuItem newI = new JMenuItem("New game");
		newI.setActionCommand(MenuCommand.NEW.toString());
		newI.addActionListener(bl);
		fileM.add(newI);
		JMenuItem exitI = new JMenuItem("Exit");
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
		statusL = new JLabel("Round: -");
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

	public void reset() {
		actionBoard.resetActions();
	}

	public void startRound(int roundNo) {
		statusL.setText("Round: " + roundNo + " / " + Game.ROUNDS);
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
