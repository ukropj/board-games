package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.ActionPerformer;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.SwingUtils;

@SuppressWarnings("serial")
public class ActionBoard extends JPanel {

	private final ActionPerformer ap;
	private final List<Action> actions;
	private final Map<ActionType, JButton> actionButtons = new HashMap<ActionType, JButton>();
	private final JPanel actionPanel;
	private final JPanel controlPanel;
	private final Color defaultColor;
//	private final Border defaultBorder;

	private JButton moreB;
	private JButton lessB;
	private JButton submitB;
	private JButton resetB;

	public ActionBoard(List<Action> actions, final ActionPerformer ap, ActionListener submitListener) {
		this.actions = actions;
		this.ap = ap;

		setLayout(new BorderLayout());

		actionPanel = new JPanel(new GridBagLayout());

		for (final Action action : actions) {
			final ActionType type = action.getType();
			final JButton b = new JButton();
			b.setMargin(new Insets(1, 1, 1, 1));
			b.setAlignmentX(JButton.CENTER_ALIGNMENT);
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (!action.isUsed()) {
						if (ap.doAction(action)) {
							switchToControl(true);
							b.setBackground(ap.getPlayer().getColor().getRealColor());
//							b.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
						}
					}
				}
			});
			
			actionButtons.put(type, b);
			ActionPanelFactory.createActionPanel(actionPanel, action, b);
		}

		controlPanel = SwingUtils.createFlowPanel(5, 0);
		buildControlPanel(submitListener);

		add(actionPanel, BorderLayout.CENTER);
		add(controlPanel, BorderLayout.SOUTH);

		defaultColor = resetB.getBackground();
//		defaultBorder = resetB.getBorder();
	}

	private void buildControlPanel(final ActionListener submitListener) {
		submitB = createButton(Msg.get("finishAction"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ap.hasAction()) {
//					Action action = ap.getAction();
					if (ap.finishAction()) {
//						JButton b = actionButtons.get(action.getType());
//						b.setBorder(defaultBorder);
						switchToControl(false);
						submitListener.actionPerformed(e);
					} else {
						System.out.println("Cannot finish action, player has unused stuff or invalid animals");
					}
				} else {
					submitListener.actionPerformed(e);
				}
			}
		});
		moreB = createButton(Msg.get("doMoreAction"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ap.doActionMore();
				updateControls();
			}
		});
		lessB = createButton(Msg.get("doLessAction"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ap.doActionLess();
				updateControls();
			}
		});
		resetB = createButton(Msg.get("undoAction"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Action action = ap.getAction();
				if (ap.undoAction()) {
					JButton b = actionButtons.get(action.getType());
					b.setBackground(defaultColor);
//					b.setBorder(defaultBorder);
					switchToControl(false);
				}
			}
		});
	}

	public JButton getSubmitButton() {
		return submitB;
	}

	private void switchToControl(boolean on) {
		for (Entry<ActionType, JButton> btnEntry : actionButtons.entrySet()) {
			btnEntry.getValue().setEnabled(!on);
		}
		for (Component c : controlPanel.getComponents()) {
			c.setEnabled(on);
		}
		if (on) {
			updateControls();
		}
	}

	private void updateControls() {
		moreB.setEnabled(ap.canDoMore());
		lessB.setEnabled(ap.canDoLess());
	}

	private JButton createButton(String label, ActionListener al) {
		JButton b = new JButton(label);
//		b.setPreferredSize(new Dimension(50, 30));
		b.addActionListener(al);
		controlPanel.add(b);
		return b;
	}

	public void resetActions() {
		for (Action action : actions) {
			action.reset();
		}
	}

	public void initActions() {
		for (Action action : actions) {
			action.init();
		}
		switchToControl(false);
	}

	public void clearActions() {
		for (Entry<ActionType, JButton> btnEntry : actionButtons.entrySet()) {
			btnEntry.getValue().setBackground(defaultColor);
		}
	}

	public void enableSubmitOnly() {
		for (Component c : actionPanel.getComponents()) {
			c.setEnabled(false);
		}
		for (Component c : controlPanel.getComponents()) {
			c.setEnabled(false);
		}
		submitB.setEnabled(true);
	}

}
