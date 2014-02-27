package com.dill.agricola.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Dir;
import com.dill.agricola.common.PointUtils;
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Farm;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.Space;
import com.dill.agricola.model.buildings.MultiImaged;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.ChangeType;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;

@SuppressWarnings("serial")
public class FarmPanel extends JPanel {

	public final static int S = 100;
	public final static int M = S / 16, L = S - 2 * M;
	public final static int X1 = S / 2, X2 = S / 2, Y1 = (int) (S * 0.28f);
	static int H = (int) (S * 3.7f);
	// static int Y2 = H - Y1 - 3 * S;

	private final static Rectangle animalArea = new Rectangle(S / 3, S / 3, S / 3, S / 3);
	private final static Map<Animal, Rectangle> animalAreas = new EnumMap<Animal, Rectangle>(Animal.class) {
		{
			put(Animal.SHEEP, new Rectangle(S / 3, S / 3, S / 6, S / 6));
			put(Animal.PIG, new Rectangle(S / 2, S / 3, S / 6, S / 6));
			put(Animal.COW, new Rectangle(S / 3, S / 2, S / 6, S / 6));
			put(Animal.HORSE, new Rectangle(S / 2, S / 2, S / 6, S / 6));
		}
	};
	// private final static Rectangle troughArea = new Rectangle(2 * S / 3, M, S / 3, S / 3 - M);
	private final static Polygon troughArea = new Polygon(
			new int[] { S - 6 * M, S - 6 * M, S - 4 * M, S - 2 * M, S - 2 * M },
			new int[] { 7 * M, 4 * M, 2 * M, 4 * M, 7 * M },
			5);
	private final static Rectangle buildingArea = new Rectangle(M, M, L, L);
	private final static Map<Dir, Rectangle> fenceAreas = new EnumMap<Dir, Rectangle>(Dir.class) {
		{
			put(Dir.N, new Rectangle(M, -M, L, 2 * M));
			put(Dir.W, new Rectangle(-M, M, 2 * M, L));
			put(Dir.S, new Rectangle(M, S - M, L, 2 * M));
			put(Dir.E, new Rectangle(S - M, M, 2 * M, L));
		}
	};
	private final static Map<Dir, Rectangle> fenceClickAreas = new EnumMap<Dir, Rectangle>(Dir.class) {
		private final static int O = 8;
		{
			for (java.util.Map.Entry<Dir, Rectangle> dirRect : fenceAreas.entrySet()) {
				Rectangle r = new Rectangle(dirRect.getValue());
				r.setBounds(r.x - O, r.y - O, r.width + 2*O, r.height+ 2*O);
				put(dirRect.getKey(), r);
			}
		}
	};
	// private final static Map<Dir, Rectangle> extAreas = new EnumMap<Dir, Rectangle>(Dir.class) {{
	// put(Dir.W, new Rectangle(0, Y1, X1, 3 * S));
	// put(Dir.E, new Rectangle(S-M, M, X2, 3 * S));
	// }};

	public final static Stroke NORMAL_STROKE = new BasicStroke();
	public final static Stroke THICK_STROKE = new BasicStroke(2.0f);
	public final static Stroke MOVABLE_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 10.0f, new float[] { 2, 2 }, 1.0f);
	public final static Font FONT = new Font("Calibri", Font.PLAIN, 12);

	private final Player player;
	private final Farm farm;
	private boolean active;

	// private final List<JLabel> looseAnimalLabels = new ArrayList<JLabel>();

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

	private static Point toRealPos(Point farmPos) {
		return new Point(X1 + S * farmPos.x, Y1 + S * farmPos.y);
	}

	private static Point toFarmPos(int x, int y) {
		return new Point(x >= X1 ? (x - X1) / S : -1, y >= Y1 ? (y - Y1) / S : -1);
	}

	public void paintComponent(Graphics g0) {
		Graphics2D g = (Graphics2D) g0;
		g.setFont(FONT);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		// background (should not be seen)
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		// farm
		drawFarm(g);

		// loose animals
		drawLooseAnimals(g);

		// unused stuff
		drawUnused(g);

		// spaces
		List<Point> grid = PointUtils.createGridRange(farm.getWidth(), farm.getHeight());
		for (Point pos : grid) {
			drawSpace(g, pos, farm.getSpace(pos));
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(X1 + farm.getWidth() * S + X2, H/* Y1 + farm.getHeight() * S + Y2 */);
	}

	private void drawFarm(Graphics2D g) {
		BufferedImage img = null;
		g.setColor(Color.BLACK);
		// west margin
		img = AgriImages.getFarmMarginImage(Dir.W);
		g.drawImage(img, X1 - S / 2, 0, S / 2, H, null);
		// west extensions
		int westExts = farm.getExtensions(Dir.W).size(), farmCore = 2;
		int i = 0;
		for (Integer id : farm.getExtensions(Dir.W)) {
			// TODO marker
			img = AgriImages.getExtensionImage(id);
			g.drawImage(img, X1 + (westExts - i - 1) * S, 0, S, H, null);
			i++;
		}
		// farm
		img = AgriImages.getFarmImage(player.getColor().ordinal());
		g.drawImage(img, X1 + westExts * S, 0, farmCore * S, H, null);
		// east extensions
		i = 0;
		for (Integer id : farm.getExtensions(Dir.E)) {
			// TODO marker
			img = AgriImages.getExtensionImage(id);
			g.drawImage(img, X1 + S * (westExts + farmCore + i), 0, S, H, null);
			i++;
		}
		// east margin
		img = AgriImages.getFarmMarginImage(Dir.E);
		g.drawImage(img, X1 + S * farm.getWidth(), 0, S / 2, H, null);

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
		g.setColor(Color.BLACK);
		// if (Main.DEBUG) {
		// g.drawRect(x + M, y + M, L, L);
		// }

		// building
		drawBuilding(g, pos, farm.getBuilding(pos));

		// animals
		drawAnimal(g, pos, space);

		// trough
		drawTrough(g, pos, space.hasTrough(), farm.getActiveType() == Purchasable.TROUGH, farm.isActiveSpot(pos, Purchasable.TROUGH));

		// fences
		for (Dir d : Dir.values()) {
			drawFence(g, pos, d, space.hasBorder(d), farm.getActiveType() == Purchasable.FENCE, farm.isActiveSpotForFence(pos, d));
		}
	}

	private void drawAnimal(Graphics2D g, Point pos, Space space) {
		Point realPos = toRealPos(pos);
		Animal type = space.getAnimalType();
		int count = space.getAnimals();
		if (count > 0) {
			BufferedImage img = AgriImages.getAnimalImage(type, ImgSize.BIG);
			int w = img.getWidth(), h = img.getHeight();
			g.drawImage(img, realPos.x + (S - w) / 2 - M, realPos.y + (S - h) / 2, w, h, null);

			g.setColor(Color.BLACK);
			g.setStroke(MOVABLE_STROKE);
			Rectangle r = new Rectangle(animalArea);
			r.translate(realPos.x, realPos.y);
			g.draw(r);
		}
		if (space.getMaxCapacity() > 0) {
			g.setStroke(NORMAL_STROKE);
			g.setColor(type != null ? (space.isValid() ? type.getColor() : Color.RED) : new Color(153, 178, 97));
			g.setClip(realPos.x + M, realPos.y + M, L + 1, L + 1);
			g.fillOval(realPos.x + S - 6 * M, realPos.y + S - 6 * M, 8 * M, 8 * M);
			g.setClip(null);

			g.setColor(type != null ? (space.isValid() ? type.getContrastingColor() : Color.BLACK) : Color.BLACK);
			String text = count + "/" + space.getMaxCapacity();
			g.drawString(text, realPos.x + S - 5 * M, realPos.y + S - 2 * M);

			if (count == 0 && farm.getLooseAnimals().size() > 0) {
				g.setColor(Color.BLACK);
				g.setStroke(MOVABLE_STROKE);
				for (Animal a : farm.guessAnimalTypesToPut(pos)) {
					if (farm.getLooseAnimals(a) > 0) {
						Rectangle r = new Rectangle(animalAreas.get(a));
						r.translate(realPos.x, realPos.y);
						g.draw(r);
						BufferedImage img = AgriImages.getAnimalImage(a, ImgSize.SMALL);
						int w = img.getWidth(), h = img.getHeight();
						g.drawImage(img, r.x, r.y, w, h, null);
					}
				}
			}
		}
	}

	private void drawTrough(Graphics2D g, Point pos, boolean hasTrough, boolean troughsActive, boolean isActive) {
		Point realPos = toRealPos(pos);

		if (hasTrough) {
			BufferedImage img = AgriImages.getTroughImage(ImgSize.BIG);
			g.drawImage(img, realPos.x + S - 6 * M, realPos.y + 2 * M, img.getWidth(), img.getHeight(), null);
		}

		if (troughsActive && (!hasTrough || isActive)) {
			troughArea.translate(realPos.x, realPos.y);
			g.setColor(makeTranslucent(player.getColor().getRealColor(), 100));
			g.fill(troughArea);
			troughArea.translate(-realPos.x, -realPos.y); // TODO clone polygon instead, or not?
		}
	}

	private void drawBuilding(Graphics2D g, Point pos, Building building) {
		Point realPos = toRealPos(pos);
		Rectangle r = new Rectangle(buildingArea);
		r.translate(realPos.x, realPos.y);

		if (building != null) {
			BuildingType type = building.getType();

			BufferedImage img = null;
			switch (type) {
			case COTTAGE:
				break;
			case STALL:
				img = AgriImages.getStallImage(((MultiImaged) building).getId());
				break;
			case STABLES:
				img = AgriImages.getStableImage(((MultiImaged) building).getId());
				break;
			default:
				img = AgriImages.getBuildingImage(type);
			}
			if (img != null) {
				g.drawImage(img, r.x, r.y, r.width, r.height, null);
			}
			if (type.isHouse()) {
				g.setColor(player.getColor().getRealColor());
				g.fillOval(realPos.x + 2 * M, realPos.y + L / 2, L / 3, L / 3);
				g.setColor(Color.BLACK);
				g.setStroke(THICK_STROKE);
				g.drawOval(realPos.x + 2 * M, realPos.y + L / 2, L / 3, L / 3);
				g.drawString(String.valueOf(player.getWorkers()), realPos.x + 3 * M + 3, realPos.y + 3 * L / 4);
				g.setStroke(NORMAL_STROKE);
			}
		}

		if (farm.getActiveType() == Purchasable.BUILDING &&
				(building == null || farm.isActiveSpot(pos, Purchasable.BUILDING))) {
			g.setColor(makeTranslucent(player.getColor().getRealColor(), 100));
			g.fill(r);
		}

	}

	private void drawFence(Graphics2D g, Point pos, Dir d, boolean hasBorder, boolean fencesActive, boolean isActive) {
		Point realPos = toRealPos(pos);

		Rectangle r = new Rectangle(fenceAreas.get(d));
		r.translate(realPos.x, realPos.y);

		if (hasBorder) {
			BufferedImage img = AgriImages.getFenceImage(d);
			g.drawImage(img, r.x, r.y, r.width, r.height, null);
		}

		if (fencesActive && (!hasBorder || isActive)) {
			g.setColor(makeTranslucent(player.getColor().getRealColor(), 100));
			g.fill(r);
		}
	}

	private void drawLooseAnimals(Graphics2D g) {
		Animals loose = farm.getLooseAnimals();
		int total = loose.size();
		if (total > 0) {
			int maxWidth = farm.getWidth() * S;
			int l = Math.min(S / 3, maxWidth / total);
			int x = X1 + (l * (total - 1));
			int y = Y1 + farm.getHeight() * S + S / 2 - 2 * M;
			int j = 0;
			for (Animal type : Animal.values()) {
				int count = loose.get(type);
				BufferedImage img = AgriImages.getAnimalImage(type, ImgSize.BIG);
				for (int i = 0; i < count; i++) {
					g.drawImage(img, x - j * l, y - img.getHeight(), img.getWidth(), img.getHeight(), null);
					j++;
				}
			}

			BufferedImage arrowImg = AgriImages.getArrowImage(Dir.E, true, ImgSize.BIG);
			g.drawImage(arrowImg, 2 * M, y - M - arrowImg.getHeight(), arrowImg.getWidth(), arrowImg.getHeight(), null);
		}
	}

	private void drawUnused(Graphics2D g) {
		int total = farm.getAllUnusedCount() + farm.getUnusedBuildings().size();
		if (total > 0) {
			int maxHeight = farm.getHeight() * S - S / 2;
			int l = Math.min(S / 3, maxHeight / total);
			int x = 0;
			int y = Y1 + S / 4;
			int j = 0;

			for (Building b : farm.getUnusedBuildings()) {
				BufferedImage img = AgriImages.getBuildingImage(b.getType());
				g.drawImage(img, x + (X1 - S / 3) / 2, y + j * l, S / 3, S / 3, null);
				j++;
			}

			for (Purchasable type : Purchasable.values()) {
				int count = farm.getUnused(type);
				float r = 1.0f;
				BufferedImage img = null;
				switch (type) {
				case FENCE:
					img = AgriImages.getUnusedFenceImage();
					r = 0.4f;
					break;
				case TROUGH:
					img = AgriImages.getTroughImage(ImgSize.BIG);
					r = 1;
					break;
				case EXTENSION:
					continue;
				case BUILDING:
					continue;
				}
				for (int i = 0; i < count; i++) {
					int w = (int) (img.getWidth() * r), h = (int) (img.getHeight() * r);
					g.drawImage(img, x + (X1 - w) / 2, y + j * l, w, h, null);
					j++;
				}
			}

			BufferedImage arrowImg = AgriImages.getArrowImage(Dir.S, true, ImgSize.BIG);
			g.drawImage(arrowImg, 2 * M, 2 * M, arrowImg.getWidth(), arrowImg.getHeight(), null);
		}
	}

	private static Color makeTranslucent(Color c, int alpha) {
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
	}

	private class SpaceListener extends MouseAdapter {

		private final Farm farm;

		public SpaceListener(Farm farm) {
			this.farm = farm;
		}

		public void mouseClicked(MouseEvent e) {
			if (!active) {
				return;
			}
			ChangeType changeType = ChangeType.FARM_CLICK;
			Point pos = toFarmPos(e.getX(), e.getY());
			boolean leftClick = e.getButton() == MouseEvent.BUTTON1;
			boolean multiClick = e.getClickCount() > 1;

			int dx = Math.abs(pos.x * S - (e.getX() - X1));
			int dy = Math.abs(pos.y * S - (e.getY() - Y1));
			Point relativePoint = new Point(dx, dy);
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
					Dir fenceDir = null;
					for (Dir d : Dir.values()) {
						if (fenceClickAreas.get(d).contains(relativePoint)) {
							fenceDir = d;
							break;
						}
					}
					if (fenceDir != null) {
						farm.toggleFence(pos, fenceDir);
						done = true;
					}
					break;
				case TROUGH:
					if (troughArea.contains(relativePoint)) {
						farm.toggleTrough(pos);
						done = true;
					}
					break;
				case BUILDING:
					if (buildingArea.contains(relativePoint)) {
						farm.toggleBuilding(pos);
						done = true;
					}
					break;
				}
			}

			if (!done & animalArea.contains(relativePoint)) {
				if (leftClick && farm.getLooseAnimals().size() > 0) {
					List<Animal> types = farm.guessAnimalTypesToPut(pos);
					Animal type = null;
					if (types.isEmpty()) {
						return;
					} else if (types.size() == 1) {
						type = types.get(0);
					} else {
						for (Animal a : types) {
							if (animalAreas.get(a).contains(relativePoint)) {
								type = a;
								break;
							}
						}
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
			Point pos = toFarmPos(e.getX(), e.getY());
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

	/*
	 * public void showLooseAnimals() { for (JLabel l : looseAnimalLabels) { remove(l); } looseAnimalLabels.clear(); for (Animal type : Animal.values()) { int
	 * count = farm.getLooseAnimals(type); for (int i = 0; i < count; i++) { looseAnimalLabels.add(SwingUtils.createAnimalLabel(type, 1, SwingUtils.NO_NUMBER));
	 * } } }
	 */

}
