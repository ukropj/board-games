package com.dill.agricola.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.Images;

public class BuildingLabel extends JLabel {
	private static final long serialVersionUID = 1L;

	private final BuildingType type;
	private boolean used = false;

	public BuildingLabel(BuildingType type, ImgSize size) {
		this(type, size, 1);
	}

	public BuildingLabel(BuildingType type, ImgSize size, float ratio) {
		super(makeSmaller(AgriImages.getBuildingIcon(type, size), ratio));
		this.type = type;
		setToolTipText(type.name);
		setAlignmentX(JLabel.CENTER_ALIGNMENT);
		setAlignmentY(JLabel.CENTER_ALIGNMENT);
	}

	private static ImageIcon makeSmaller(ImageIcon icon, float ratio) {
		return new ImageIcon(Images.getBestScaledInstance((BufferedImage) icon.getImage(), ratio));
	}

	public BuildingType getType() {
		return type;
	}

	public void setUsed(boolean used) {
		if (this.used != used) {
			this.used = used;
			this.repaint();
			setToolTipText(type.name + (used ? " " + Msg.get("buildingTaken") : ""));
		}
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (used) {
			Dimension size = getSize();
			// overlay
			g.setColor(AgriImages.OVERLAY_COLOR);
			g.fillRect(0, 0, size.width, size.height);
		}
	}
}
