package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.GeneralSupply.Supplyable;
import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.ActionPerformer;
import com.dill.agricola.actions.StateChangeListener;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
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

	private JButton moreB;
	private JButton lessB;
	private JButton submitB;
	private JButton resetB;

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
						if (ap.doAction(action)) {
							switchToControl(true);
							b.setBackground(ap.getPlayer().getColor().getRealColor());
							b.setEnabled(false);
//							b.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
						}
					}
				}
			});

			actionButtons.put(type, b);
			ActionPanelFactory.createActionPanel(actionPanel, action, b);
		}

		buildGeneralSupplyPanel();

		controlPanel = UiFactory.createFlowPanel(5, 0);
		buildControlPanel(submitListener);

		add(actionPanel, BorderLayout.CENTER);
		add(controlPanel, BorderLayout.SOUTH);

		defaultColor = resetB.getBackground();
	}

	private void buildGeneralSupplyPanel() {
		JPanel p = UiFactory.createFlowPanel(15, 0);

		JLabel t = UiFactory.createPurchasableLabel(Purchasable.TROUGH, GeneralSupply.MAX_TROUGHS, UiFactory.ICON_FIRST);
		actions.get(ActionType.TROUGHS).addChangeListener(new SupplyChangeListener(Supplyable.TROUGH, t));
		p.add(t);
		JLabel e = UiFactory.createPurchasableLabel(Purchasable.EXTENSION, GeneralSupply.EXTS.length, UiFactory.ICON_FIRST);
		actions.get(ActionType.EXPAND).addChangeListener(new SupplyChangeListener(Supplyable.EXTENSION, e));
		p.add(e);
		JLabel s = UiFactory.createGeneralLabel(GeneralSupply.STALLS.length, AgriImages.getBuildingIcon(BuildingType.STALL, ImgSize.SMALL),
				UiFactory.ICON_FIRST);
		StateChangeListener stallListener = new SupplyChangeListener(Supplyable.STALL, s);
		actions.get(ActionType.STALLS).addChangeListener(stallListener);
		actions.get(ActionType.SPECIAL).addChangeListener(stallListener);
		actions.get(ActionType.SPECIAL2).addChangeListener(stallListener);
		p.add(s);

		JPanel b = UiFactory.createFlowPanel(5, 0);
		StateChangeListener buildingListener = new BuildingChangeListener(b);
		actions.get(ActionType.SPECIAL).addChangeListener(buildingListener);
		actions.get(ActionType.SPECIAL2).addChangeListener(buildingListener);
		p.add(b);

		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 9;
		c.gridwidth = 6;
		c.fill = GridBagConstraints.HORIZONTAL;
		actionPanel.add(p, c);
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
					b.setEnabled(true);
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
		for (Action action : actions.values()) {
			action.reset();
		}
	}

	public void initActions() {
		for (Action action : actions.values()) {
			action.init();
		}
		switchToControl(false);
	}

	public void clearActions() {
		for (JButton btn : actionButtons.values()) {
			btn.setBackground(defaultColor);
			btn.setEnabled(true);
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

	private static class SupplyChangeListener implements StateChangeListener {

		private final Supplyable type;
		private final JLabel label;

		public SupplyChangeListener(Supplyable type, JLabel label) {
			this.type = type;
			this.label = label;
		}

		public void stateChanges(Action action) {
			label.setText(String.valueOf(GeneralSupply.getLeft(type)));
		}

	}

	private static class BuildingChangeListener implements StateChangeListener {

		private JPanel buildingPanel;

		public BuildingChangeListener(JPanel buildingPanel) {
			this.buildingPanel = buildingPanel;
		}

		public void stateChanges(Action action) {
			buildingPanel.removeAll();
			for (BuildingType b : GeneralSupply.getBuildingsLeft()) {
				buildingPanel.add(new JLabel(AgriImages.getBuildingIcon(b, ImgSize.SMALL)));
			}
		}
	}

}
