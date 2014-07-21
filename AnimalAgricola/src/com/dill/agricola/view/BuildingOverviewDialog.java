package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.Main;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.Images;
import com.dill.agricola.view.utils.UiFactory;

public class BuildingOverviewDialog extends JDialog implements ActionListener, ItemListener {
	private static final long serialVersionUID = 1L;

	private JPanel buildingPanel;
	private JPanel selectorPanel;
	private JScrollPane selectorPanelPane;

	boolean selectorBuilt = false;

	public BuildingOverviewDialog(JFrame parent, boolean canRandomize) {
		super(parent);
		setTitle(Msg.get("buildingOverviewTitle"));
		setIconImage(Images.createIcon("document-new", ImgSize.SMALL).getImage());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);

		buildOptions(canRandomize);
		pack();
		setLocationRelativeTo(parent);
		setMinimumSize(new Dimension(600, 400));

		if (!Main.DEBUG) {
			setVisible(true);
		} else {
			dispose();
		}
	}

	private void buildOptions(boolean canRandomize) {
		JPanel main = UiFactory.createVerticalPanel();
		main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		getContentPane().add(main);

		main.add(buildBuildingPanel());
		main.add(buildSelectorPanel());

		JPanel buttonP = UiFactory.createFlowPanel(5, 0);

		JButton submitButton = UiFactory.createTextButton(Msg.get("startGameBtn"), this);
		submitButton.setActionCommand(OptionCommand.SUBMIT.toString());
		buttonP.add(submitButton);
		buttonP.add(Box.createHorizontalStrut(10));

		if (canRandomize) {
			JButton randomizeButton = UiFactory.createTextButton(Msg.get("randomizeBtn"), this);
			randomizeButton.setActionCommand(OptionCommand.RANDOMIZE.toString());
			buttonP.add(randomizeButton);
		}

		JButton selectButton = UiFactory.createTextButton(Msg.get("openSelectorBtn"), this);
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

		buildingPanel = new JPanel(new GridLayout(2, 0, 3, 3));
		buildingPanel.setOpaque(true);
		buildingP.add(buildingPanel, BorderLayout.CENTER);
		updateBuildingPanel();
		return buildingP;
	}

	private void updateBuildingPanel() {
		buildingPanel.removeAll();
		for (BuildingType type : GeneralSupply.getBuildingsAll()) {
			BuildingLabel bl = new BuildingLabel(type, ImgSize.BIG);
			buildingPanel.add(bl);
		}
		buildingPanel.repaint();
		pack();
	}

	private JComponent buildSelectorPanel() {
		/*JPanel buildingP = UiFactory.createBorderPanel(5, 0);
		buildingP.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(Msg.get("specialBuildingsForGameTitle")),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)
				));*/

		selectorPanel = new JPanel(new GridLayout(1, 0, 3, 3));
//		buildingP.add(selectorPanel, BorderLayout.CENTER);

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
					updateBuildingPanel();
				}
			});
			bl.setUsed(enabledBuildings.contains(type));
			selectorPanel.add(bl);
		}
		selectorPanel.revalidate();
	}

	private static enum OptionCommand {
		SUBMIT, RANDOMIZE, OPEN_SELECTOR;
	}

	public void actionPerformed(ActionEvent e) {
		OptionCommand command = OptionCommand.valueOf(e.getActionCommand());
		switch (command) {
		case RANDOMIZE:
			GeneralSupply.randomizeBuildings();
			updateBuildingPanel();
			updateSelectorPanel();
			pack();
			break;
		case OPEN_SELECTOR:
			boolean visible = selectorPanelPane.isVisible();
			if (!selectorBuilt && !visible) {
				updateSelectorPanel();
				selectorBuilt = true;
			}
			selectorPanelPane.setVisible(!visible);
			updateBuildingPanel();
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
