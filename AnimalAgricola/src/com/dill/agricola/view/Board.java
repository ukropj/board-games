package com.dill.agricola.view;

import java.awt.BorderLayout;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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

	public static final int BORDER_WIDTH = 5;

	private Game game;

	private final ActionPerformer ap;
	private final TurnUndoManager undoManager;

	private final JPanel mainPane;

	private JLabel statusL;
	private final PlayerBoard[] playerBoards;
	private ActionBoard actionBoard;
	private DebugPanel debugPanel;
	private JButton undoBtn;
	private JButton redoBtn;

	public Board(Game game, ActionPerformer ap, TurnUndoManager undoManager) {
		this.game = game;
		this.ap = ap;
		this.undoManager = undoManager;
		ap.addUndoableEditListener(new UndoButtonUpdater());

		playerBoards = new PlayerBoard[2];

		setTitle(Msg.get("gameTitle"));
//		BufferedImage img = Images.createImage("a_all");
		BufferedImage img = Images.createImage("a");
		setIconImages(Arrays.asList(new Image[] {
				Images.getBestScaledInstance(img, 16),
				Images.getBestScaledInstance(img, 32),
				Images.getBestScaledInstance(img, 64)
		}));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		mainPane = UiFactory.createFlowPanel();
		mainPane.setLayout(new GridBagLayout());
		int b = 10;
		mainPane.setBorder(BorderFactory.createEmptyBorder(b, b, b, b));
		getContentPane().add(mainPane, BorderLayout.CENTER);

		initToolbar();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				quitGame();
			}
		});

		initActionsBoard();
		initPlayerBoard(PlayerColor.BLUE, 0);
		initPlayerBoard(PlayerColor.RED, 2);
	}

	private void initToolbar() {
		ToolListener bl = new ToolListener();
		JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
		toolbar.setMargin(new Insets(0, 0, 0, 0));
		toolbar.setFloatable(false);
		toolbar.setRollover(true);

		JButton newB = UiFactory.createToolbarButton(null, "document-new", Msg.get("newGameBtn"), bl);
		newB.setActionCommand(ActionCommand.NEW.toString());
		toolbar.add(newB);
		toolbar.add(UiFactory.createToolbarSeparator());

		undoBtn = UiFactory.createToolbarButton(null, "edit-undo", "", bl);
		undoBtn.setActionCommand(ActionCommand.UNDO.toString());
		undoBtn.setEnabled(false);
		toolbar.add(undoBtn);
		redoBtn = UiFactory.createToolbarButton(null, "edit-redo", "", bl);
		redoBtn.setActionCommand(ActionCommand.REDO.toString());
		redoBtn.setEnabled(false);
		toolbar.add(redoBtn);
		toolbar.add(UiFactory.createToolbarSeparator());
		toolbar.add(Box.createHorizontalStrut(10));
		toolbar.add(Box.createHorizontalGlue());

		statusL = UiFactory.createLabel(Msg.get("round", 0, Game.ROUNDS));
		statusL.setFont(Fonts.TOOLBAR_TEXT);
		toolbar.add(statusL);
		toolbar.add(Box.createHorizontalGlue());
//		toolbar.add(Box.createHorizontalStrut(100));

//		JButton settingB = UiFactory.createToolbarButton(null, "preferences-system", Msg.get("settingsBtn"), bl);
//		settingB.setActionCommand(ActionCommand.SETTINGS.toString());
//		toolbar.add(settingB);

		JButton aboutB = UiFactory.createToolbarButton(null, "help-browser", Msg.get("aboutBtn"), bl);
		aboutB.setActionCommand(ActionCommand.ABOUT.toString());
		toolbar.add(aboutB);

		getContentPane().add(toolbar, BorderLayout.PAGE_START);
	}

	private void initPlayerBoard(PlayerColor color, int x) {
		Player player = game.getPlayer(color);
		PlayerBoard playerBoard = new PlayerBoard(player, ap, game.getSubmitListener());
		playerBoards[color.ordinal()] = playerBoard;

		JScrollPane scrollPane = new JScrollPane(playerBoard);
		scrollPane.setBorder(BorderFactory.createMatteBorder(
				BORDER_WIDTH,
				color == PlayerColor.BLUE ? BORDER_WIDTH : 0,
				BORDER_WIDTH,
				color == PlayerColor.RED ? BORDER_WIDTH : 0,
				color.getRealColor()));

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = x;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;

		mainPane.add(scrollPane, c);
	}

	private void initActionsBoard() {

		actionBoard = new ActionBoard(game.getActions(), ap, game.getSubmitListener());
		actionBoard.addTabs(new ScorePanel(game));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.ipadx = 5;
		c.ipady = 5;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;

		mainPane.add(actionBoard, c);
	}

	public void buildDebugPanel(Player[] players) {
		debugPanel = new DebugPanel(players);
		getContentPane().add(debugPanel, BorderLayout.PAGE_END);
	}

	public void start() {
		actionBoard.resetActions();
		playerBoards[0].reset();
		playerBoards[1].reset();
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
//		undoB.setText(undoManager.getUndoPresentationName());
		undoBtn.setToolTipText(undoManager.getUndoPresentationName());
		undoBtn.setEnabled(undoManager.canUndo());

		// refresh redo
//		redoB.setText(undoManager.getRedoPresentationName());
		redoBtn.setToolTipText(undoManager.getRedoPresentationName());
		redoBtn.setEnabled(undoManager.canRedo());
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

	public void startingPlayerChanged() {
		playerBoards[0].updateFarm();
		playerBoards[1].updateFarm();
	}

	public void endGame() {
		playerBoards[0].setActive(false, false);
		playerBoards[1].setActive(false, false);

		actionBoard.showScoring();
	}

	public void quitGame() {
		if (Main.DEBUG
				|| !game.isStarted()
				|| UiFactory.showQuestionDialog(this, Msg.get("endGameMsg"), Msg.get("gameInProgressTitle"))) {
			System.exit(0);
		}
	}

	private class ToolListener implements ActionListener, ItemListener {

		public void actionPerformed(ActionEvent e) {
			ActionCommand command = ActionCommand.valueOf(e.getActionCommand());
			switch (command) {
			case NEW:
				if (!game.isStarted()
						|| UiFactory.showQuestionDialog(Board.this, Msg.get("restartGameMsg"), Msg.get("gameInProgressTitle"))) {
					game.start();
				}
				break;
			case UNDO:
				undoManager.undo();
				break;
			case REDO:
				undoManager.redo();
				break;
//			case SETTINGS:
//				showSettings();
//				break;
			case ABOUT:
				BufferedImage img = Images.getBestScaledInstance(Images.createImage("a"), 50);
				JOptionPane.showMessageDialog(Board.this, Msg.get("aboutMsg"), Msg.get("aboutTitle"), JOptionPane.INFORMATION_MESSAGE, new ImageIcon(img));
				break;
			case EXIT:
				quitGame();
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
