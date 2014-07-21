package com.dill.agricola.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JToolTip;
import javax.swing.plaf.metal.MetalToolTipUI;

import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.Images;

public class BuildingLabel extends JLabel {
	private static final long serialVersionUID = 1L;

	private final BuildingType type;
	private boolean used = false;

	private JToolTip tooltip;
	private boolean showImageTooltip = false;;

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
	
	public void setShowImageTooltip(boolean showImageTooltip) {
		this.showImageTooltip = showImageTooltip;
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
	
	public boolean isUsed() {
		return used;
	}

	public JToolTip createToolTip() {
		if (showImageTooltip) {
			if (tooltip == null) {
				tooltip = new ImageTooltip(AgriImages.getBuildingIcon(type, ImgSize.BIG).getImage());
				tooltip.setComponent(this);
			}
			return tooltip;			
		} else {
			return super.createToolTip();
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

	private static class ImageTooltip extends JToolTip {
		private static final long serialVersionUID = 1L;

		public ImageTooltip(Image image) {
			setUI(new ImageToolTipUI(image));
		}
	}

	private static class ImageToolTipUI extends MetalToolTipUI {
		private final Image img;

		public ImageToolTipUI(Image image) {
			this.img = image;
		}

		public void paint(Graphics g, JComponent c) {
			g.drawImage(this.img, 0, 0, this.img.getWidth(c), this.img.getHeight(c), c);
		}

		public Dimension getPreferredSize(JComponent c) {
			return new Dimension(this.img.getWidth(c), this.img.getHeight(c));
		}
	}

}
