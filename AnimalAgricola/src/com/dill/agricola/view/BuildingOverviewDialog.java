package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

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

	public BuildingOverviewDialog(JFrame parent) {
		super(parent);
		setTitle(Msg.get("buildingOverviewTitle"));
		setIconImage(Images.createIcon("document-new", ImgSize.SMALL).getImage());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);

		buildOptions();
		pack();
		setLocationRelativeTo(parent);

		if (!Main.DEBUG) {
			setVisible(true);
		} else {
			dispose();
		}
	}

	private void buildOptions() {
		JPanel main = UiFactory.createVerticalPanel();
		main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		getContentPane().add(main);

		main.add(buildBuildingPanel());

		JButton submitButton = UiFactory.createTextButton(Msg.get("startGameBtn"), this);
		submitButton.setActionCommand(OptionCommand.SUBMIT.toString());
		JButton randomizeButton = UiFactory.createTextButton(Msg.get("randomizeBtn"), this);
		randomizeButton.setActionCommand(OptionCommand.RANDOMIZE.toString());
		
		JPanel submitP = UiFactory.createFlowPanel(5, 0);
		submitP.add(submitButton);
		submitP.add(randomizeButton);
		// TODO add some kind of picker

		main.add(Box.createVerticalStrut(5));
		main.add(submitP);
	}

	private JPanel buildBuildingPanel() {
		JPanel buildingP = UiFactory.createBorderPanel(5, 0);
		buildingP.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(Msg.get("specialBuildingsForGameTitle")),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)
				));

		buildingPanel = new JPanel(new GridLayout(0, 4, 3, 3));
		buildingP.add(buildingPanel, BorderLayout.CENTER);
		updateBuildingPanel();
		return buildingP;
	}

	private void updateBuildingPanel() {
		buildingPanel.removeAll();
		for (BuildingType type : GeneralSupply.getBuildingsAll()) {
			buildingPanel.add(new BuildingLabel(type, ImgSize.BIG));
		}
		buildingPanel.revalidate();
	}

	private static enum OptionCommand {
		SUBMIT, RANDOMIZE;
	}

	public void actionPerformed(ActionEvent e) {
		OptionCommand command = OptionCommand.valueOf(e.getActionCommand());
		switch (command) {
		case RANDOMIZE:
			GeneralSupply.randomizeBuildings();
			updateBuildingPanel();
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
