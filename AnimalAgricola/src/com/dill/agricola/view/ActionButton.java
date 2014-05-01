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

	private static final BufferedImage[] workerIcons = { AgriImages.getWorkerImage(PlayerColor.BLUE),
			AgriImages.getWorkerImage(PlayerColor.RED) };
	private static final Color OVERLAY_COLOR = new Color(255, 255, 255, 150);

//	private static final Color BACK_COLOR = new Color(236, 220, 122);
//	private static final Color BACK_COLOR_MO = new Color(243, 217, 60);
//	private static final Color BACK_COLOR_DISABLED = new Color(212, 208, 182);
//
//	private static final Color BORDER_DARK = new Color(150, 25, 10);
//	private static final Color BORDER_LIGHT = new Color(220, 110, 85);
//	private static final Color BORDER_DARK_MO = new Color(100, 25, 10);
//	private static final Color BORDER_LIGHT_MO = new Color(150, 110, 85);
//
//	private final static Border BORDER =
//			BorderFactory.createCompoundBorder(
//					BorderFactory.createBevelBorder(BevelBorder.LOWERED, BORDER_DARK, BORDER_LIGHT)
//					,
//					BorderFactory.createBevelBorder(BevelBorder.RAISED, BORDER_DARK, BORDER_LIGHT)
//					);
//	private final static Border BORDER_MO =
//			BorderFactory.createCompoundBorder(
//					BorderFactory.createBevelBorder(BevelBorder.LOWERED, BORDER_DARK_MO, BORDER_LIGHT_MO)
//					,
//					BorderFactory.createBevelBorder(BevelBorder.RAISED, BORDER_DARK_MO, BORDER_LIGHT_MO)
//					);

	private PlayerColor usedBy = null;

	public ActionButton(ActionType actionType) {
		setMargin(new Insets(1, 1, 1, 1));
		setAlignmentX(JButton.CENTER_ALIGNMENT);
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		setToolTipText(actionType.desc);
		setActionCommand(ActionCommand.SUBMIT.toString());
//		setBackground(BACK_COLOR);
//		setBorder(BORDER);
//		addMouseListener(new ActionButtonMouseListener(this));
	}

	public void setEnabled(boolean b) {
//		boolean orig = this.isEnabled();
		super.setEnabled(b);
//		if (orig != b) {
//			setBackground(b ? BACK_COLOR : BACK_COLOR_DISABLED);
//		}
	}

	public void setUsed(PlayerColor usedBy) {
		if (this.usedBy != usedBy) {
//			setBackground(BACK_COLOR);
//			setBorder(ActionButton.BORDER);
			this.usedBy = usedBy;
			this.repaint();
		}
	}

	protected void paintChildren(Graphics g) {
		super.paintChildren(g);

		if (usedBy != null) {
			Insets i = getBorder().getBorderInsets(this);
			Dimension size = getSize();
			// overlay
			g.setColor(OVERLAY_COLOR);
			g.fillRect(i.left, i.top, size.width - i.left - i.right, size.height - i.top - i.bottom);
			// player worker
			BufferedImage img = workerIcons[usedBy.ordinal()];
			int x = (size.width - img.getWidth()) / 2;
			int y = (size.height - img.getHeight()) / 2;
			g.drawImage(workerIcons[usedBy.ordinal()], x, y, null);
		}
	}

	/*private static final class ActionButtonMouseListener implements MouseListener {

		private final ActionButton actionButton;

		public ActionButtonMouseListener(ActionButton actionButton) {
			this.actionButton = actionButton;
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
			if (actionButton.isEnabled()) {
				actionButton.setBackground(BACK_COLOR);
				actionButton.setBorder(ActionButton.BORDER);
			}
		}

		public void mouseEntered(MouseEvent e) {
			if (actionButton.isEnabled()) {
				actionButton.setBackground(BACK_COLOR_MO);
				actionButton.setBorder(ActionButton.BORDER_MO);
			}
		}

		public void mouseClicked(MouseEvent e) {
		}
	}*/

}
