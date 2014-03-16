package com.dill.agricola.view;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import com.dill.agricola.Game;
import com.dill.agricola.Main;
import com.dill.agricola.actions.ActionPerformer;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.UiFactory;

@SuppressWarnings("serial")
public class Board extends JFrame {

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
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		mainPane = getContentPane();
		mainPane.setLayout(new GridBagLayout());

		buildMenu();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				endGame();
			}
		});
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
		initPlayerBoard(PlayerColor.BLUE, 0);
		initPlayerBoard(PlayerColor.RED, 2);
	}

	private void initStatusBar() {
		statusL = UiFactory.createLabel(Msg.get("round", 0, Game.ROUNDS));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.BOTH;
		mainPane.add(statusL, c);
	}

	private void initPlayerBoard(PlayerColor color, int x) {
		Player player = game.getPlayer(color);
		PlayerBoard playerBoard = new PlayerBoard(player, ap);
		playerBoards[color.ordinal()] = playerBoard;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = x;
		c.gridy = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;

		mainPane.add(new JScrollPane(playerBoard), c);

		player.addObserver(actionBoard);
	}

	private void initActionsBoard() {
		actionBoard = new ActionBoard(game.getActions(), ap, game.getSubmitListener());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;

		mainPane.add(actionBoard, c);
	}

	public void buildDebugPanel(Player[] players) {
		debugPanel = new DebugPanel(players);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.BOTH;
		mainPane.add(debugPanel, c);
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
		actionBoard.updateActions();
		if (Main.DEBUG) {
			debugPanel.setCurrentPlayer(currentPlayer.getColor());
		}
	}

	public void endRound() {
		playerBoards[0].setActive(true);
		playerBoards[1].setActive(true);
		actionBoard.clearActions();
		ap.setPlayer(null);
		actionBoard.enableFinishOnly();
	}

	public void showScoring(Player[] players, PlayerColor initialStartingPlayer) {
		ScoreDialog sd = new ScoreDialog(players, initialStartingPlayer);
		sd.setVisible(true);
//		sd.setLocation(getLocation());
		sd.setLocationRelativeTo(null);
	}

	public void startingPlayerChanged() {
		playerBoards[0].updatePlayer();
		playerBoards[1].updatePlayer();
	}
	
	public void endGame() {
		if (Main.DEBUG
				|| !game.isStarted()
				|| UiFactory.showQuestionDialog(Msg.get("endGame"), Msg.get("gameInProgress"))) {
			System.exit(0);
		}
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
				if (!board.game.isStarted()
						|| UiFactory.showQuestionDialog(Msg.get("restartGame"), Msg.get("gameInProgress"))) {
					board.game.start();
				}
				break;
			case EXIT:
				board.endGame();
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

}
