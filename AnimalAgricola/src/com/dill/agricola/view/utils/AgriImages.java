package com.dill.agricola.view.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.ImageIcon;

import com.dill.agricola.Main;
import com.dill.agricola.common.Dir;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.support.Fonts;

public class AgriImages {

	private final static int MISC_U_FENCE = 0;
	// private final static int MISC_TROUGH = 1;
	// private final static int MISC_U_TROUGH = 2;

	private final static Map<BuildingType, BufferedImage> buildings = new EnumMap<BuildingType, BufferedImage>(BuildingType.class);
	private final static Map<Animal, BufferedImage> animalsSmall = new EnumMap<Animal, BufferedImage>(Animal.class);
	private final static Map<Animal, BufferedImage> animalsMedium = new EnumMap<Animal, BufferedImage>(Animal.class);
	private final static Map<Animal, BufferedImage> animalsBig = new EnumMap<Animal, BufferedImage>(Animal.class);
	private final static Map<Animal, BufferedImage> animalOutlinesMedium = new EnumMap<Animal, BufferedImage>(Animal.class);
	private final static BufferedImage[] materials = new BufferedImage[Material.values().length];
	private final static BufferedImage[] fences = new BufferedImage[Dir.values().length];
	private final static BufferedImage[] troughs = new BufferedImage[ImgSize.values().length];
	private final static BufferedImage[] firstTokens = new BufferedImage[ImgSize.values().length * PlayerColor.values().length];
	private final static BufferedImage[] farmsAndMargins = new BufferedImage[4];
	private final static BufferedImage[] workers = new BufferedImage[PlayerColor.values().length];
	private final static BufferedImage[] misc = new BufferedImage[4];
	private static BufferedImage[] cottages = new BufferedImage[PlayerColor.values().length];
	private static BufferedImage[] stallsAndStables = new BufferedImage[8];
	private static BufferedImage[] exts = new BufferedImage[4];

	private final static Map<Dir, BufferedImage> arrowsMedium = new EnumMap<Dir, BufferedImage>(Dir.class);
	private final static Map<Dir, BufferedImage> arrowsBig = new EnumMap<Dir, BufferedImage>(Dir.class);
	private final static Map<Dir, BufferedImage> redArrowsMedium = new EnumMap<Dir, BufferedImage>(Dir.class);
	private final static Map<Dir, BufferedImage> redArrowsBig = new EnumMap<Dir, BufferedImage>(Dir.class);

	private AgriImages() {
	}

	public static BufferedImage getFirstTokenImage(int id, ImgSize size) {
		int arrId = id + size.ordinal() * 2;
		Main.asrtInRange(arrId, 0, firstTokens.length, "Invalid img id");
		if (firstTokens[arrId] != null) {
			return firstTokens[id];
		} else {
			BufferedImage img = Images.createImage("first" + (id + 1));
			img = Images.getBestScaledInstance(img, size == ImgSize.BIG ? 0.3f : 0.2f);
			firstTokens[arrId] = img;
			return img;
		}
	}

	public static ImageIcon getFirstTokenIcon(int id, ImgSize size) {
		return new ImageIcon(getFirstTokenImage(id, size));
	}

	public static BufferedImage getWorkerImage(int id) {
		Main.asrtInRange(id, 0, workers.length, "Invalid img id");
		if (workers[id] != null) {
			return workers[id];
		} else {
			BufferedImage img = Images.createImage("worker" + (id + 1));
			img = Images.getBestScaledInstance(img, 0.3f);
			workers[id] = img;
			return img;
		}
	}

	public static ImageIcon getWorkerIcon(int id) {
		return new ImageIcon(getWorkerImage(id));
	}

	public static BufferedImage getFarmImage(int id) {
		Main.asrtInRange(id, 0, 2, "Invalid img id");
		if (farmsAndMargins[id] == null) {
			farmsAndMargins[id] = Images.createImage("farm" + (id + 1));
		}
		return farmsAndMargins[id];
	}

	public static BufferedImage getFarmMarginImage(Dir d) {
		int id = d == Dir.W ? 0 : 1;
		int arrId = id + 2;
		if (farmsAndMargins[arrId] == null) {
			farmsAndMargins[arrId] = Images.createImage("b" + (id + 1));
		}
		return farmsAndMargins[arrId];
	}

	public static BufferedImage getExtensionImage(int id) {
		Main.asrtInRange(id, 0, exts.length, "Invalid img id");
		if (exts[id] == null) {
			exts[id] = Images.createImage("ext" + (id + 1));
		}
		return exts[id];
	}

	public static ImageIcon getExtensionIcon(int id) {
		return new ImageIcon(Images.getBestScaledInstance(Images.createImage("ext1"), 0.03f));
	}

	public static BufferedImage getTroughImage(ImgSize size) {
		if (troughs[size.ordinal()] == null) {
			BufferedImage img = Images.createImage("trough");
			img = Images.getBestScaledInstance(img, size == ImgSize.BIG ? 0.6f : 0.5f);
			troughs[size.ordinal()] = img;
		}
		return troughs[size.ordinal()];
	}

	public static ImageIcon getTroughIcon() {
		return new ImageIcon(AgriImages.getTroughImage(ImgSize.MEDIUM));
	}

	/*
	 * public static BufferedImage getUnusedTroughImage() {
	 * if (misc[MISC_U_TROUGH] == null) {
	 * misc[MISC_U_TROUGH] = Images.createImage("trough-unused");
	 * }
	 * return misc[MISC_U_TROUGH];
	 * }
	 */

	public static BufferedImage getFenceImage(Dir d) {
		if (fences[d.ordinal()] == null) {
			BufferedImage img = null;
			switch (d) {
			case N:
			case S:
				img = Images.createImage("border1");
				break;
			case W:
			case E:
				img = Images.createImage("border2");
				break;
			}
			img = Images.getBestScaledInstance(img, 0.8f);
			fences[d.ordinal()] = img;
			fences[d.opposite().ordinal()] = img;
		}
		return fences[d.ordinal()];
	}

	public static BufferedImage getUnusedFenceImage() {
		if (misc[MISC_U_FENCE] == null) {
			misc[MISC_U_FENCE] = Images.createImage("border-unused");
		}
		return misc[MISC_U_FENCE];
	}

	public static BufferedImage getAnimalImage(Animal type, ImgSize size) {
		Map<Animal, BufferedImage> map = null;
		float ratio = 1.0f;
		switch (size) {
		case SMALL:
			map = animalsSmall;
			ratio = 0.25f;
			break;
		case MEDIUM:
			map = animalsMedium;
			ratio = 0.3f;
			break;
		case BIG:
			map = animalsBig;
			ratio = 0.5f;
			break;
		}

		if (map.containsKey(type)) {
			return map.get(type);
		} else {
			BufferedImage img = Images.createImage(type.toString().toLowerCase());
			img = Images.getBestScaledInstance(img, ratio);
			map.put(type, img);
			return img;
		}
	}

	public static BufferedImage getAnimalOutlineImage(Animal type) {
		if (animalOutlinesMedium.containsKey(type)) {
			return animalOutlinesMedium.get(type);
		} else {
			BufferedImage img = Images.createImage(type.toString().toLowerCase() + "-o");
			img = Images.getBestScaledInstance(img, 0.3f);
			animalOutlinesMedium.put(type, img);
			return img;
		}
	}

	public static ImageIcon getAnimalIcon(Animal type, ImgSize size) {
		return new ImageIcon(getAnimalImage(type, size));
	}

	public static BufferedImage getMaterialImage(Material type) {
		if (materials[type.ordinal()] == null) {
			BufferedImage img = Images.createImage(type.toString().toLowerCase());
			img = Images.getBestScaledInstance(img, 0.4f);
			materials[type.ordinal()] = img;
			return img;
		}
		return materials[type.ordinal()];
	}

	public static ImageIcon getMaterialIcon(Material type) {
		return new ImageIcon(getMaterialImage(type));
	}

	public static BufferedImage getBuildingImage(BuildingType type) {
		if (buildings.containsKey(type)) {
			return buildings.get(type);
		} else {
			BufferedImage img = null;
			switch (type) {
			/*case BUILDING:
				img = Images.createImage("special");
				break;*/
			case STALL:
				img = Images.createImage("stall1");
				break;
			case STABLES:
				img = Images.createImage("stables1");
				break;
			case COTTAGE:
				img = null;
				break;
			case HALF_TIMBERED_HOUSE:
				img = Images.createImage("half-timbered-house");
				break;
			case STORAGE_BUILDING:
				img = Images.createImage("storage-building");
				break;
			case SHELTER:
				img = Images.createImage("shelter");
				break;
			case OPEN_STABLES:
				img = Images.createImage("open-stables");
				break;
			}
			addBuildingText(type, img);
			buildings.put(type, img);
			return img;
		}
	}

	public static BufferedImage getCottageImage(int id) {
		if (cottages[id] == null) {
			BufferedImage img = Images.createImage("cottage" + (id + 1));
			addBuildingText(BuildingType.COTTAGE, img);
			cottages[id] = img;
		}
		return cottages[id];
	}
	
	public static BufferedImage getStallImage(int id) {
		if (stallsAndStables[id] == null) {
			BufferedImage img = Images.createImage("stall" + (id + 1));
			addBuildingText(BuildingType.STALL, img);
			stallsAndStables[id] = img;
		}
		return stallsAndStables[id];
	}

	public static BufferedImage getStableImage(int id) {
		int arrId = id + 4;
		if (stallsAndStables[arrId] == null) {
			BufferedImage img = Images.createImage("stables" + (id + 1));
			addBuildingText(BuildingType.STABLES, img);
			//			img = Images.getBestScaledInstance(img, 0.5f);
			stallsAndStables[arrId] = img;
		}
		return stallsAndStables[arrId];
	}

	private static void addBuildingText(BuildingType type, BufferedImage img) {
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g.setFont(Fonts.BUILDING_FONT);
		g.setColor(Color.BLACK);
		String text = type.name;
		int x = 46, y = 78, maxw = 160;
		Fonts.updateFontToFit(g, text, maxw);
		int w = g.getFontMetrics().stringWidth(text);
		g.drawString(type.name, x + (maxw - w) / 2, y);
		g.dispose();
	}

	public static ImageIcon getBuildingIcon(BuildingType type, ImgSize size) {
		BufferedImage img = getBuildingImage(type);
		switch (size) {
		case SMALL:
			img = Images.getBestScaledInstance(img, 0.1f);
			break;
		case MEDIUM:
			img = Images.getBestScaledInstance(img, 0.3f);
			break;
		case BIG:
			img = Images.getBestScaledInstance(img, 0.8f);
			break;
		}
		return new ImageIcon(img);
	}

	public static BufferedImage getArrowImage(Dir d, boolean red, ImgSize size) {
		Map<Dir, BufferedImage> map = null;
		float ratio = 1.0f;
		switch (size) {
		case SMALL:
		case MEDIUM:
			map = red ? redArrowsMedium : arrowsMedium;
			ratio = 0.2f;
			break;
		case BIG:
			map = red ? redArrowsBig : arrowsBig;
			ratio = 0.5f;
			break;
		}

		if (map.containsKey(d)) {
			return map.get(d);
		} else {
			BufferedImage img = Images.createImage("arrow" + (red ? "-red" : "") + (d.ordinal() + 1));
			img = Images.getBestScaledInstance(img, ratio);
			map.put(d, img);
			return img;
		}
	}

	public static ImageIcon getArrowIcon(Dir d, boolean red) {
		return new ImageIcon(getArrowImage(d, red, ImgSize.MEDIUM));
	}

	public static enum ImgSize {
		SMALL, MEDIUM, BIG;
	}

}
