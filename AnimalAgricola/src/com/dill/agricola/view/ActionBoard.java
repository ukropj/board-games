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
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.ActionPerformer;
import com.dill.agricola.actions.ActionPerformer.ActionPerfListener;
import com.dill.agricola.actions.ActionStateChangeListener;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.ChangeType;
import com.dill.agricola.support.Fonts;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.UiFactory;

public class ActionBoard extends JPanel implements Observer {
	private static final long serialVersionUID = 1L;

	private final ActionPerformer ap;

	private final Map<ActionType, Action> actions = new EnumMap<ActionType, Action>(ActionType.class);
	private final Map<ActionType, JButton> actionButtons = new EnumMap<ActionType, JButton>(ActionType.class);
	private final JPanel actionPanel;
	private final JLabel hintLabel;
//	private final Border defaultBorder;

	private static final Color defaultBtnColor = UIManager.getColor("Button.background");
	private static final Color defaultPanelColor = UIManager.getColor("Panel.background");

	private JButton finishB;
	
	final ActionListener submitListener;

	public ActionBoard(List<Action> actions, final ActionPerformer ap, final ActionListener submitListener) {
		this.ap = ap;
		this.submitListener = submitListener;

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
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (!action.isUsed()) {
						if (ap.startAction(action)) {
							b.setBackground(ap.getPlayer().getColor().getRealColor());
							updateActions();

//							if (ap.isFinished()) {
//								submitListener.actionPerformed(e);
//							}
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
		buildControlPanel(submitListener);

		add(actionPanel, BorderLayout.CENTER);
	}

	private void buildControlPanel(final ActionListener submitListener) {
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

		JPanel buttons = UiFactory.createHorizontalPanel();
		buttons.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));

		buttons.add(Box.createHorizontalGlue());
		finishB = new JButton(Msg.get("finishAction"), AgriImages.getYesIcon());
		finishB.setEnabled(false);
		finishB.setToolTipText(Msg.get("finishActionTip"));
		finishB.setMargin(new Insets(1, 2, 2, 2));
		finishB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ap.hasAction()) {
					if (ap.finishAction()) {
						updateActions();
//						submitListener.actionPerformed(e);
					} else {
						System.out.println("Cannot finish action!");
					}
				} else {
					submitListener.actionPerformed(e);
				}
			}
		});
		finishB.setCursor(new Cursor(Cursor.HAND_CURSOR));
		buttons.add(finishB);
		buttons.add(Box.createHorizontalStrut(5));

		controlPanel.add(buttons, BorderLayout.SOUTH);

		ap.setActionPerfListener(new ActionPerfListener() {
			public void stateChanges(Action action) {
				updateControls();
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
		updateControls();
		updateFinishLabel();
	}

	private void updateControls() {
		finishB.setEnabled(ap.getPlayer() == null || ap.canFinish());
	}

	private void updateFinishLabel() {
		if (ap.canFinish()) {
			Player p = ap.getPlayer();
			int looseAnimals = p != null ? p.getFarm().getLooseAnimals().size() : -1;
			if (looseAnimals > 0) {
				finishB.setText(Msg.getNum(looseAnimals, "finishActionRunAway", looseAnimals));
				return;
			}
		}
		finishB.setText(Msg.get("finishAction"));
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

	public void enableFinishOnly() {
		for (Component c : actionPanel.getComponents()) {
			c.setEnabled(false);
		}
		finishB.setEnabled(true);
		setHint(Msg.get("animalsBreed"));
	}

	private void setHint(String str) {
		hintLabel.setText("<html>" + str);
	}

	public void update(Observable o, Object arg) {
		if (arg == ChangeType.FARM_CLICK || arg == ChangeType.FARM_ANIMALS) {
			updateControls();
		}
		if (arg == ChangeType.FARM_ANIMALS) {
			updateFinishLabel();
		}
		if (arg == ChangeType.ACTION_DONE) {
			submitListener.actionPerformed(null);
		}
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
