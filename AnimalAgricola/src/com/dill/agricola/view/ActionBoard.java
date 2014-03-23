package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.dill.agricola.Game.ActionCommand;
import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.ActionPerformer;
import com.dill.agricola.actions.ActionPerformer.ActionPerfListener;
import com.dill.agricola.actions.ActionStateChangeListener;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.support.Fonts;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.UiFactory;

public class ActionBoard extends JPanel {
	private static final long serialVersionUID = 1L;

	private final ActionPerformer ap;

	private final Map<ActionType, Action> actions = new EnumMap<ActionType, Action>(ActionType.class);
	private final Map<ActionType, JButton> actionButtons = new EnumMap<ActionType, JButton>(ActionType.class);
	private final JPanel actionPanel;
	private final JLabel hintLabel;

	private static final Color defaultBtnColor = UIManager.getColor("Button.background");
	private static final Color defaultPanelColor = UIManager.getColor("Panel.background");

	public ActionBoard(List<Action> actions, final ActionPerformer ap, final ActionListener submitListener) {
		this.ap = ap;

		setLayout(new BorderLayout());

		actionPanel = new JPanel(new GridBagLayout());

		for (final Action action : actions) {
			final ActionType type = action.getType();
			this.actions.put(type, action);
			final JButton b = new JButton();
			b.setEnabled(false);
			b.setMargin(new Insets(1, 1, 1, 1));
			b.setAlignmentX(JButton.CENTER_ALIGNMENT);
			b.setCursor(new Cursor(Cursor.HAND_CURSOR));
			b.setToolTipText(type.name); // TODO better tooltip
			b.setActionCommand(ActionCommand.SUBMIT.toString());
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (!action.isUsed()) {
						if (ap.startAction(action)) {
							b.setBackground(ap.getPlayer().getColor().getRealColor());
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

		hintLabel = UiFactory.createLabel("");
		hintLabel.setFont(Fonts.ACTION_HINT);
		buildControlPanel();

		add(actionPanel, BorderLayout.CENTER);
	}

	private void buildControlPanel() {
		GridBagConstraints c = new GridBagConstraints();
		JPanel controlPanel = UiFactory.createBorderPanel(5, 5);
		controlPanel.setOpaque(true);
		controlPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		controlPanel.add(hintLabel, BorderLayout.CENTER);
		c.gridy = 10;
		c.gridwidth = 6;
		c.gridheight = 2;
		c.ipadx = c.ipady = 3;
		c.insets = new Insets(2, 2, 2, 2);
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		actionPanel.add(controlPanel, c);

		ap.setActionPerfListener(new ActionPerfListener() {
			public void stateChanges(Action action) {
				updateFinishLabel();
			}
		});
	}

	public void updateActions() {
		for (Entry<ActionType, JButton> btnEntry : actionButtons.entrySet()) {
			ActionType type = btnEntry.getKey();
			Action a = actions.get(type);
			JButton button = btnEntry.getValue();
			if (ap.hasAction()) {
				// when action is being performed, disable everything
				button.setEnabled(false);
				if (ap.hasAction(type)) {
					// TODO mark current action
					// update hint message
					StringBuilder text = new StringBuilder();
					if (a.isResourceAction()) {
						text.append(Msg.get("resourcesRecieved"));
					}
					if (a.isPurchaseAction()) {
						if (text.length() > 0) {
							text.append("<br>");
						}
						text.append(Msg.get("purchaseExpected"));
					}
					setHint(text.toString());
				}
			} else {
				// when no action being performed, disable those that cannot be currently performed
				new ActionUsageListener(button).stateChanges(a);
			}
		}
		if (ap.getPlayer() != null) {
			actionPanel.setBackground(ap.getPlayer().getColor().getRealColor());
			if (!ap.hasAction()) {
				setHint(Msg.get("chooseAction"));
			}
		} else {
			actionPanel.setBackground(defaultPanelColor);
		}
		updateFinishLabel();
	}

//	private void updateControls() {
//		finishB.setEnabled(ap.getPlayer() == null || ap.canFinish());
//	}

	private void updateFinishLabel() {
		if (ap.canFinish()) {
			Player p = ap.getPlayer();
			int looseAnimals = p != null ? p.getFarm().getLooseAnimals().size() : -1;
			if (looseAnimals > 0) {
//				finishB.setText(Msg.getNum(looseAnimals, "finishActionRunAway", looseAnimals));
				return;
			}
		}
//		finishB.setText(Msg.get("finishAction"));
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
		setHint(Msg.get("animalsBreed"));
	}

	private void setHint(String str) {
		hintLabel.setText("<html>" + str);
	}
	
	private class ActionUsageListener implements ActionStateChangeListener {

		private final JButton actionButtton;

		public ActionUsageListener(JButton button) {
			actionButtton = button;
		}

		public void stateChanges(Action action) {
			if (!action.isUsed()) {
				actionButtton.setBackground(defaultBtnColor);
				actionButtton.setEnabled(ap.getPlayer() != null && action.canDo(ap.getPlayer()));
			} else {
				actionButtton.setBackground(action.getUser().getRealColor());
				actionButtton.setEnabled(false);
			}
		}
	}

}
