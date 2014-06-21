package com.dill.agricola.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import javax.swing.JButton;

import com.dill.agricola.Game.FarmActionCommand;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;

public class ActionButton extends JButton {
	private static final long serialVersionUID = 1L;

	private static final BufferedImage[] workerIcons = { AgriImages.getWorkerImage(PlayerColor.BLUE, ImgSize.MEDIUM),
			AgriImages.getWorkerImage(PlayerColor.RED, ImgSize.MEDIUM) };
	private static final Color OVERLAY_COLOR = new Color(255, 255, 255, 150);
	private final static Stroke THICK_STROKE = new BasicStroke(3.0f);

	private PlayerColor usedBy = null;
	private boolean active = false;

	private final ActionType actionType;

	public ActionButton(ActionType actionType) {
		this.actionType = actionType;
		setMargin(new Insets(0, 0, 0, 0));
		setAlignmentX(JButton.CENTER_ALIGNMENT);
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		setToolTipText(actionType.desc);
		setActionCommand(FarmActionCommand.SUBMIT.toString());
	}

	public void setEnabled(boolean b) {
		super.setEnabled(b);
	}

	public ActionType getActionType() {
		return actionType;
	}

	public void setUsed(PlayerColor usedBy) {
		if (this.usedBy != usedBy) {
			this.usedBy = usedBy;
			this.repaint();
		}
	}

	public void setActive(boolean active) {
		if (this.active != active) {
			this.active = active;
			this.repaint();
		}
	}

	protected void paintChildren(Graphics g0) {
		super.paintChildren(g0);
		Graphics2D g = (Graphics2D) g0;

		if (usedBy != null) {
			Insets i = getBorder().getBorderInsets(this);
			Dimension size = getSize();
			// overlay
			g.setColor(OVERLAY_COLOR);
			g.fillRect(i.left, i.top, size.width - i.left - i.right, size.height - i.top - i.bottom);
			// active border
			if (active) {
				g.setColor(usedBy.getRealColor());
				g.setStroke(THICK_STROKE);
				int o = 1;
				g.drawRect(i.left + o, i.top + o, size.width - i.left - i.right - 2 * o - 1, size.height - i.top - i.bottom - 2 * o - 1);
			}
			// player worker
			BufferedImage img = workerIcons[usedBy.ordinal()];
			int x = (size.width - img.getWidth()) / 2;
			int y = (size.height - img.getHeight()) / 2;
			g.drawImage(workerIcons[usedBy.ordinal()], x, y, null);
		}
	}
}
