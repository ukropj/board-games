package com.dill.agricola.view;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.dill.agricola.GeneralSupply;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.support.Fonts;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.Images;
import com.dill.agricola.view.utils.UiFactory;

public class BuildingsPanel extends JScrollPane {
	private static final long serialVersionUID = 1L;

	private static final Font COUNT_FONT = Fonts.TEXT_FONT.deriveFont(14f);

	public BuildingsPanel() {
		setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
		setBorder(null);
		JPanel p = new JPanel();
		setViewportView(p);
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

		p.add(setFont(UiFactory.createLabel(Msg.get("basicBuildingsTitle")), COUNT_FONT));
		p.add(Box.createVerticalStrut(5));
		p.add(buildBasic());
		p.add(Box.createVerticalStrut(15));
		p.add(setFont(UiFactory.createLabel(Msg.get("specialBuildingsTitle")), COUNT_FONT));
		p.add(Box.createVerticalStrut(5));
		p.add(buildSpecial());
		p.add(Box.createVerticalGlue());
	}

	private JComponent buildBasic() {
		JPanel p = new JPanel(new GridLayout(1, 3, 2, 0));
		p.add(UiFactory.createLabel(makeSmaller(AgriImages.getBuildingIcon(BuildingType.COTTAGE, ImgSize.BIG))));
		p.add(UiFactory.createLabel(makeSmaller(AgriImages.getBuildingIcon(BuildingType.STALL, ImgSize.BIG))));
		p.add(UiFactory.createLabel(makeSmaller(AgriImages.getBuildingIcon(BuildingType.STABLES, ImgSize.BIG))));
		return p;
	}

	private ImageIcon makeSmaller(ImageIcon icon) {
		return new ImageIcon(Images.getBestScaledInstance((BufferedImage) icon.getImage(), 0.8f));
	}

	private JComponent buildSpecial() {
		JPanel p = new JPanel(new GridLayout(0, 2, 2, 5));

		for (BuildingType type : GeneralSupply.getBuildingsAll()) {
			p.add(UiFactory.createLabel(AgriImages.getBuildingIcon(type, ImgSize.BIG)));
		}
		return p;
	}

	private <T extends JComponent> T setFont(T component, Font font) {
		component.setFont(font);
		return component;
	}
}
