package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import com.dill.agricola.Game;
import com.dill.agricola.Main;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.support.Fonts;
import com.dill.agricola.support.Msg;
import com.dill.agricola.support.scoring.Score;
import com.dill.agricola.support.scoring.Score.PlayerScore;
import com.dill.agricola.support.scoring.ScoreIO;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.Images;
import com.dill.agricola.view.utils.UiFactory;

public class ScorePanel extends JScrollPane implements ActionListener {
	private static final long serialVersionUID = 1L;

	private static final DecimalFormat scoreFormat = new DecimalFormat("###.#");
	private static final Color MORE_COLOR = new Color(23, 133, 23);
	private static final Color BORDER_COLOR = Color.LIGHT_GRAY;
	private static final Font SMALLER_FONT = Fonts.TEXT_FONT.deriveFont(14f);
	private static final Font BIGGER_FONT = Fonts.TEXT_FONT.deriveFont(16f);
	public static final Font BIGGER_NUMBER = Fonts.TEXT_FONT_BOLD.deriveFont(20f);

	private final JPanel scoringP;
	private JButton saveButton;

	private final Game game;
	private final ScoreIO io = new ScoreIO();

	public ScorePanel(Game game) {
		this.game = game;
		setFont(SMALLER_FONT);
		setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
		setBorder(null);

		JPanel p = new JPanel();
		setViewportView(p);
		p.setLayout(new BorderLayout(0, 10));
		p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel mainP = UiFactory.createVerticalPanel();

		scoringP = UiFactory.createVerticalPanel();
		mainP.add(scoringP);
		mainP.add(Box.createVerticalGlue());
		mainP.add(buildButtons());

		p.add(mainP, BorderLayout.CENTER);
	}

	private Component buildButtons() {
		JPanel buttonP = UiFactory.createHorizontalPanel();
		buttonP.add(Box.createHorizontalGlue());

		saveButton = UiFactory.createIconButton("document-save", Msg.get("saveScoresTitle"), this);
		saveButton.setActionCommand(Command.SAVE_SCORE.toString());
		buttonP.add(saveButton);

		buttonP.add(Box.createRigidArea(new Dimension(5, 5)));

		JButton viewB = UiFactory.createIconButton("application-certificate", Msg.get("viewScoresTitle"), this);
		viewB.setActionCommand(Command.VIEW_SCORE.toString());
		buttonP.add(viewB);

		return buttonP;
	}

	public void updateScoring() {

		Player[] players = game.getPlayers();
		JPanel p = new JPanel(new GridLayout(0, 3, 0, 0));
//		p.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		// heading
		JPanel emptyP = new JPanel();
		emptyP.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, BORDER_COLOR));
		p.add(emptyP);
		for (Player player : players) {
			JLabel playerL = UiFactory.createLabel(Msg.get("player", Msg.get(player.getColor().toString().toLowerCase())));
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

		scoringP.removeAll();

		scoringP.add(p);
		// tie breaker
		if (blueTotal == redTotal) {
			scoringP.add(Box.createVerticalStrut(5));
			JLabel bottomLabel = UiFactory.createLabel(Msg.get("tieBreakerMsg",
					Msg.get(game.getInitialStartPlayer().other().toString().toLowerCase())));
			scoringP.add(bottomLabel);
		}
		scoringP.add(Box.createVerticalStrut(15));
		// winner
		PlayerColor winner = blueTotal > redTotal ? PlayerColor.BLUE
				: blueTotal < redTotal ? PlayerColor.RED : game.getInitialStartPlayer().other();
		JLabel winnerLabel = UiFactory.createLabel(Msg.get(game.isEnded() ? "winnerMsg" : "wouldBeWinnerMsg",
				Msg.get(winner.toString().toLowerCase())));
		winnerLabel.setFont(BIGGER_FONT);
		winnerLabel.setForeground(winner.getRealColor());
		scoringP.add(winnerLabel);

		updateSaveButton();
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

	private void updateSaveButton() {
		boolean alreadySaved = io.isSaved(game.getStartTime());
		if (!Main.DEBUG) {
			saveButton.setEnabled(game.isEnded() && !alreadySaved);
		}
		saveButton.setText(alreadySaved ? Msg.get("alreadySaved") : "");
	}

	private void saveScores() {
		NameDialog nameDialog = new NameDialog(
				(JFrame) SwingUtilities.getWindowAncestor(this),
				io.getNamesInScores());
		Map<PlayerColor, String> nameMap = nameDialog.getNameMap();
		if (nameMap != null) {
			Score score = new Score(game, nameMap);
			io.appendScore(score);
			game.captureBoard(true);
		}
	}

	private void showScores() {
		new ScoresDialog(
				(JFrame) SwingUtilities.getWindowAncestor(this),
				new ArrayList<Score>(io.getScores()));
	}

	private static enum Command {
		VIEW_SCORE, SAVE_SCORE;
	}

	public void actionPerformed(ActionEvent e) {
		Command command = Command.valueOf(e.getActionCommand());
		switch (command) {
		case VIEW_SCORE:
			showScores();
			break;
		case SAVE_SCORE:
			saveScores();
			updateSaveButton();
			break;
		default:
			break;
		}
	}

	private final static class NameDialog extends JDialog implements ActionListener {
		private static final long serialVersionUID = 1L;

		private final List<JComboBox> nameBoxes = new ArrayList<JComboBox>();
		private Map<PlayerColor, String> nameMap = null;

		public NameDialog(JFrame parent, List<String> usualNames) {
			super(parent);
			setTitle(Msg.get("selectNameTitle"));
			setIconImage(Images.createIcon("application-certificate", ImgSize.SMALL).getImage());
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			setModalityType(ModalityType.APPLICATION_MODAL);
			setResizable(false);

			buildLayout(usualNames);
			pack();
			setLocationRelativeTo(parent);

			setVisible(true);
		}

		private void buildLayout(List<String> usualNames) {
			JPanel main = UiFactory.createFlowPanel(5, 5);

			main.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(5, 5, 5, 5),
					BorderFactory.createCompoundBorder(
							BorderFactory.createTitledBorder(Msg.get("pickPlayerNamesMsg")),
							BorderFactory.createEmptyBorder(0, 5, 5, 5))
					));

			for (PlayerColor color : PlayerColor.values()) {
				JLabel nameLabel = UiFactory.createLabel(Msg.get("player", Msg.get(color.toString().toLowerCase())));
				nameLabel.setFont(BIGGER_FONT);
				nameLabel.setForeground(color.getRealColor());
				nameLabel.setPreferredSize(new Dimension(100, nameLabel.getPreferredSize().height));

				JComboBox nameList = new JComboBox(usualNames.toArray());
				nameList.setActionCommand(color.toString());
				nameList.setEditable(true);
				if (usualNames.size() > color.ordinal()) {
					nameList.setSelectedIndex(color.ordinal());
				}
				nameBoxes.add(nameList);

				JPanel colorP = UiFactory.createFlowPanel(5, 0);
				colorP.add(nameLabel);
				colorP.add(nameList);
				main.add(colorP);
			}

			JButton submitButton = UiFactory.createTextButton(Msg.get("okBtn"), this);
			JPanel submitP = UiFactory.createFlowPanel();
			submitP.add(submitButton);
			main.add(submitP);
			main.setPreferredSize(new Dimension(300, 130));

			getContentPane().add(main);
		}

		public Map<PlayerColor, String> getNameMap() {
			return nameMap;
		}

		public void actionPerformed(ActionEvent e) {
			nameMap = new HashMap<PlayerColor, String>();
			for (JComboBox nameList : nameBoxes) {
				nameMap.put(PlayerColor.valueOf(nameList.getActionCommand()), (String) nameList.getSelectedItem());
			}

			setVisible(false);
			dispose();
		}
	}

	private final static class ScoresDialog extends JDialog {
		private static final long serialVersionUID = 1L;

		public ScoresDialog(JFrame parent, List<Score> scores) {
			super(parent);

			setTitle(Msg.get("scoresTitle"));
			setIconImage(Images.createIcon("application-certificate", ImgSize.SMALL).getImage());
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			setModalityType(ModalityType.APPLICATION_MODAL);
			setResizable(false);

			buildLayout(scores);
			pack();
			setLocationRelativeTo(parent);

			setVisible(true);
		}

		private void buildLayout(List<Score> scores) {
			JTable table = new JTable(new ScoreModel(scores));
			table.setPreferredScrollableViewportSize(new Dimension(500, 300));
			table.setAutoCreateRowSorter(true);
			table.setDragEnabled(false);
			table.getTableHeader().setDefaultRenderer(new ScoreHeaderRenderer(table.getTableHeader().getDefaultRenderer()));
			table.setDefaultRenderer(Float.class, new ScoreNumberRenderer(table.getDefaultRenderer(Float.class)));
			table.setDefaultRenderer(String.class, new ScoreStringRenderer(table.getDefaultRenderer(String.class)));

			JScrollPane scrollPane = new JScrollPane(table);
			scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			getContentPane().add(scrollPane, BorderLayout.CENTER);
		}
	}

	private static final class ScoreModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;

		public static final int PLAYER_COLS = 2;
		private static final DateFormat FORMAT = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

		private final List<String> columnNames;

		private final List<Score> scores;

		public ScoreModel(List<Score> scores) {
			this.scores = scores;

			columnNames = new ArrayList<String>();
			columnNames.add(Msg.get("dateCol"));
			for (PlayerColor color : PlayerColor.values()) {
				columnNames.add(Msg.get(color.toString().toLowerCase()));
				columnNames.add(Msg.get("scoreCol"));
			}
		}

		public int getColumnCount() {
			return columnNames.size();
		}

		public int getRowCount() {
			return scores.size();
		}

		public String getColumnName(int col) {
			return columnNames.get(col);
		}

		private Object getColValue(Score s, int col) {
			if (col == 0) {
				return FORMAT.format(s.getDate());
			}
			PlayerColor color = getPlayer(col);
			int playerCol = (col-1) % PLAYER_COLS;
			PlayerScore ps = s.getPlayerScores().get(color.ordinal());

			switch (playerCol) {
			case 0:
				return ps.getName() + (s.getStartingPlayer() == color ? "*" : "");
			case 1:
				return ps.getScore();
			default:
				throw new IllegalArgumentException("Invalid column index " + col);
			}
		}

		public Object getValueAt(int row, int col) {
			return getColValue(scores.get(row), col);
		}

		public PlayerColor getPlayer(int col) {
			if (col == 0) {
				return null;
			}
			return PlayerColor.values()[(col - 1) / ScoreModel.PLAYER_COLS];
		}
		
		public boolean isWinner(int row, int col) {
			return getPlayer(col) == scores.get(row).getWinner();
		}

		public Class<?> getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}

	}

	private static final class ScoreHeaderRenderer implements TableCellRenderer {

		private final TableCellRenderer defaultRendered;

		public ScoreHeaderRenderer(TableCellRenderer tableCellRenderer) {
			defaultRendered = tableCellRenderer;
		}

		public Component getTableCellRendererComponent(
				JTable table, Object value,
				boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component comp = defaultRendered.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			comp.setFont(SMALLER_FONT);
			if (column > 0) {
				comp.setForeground(((ScoreModel)table.getModel()).getPlayer(column).getRealColor());
			}
			return comp;
		}
	}

	private static final class ScoreNumberRenderer implements TableCellRenderer {

		private final TableCellRenderer defaultRendered;

		public ScoreNumberRenderer(TableCellRenderer tableCellRenderer) {
			defaultRendered = tableCellRenderer;
		}

		public Component getTableCellRendererComponent(
				JTable table, Object value,
				boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component comp = defaultRendered.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (comp instanceof JLabel) {
				((JLabel) comp).setHorizontalAlignment(SwingConstants.CENTER);
				if (((ScoreModel)table.getModel()).isWinner(row, column)) {
					comp.setForeground(MORE_COLOR);
				} else {
					comp.setForeground(Color.DARK_GRAY);					
				}
			}
			return comp;
		}
	}

	private static final class ScoreStringRenderer implements TableCellRenderer {

		private final TableCellRenderer defaultRendered;

		public ScoreStringRenderer(TableCellRenderer tableCellRenderer) {
			defaultRendered = tableCellRenderer;
		}

		public Component getTableCellRendererComponent(
				JTable table, Object value,
				boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component comp = defaultRendered.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (comp instanceof JLabel) {
				((JLabel) comp).setHorizontalAlignment(SwingConstants.CENTER);
				if (((ScoreModel)table.getModel()).isWinner(row, column)) {
					comp.setForeground(MORE_COLOR);
				} else {
					comp.setForeground(Color.DARK_GRAY);					
				}
			}
			return comp;
		}
	}
}
