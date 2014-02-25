/*package com.dill.agricola.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import com.dill.agricola.model.Space;
import com.dill.agricola.model.enums.Point;

@SuppressWarnings("serial")
public abstract class SpaceTile extends JLabel {

	int S = 40;
	int M = 40;

	private final Space space;
//	private final Point pos;

	public SpaceTile(Space space, Point pos) {
		super();
		this.space = space;
//		this.pos = pos;
		setBounds(S * pos.x + M, S * pos.y + M, S - 2 * M, S - 2 * M);
//		setBorder(null);
//		setOpaque(true);
		addMouseListener(new SpaceListener(space));
		setBackground(Color.GREEN);
	}

	public void paint(Graphics g) {
		g.setColor(Color.GREEN);
		g.drawRoundRect(0, 0, S - 2 * M, S - 2 * M, M, M);
		
		g.drawString(String.valueOf(space.getAnimals()), S/2, S/2);
	}

	public abstract Image getImage();

	public Rectangle getImageBounds() {
		Rectangle r = getBounds();
		return r;
	}

	private static class SpaceListener extends MouseAdapter {

		private final Space space;

		public SpaceListener(Space space) {
			this.space = space;
		}

		public void mouseClicked(MouseEvent e) {
			space.addAnimals(1);
		}

	}
}*/