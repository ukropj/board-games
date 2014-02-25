package com.dill.agricola.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.dill.agricola.model.Building;
import com.dill.agricola.model.Farm;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.Space;
import com.dill.agricola.model.buildings.BuildingType;
import com.dill.agricola.model.enums.Animal;
import com.dill.agricola.model.enums.Animals;
import com.dill.agricola.model.enums.ChangeType;
import com.dill.agricola.model.enums.Dir;
import com.dill.agricola.model.enums.Point;
import com.dill.agricola.model.enums.Purchasable;
import com.dill.agricola.view.Images.IconSize;

@SuppressWarnings("serial")
public class FarmPanel extends JPanel {

	public final static int S = 100;
	public final static int M = S / 16;
	public final static int L = S - 2 * M;
	public final static int X1 = S / 2, X2 = S / 2, Y1 = S / 4, Y2 = S / 2;

	static int h = (int) (S * 3.7f);

	public final static Stroke NORMAL_STROKE = new BasicStroke();
	public final static Stroke THICK_STROKE = new BasicStroke(2.0f);
	public final static Stroke MOVABLE_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 10.0f, new float[] { 2, 2 }, 1.0f);

	private final Player player;
	private final Farm farm;
	private boolean active;

	//	private final List<JLabel> looseAnimalLabels = new ArrayList<JLabel>();

	public FarmPanel(Player player) {
		this.player = player;
		this.farm = player.getFarm();

		SpaceListener mouseListener = new SpaceListener(farm);
		addMouseListener(mouseListener);
		addMouseWheelListener(mouseListener);
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void paintComponent(Graphics g0) {
		Graphics2D g = (Graphics2D) g0;
		// background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		// farm
		drawFarm(g);
		// spaces
		List<Point> grid = Point.createGridRange(farm.getWidth(), farm.getHeight());
		for (Point pos : grid) {
			drawSpace(g, pos, farm.getSpace(pos));
		}
		// loose animals
		drawLooseAnimals(g);
	}

	public Dimension getPreferredSize() {
		return new Dimension(X1 + farm.getWidth() * S + X2, h/*Y1 + farm.getHeight() * S + Y2*/);
	}

	/*
	 * protected void drawPlayer(Graphics2D g) { g.setColor(player.getColor().getRealColor()); Font origFont = g.getFont(); g.setFont(new Font("Helvetica",
	 * Font.BOLD, 20)); String info = player.getWorkers() + (player.isStarting() ? " *" : ""); g.drawString(info, X1 / 4, 3 * Y1 / 4); // if (active) { //
	 * g.drawRect(2, 2, X1 - 2, Y1 - 2); // } g.setFont(origFont); }
	 */

	private void drawFarm(Graphics2D g) {
		BufferedImage img = null;
		g.setColor(Color.BLACK);
		// left margin
		img = Images.getFarmMarginImage(Dir.W);
		g.drawImage(img, X1 - S / 2, 0, S / 2, h, null);
		// left extensions
		for (int i = 0; i < farm.getExtensions(Dir.W); i++) {
			// g.drawLine(X1 + S * (i + 1), Y1 - BY1, X1 + S * (i + 1), Y1 + S * farm.getHeight() + BY2);
			img = Images.getExtensionImage();
			g.drawImage(img, X1 + S * i, 0, S, h, null);
		}
		// farm
		img = Images.getFarmImage(player.getColor().ordinal());
		g.drawImage(img, X1 + farm.getExtensions(Dir.W) * S, 0, 2 * S, h, null);
		// right extensions
		for (int i = 0; i < farm.getExtensions(Dir.E); i++) {
			// g.drawLine(X1 + S * (farm.getWidth() - i - 1), Y1 - BY1, X1 + S * (farm.getWidth() - i - 1), Y1 + S * farm.getHeight() + BY2);
			img = Images.getExtensionImage();
			g.drawImage(img, X1 + S * (farm.getWidth() - i - 1), 0, S, h, null);
		}
		// right margin
		img = Images.getFarmMarginImage(Dir.E);
		g.drawImage(img, X1 + S * farm.getWidth(), 0, S / 2, h, null);

		if (farm.getActiveType() == Purchasable.EXTENSION) {
			g.setStroke(MOVABLE_STROKE);
			if (!farm.isActiveSpot(new Point(0, 0), Purchasable.EXTENSION)) {
				g.drawRect(X1 - S + M, Y1 + M, L, farm.getHeight() * S - 2 * M);
			}
			if (!farm.isActiveSpot(new Point(farm.getWidth() - 1, 0), Purchasable.EXTENSION)) {
				g.drawRect(X1 + farm.getWidth() * S + M, Y1 + M, L, farm.getHeight() * S - 2 * M);
			}
			g.setStroke(NORMAL_STROKE);
		}
	}

	private void drawSpace(Graphics2D g, Point pos, Space space) {
		int x = X1 + S * pos.x, y = Y1 + S * pos.y;
		// toggleMovableStroke(g, farm.isActiveSpot(new Point(pos.x, 0), Purchasable.EXTENSION));

		g.setColor(Color.BLACK);
		/*if (Main.DEBUG) {
			g.drawRect(x + M, y + M, L, L);
		}*/

		// building
		Building b = farm.getBuilding(pos);
		if (b != null) {
			drawBuilding(g, pos, b);
			// toggleMovableStroke(g, farm.isActiveSpot(pos, Purchasable.BUILDING));
			// DrawUtils.drawFillRect(g, x + M, y + S / 2, L / 2, L / 2, Color.RED);
			// g.drawString(b.getShortName(), x + M + 2, y + 3 * S / 4 + 2);
		}

		// animals
		// if (space.getMaxCapacity() > 0) {
		// g.setStroke(NORMAL_STROKE);
		g.setColor(Color.BLACK);
		if (space.getAnimals() > 0) {
			Color borderColor = space.isValid() ? space.getAnimalType().getContrastingColor() : Color.RED;
			// Color fillColor = space.getAnimalType().getColor();
			// DrawUtils.drawFillCircle(g, x + M, y + M, L / 2, fillColor, borderColor);
			g.setColor(borderColor);
			BufferedImage img = Images.getAnimalImage(space.getAnimalType());
			g.drawImage(img, x + L / 2, y + 2 * M, img.getWidth() / 2, img.getHeight() / 2, null);
		}
		String text = space.getAnimals() + "/" + space.getMaxCapacity();
		g.drawString(text, x + L / 2 + 2 * M, y + M + L / 4);
		// }
		// trough
		if (space.hasTrough()) {
			// toggleMovableStroke(g, farm.isActiveSpot(pos, Purchasable.TROUGH));
			// DrawUtils.drawFillRect(g, x + S / 2, y + S / 2, L / 3, L / 3, Color.YELLOW);
			BufferedImage img = Images.getThroughImage();
			g.drawImage(img, x + S / 2, y + S / 2, img.getWidth() / 2, img.getHeight() / 2, null);
		}

		for (Dir d : Dir.values()) {
			if (space.hasBorder(d)) {
				drawFence(g, pos, d);
			}
		}
	}

	private void drawBuilding(Graphics2D g, Point pos, Building building) {
		int x = X1 + S * pos.x + M, y = Y1 + S * pos.y + M;

		BuildingType type = building.getType();
		if (type == BuildingType.COTTAGE) {
			g.setColor(player.getColor().getRealColor());
			g.fillOval(x + 2 * M, y + L / 2, L / 3, L / 3);
			g.setColor(Color.BLACK);
			g.setStroke(THICK_STROKE);
			g.drawOval(x + 2 * M, y + L / 2, L / 3, L / 3);
			g.drawString(String.valueOf(player.getWorkers()), x + 3 * M + 3, y + 3 * L / 4);
			g.setStroke(NORMAL_STROKE);
		} else {
			BufferedImage img = Images.getBuildingImage(type);
			g.drawImage(img, x, y, L, L, null);
		}
	}

	private void drawFence(Graphics2D g, Point pos, Dir d) {
		int x = X1 + S * pos.x, y = Y1 + S * pos.y;
		int o = M, w = 0, h = 0;
		float ratio;

		BufferedImage img = Images.getFenceImage(d);
		switch (d) {
		case S:
			y += S;
		case N:
			x += o;
			y += -o;
			w = S - 2 * o;
			ratio = 1.0f * w / img.getWidth();
			h = (int) (img.getHeight() * ratio);
			break;
		case E:
			x += S;
		case W:
			x += -o;
			y += o;
			h = S - 2 * o;
			ratio = 1.0f * h / img.getHeight();
			w = (int) (img.getWidth() * ratio);
			break;
		}
		// toggleMovableStroke(g, farm.isActiveSpotForFence(pos, d));
		// DrawUtils.drawFillRect(g, x, y, w, h, Color.YELLOW);

		g.drawImage(img, x, y, w, h, null);
	}

	private void drawLooseAnimals(Graphics2D g) {
		Animals loose = farm.getLooseAnimals();
		int total = loose.size();
		if (total > 0) {
			int maxWidth =  farm.getWidth() * S;
			int l = Math.min(S / 3, maxWidth / total);
			int x = X1 + maxWidth / 2 + (l * total / 2) - l;
			int y = Y1 + farm.getHeight() * S + Y2 - 2 * M;
			int j = 0;
			for (Animal type : Animal.values()) {
				int count = loose.get(type);
				BufferedImage img = Images.getAnimalImage(type);
				for (int i = 0; i < count; i++) {
					g.drawImage(img, x - j * l, y - img.getHeight() / 2, img.getWidth() / 2, img.getHeight() / 2, null);
					j++;
				}
			}
		}
	}

	private class SpaceListener extends MouseAdapter {

		private final Farm farm;

		public SpaceListener(Farm farm) {
			this.farm = farm;
		}

		private Point getPos(MouseEvent e) {
			int x = e.getX(), y = e.getY();
			return new Point(x >= X1 ? (x - X1) / S : -1, y >= Y1 ? (y - Y1) / S : -1);
		}

		public void mouseClicked(MouseEvent e) {
			if (!active) {
				return;
			}
			ChangeType changeType = ChangeType.FARM_CLICK;
			Point pos = getPos(e);
			boolean leftClick = e.getButton() == MouseEvent.BUTTON1;
			boolean multiClick = e.getClickCount() > 1;

			int dx = Math.abs(pos.x * S - (e.getX() - X1));
			int dy = Math.abs(pos.y * S - (e.getY() - Y1));
			boolean done = false;
			Purchasable active = farm.getActiveType();
			if (active != null) {
				switch (active) {
				case EXTENSION:
					Dir extDir = null;
					if (pos.x < 0) {
						extDir = Dir.W;
					} else if (pos.x >= farm.getWidth()) {
						extDir = Dir.E;
					}
					if (extDir != null) {
						if (farm.getUnused(Purchasable.EXTENSION) > 0) {
							farm.extend(extDir);
						} else {
							farm.moveExtension(extDir);
						}
						changeType = ChangeType.FARM_RESIZE;
						done = true;
					}
					break;
				case FENCE:
					int feps = S / 4;
					Dir fenceDir = null;
					if (dy <= feps) {
						fenceDir = Dir.N;
					} else if (S - dx <= feps) {
						fenceDir = Dir.E;
					} else if (S - dy <= feps) {
						fenceDir = Dir.S;
					} else if (dx <= feps) {
						fenceDir = Dir.W;
					}
					if (fenceDir != null) {
						farm.toggleFence(pos, fenceDir);
						done = true;
					}
					break;
				case TROUGH:
					if (S - dx >= S / 2 || dy >= S / 2) { // not top right
						farm.toggleTrough(pos);
						done = true;
					}
					break;
				case BUILDING:
					if (S - dx >= S / 2 || dy >= S / 2) { // not top right
						farm.toggleBuilding(pos);
						done = true;
					}
					break;
				}
			}

			if (!done) {
				if (leftClick) {
					List<Animal> types = farm.guessAnimalTypesToPut(pos);
					if (types.isEmpty()) {
						return;
					}
					Animal type = null;
					if (types.size() == 1) {
						type = types.get(0);
					} else {
						Icon[] icons = new ImageIcon[types.size()];
						for (int i = 0; i < types.size(); i++) { // TODO refactor
							icons[i] = Images.getAnimalIcon(types.get(i), IconSize.BIG);
						}
						int result = JOptionPane.showOptionDialog(null, "Choose animal", "Put animals", JOptionPane.DEFAULT_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, icons, icons[0]);
						type = result != -1 ? types.get(result) : null;
					}
					if (type != null) {
						farm.putAnimals(pos, type, multiClick ? Integer.MAX_VALUE : 1);
						done = true;
					}
				} else {
					farm.takeAnimals(pos, multiClick ? Integer.MAX_VALUE : 1);
					done = true;
				}
			}

			if (done) {
				farm.notifyObservers(changeType);
			}
		}

		public void mouseWheelMoved(MouseWheelEvent e) {
			Point pos = getPos(e);
			int count = -e.getWheelRotation();

			if (count > 0) {
				List<Animal> types = farm.guessAnimalTypesToPut(pos);
				if (types.size() != 1) {
					// wheel can be used only when animal type is determined before
					return;
				}
				farm.putAnimals(pos, types.get(0), count);
			} else {
				farm.takeAnimals(pos, -count);
			}
			farm.notifyObservers(ChangeType.FARM_CLICK);
		}

	}

	/*public void showLooseAnimals() {
		for (JLabel l : looseAnimalLabels) {
			remove(l);
		}
		looseAnimalLabels.clear();
		for (Animal type : Animal.values()) {
			int count = farm.getLooseAnimals(type);
			for (int i = 0; i < count; i++) {
				looseAnimalLabels.add(SwingUtils.createAnimalLabel(type, 1, SwingUtils.NO_NUMBER));
			}
		}
	}*/

}
