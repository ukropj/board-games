package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.Main;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.Images;
import com.dill.agricola.view.utils.UiFactory;

public class BuildingOverviewDialog extends JDialog implements ActionListener, ItemListener {
	private static final long serialVersionUID = 1L;

	private JPanel[] buildingPanels;
	private JTextField[] buildingCounters;
	private JButton[] buildingButtons;
	private JPanel selectorPanel;
	private JScrollPane selectorPanelPane;

	boolean selectorBuilt = false;

	private final boolean more;
	private final boolean evenMore;

	private final int[] buildingCounts = { 0, 0, 0 };

	public BuildingOverviewDialog(JFrame parent, boolean more, boolean evenMore) {
		super(parent);
		setTitle(Msg.get("buildingOverviewTitle"));
		setIconImage(Images.createIcon("document-new", ImgSize.SMALL).getImage());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(true);

		this.more = more;
		this.evenMore = evenMore;
		buildOptions();
		setMinimumSize(new Dimension(720, 285));
		pack();

		setLocationRelativeTo(parent); // call after pack!

		if (!Main.DEBUG) {
			setVisible(true);
		} else {
			dispose();
		}
	}

	private void buildOptions() {
		JPanel main = UiFactory.createVerticalPanel();
		main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		getContentPane().add(main, BorderLayout.CENTER);

		main.add(buildBuildingPanel());
		main.add(buildSelectorPanel());

		JPanel buttonP = UiFactory.createHorizontalPanel();
		buttonP.add(Box.createHorizontalStrut(100));
		buttonP.add(Box.createHorizontalGlue());

		JButton submitButton = UiFactory.createTextButton(Msg.get("startGameBtn"), this);
		submitButton.setActionCommand(OptionCommand.SUBMIT.toString());
		buttonP.add(submitButton);
		buttonP.add(Box.createHorizontalGlue());

		JButton randomizeButton = UiFactory.createIconButton("view-refresh", Msg.get("randomizeBtnTitle"), this);
		randomizeButton.setActionCommand(OptionCommand.RANDOMIZE.toString());
		buttonP.add(randomizeButton);

		buttonP.add(Box.createHorizontalStrut(5));

		JButton selectButton = UiFactory.createIconButton("system-search", Msg.get("openSelectorBtnTitle"), this);
		selectButton.setActionCommand(OptionCommand.OPEN_SELECTOR.toString());
		buttonP.add(selectButton);

		main.add(Box.createVerticalStrut(5));
		main.add(buttonP);
	}

	private JComponent buildBuildingPanel() {
		JPanel buildingP = UiFactory.createBorderPanel(5, 0);
		buildingP.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(Msg.get("specialBuildingsForGameTitle")),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)
				));

		JPanel allBuildingsPanel = UiFactory.createVerticalPanel();

		buildingCounters = new JTextField[3];
		buildingButtons = new JButton[6];
		buildingPanels = new JPanel[] { null, null, null };
		buildingPanels[0] = buildBuildingRow(allBuildingsPanel, 0);
		if (more) {
			buildingPanels[1] = buildBuildingRow(allBuildingsPanel, 1);
		}
		if (evenMore) {
			buildingPanels[2] = buildBuildingRow(allBuildingsPanel, 2);
		}

		JScrollPane sp = new JScrollPane(allBuildingsPanel);
		sp.setBorder(BorderFactory.createEmptyBorder());
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		sp.setPreferredSize(new Dimension(650, 15 + 160 * (1 + (more ? 1 : 0) + (evenMore ? 1 : 0))));

		buildingP.add(sp, BorderLayout.CENTER);
		updateBuildingPanelsAfterRandomize();
		return buildingP;
	}

	private JPanel buildBuildingRow(JComponent parent, final int set) {
		final JPanel buildingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
		buildingsPanel.setOpaque(true);

		buildingCounts[set] = GeneralSupply.INITIAL_BUILDING_COUNTS[set];

		final JTextField countTB = new JTextField(String.valueOf(buildingCounts[set]), 1);
		countTB.setHorizontalAlignment(JTextField.TRAILING);
		countTB.setEditable(false);
		final JButton upBtn = new BasicArrowButton(SwingConstants.NORTH);
		upBtn.setActionCommand(OptionCommand.MORE.toString());
		final JButton dnBtn = new BasicArrowButton(SwingConstants.SOUTH);
		dnBtn.setActionCommand(OptionCommand.LESS.toString());

		ActionListener countListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OptionCommand command = OptionCommand.valueOf(e.getActionCommand());
				int d = command == OptionCommand.MORE ? +1 : -1;
				int newCount = buildingCounts[set] + d;
				if (newCount >= 0 && newCount <= BuildingType.set(set).size()) {
					buildingCounts[set] = newCount;
					countTB.setText(String.valueOf(newCount));
					upBtn.setEnabled(newCount < BuildingType.set(set).size());
					dnBtn.setEnabled(newCount > 0);

					updateBuildingPanel(set);
					if (selectorBuilt) {
						updateSelectorPanel();
					}
				}
			}
		};
		upBtn.addActionListener(countListener);
		dnBtn.addActionListener(countListener);
		upBtn.setEnabled(buildingCounts[set] < BuildingType.set(set).size());
		dnBtn.setEnabled(buildingCounts[set] > 0);

		JPanel controlPanel = new JPanel(new GridLayout(5, 1));
		controlPanel.add(Box.createVerticalGlue());
		controlPanel.add(upBtn, BorderLayout.NORTH);
		controlPanel.add(countTB, BorderLayout.CENTER);
		controlPanel.add(dnBtn, BorderLayout.SOUTH);
		controlPanel.add(Box.createVerticalGlue());
		buildingCounters[set] = countTB;
		buildingButtons[set * 2] = upBtn;
		buildingButtons[set * 2 + 1] = dnBtn;

		JPanel p = UiFactory.createBorderPanel(3, 0);
		p.add(buildingsPanel, BorderLayout.CENTER);
		p.add(controlPanel, BorderLayout.LINE_START);

		parent.add(p);
		// setup popup
		final JPopupMenu popup = new JPopupMenu();
		ActionListener popupActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Component c = popup.getInvoker();
				if (c == null || !(c instanceof BuildingLabel)) {
					return;
				}
				OptionCommand command = OptionCommand.valueOf(e.getActionCommand());
				BuildingType type = ((BuildingLabel) c).getType();
				switch (command) {
				case RANDOMIZE_ONE:
					GeneralSupply.enableBuilding(type, false);
					break;
				case RANDOMIZE_SET:
					GeneralSupply.randomizeBuildings(set, buildingCounts[set]);
					updateBuildingPanel(set);
					updateSelectorPanel();
					break;
				case REMOVE_ONE:
					GeneralSupply.enableBuilding(type, false);
					buildingCounts[set]--;
					break;
				default:
					return;
				}
				updateBuildingPanel(set);
				updateSelectorPanel();
			}
		};
		JMenuItem menuItem = new JMenuItem(Msg.get("removeOneMenu"));
		menuItem.setActionCommand(OptionCommand.REMOVE_ONE.toString());
		menuItem.addActionListener(popupActionListener);
		popup.add(menuItem);
		menuItem = new JMenuItem(Msg.get("randomizeOneMenu"));
		menuItem.setActionCommand(OptionCommand.RANDOMIZE_ONE.toString());
		menuItem.addActionListener(popupActionListener);
		popup.add(menuItem);
		popup.addSeparator();
		menuItem = new JMenuItem(Msg.get("randomizeSetMenu"));
		menuItem.setActionCommand(OptionCommand.RANDOMIZE_SET.toString());
		menuItem.addActionListener(popupActionListener);
		popup.add(menuItem);
		buildingsPanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}

			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e) {
				if (e.isPopupTrigger() && e.getComponent() instanceof BuildingLabel) {
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		return buildingsPanel;
	}

	private void updateBuildingPanel(int set) {
		JPanel p = buildingPanels[set];
		if (p == null) {
			return;
		}
		int expectedCount = buildingCounts[set];
		p.removeAll();
		List<BuildingType> currentBuildings = new ArrayList<BuildingType>(GeneralSupply.getBuildingsAll());
		for (BuildingType type : currentBuildings) {
			if (type.set == set) {
				if (p.getComponentCount() < expectedCount) {
					p.add(new BuildingLabel(type, ImgSize.BIG, p.getMouseListeners()[0]));
				} else {
					GeneralSupply.enableBuilding(type, false);
				}
			}
		}
		if (p.getComponentCount() < expectedCount) {
			List<BuildingType> missingTypes = GeneralSupply.getNextRandomBuildings(set, buildingCounts[set] - p.getComponentCount());
			for (BuildingType type : missingTypes) {
				p.add(new BuildingLabel(type, ImgSize.BIG, p.getMouseListeners()[0]));
				GeneralSupply.enableBuilding(type, true);
			}
		}
		buildingCounters[set].setText(String.valueOf(expectedCount));
		buildingButtons[set * 2].setEnabled(expectedCount < BuildingType.set(set).size());
		buildingButtons[set * 2 + 1].setEnabled(expectedCount > 0);
		p.repaint();
		p.revalidate();
	}

	private void updateBuildingPanelsAfterRandomize() {
		for (JPanel p : buildingPanels) {
			if (p != null) {
				p.removeAll();
			}
		}
		for (BuildingType type : GeneralSupply.getBuildingsAll()) {
			JPanel p = buildingPanels[type.set];
			if (p != null) {
				p.add(new BuildingLabel(type, ImgSize.BIG, p.getMouseListeners()[0]));
			}
		}
		for (JPanel p : buildingPanels) {
			if (p != null) {
				p.repaint();
				p.revalidate();
			}
		}
	}

	private JComponent buildSelectorPanel() {
		selectorPanel = new JPanel(new GridLayout(1, 0, 3, 3));

		selectorPanelPane = new JScrollPane(selectorPanel);
		selectorPanelPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		selectorPanelPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		selectorPanelPane.setVisible(false);

		selectorPanelPane.setPreferredSize(new Dimension(100, 180));

		return selectorPanelPane;
	}

	private void updateSelectorPanel() {
		selectorPanel.removeAll();
		List<BuildingType> enabledBuildings = GeneralSupply.getBuildingsAll();
		for (final BuildingType type : GeneralSupply.getBuildingsAllPossible()) {
			final BuildingLabel bl = new BuildingLabel(type, ImgSize.BIG);
			bl.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					boolean used = bl.isUsed();
					bl.setUsed(!used);
					GeneralSupply.enableBuilding(type, !used);
					buildingCounts[type.set] += !used ? 1 : -1;
					updateBuildingPanel(type.set);
				}
			});
			bl.setUsed(enabledBuildings.contains(type));
			selectorPanel.add(bl);
		}
		selectorPanel.revalidate();
	}

	private static enum OptionCommand {
		SUBMIT, RANDOMIZE, OPEN_SELECTOR, MORE, LESS, REMOVE_ONE, RANDOMIZE_ONE, RANDOMIZE_SET;
	}

	public void actionPerformed(ActionEvent e) {
		OptionCommand command = OptionCommand.valueOf(e.getActionCommand());
		switch (command) {
		case RANDOMIZE:
			GeneralSupply.randomizeBuildings(buildingCounts);
			updateBuildingPanelsAfterRandomize();
			updateSelectorPanel();
			break;
		case OPEN_SELECTOR:
			boolean visible = selectorPanelPane.isVisible();
			if (!selectorBuilt && !visible) {
				updateSelectorPanel();
				selectorBuilt = true;
			}
			selectorPanelPane.setVisible(!visible);
//			updateBuildingPanels();
			pack();
			break;
		case SUBMIT:
			setVisible(false);
			dispose();
			break;
		default:
			break;
		}
	}

	public void itemStateChanged(ItemEvent e) {
//		JCheckBox source = (JCheckBox) e.getItemSelectable();
//		OptionCommand command = OptionCommand.valueOf(source.getActionCommand());
//		boolean selected = e.getStateChange() == ItemEvent.SELECTED;
//		switch (command) {
//		case MORE_BUILDINGS:
//			useMoreBuildings = selected;
//			break;
//		case EVEN_MORE_BUILDINGS:
//			useEvenMoreBuildings = selected;
//			break;
//		default:
//			break;
//		}
	}

}