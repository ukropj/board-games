package com.dill.agricola.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
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
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
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
import com.dill.agricola.support.Fonts;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;

@SuppressWarnings("serial")
public class FarmPanel extends JPanel {

	public final static int S = 100;
	public final static int M = S / 16, L = S - 2 * M;
	public final static int X1 = S / 2, X2 = S / 2, Y1 = (int) (S * 0.28f);
	static int H = (int) (S * 3.7f);
	// static int Y2 = H - Y1 - 3 * S;

	private final static Area animalArea = new Area(new Ellipse2D.Float(S / 4 - M, S / 4 - M, S / 2 + 2 * M, S / 2 + 2 * M));
	private final static int AR = S / 6 + M;
	private final static Point[][] animalPositions = new Point[][] {
			new Point[] {
					new Point(S / 2, S / 2) },
			new Point[] {
					new Point(S / 2 + AR / 2, S / 2 - AR / 2),
					new Point(S / 2 - AR / 2, S / 2 + AR / 2) },
			new Point[] {
					new Point(S / 2, S / 2 + M + AR / 2),
					new Point(S / 2 + M + AR / 2, S / 2 - M),
					new Point(S / 2 - M - AR / 2, S / 2 - M) },
			new Point[] {
					new Point(S / 2, S / 2 - AR + M),
					new Point(S / 2, S / 2 + AR),
					new Point(S / 2 + AR, S / 2),
					new Point(S / 2 - AR, S / 2 + M / 2) }
	};
	private final static Line2D[][] animalDividers = new Line2D[][] {
			new Line2D[] {},
			new Line2D[] { new Line2D.Float(new Point(S / 4, S / 4), new Point(3 * S / 4, 3 * S / 4)) },
			new Line2D[] {
					new Line2D.Float(new Point(S / 2, S / 2), new Point(S / 2, S / 4 - 2 * M)),
					new Line2D.Float(new Point(S / 2, S / 2), new Point(3 * S / 4, 3 * S / 4)),
					new Line2D.Float(new Point(S / 2, S / 2), new Point(S / 4, 3 * S / 4)) },
			new Line2D[] {
					new Line2D.Float(new Point(S / 4, S / 4), new Point(3 * S / 4, 3 * S / 4)),
					new Line2D.Float(new Point(S / 4, 3 * S / 4), new Point(3 * S / 4, S / 4)) }
	};
	private final static Area[][] animalAreas = new Area[][] {
			new Area[] { animalArea },
			new Area[] {
					intersect(new Area(new Polygon(new int[] { 0, S, S }, new int[] { 0, 0, S }, 3)), animalArea),
					intersect(new Area(new Polygon(new int[] { 0, 0, S }, new int[] { 0, S, S }, 3)), animalArea) },
			new Area[] {
					intersect(new Area(new Polygon(new int[] { 0, S / 2, S }, new int[] { S + 1, S / 2, S }, 3)), animalArea),
					intersect(new Area(new Polygon(new int[] { S / 2, S / 2, S, S }, new int[] { 0, S / 2, S, 0 }, 4)), animalArea),
					intersect(new Area(new Polygon(new int[] { 0, S / 2, S / 2, 0 }, new int[] { 0, 0, S / 2, S + 1 }, 4)), animalArea)
			},
			new Area[] {
					intersect(new Area(new Polygon(new int[] { 0, S / 2, S + 1 }, new int[] { 0, S / 2, 0 }, 3)), animalArea),
					intersect(new Area(new Polygon(new int[] { 0, S / 2, S + 1 }, new int[] { S + 1, S / 2, S }, 3)), animalArea),
					intersect(new Area(new Polygon(new int[] { S + 1, S / 2, S + 1 }, new int[] { 0, S / 2, S }, 3)), animalArea),
					intersect(new Area(new Polygon(new int[] { 0, S / 2, 0 }, new int[] { 0, S / 2, S + 1 }, 3)), animalArea)
			}
			/*new Rectangle[] {
					new Rectangle(S / 2 - AR / 2, S / 2 - AR / 2, AR, AR) },
			new Rectangle[] {
					new Rectangle(S / 2, S / 3 - M, AR, AR),
					new Rectangle(S / 3 - M, S / 2, AR, AR) },
			new Rectangle[] {
					new Rectangle(S / 2 - M - AR, S / 2 - AR, AR, AR),
					new Rectangle(S / 2 + M, S / 2 - AR, AR, AR),
					new Rectangle(S / 2 - AR / 2, S / 2 + M, AR, AR) },
			new Rectangle[] {
					new Rectangle(S / 2 - AR/2, S / 2 - 3*AR/2, AR, AR),
					new Rectangle(S / 2 - AR/2, S / 2 + AR/2, AR, AR),
					new Rectangle(S / 2 + AR/2, S / 2 - AR/2, AR, AR),
					new Rectangle(S / 2 - 3*AR/2, S / 2 - AR/2, AR, AR) }*/

			/*new Rectangle[] {
					new Rectangle(S / 2 - AR / 2, S / 3 - M, AR, AR),
					new Rectangle(S / 2 - AR / 2, S / 2, AR, AR) },
			new Rectangle[] {
					new Rectangle(S / 3 - M, S / 3 - M, AR, AR),
					new Rectangle(S / 3 - M, S / 2, AR, AR),
					new Rectangle(S / 2, S / 2 - AR / 2, AR, AR) },
			new Rectangle[] {
					new Rectangle(S / 3 - M, S / 3 - M, AR, AR),
					new Rectangle(S / 3 - M, S / 2, AR, AR),
					new Rectangle(S / 2, S / 3 - M, AR, AR),
					new Rectangle(S / 2, S / 2, AR, AR) }*/
	};
	// private final static Area[] animalRects = new Area[] {
	// intersect(new Area(new Rectangle(S / 3 - M, S / 3 - M, AR, AR)), animalArea),
	// intersect(new Area(new Rectangle(S / 2, S / 3 - M, AR, AR)), animalArea),
	// intersect(new Area(new Rectangle(S / 3 - M, S / 2, AR, AR)), animalArea),
	// intersect(new Area(new Rectangle(S / 2, S / 2, AR, AR)), animalArea)
	// };

	private final static Polygon troughShape = new Polygon(
			new int[] { S - 6 * M, S - 6 * M, S - 4 * M, S - 2 * M, S - 2 * M },
			new int[] { 7 * M, 4 * M, 2 * M, 4 * M, 7 * M },
			5);
	private final static Rectangle buildingRect = new Rectangle(M, M, L, L);
	//	private final static Area buildingSansAnimalRect = subtract(new Area(buildingRect), animalArea);
	private final static Rectangle extRect = new Rectangle(M, Y1 + M, L, 3 * S - 2 * M);
	private final static Map<Dir, Rectangle> fenceRects = new EnumMap<Dir, Rectangle>(Dir.class) {
		{
			put(Dir.N, new Rectangle(M, -M, L, 2 * M));
			put(Dir.W, new Rectangle(-M, M, 2 * M, L));
			put(Dir.S, new Rectangle(M, S - M, L, 2 * M));
			put(Dir.E, new Rectangle(S - M, M, 2 * M, L));
		}
	};
	private final static Map<Dir, Rectangle> fenceClickRects = new EnumMap<Dir, Rectangle>(Dir.class) {
		private final static int O = 8;
		{
			for (java.util.Map.Entry<Dir, Rectangle> dirRect : fenceRects.entrySet()) {
				Rectangle r = new Rectangle(dirRect.getValue());
				r.setBounds(r.x - O, r.y - O, r.width + 2 * O, r.height + 2 * O);
				put(dirRect.getKey(), r);
			}
		}
	};

	public final static Stroke NORMAL_STROKE = new BasicStroke();
	public final static Stroke THICK_STROKE = new BasicStroke(2.0f);
	public final static Stroke MOVABLE_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 10.0f, new float[] { 2, 2 }, 1.0f);
	public final static Color PASTURE_COLOR = new Color(153, 178, 97);

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

	private static Area intersect(Area area, Area intersector) {
		area.intersect(intersector);
		return area;
	}

	/*private static Area subtract(Area area, Area subtractor) {
		area.subtract(subtractor);
		return area;
	}*/

	private static Point toRealPos(Point farmPos) {
		return new Point(X1 + S * farmPos.x, Y1 + S * farmPos.y);
	}

	private static Point toFarmPos(int x, int y) {
		return new Point(x >= X1 ? (x - X1) / S : -1, y >= Y1 ? (y - Y1) / S : -1);
	}

	private static Point toRealRelativePos(int x, int y) {
		Point pos = toFarmPos(x, y);
		int dx = Math.abs(pos.x * S - (x - X1));
		int dy = Math.abs(pos.y * S - (y - Y1));
		return new Point(dx, dy);
	}

	public void paintComponent(Graphics g0) {
		super.paintComponent(g0);
		Graphics2D g = (Graphics2D) g0;

//		g.setClip(getVisibleRect());

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g.setFont(Fonts.FARM_FONT);

		// background (should not be seen)
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		// farm
		drawFarm(g);

		// spaces
		List<Point> grid = PointUtils.createGridRange(farm.getWidth(), farm.getHeight());
		for (Point pos : grid) {
			drawSpace(g, pos, farm.getSpace(pos));
		}

		// loose animals
		drawLooseAnimals(g);

		// unused stuff
		drawUnused(g);
	}

	public Dimension getPreferredSize() {
		return new Dimension(X1 + farm.getWidth() * S + X2, H/* Y1 + farm.getHeight() * S + Y2 */);
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public Dimension getMaximumSize() {
		return getPreferredSize();
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
			g.setColor(makeTranslucent(player.getColor().getRealColor(), 140));
			Rectangle r = extRect;
			if (!farm.isActiveSpot(new Point(0, 0), Purchasable.EXTENSION)) {
				r.translate(X1 - S, 0);
				g.fill(r);
				r.translate(-X1 + S, 0);
			} else {
				r.translate(X1, 0);
				g.fill(r);
				r.translate(-X1, 0);
			}
			if (!farm.isActiveSpot(new Point(farm.getWidth() - 1, 0), Purchasable.EXTENSION)) {
				r.translate(X1 + farm.getWidth() * S, 0);
				g.fill(r);
				r.translate(-(X1 + farm.getWidth() * S), 0);
			} else {
				r.translate(X1 + (farm.getWidth() - 1) * S, 0);
				g.fill(r);
				r.translate(-(X1 + (farm.getWidth() - 1) * S), 0);
			}
			g.setStroke(NORMAL_STROKE);
		}
	}

	private void drawSpace(Graphics2D g, Point pos, Space space) {
		g.setColor(Color.BLACK);
		// if (Main.DEBUG) {
		// g.drawRect(x + M, y + M, L, L);
		// }

		List<Animal> availableAnimals = farm.guessAnimalTypesToPut(pos, true);

		// building
		drawBuilding(g, pos, space, farm.getBuilding(pos), availableAnimals);

		// fences
		for (Dir d : Dir.values()) {
			drawFence(g, pos, d, space);
		}

		// trough
		drawTrough(g, pos, space);

		// animals
		drawAnimal(g, pos, space, availableAnimals);
	}

	private void drawAnimal(Graphics2D g, Point pos, Space space, List<Animal> availableAnimals) {
		Point realPos = toRealPos(pos);

		Animal type = space.getAnimalType();
		int count = space.getAnimals();
		if (space.getMaxCapacity() > 0 || count > 0) {
			g.setStroke(NORMAL_STROKE);
			g.setColor(makeTranslucent(type != null ? (space.isValid() ? type.getColor() : PASTURE_COLOR) : PASTURE_COLOR, 200));
			g.setClip(realPos.x + M, realPos.y + M, L + 1, L + 1);
			g.fillOval(realPos.x + S - 6 * M, realPos.y + S - 6 * M, 8 * M, 8 * M);
			g.setClip(null);

			g.setColor(type != null ? (space.isValid() ? type.getContrastingColor() : Color.RED) : Color.BLACK);
			String text = count + "/" + space.getMaxCapacity();
			g.drawString(text, realPos.x + S - 5 * M, realPos.y + S - 2 * M);
		}

		if (space.getMaxCapacity() > count && availableAnimals.size() > 0) {
			// can add more - show "area"
			AffineTransform tr = AffineTransform.getTranslateInstance(realPos.x, realPos.y);
			Area r = animalArea.createTransformedArea(tr);
			g.setColor(makeTranslucent(player.getColor().getRealColor(), 120));
			g.fill(r);
			if (count == 0) {
				// no animals present - show options
				int typeCount = availableAnimals.size();
				for (int i = 0; i < typeCount; i++) {
					Animal t = availableAnimals.get(i);
					Point p = new Point(animalPositions[typeCount - 1][i]);
					p.translate(realPos.x, realPos.y);
					BufferedImage img = AgriImages.getAnimalImage(t, ImgSize.MEDIUM);
					//						BufferedImage img = AgriImages.getAnimalOutlineImage(t);
					int w = img.getWidth(), h = img.getHeight();
					g.drawImage(img, p.x - w / 2, p.y - h / 2, w, h, null);

					g.setColor(Color.BLACK);
					g.setStroke(NORMAL_STROKE);
					for (Line2D line : animalDividers[typeCount - 1]) {
						g.draw(tr.createTransformedShape(line));
					}
				}
			}
		}
		if (count > 0) {
			// show present animals
			BufferedImage img = AgriImages.getAnimalImage(type, ImgSize.BIG);
			int w = img.getWidth(), h = img.getHeight();
			g.drawImage(img, realPos.x + (S - w) / 2, realPos.y + (S - h) / 2, w, h, null);
		}
	}

	private void drawTrough(Graphics2D g, Point pos, Space space) {
		Point realPos = toRealPos(pos);
		boolean hasTrough = space.hasTrough();

		if (hasTrough) {
			BufferedImage img = AgriImages.getTroughImage(ImgSize.BIG);
			g.drawImage(img, realPos.x + S - 6 * M, realPos.y + 2 * M + 2, img.getWidth(), img.getHeight(), null);
		}

		if (isActiveTrough(pos, hasTrough)) {
			troughShape.translate(realPos.x, realPos.y);
			g.setColor(makeTranslucent(player.getColor().getRealColor(), 140));
			g.fill(troughShape);
			troughShape.translate(-realPos.x, -realPos.y);
		}
	}

	private void drawBuilding(Graphics2D g, Point pos, Space space, Building building, List<Animal> availableAnimals) {
		Point realPos = toRealPos(pos);
		Rectangle r = new Rectangle(buildingRect);
		r.translate(realPos.x, realPos.y);

		if (building != null) {
			BuildingType type = building.getType();
			BufferedImage img = null;
			switch (type) {
			case COTTAGE:
				img = AgriImages.getCottageImage(player.getColor().ordinal());
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
		}

		if (isActiveBuilding(pos, space)) {
			g.setColor(makeTranslucent(player.getColor().getRealColor(), 120));
//			if (availableAnimals.size() > 0) {
//				g.fill(AffineTransform.getTranslateInstance(realPos.x, realPos.y).createTransformedShape(buildingRect));
//			} else {
			g.fill(r);
//			}
		}

	}

	private void drawFence(Graphics2D g, Point pos, Dir d, Space space) {
		if ((d == Dir.N && pos.y > 0) || (d == Dir.W && pos.x > 0)) {
			// will be brawn by neighouring spaces
			return;
		}

		Point realPos = toRealPos(pos);
		boolean hasBorder = space.hasBorder(d);

		Rectangle r = new Rectangle(fenceRects.get(d));
		r.translate(realPos.x, realPos.y);

		if (hasBorder) {
			BufferedImage img = AgriImages.getFenceImage(d);
			g.drawImage(img, r.x, r.y, r.width, r.height, null);
		}

		if (isActiveFence(pos, d, hasBorder)) {
			g.setColor(makeTranslucent(player.getColor().getRealColor(), 130));
			g.fill(r);
		}
	}

	private boolean isActiveTrough(Point pos, boolean hasTrough) {
		return active && farm.getActiveType() == Purchasable.TROUGH
				&& ((!hasTrough && farm.getUnused(Purchasable.TROUGH) > 0) || farm.isActiveSpot(pos, Purchasable.TROUGH));
	}

	private boolean isActiveBuilding(Point pos, Space space) {
		Building topUnused = farm.getUnusedBuildings().isEmpty() ? null : farm.getUnusedBuildings().peek();
		return active && farm.getActiveType() == Purchasable.BUILDING
				&& ((topUnused != null && topUnused.canBuildAt(space)) || farm.isActiveSpot(pos, Purchasable.BUILDING));
	}

	private boolean isActiveFence(Point pos, Dir d, boolean hasBorder) {
		return active && farm.getActiveType() == Purchasable.FENCE
				&& ((!hasBorder && farm.getUnused(Purchasable.FENCE) > 0) || farm.isActiveSpotForFence(pos, d));
	}

	private void drawLooseAnimals(Graphics2D g) {
		Animals loose = farm.getLooseAnimals();
		int total = loose.size();
		if (total > 0) {
			int maxWidth = farm.getWidth() * S;
			int l = Math.min(S / 8, maxWidth / (total + 3));
			int x = X1 + (l * (total + 3));
			int y = Y1 + farm.getHeight() * S + S / 2 - 2 * M;
			int j = 0;
			for (Animal type : Animal.values()) {
				int count = loose.get(type);
				if (count > 0) {
					BufferedImage img = AgriImages.getAnimalImage(type, ImgSize.BIG);
					for (int i = 0; i < count; i++) {
						g.drawImage(img, x - j * l, y - img.getHeight(), img.getWidth(), img.getHeight(), null);
						j++;
					}
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

		private Animal getAnimalType(List<Animal> types, Point relativePoint) {
			int typeCount = types.size();
			if (typeCount != 0) {
				for (int i = 0; i < animalAreas[typeCount - 1].length; i++) {
					if (animalAreas[typeCount - 1][i].contains(relativePoint)) {
						return types.get(i);
					}
				}
			}
			return null;
		}

		public void mouseClicked(MouseEvent e) {
			if (!active) {
				return;
			}
			ChangeType changeType = ChangeType.FARM_CLICK;

			Point pos = toFarmPos(e.getX(), e.getY());
			Point relativePoint = toRealRelativePos(e.getX(), e.getY());

			boolean leftClick = e.getButton() == MouseEvent.BUTTON1;
			boolean multiClick = e.getClickCount() > 1;

			List<Animal> availableAnimals = null;

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
						if (fenceClickRects.get(d).contains(relativePoint)) {
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
					if (troughShape.contains(relativePoint)) {
						farm.toggleTrough(pos);
						done = true;
					}
					break;
				case BUILDING:
					if (buildingRect.contains(relativePoint)) {
						availableAnimals = farm.guessAnimalTypesToPut(pos, true);
						if (!animalArea.contains(relativePoint) // not clicked in animal area OR
								|| (availableAnimals.size() == 0 && farm.getAnimals(pos) == 0)) {// no animals available AND no animals present
							farm.toggleBuilding(pos);
							done = true;
						}
					}
					break;
				}
			}

			if (!done) {
				if (leftClick) {
					availableAnimals = availableAnimals != null ? availableAnimals : farm.guessAnimalTypesToPut(pos, true);
					Animal type = getAnimalType(availableAnimals, relativePoint);
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
			Point relativePoint = toRealRelativePos(e.getX(), e.getY());
			int count = -e.getWheelRotation();

			if (count > 0) {
				Animal type = getAnimalType(farm.guessAnimalTypesToPut(pos, true), relativePoint);
				if (type != null) {
					farm.putAnimals(pos, type, count);
				}
			} else {
				farm.takeAnimals(pos, -count);
			}
			farm.notifyObservers(ChangeType.FARM_CLICK);
		}

	}

//	public void showLooseAnimals() {
//		for (JLabel l : looseAnimalLabels) {
//			remove(l);
//		}
//		looseAnimalLabels.clear();
//		for (Animal type : Animal.values()) {
//			int count = farm.getLooseAnimals(type);
//			for (int i = 0; i < count; i++) {
//				looseAnimalLabels.add(SwingUtils.createAnimalLabel(type, 1, SwingUtils.NO_NUMBER));
//			}
//		}
//	}

}
