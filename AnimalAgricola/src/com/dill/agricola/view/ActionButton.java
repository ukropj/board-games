package com.dill.agricola.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.JButton;

import com.dill.agricola.Game.ActionCommand;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.view.utils.AgriImages;

public class ActionButton extends JButton {
	private static final long serialVersionUID = 1L;

	private final BufferedImage[] workerIcons = { AgriImages.getWorkerImage(PlayerColor.BLUE),
			AgriImages.getWorkerImage(PlayerColor.RED) };
	private final Color OVERLAY_COLOR = new Color(255, 255, 255, 150);

	private PlayerColor usedBy = null;

	public ActionButton(ActionType actionType) {
		setMargin(new Insets(1, 1, 1, 1));
		setAlignmentX(JButton.CENTER_ALIGNMENT);
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		setToolTipText(actionType.name); // TODO better tooltip
		setActionCommand(ActionCommand.SUBMIT.toString());
	}

	public void setUsed(PlayerColor usedBy) {
		if (this.usedBy != usedBy) {
			this.usedBy = usedBy;
			this.repaint();
		}
	}

	protected void paintChildren(Graphics g) {
		super.paintChildren(g);

		if (usedBy != null) {
			Dimension size = getSize();
			// overlay
			g.setColor(OVERLAY_COLOR);
			g.fillRect(1, 1, size.width - 2, size.height - 2);
			// player worker
			BufferedImage img = workerIcons[usedBy.ordinal()];
			int x = (size.width - img.getWidth()) / 2;
			int y = (size.height - img.getHeight()) / 2;
			g.drawImage(workerIcons[usedBy.ordinal()], x, y, null);
		}
	}

}
