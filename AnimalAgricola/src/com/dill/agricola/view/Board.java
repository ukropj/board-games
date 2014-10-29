package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import com.dill.agricola.Game;
import com.dill.agricola.Game.Phase;
import com.dill.agricola.GeneralSupply;
import com.dill.agricola.Main;
import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.ActionPerformer;
import com.dill.agricola.actions.ActionStateChangeListener;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.support.Config;
import com.dill.agricola.support.Config.ConfigKey;
import com.dill.agricola.support.Fonts;
import com.dill.agricola.support.Msg;
import com.dill.agricola.undo.TurnUndoManager;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.Images;
import com.dill.agricola.view.utils.UiFactory;

public class Board extends JFrame {
	private static final long serialVersionUID = 1L;

	private Game game;

	private final ActionPerformer ap;
	private final TurnUndoManager undoManager;

	private final JPanel mainPane;

	private JLabel roundL;
	private JLabel playerL;
	private JLabel turnL;
	private final PlayerBoard[] playerBoards;
	private final JComponent[] playerPanes;
	private JTabbedPane playerTabPane;
	private ActionBoard actionBoard;
//	private DebugPanel debugPanel;
	private JButton undoBtn;
	private JButton redoBtn;

	private JComponent buildingsView;
	private JPanel buildingsRibbon;

	private boolean condensedLayout;
	private boolean showBuildingsRibbon;

	public Board(Game game, ActionPerformer ap, TurnUndoManager undoManager) {
		this.game = game;
		this.ap = ap;
		this.undoManager = undoManager;
		this.condensedLayout = Config.getBoolean(ConfigKey.CONDENSED_LAYOUT, false);
		this.showBuildingsRibbon = Config.getBoolean(ConfigKey.BUILDINGS_RIBBON, false);
		ap.addUndoableEditListener(new UndoButtonUpdater());

		playerBoards = new PlayerBoard[2];
		playerPanes = new JScrollPane[2];

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
		int b = 5;
		mainPane.setBorder(BorderFactory.createEmptyBorder(b, b, b, b));
		getContentPane().add(mainPane, BorderLayout.CENTER);

		initToolbar();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				quitGame();
			}
		});

		initActionsBoard();
		initPlayerBoards();
		initBuildingRibbon();
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
		toolbar.add(Box.createHorizontalGlue());

		roundL = UiFactory.createLabel(Msg.get("roundLab", 0, Game.ROUNDS));
		roundL.setFont(Fonts.TOOLBAR_TEXT);
		toolbar.add(roundL);
		toolbar.add(Box.createHorizontalStrut(30));
		playerL = UiFactory.createLabel("");
		playerL.setFont(Fonts.TOOLBAR_TEXT);
		toolbar.add(playerL);
		turnL = UiFactory.createLabel("");
		turnL.setFont(Fonts.TOOLBAR_TEXT);
		toolbar.add(turnL);
		toolbar.add(Box.createHorizontalGlue());
//		toolbar.add(Box.createHorizontalStrut(100));

		JButton ribbonB = UiFactory.createToolbarButton(null, "buildings-view", Msg.get("viewBuildingsBtn"), bl);
		ribbonB.setActionCommand(ActionCommand.BUILDINGS.toString());
		toolbar.add(ribbonB);

		JButton layoutB = UiFactory.createToolbarButton(null, "video-display", Msg.get("layoutBtn"), bl);
		layoutB.setActionCommand(ActionCommand.LAYOUT.toString());
		toolbar.add(layoutB);

		JButton captureB = UiFactory.createToolbarButton(null, "camera-photo", Msg.get("captureBtn"), bl);
		captureB.setActionCommand(ActionCommand.CAPTURE.toString());
		toolbar.add(captureB);

//		JButton settingB = UiFactory.createToolbarButton(null, "preferences-system", Msg.get("settingsBtn"), bl);
//		settingB.setActionCommand(ActionCommand.SETTINGS.toString());
//		toolbar.add(settingB);

		JButton aboutB = UiFactory.createToolbarButton(null, "help-browser", Msg.get("aboutBtn"), bl);
		aboutB.setActionCommand(ActionCommand.ABOUT.toString());
		toolbar.add(aboutB);

		getContentPane().add(toolbar, BorderLayout.PAGE_START);
	}

	private void initPlayerBoards() {
		initPlayerBoard(PlayerColor.BLUE, 0);
		initPlayerBoard(PlayerColor.RED, 2);

		playerTabPane = new JTabbedPane();

		replacePlayerBoards();
	}

	private void replacePlayerBoards() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;

		for (PlayerColor color : PlayerColor.values()) {
			int ord = color.ordinal();
			if (condensedLayout) {
				mainPane.remove(playerPanes[ord]);
				playerPanes[ord].setBorder(BorderFactory.createEmptyBorder());
				playerTabPane.addTab(Msg.get(color.toString().toLowerCase()),
						AgriImages.getWorkerIcon(color, ImgSize.SMALL),
						playerPanes[ord]);
			} else {
				playerTabPane.remove(playerPanes[ord]);
				playerPanes[ord].setBorder(PlayerBorderFactory.getBorder(color, color == PlayerColor.RED));

				c.gridx = ord * 2; // BLUE = 0, RED = 2
				mainPane.add(playerPanes[ord], c);
			}
		}
		if (condensedLayout) {
			c.gridx = 0;
			playerTabPane.setBorder(PlayerBorderFactory.getBorder(PlayerColor.BLUE, false));
			mainPane.add(playerTabPane, c);
		} else {
			mainPane.remove(playerTabPane);
		}
	}

	private void initPlayerBoard(PlayerColor color, int x) {
		Player player = game.getPlayer(color);
		PlayerBoard playerBoard = new PlayerBoard(player, ap, game.getSubmitListener());

		JScrollPane scrollPane = new JScrollPane(playerBoard);

		playerBoards[color.ordinal()] = playerBoard;
		playerPanes[color.ordinal()] = scrollPane;
	}

	private void initActionsBoard() {
		actionBoard = new ActionBoard(game.getActions(), ap, game.getSubmitListener(), this.condensedLayout);
		actionBoard.addTabs(new ScorePanel(game));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.ipadx = 5;
		c.ipady = 5;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;

		mainPane.add(actionBoard, c);
	}

	private void initBuildingRibbon() {
		buildingsRibbon = UiFactory.createFlowPanel();

		JScrollPane sp = new JScrollPane(buildingsRibbon);
		sp.setBorder(BorderFactory.createEmptyBorder());
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
//		sp.setPreferredSize(new Dimension(650, 15 + 160 * (1 + (more ? 1 : 0) + (evenMore ? 1 : 0))));
		buildingsView = sp;
		buildingsView.setVisible(showBuildingsRibbon);
		getContentPane().add(buildingsView, BorderLayout.PAGE_END);

		for (Action action : game.getActions()) {
			action.addChangeListener(new ActionUsageListener());
		}
	}

	private void resetRibbon() {
		buildingsRibbon.removeAll();

		List<BuildingType> types = new ArrayList<BuildingType>(GeneralSupply.getBuildingsAll());
		for (BuildingType type : types) {
			final JButton button = new JButton();
			button.setMargin(new Insets(2, 2, 2, 2));
			button.add(UiFactory.createLabel(AgriImages.getBuildingIcon(type, ImgSize.BIG, false)));
			button.setActionCommand(type.toString());
			button.addActionListener(new BuildingRibbonListener(type));
			buildingsRibbon.add(button);
		}
	}

	private void updateRibbon() {
		List<BuildingType> left = new ArrayList<BuildingType>(GeneralSupply.getBuildingsLeft());

		for (Component c : buildingsRibbon.getComponents()) {
			JButton btn = (JButton) c;
			BuildingType type = BuildingType.valueOf(btn.getActionCommand());
			boolean isLeft = left.contains(type);
			btn.setVisible(isLeft);
			if (isLeft) {
				boolean canBuild = actionBoard.canBuildSpecial(type);
				if (btn.isEnabled() != canBuild) {
					btn.setEnabled(canBuild);
					btn.remove(0);
					btn.add(UiFactory.createLabel(AgriImages.getBuildingIcon(type, ImgSize.BIG, canBuild)));
				}
			}
		}

		buildingsRibbon.revalidate();
		buildingsRibbon.repaint();
	}

	public void toggleRibbon() {
		showBuildingsRibbon = !showBuildingsRibbon;
		buildingsView.setVisible(showBuildingsRibbon);
		Config.putBoolean(ConfigKey.BUILDINGS_RIBBON, showBuildingsRibbon);
//		invalidate();
//		repaint();
	}

//	public void buildDebugPanel(Player[] players) {
//		debugPanel = new DebugPanel(players);
//		getContentPane().add(debugPanel, BorderLayout.PAGE_END);
//	}

	public void setLayoutType(boolean isCondensed) {
		if (this.condensedLayout != isCondensed) {
			this.condensedLayout = isCondensed;
			replacePlayerBoards();
			actionBoard.setLayout(condensedLayout);
			mainPane.revalidate();
			updateActivePlayer();
			Config.putBoolean(ConfigKey.CONDENSED_LAYOUT, condensedLayout);
		}
	}

	public BufferedImage paintPlayerBoards() {
		/*int D = 1;
		int w = 0;
		int h = 0;
		for (PlayerBoard pb : playerBoards) {
			Dimension size = pb.getPreferredSize();
			w += size.width + D;
			h = Math.max(h, size.height);
		}
		w += actionBoard.getPreferredSize().width;

		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();

		int x = 0;
		for (int i = 0; i < playerBoards.length; i++) {
			PlayerBoard pb = playerBoards[i];
			JViewport p = (JViewport) pb.getParent();
			Point pos = p.getViewPosition();
			Dimension size = pb.getPreferredSize();

			pb.setSize(pb.getPreferredSize());
			layoutComponent(pb);
			SwingUtilities.paintComponent(g, pb, p, x, 0, size.width, size.height);

			x += size.width + D;
			// undo paintComponent changes
			p.setView(pb);
			p.setViewPosition(pos);
		}*/
		Component c = getContentPane();
		
		Board.lockComponentSize(buildingsRibbon, true);
//		actionBoard.lockBuildingDisplaySize(true);
		
		Dimension size = c.getPreferredSize();
		size.setSize(size.getWidth(), 800);
		
		Container parent = c.getParent();
		BufferedImage img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		c.setSize(size);
		layoutComponent(c);
		SwingUtilities.paintComponent(g, c, parent, 0, 0, size.width, size.height);
		
		Board.lockComponentSize(buildingsRibbon, false);
//		actionBoard.lockBuildingDisplaySize(false);
		parent.add(c);
		
		g.dispose();

		return img;
	}

	private void layoutComponent(Component c) {
		synchronized (c.getTreeLock()) {
			c.doLayout();
			if (c instanceof Container) {
				for (Component child : ((Container) c).getComponents()) {
					layoutComponent(child);
				}
			}
		}
	}

	public boolean isMaximized() {
		return (getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0;
	}

	public void startGame() {
		actionBoard.resetBuildings();
		resetRibbon();
		updateRibbon();
		actionBoard.resetActions();
		playerBoards[0].reset();
		playerBoards[1].reset();
	}

	public void refreshUndoRedo() {
		// refresh undo
		undoBtn.setToolTipText(undoManager.getUndoPresentationName());
		undoBtn.setEnabled(undoManager.canUndo());

		// refresh redo
		redoBtn.setToolTipText(undoManager.getRedoPresentationName());
		redoBtn.setEnabled(undoManager.canRedo());
	}

	public void refresh() {
		roundL.setText(Msg.get("roundLab", game.getRound(), Game.ROUNDS));

		updateActivePlayer();

		Phase phase = game.getPhase();
		switch (phase) {
		case BEFORE_WORK:
		case BEFORE_BREEDING:
		case FEATURE_BEFORE_WORK:
			turnL.setText(Msg.get("extraturnLab"));
			break;
		case WORK:
			turnL.setText(Msg.get("turnLab"));
			break;
		case BREEDING:
			turnL.setText(Msg.get("breedingLab"));
			break;
		case CLEANUP:
			if (game.isEnded()) {
				turnL.setText(Msg.get("winnerLab"));
			} else {
				turnL.setText("");
			}
			break;
		default:
			break;
		}

		actionBoard.updateActions();
		updateRibbon();

		if (phase == Phase.WORK) {
			actionBoard.showActions();
		} else {
			actionBoard.disableActions();
		}

		refreshUndoRedo();
	}

	private void updateActivePlayer() {
		Player currentPlayer = ap.getPlayer();
		if (currentPlayer != null && !game.isEnded()) {
			PlayerColor c = currentPlayer.getColor();
			playerL.setText(Msg.get(c.toString().toLowerCase()));
			playerL.setForeground(c.getRealColor());

			PlayerColor currentColor = currentPlayer.getColor();
			playerBoards[currentColor.ordinal()].activate(game.getPhase());
			playerBoards[currentColor.other().ordinal()].deactivate();

//			if (Main.DEBUG) {
//				debugPanel.setCurrentPlayer(currentColor);
//			}

			if (condensedLayout) {
				playerTabPane.setSelectedIndex(currentColor.ordinal());
				playerTabPane.setBorder(PlayerBorderFactory.getBorder(currentColor, false));
			}
		} else {
			if (game.isEnded()) {
				PlayerColor winner = game.getWinner();
				playerL.setText(Msg.get(winner.toString().toLowerCase()));
				playerL.setForeground(winner.getRealColor());
			} else {
				playerL.setText("");
			}
			playerBoards[0].deactivate();
			playerBoards[1].deactivate();

			if (condensedLayout) {
				// not grafically ideal, but not worth some special gradient or whatnot
				playerTabPane.setBorder(PlayerBorderFactory.getBorder(PlayerColor.BLUE, false));
			}
		}
	}

	public void startRound() {
		actionBoard.initActions();
		if (Main.DEBUG && game.getRound() == 1) {
			actionBoard.initActions();
		}
		refresh();
	}
	
	public void workersGoHome() {
		actionBoard.unuseActions();
		refresh();
	}

	public void startingPlayerChanged() {
		playerBoards[0].updateFarm();
		playerBoards[1].updateFarm();
	}

	public void endGame() {
		refresh();
		actionBoard.showScoring();
	}

	public void quitGame() {
		if (Main.DEBUG
				|| !game.isStarted()
				|| UiFactory.showQuestionDialog(this, Msg.get("endGameMsg"), Msg.get("gameInProgressTitle"), null)) {
			System.exit(0);
		}
	}
	
	public static void lockComponentSize(Component c, boolean lock) {
		Container parent = c.getParent();
		if (parent instanceof JViewport) {
			JViewport viewport = (JViewport) c.getParent();
			c.setPreferredSize(lock ? viewport.getSize() : null);	
			viewport.setPreferredSize(lock ? viewport.getSize() : null);	
			viewport.doLayout();
		} else {
			c.setPreferredSize(lock ? c.getSize() : null);			
		}
		c.doLayout();
	}

	public static enum ActionCommand {
		NEW, EXIT, UNDO, REDO, ABOUT, SETTINGS, LAYOUT, BUILDINGS, CAPTURE;
	}

	private class ToolListener implements ActionListener, ItemListener {

		public void actionPerformed(ActionEvent e) {
			ActionCommand command = ActionCommand.valueOf(e.getActionCommand());
			switch (command) {
			case NEW:
				if (!game.isStarted()
						|| UiFactory.showQuestionDialog(Board.this, Msg.get("restartGameMsg"), Msg.get("gameInProgressTitle"), null)) {
					game.start();
				}
				break;
			case UNDO:
				undoManager.undo();
				break;
			case REDO:
				undoManager.redo();
				break;
			case BUILDINGS:
				toggleRibbon();
				break;
			case LAYOUT:
				setLayoutType(!condensedLayout);
				break;
			case CAPTURE:
				String path = game.captureBoard(false);
				JOptionPane.showMessageDialog(Board.this, Msg.get("capturedMsg", path), Msg.get("capturedTitle"),
						JOptionPane.INFORMATION_MESSAGE, Images.createIcon("camera-photo", ImgSize.SMALL));
				break;
//			case SETTINGS:
//				showSettings();
//				break;
			case ABOUT:
				BufferedImage img = Images.getBestScaledInstance(Images.createImage("a"), 50);
				JOptionPane.showMessageDialog(Board.this, Msg.get("aboutMsg", Main.VERSION),
						Msg.get("aboutTitle"), JOptionPane.INFORMATION_MESSAGE, new ImageIcon(img));
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

	public static class PlayerBorderFactory {

		public static final int BORDER_WIDTH = 5;

		private static final Border defaultBorder = BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(
				BORDER_WIDTH, 0, BORDER_WIDTH, 0, new Icon() {

					private final int W = 300;

					public void paintIcon(Component c, Graphics g, int x, int y) {
						Graphics2D g2 = (Graphics2D) g.create();
						Point2D start = new Point2D.Float(0f, 0f);
						Point2D end = new Point2D.Float(W, 0f);
						float[] dist = { 0.5f, 1.0f };
						Color[] colors = { PlayerColor.BLUE.getRealColor(), PlayerColor.RED.getRealColor() };
						g2.setPaint(new LinearGradientPaint(start, end, dist, colors));
						g2.fillRect(x, y, W, BORDER_WIDTH);
						g2.dispose();
					}

					public int getIconWidth() {
						return W;
					}

					public int getIconHeight() {
						return BORDER_WIDTH;
					}
				}), BorderFactory.createEmptyBorder(0, BORDER_WIDTH, 0, BORDER_WIDTH));

		private static final Border[] playerBorders = {
				// right
				createPlayerBorder(PlayerColor.BLUE, true),
				createPlayerBorder(PlayerColor.RED, true),
				// left
				createPlayerBorder(PlayerColor.BLUE, false),
				createPlayerBorder(PlayerColor.RED, false)
		};

		private static Border createPlayerBorder(PlayerColor c, boolean right) {
			return BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(
					BORDER_WIDTH,
					!right ? BORDER_WIDTH : 0,
					BORDER_WIDTH,
					right ? BORDER_WIDTH : 0,
					c.getRealColor()),
					BorderFactory.createEmptyBorder(
							0,
							!right ? 0 : BORDER_WIDTH,
							0,
							right ? 0 : BORDER_WIDTH));
		}

		public static Border getBorder(PlayerColor c, boolean right) {
			return playerBorders[(right ? 0 : 2) + c.ordinal()];
		}

		public static Border getBorder() {
			return defaultBorder;
		}

	}

	private final class BuildingRibbonListener implements ActionListener {

		private final BuildingType type;

		public BuildingRibbonListener(BuildingType type) {
			this.type = type;
		}

		public void actionPerformed(ActionEvent e) {
			actionBoard.buildSpecial(type);
		}
	}

	private final class ActionUsageListener implements ActionStateChangeListener {

		public void stateChanges(Action action) {
			// this is called when any action changes - not optimal, should be called when build action changes or any action is activated
			updateRibbon();
		}
	}

}
