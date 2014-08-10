package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.ActionPerformer;
import com.dill.agricola.actions.ActionStateChangeListener;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.Board.PlayerBorderFactory;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.Images;

public class ActionBoard extends JPanel {
	private static final long serialVersionUID = 1L;

	private final ActionPerformer ap;

	private final Map<ActionType, Action> actions = new EnumMap<ActionType, Action>(ActionType.class);
	private final Map<ActionType, ActionButton> actionButtons = new EnumMap<ActionType, ActionButton>(ActionType.class);

	private final JTabbedPane tabPane;
	private int scoringTabIndex;
	private int actionsTabIndex;

	private BuildingsPanel buildingPanel;
	private JPanel buildingDisplay;

	private boolean actionsDisabled;
	private boolean condensedLayout;

	public ActionBoard(List<Action> actions, final ActionPerformer ap, final ActionListener submitListener, boolean condensedLayout) {
		this.ap = ap;
		this.condensedLayout = condensedLayout;
		setLayout(new BorderLayout());
		setBorder(PlayerBorderFactory.getBorder());

		tabPane = new JTabbedPane();
		add(tabPane, BorderLayout.CENTER);

		JPanel actionPanel = new JPanel(new GridBagLayout());

		buildingDisplay = ActionPanelFactory.createBuildingPanel(actionPanel);

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

			if (type == ActionType.SPECIAL || type == ActionType.SPECIAL2) {
				ActionPanelFactory.bindBuildingPanel(action, buildingDisplay);
			}
		}

		tabPane.addTab(Msg.get("actionsTitle"), AgriImages.getFirstTokenIcon(1, ImgSize.SMALL), actionPanel);
		actionsTabIndex = tabPane.indexOfComponent(actionPanel);
	}

	public void addTabs(final ScorePanel scorePanel) {
		buildingPanel = new BuildingsPanel();
		tabPane.addTab(Msg.get("buildingsTitle"), Images.createIcon("go-home", ImgSize.SMALL), buildingPanel);
		tabPane.addTab(Msg.get("animalTitle"), AgriImages.getAnimalIcon(Animal.HORSE, ImgSize.SMALL), new AnimalScoringPanel());
		tabPane.addTab(Msg.get("scoresTitle"), Images.createIcon("application-certificate", ImgSize.SMALL), scorePanel);

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
	}

	public void showActions() {
		tabPane.setSelectedIndex(actionsTabIndex);
	}

	public void showScoring() {
		tabPane.setSelectedIndex(scoringTabIndex);
	}
	
	public void resetBuildings() {
		buildingPanel.resetBuildings();
		ActionPanelFactory.repopulateBuildingPanel(buildingDisplay);
	}

	public void updateActions() {
		actionsDisabled = false;
		for (Entry<ActionType, ActionButton> btnEntry : actionButtons.entrySet()) {
			ActionType type = btnEntry.getKey();
			Action a = actions.get(type);
			ActionButton button = btnEntry.getValue();
			if (ap.hasAction()) {
				// when action is being performed, disable everything
				button.setEnabled(false);
				button.setActive(ap.hasAction(type));
			} else {
				// when no action being performed, disable those that cannot be currently performed
				if (!a.isUsed()) {
					button.setEnabled(ap.getPlayer() != null && a.canDo(ap.getPlayer()));
				} else {
					button.setEnabled(false);
				}
				button.setActive(false);
			}
			updateBorder();
		}
	}

	private void updateBorder() {
		if (ap.getPlayer() != null) {
			PlayerColor pc = ap.getPlayer().getColor();
			setBorder(PlayerBorderFactory.getBorder(pc, condensedLayout ? true : pc == PlayerColor.BLUE));
		} else {
			setBorder(PlayerBorderFactory.getBorder());
		}
	}

	public void resetActions() {
		actionsDisabled = false;
		for (Action action : actions.values()) {
			action.reset();
		}
	}

	public void initActions() {
		actionsDisabled = false;
		for (Action action : actions.values()) {
			ap.postEdit(action.init());
		}
		revalidate();
	}

	public void disableActions() {
		actionsDisabled = true;
		for (ActionButton b : actionButtons.values()) {
			b.setEnabled(false);
		}
	}

	public void setLayout(boolean isCondensed) {
		this.condensedLayout = isCondensed;
		updateBorder();
	}

	private class ActionUsageListener implements ActionStateChangeListener {

		private final ActionButton actionButtton;

		public ActionUsageListener(ActionButton button) {
			actionButtton = button;
		}

		public void stateChanges(Action action) {
			actionButtton.setEnabled(!actionsDisabled
					&& !action.isUsed()
					&& ap.getPlayer() != null
					&& action.canDo(ap.getPlayer()));
			actionButtton.setUsed(action.getUser());
		}
	}

}
