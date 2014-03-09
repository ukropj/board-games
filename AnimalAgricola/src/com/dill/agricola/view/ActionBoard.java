package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
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
	private final JPanel controlPanel;
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

//		buildGeneralSupplyPanel();

		controlPanel = UiFactory.createFlowPanel(5, 0);
		buildControlPanel(submitListener);

		add(actionPanel, BorderLayout.CENTER);
		add(controlPanel, BorderLayout.SOUTH);

		defaultColor = revertB.getBackground();
	}

	/*private void buildGeneralSupplyPanel() {
		JPanel b = UiFactory.createFlowPanel(5, 0);
		b.setOpaque(true);
		b.setBackground(Color.RED);
		StateChangeListener buildingListener = new BuildingChangeListener(b);
		buildingListener.stateChanges(null);
		actions.get(ActionType.SPECIAL).addChangeListener(buildingListener);
		actions.get(ActionType.SPECIAL2).addChangeListener(buildingListener);

		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 9;
		c.gridwidth = 6;
		c.fill = GridBagConstraints.BOTH;
		actionPanel.add(b, c);
	}*/

	private void buildControlPanel(final ActionListener submitListener) {
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
		controlPanel.add(finishB);

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
		controlPanel.add(revertB);

		ap.setActionPerfListener(new ActionPerfListener() {
			public void stateChanges() {
				updateControls();
			}
		});
	}

	public void updateActions() {
		for (Entry<ActionType, JButton> btnEntry : actionButtons.entrySet()) {
			ActionType type = btnEntry.getKey();
			JButton button = btnEntry.getValue();
			if (ap.hasAction()) {
				// when action is being performed, disable everything
				button.setEnabled(false);
				if (ap.hasAction(type)) {
					// TODO mark current action
				}
			} else {
				// when no action being performed, disable those that cannot be currently performed 
				Action a = actions.get(type);
				btnEntry.getValue().setEnabled(ap.getPlayer() != null && a.canDo(ap.getPlayer(), 0));
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
		for (Component c : controlPanel.getComponents()) {
			c.setEnabled(false);
		}
		finishB.setEnabled(true);
	}
}
