package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.ActionPerformer;
import com.dill.agricola.actions.ActionStateChangeListener;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.support.Msg;

public class ActionBoard extends JPanel {
	private static final long serialVersionUID = 1L;

	private final ActionPerformer ap;

	private final Map<ActionType, Action> actions = new EnumMap<ActionType, Action>(ActionType.class);
	private final Map<ActionType, ActionButton> actionButtons = new EnumMap<ActionType, ActionButton>(ActionType.class);

	private final JTabbedPane tabPane;
	private int scoringTabIndex = 1;

	private static final Border defaultPanelBorder = BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(
			Board.BORDER_WIDTH, 0, Board.BORDER_WIDTH, 0, new Icon() {

				private final int W = 300;

				public void paintIcon(Component c, Graphics g, int x, int y) {
					Graphics2D g2 = (Graphics2D) g.create();
					Point2D start = new Point2D.Float(0f, 0f);
					Point2D end = new Point2D.Float(W, 0f);
					float[] dist = { 0.5f, 1.0f };
					Color[] colors = { PlayerColor.BLUE.getRealColor(), PlayerColor.RED.getRealColor() };
					g2.setPaint(new LinearGradientPaint(start, end, dist, colors));
					g2.fillRect(x, y, W, Board.BORDER_WIDTH);
					g2.dispose();
				}

				public int getIconWidth() {
					return W;
				}

				public int getIconHeight() {
					return Board.BORDER_WIDTH;
				}
			}), BorderFactory.createEmptyBorder(0, Board.BORDER_WIDTH, 0, Board.BORDER_WIDTH));

	private static final Border[] playerBorders = {
			createPlayerBorder(PlayerColor.BLUE),
			createPlayerBorder(PlayerColor.RED)
	};

	public ActionBoard(List<Action> actions, final ActionPerformer ap, final ActionListener submitListener) {
		this.ap = ap;
		setLayout(new BorderLayout());
		setBorder(defaultPanelBorder);

		tabPane = new JTabbedPane();
		add(tabPane, BorderLayout.CENTER);

		JPanel actionPanel = new JPanel(new GridBagLayout());
		actionPanel.setName("abc");

		for (final Action action : actions) {
			ActionType type = action.getType();
			this.actions.put(type, action);
			ActionButton b = new ActionButton(type);
			b.setEnabled(false);

			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (!action.isUsed()) {
						if (ap.startAction(action)) {
							updateActions();

							if (ap.isFinished()) {
								submitListener.actionPerformed(e);
							}
						}
					}
				}
			});
			action.addChangeListener(new ActionUsageListener(b));

			actionButtons.put(type, b);
			ActionPanelFactory.createActionPanel(actionPanel, action, b);
		}

		tabPane.addTab(Msg.get("actionsTitle"), actionPanel);

	}

	public void addTabs(final ScorePanel scorePanel) {
		final BuildingsPanel buildingPanel = new BuildingsPanel();
		tabPane.addTab(Msg.get("buildingsTitle"), buildingPanel);
		tabPane.addTab(Msg.get("animalTitle"), new AnimalScoringPanel());
		tabPane.addTab(Msg.get("scoresTitle"), /*Images.createIcon("application-certificate", ImgSize.SMALL),*/scorePanel);
		
		tabPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (tabPane.getSelectedComponent() == scorePanel) {
					scorePanel.updateScoring();
				} else if (tabPane.getSelectedComponent() == buildingPanel) {
					buildingPanel.updateBuildings();
				}
			}
		});

		scoringTabIndex = tabPane.indexOfComponent(scorePanel);
		System.out.println(scoringTabIndex);
	}

	public void showScoring() {
		tabPane.setSelectedIndex(scoringTabIndex);
	}

	public void updateActions() {
		for (Entry<ActionType, ActionButton> btnEntry : actionButtons.entrySet()) {
			ActionType type = btnEntry.getKey();
			Action a = actions.get(type);
			ActionButton button = btnEntry.getValue();
			if (ap.hasAction()) {
				// when action is being performed, disable everything
				button.setEnabled(false);
				if (ap.hasAction(type)) {
					// NTH mark current action
				}
			} else {
				// when no action being performed, disable those that cannot be currently performed
				if (!a.isUsed()) {
					button.setEnabled(ap.getPlayer() != null && a.canDo(ap.getPlayer()));
				} else {
					button.setEnabled(false);
				}
			}
		}
		if (ap.getPlayer() != null) {
			PlayerColor pc = ap.getPlayer().getColor();
			setBorder(playerBorders[pc.ordinal()]);
		} else {
			setBorder(defaultPanelBorder);
		}
	}

	public void resetActions() {
		for (Action action : actions.values()) {
			action.reset();
		}
	}

	public void initActions() {
		for (Action action : actions.values()) {
			ap.postEdit(action.init());
		}
		revalidate();
	}

	public void disableActions() {
		for (JButton b : actionButtons.values()) {
			b.setEnabled(false);
		}
	}
	
	private static Border createPlayerBorder(PlayerColor c) {
		return BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(
				Board.BORDER_WIDTH,
				c == PlayerColor.RED ? Board.BORDER_WIDTH : 0,
				Board.BORDER_WIDTH,
				c == PlayerColor.BLUE ? Board.BORDER_WIDTH : 0,
				c.getRealColor()),
				BorderFactory.createEmptyBorder(
						0,
						c == PlayerColor.RED ? 0 : Board.BORDER_WIDTH,
						0,
						c == PlayerColor.BLUE ? 0 : Board.BORDER_WIDTH));
	}

	private class ActionUsageListener implements ActionStateChangeListener {

		private final ActionButton actionButtton;

		public ActionUsageListener(ActionButton button) {
			actionButtton = button;
		}

		public void stateChanges(Action action) {
			if (!action.isUsed()) {
				actionButtton.setEnabled(ap.getPlayer() != null && action.canDo(ap.getPlayer()));
			} else {
				actionButtton.setEnabled(false);
			}
			actionButtton.setUsed(action.getUser());
		}
	}

}
