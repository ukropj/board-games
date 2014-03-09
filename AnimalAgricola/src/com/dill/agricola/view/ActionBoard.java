package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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

import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.ActionPerformer;
import com.dill.agricola.actions.ActionPerformer.ActionPerfListener;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.UiFactory;

@SuppressWarnings("serial")
public class ActionBoard extends JPanel {

	private final ActionPerformer ap;
	private final Map<ActionType, Action> actions = new EnumMap<ActionType, Action>(ActionType.class);
	private final Map<ActionType, JButton> actionButtons = new EnumMap<ActionType, JButton>(ActionType.class);
	private final JPanel actionPanel;
	private final JLabel hintLabel;
	private final Color defaultColor;
//	private final Border defaultBorder;

	private JButton finishB;
	private JButton revertB;

	public ActionBoard(List<Action> actions, final ActionPerformer ap, ActionListener submitListener) {
		this.ap = ap;

		setLayout(new BorderLayout());

		actionPanel = new JPanel(new GridBagLayout());

		for (final Action action : actions) {
			final ActionType type = action.getType();
			this.actions.put(type, action);
			final JButton b = new JButton();
			b.setMargin(new Insets(1, 1, 1, 1));
			b.setAlignmentX(JButton.CENTER_ALIGNMENT);
			b.setCursor(new Cursor(Cursor.HAND_CURSOR));
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (!action.isUsed()) {
						if (ap.startAction(action)) {
							b.setBackground(ap.getPlayer().getColor().getRealColor());
							updateActions();
						}
					}
				}
			});

			actionButtons.put(type, b);
			ActionPanelFactory.createActionPanel(actionPanel, action, b);
		}
		
		hintLabel = UiFactory.createLabel("abbc");
		buildControlPanel(submitListener);

		add(actionPanel, BorderLayout.CENTER);

		defaultColor = revertB.getBackground();
	}

	private void buildControlPanel(final ActionListener submitListener) {
		GridBagConstraints c = new GridBagConstraints();
		JPanel controlPanel = UiFactory.createBorderPanel(5,5);
		controlPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		controlPanel.add(hintLabel, BorderLayout.CENTER);
		c.gridy = 10;
		c.gridwidth = 6;
		c.gridheight = 2;
		c.ipadx = c.ipady = 3;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.BOTH;
		actionPanel.add(controlPanel, c);
		
		JPanel buttons = UiFactory.createFlowPanel(5, 5);
		finishB = new JButton(Msg.get("finishAction"));
		finishB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ap.hasAction()) {
					if (ap.finishAction()) {
						updateActions();
						submitListener.actionPerformed(e);
					} else {
						System.out.println("Cannot finish action");
					}
				} else {
					submitListener.actionPerformed(e);
				}
			}
		});
		finishB.setCursor(new Cursor(Cursor.HAND_CURSOR));
		buttons.add(finishB);

		revertB = new JButton(Msg.get("undoAction"));
		revertB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Action action = ap.getAction();
				if (ap.revertAction()) {
					JButton b = actionButtons.get(action.getType());
					b.setBackground(defaultColor);
					b.setEnabled(true);
					updateActions();
				}
			}
		});
		revertB.setCursor(new Cursor(Cursor.HAND_CURSOR));
		buttons.add(revertB);
		
		controlPanel.add(buttons, BorderLayout.SOUTH);

		ap.setActionPerfListener(new ActionPerfListener() {

			public void stateChanges(Action action) {
				updateControls();
//				messageLabel
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
					StringBuilder text = new StringBuilder("<html>");
					if (a.isResourceAction()) {
						text.append( Msg.get("resourcesRecieved"));
					}
					if (a.isPurchaseAction()) {
						if (text.length() > "<html>".length()) {
							text.append("<br>");
						}
						text.append( Msg.get("purchaseExpected"));
					}
					hintLabel.setText(text.toString());
				}
			} else {
				// when no action being performed, disable those that cannot be currently performed 
				btnEntry.getValue().setEnabled(ap.getPlayer() != null && a.canDo(ap.getPlayer(), 0));
				
				hintLabel.setText(Msg.get("chooseAction"));
			}
		}
		updateControls();
	}

	private void updateControls() {
		finishB.setEnabled(ap.canFinish());
		revertB.setEnabled(ap.canRevert());
	}

	public void resetActions() {
		for (Action action : actions.values()) {
			action.reset();
		}
		clearActions();
	}

	public void initActions() {
		for (Action action : actions.values()) {
			action.init();
		}
	}

	public void clearActions() {
		for (JButton btn : actionButtons.values()) {
			btn.setBackground(defaultColor);
			btn.setEnabled(true);
		}
	}

	public void enableFinishOnly() {
		for (Component c : actionPanel.getComponents()) {
			c.setEnabled(false);
		}
		revertB.setEnabled(true);
		finishB.setEnabled(true);
	}
}
