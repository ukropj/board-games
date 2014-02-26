package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import com.dill.agricola.view.utils.SwingUtils;

@SuppressWarnings("serial")
public class ActionBoard extends JPanel {

	private final ActionPerformer ap;
	private final List<Action> actions;
	private final Map<ActionType, JButton> actionButtons = new HashMap<ActionType, JButton>();
	private final JPanel actionPanel;
	private final JPanel controlPanel;
	private final Color defaultColor;

	private JButton addB;
	private JButton removeB;
	private JButton submitB;
	private JButton resetB;

	public ActionBoard(List<Action> actions, final ActionPerformer ap, ActionListener submitListener) {
		this.actions = actions;
		this.ap = ap;

		setLayout(new BorderLayout());

//		actionPanel = SwingUtils.createVerticalPanel();
		actionPanel = new JPanel(new GridBagLayout());
//		JPanel rowPanel = null;
//		int i = 0;

//		List<Integer> rowStarts = Arrays.asList(new Integer[] { 0, 2, 4, 5, 7, 9, 11, 13 });

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
						}
					}
				}
			});
			
			actionButtons.put(type, b);
//			if (rowStarts.contains(i)) {
//				rowPanel = new JPanel(new GridLayout(1, 0, 0, 0));
//				actionPanel.add(rowPanel);
//			}
			/*JComponent actionPanel = */ActionPanelFactory.createActionPanel(actionPanel, action, b);
			/*if (actionPanel != null) {
				rowPanel.add(actionPanel);				
				rowPanel.add(actionPanel);				
			}
			i++;*/
		}

		controlPanel = SwingUtils.createFlowPanel(5, 0);
		buildControlPanel(submitListener);

		add(actionPanel, BorderLayout.CENTER);
		add(controlPanel, BorderLayout.SOUTH);

		defaultColor = resetB.getBackground();
	}

	private void buildControlPanel(final ActionListener submitListener) {
		submitB = createButton("Ok", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ap.hasAction()) {
					//					Action action = ap.getAction();
					if (ap.finishAction()) {
						switchToControl(false);
						//						JButton b = actionButtons.get(action.getType());
						submitListener.actionPerformed(e);
					} else {
						System.out.println("Cannot finish action, player has unused stuff or invalid animals");
					}
				} else {
					submitListener.actionPerformed(e);
				}
			}
		});
		addB = createButton("+", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ap.doActionMore();
				updateControls();
			}
		});
		removeB = createButton("-", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ap.doActionLess();
				updateControls();
			}
		});
		resetB = createButton("X", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Action action = ap.getAction();
				if (ap.undoAction()) {
					JButton b = actionButtons.get(action.getType());
					b.setBackground(defaultColor);
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
		addB.setEnabled(ap.canDoMore());
		removeB.setEnabled(ap.canDoLess());
	}

	private JButton createButton(String label, ActionListener al) {
		JButton b = new JButton(label);
		b.setPreferredSize(new Dimension(50, 30));
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
