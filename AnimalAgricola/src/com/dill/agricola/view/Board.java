package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import com.dill.agricola.Game;
import com.dill.agricola.Game.ActionCommand;
import com.dill.agricola.Main;
import com.dill.agricola.actions.ActionPerformer;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.support.Fonts;
import com.dill.agricola.support.Msg;
import com.dill.agricola.undo.TurnUndoManager;
import com.dill.agricola.view.utils.Images;
import com.dill.agricola.view.utils.UiFactory;

public class Board extends JFrame {
	private static final long serialVersionUID = 1L;

	private Game game;

	private final ActionPerformer ap;
	private final TurnUndoManager undoManager;

	private final Container mainPane;

	private JLabel statusL;
	private final PlayerBoard[] playerBoards;
	private ActionBoard actionBoard;
	private DebugPanel debugPanel;
	private JButton undoB;
	private JButton redoB;
	
	private ScoreDialog scoreDialog;

	public Board(Game game, ActionPerformer ap, TurnUndoManager undoManager) {
		this.game = game;
		this.ap = ap;
		this.undoManager = undoManager;
		ap.addUndoableEditListener(new UndoButtonUpdater());

		playerBoards = new PlayerBoard[2];

		setTitle(Msg.get("gameTitle"));
		BufferedImage img = Images.createImage("a_all");
		setIconImages(Arrays.asList(new Image[] {
				Images.getBestScaledInstance(img, 16),
				Images.getBestScaledInstance(img, 32),
				Images.getBestScaledInstance(img, 64)
		}));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		mainPane = UiFactory.createFlowPanel();
		mainPane.setLayout(new GridBagLayout());
		getContentPane().add(mainPane, BorderLayout.CENTER);

		initToolbar();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				endGame();
			}
		});

		initActionsBoard();
		initPlayerBoard(PlayerColor.BLUE, 0);
		initPlayerBoard(PlayerColor.RED, 2);
	}

	private void initToolbar() {
		ToolListener bl = new ToolListener(this);
		JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
		toolbar.setMargin(new Insets(0,0,0,0));
		toolbar.setFloatable(false);
		toolbar.setRollover(true);

		JButton newB = UiFactory.createToolbarButton(null, "document-new", Msg.get("newGame"), bl);
		newB.setActionCommand(ActionCommand.NEW.toString());
		toolbar.add(newB);
		toolbar.addSeparator();
		
		undoB = UiFactory.createToolbarButton(null, "edit-undo", "", bl);
		undoB.setActionCommand(ActionCommand.UNDO.toString());
		toolbar.add(undoB);
		redoB = UiFactory.createToolbarButton(null, "edit-redo", "", bl);
		redoB.setActionCommand(ActionCommand.REDO.toString());
		toolbar.add(redoB);

		toolbar.add(Box.createHorizontalGlue());
		
		statusL = UiFactory.createLabel(Msg.get("round", 0, Game.ROUNDS));
		statusL.setFont(Fonts.TEXT_BIG);
		toolbar.add(statusL);
		toolbar.addSeparator(new Dimension(5, 0));
		
		getContentPane().add(toolbar, BorderLayout.PAGE_START);
	}

	private void initPlayerBoard(PlayerColor color, int x) {
		Player player = game.getPlayer(color);
		PlayerBoard playerBoard = new PlayerBoard(player, ap, game.getSubmitListener());
		playerBoards[color.ordinal()] = playerBoard;

		JScrollPane scrollPane = new JScrollPane(playerBoard);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = x;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;

		mainPane.add(scrollPane, c);
	}

	private void initActionsBoard() {
		actionBoard = new ActionBoard(game.getActions(), ap, game.getSubmitListener());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.ipadx = 5;
		c.ipady = 5;
		c.fill = GridBagConstraints.BOTH;

		mainPane.add(actionBoard, c);
	}

	public void buildDebugPanel(Player[] players) {
		debugPanel = new DebugPanel(players);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.BOTH;
		mainPane.add(debugPanel, c);
	}

	public void start() {
		scoreDialog = null;
		actionBoard.resetActions();
	}

	public void updateState(int roundNo) {
		if (roundNo > 0) {
			statusL.setText(Msg.get("round", roundNo, Game.ROUNDS));
		}
		actionBoard.updateActions();
		refreshUndoRedo();
	}
	
	public void refreshUndoRedo() {
		// refresh undo
		undoB.setText(undoManager.getUndoPresentationName());
		undoB.setToolTipText(undoManager.getUndoPresentationName());
		undoB.setEnabled(undoManager.canUndo());

		// refresh redo
		redoB.setText(undoManager.getRedoPresentationName());
		redoB.setToolTipText(undoManager.getUndoPresentationName());
		redoB.setEnabled(undoManager.canRedo());
	}

	public void startRound(int roundNo) {
		actionBoard.initActions();
		if (Main.DEBUG && roundNo == 1) {
			actionBoard.initActions();
		}
		updateState(roundNo);
	}

	public void startTurn(Player currentPlayer) {
		playerBoards[currentPlayer.getColor().ordinal()].setActive(true, false);
		playerBoards[currentPlayer.getColor().other().ordinal()].setActive(false, false);
		updateState(-1);
		if (Main.DEBUG) {
			debugPanel.setCurrentPlayer(currentPlayer.getColor());
		}
	}

	public void endRound() {
		playerBoards[0].setActive(true, true);
		playerBoards[1].setActive(true, true);

		updateState(-1);
		actionBoard.disableActions();
	}

	public void showScoring(Player[] players, PlayerColor initialStartingPlayer) {
		if (scoreDialog == null) {
			scoreDialog = new ScoreDialog(players, initialStartingPlayer);
		}
		scoreDialog.setLocationRelativeTo(this);
		scoreDialog.setVisible(true);
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

	private class ToolListener implements ActionListener, ItemListener {

		private final Board board;

		public ToolListener(Board board) {
			this.board = board;
		}

		public void actionPerformed(ActionEvent e) {
			ActionCommand command = ActionCommand.valueOf(e.getActionCommand());
			switch (command) {
			case NEW:
				if (!board.game.isStarted()
						|| UiFactory.showQuestionDialog(Msg.get("restartGame"), Msg.get("gameInProgress"))) {
					board.game.start();
				}
				break;
			case UNDO:
				board.undoManager.undo();
				break;
			case REDO:
				board.undoManager.redo();
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
			ActionCommand command = ActionCommand.valueOf(mi.getActionCommand());
			switch (command) {
			default:
				break;
			}
		}

	}

	private class UndoButtonUpdater implements UndoableEditListener {
		public void undoableEditHappened(UndoableEditEvent evt) {
			refreshUndoRedo();
		}
	}

}
