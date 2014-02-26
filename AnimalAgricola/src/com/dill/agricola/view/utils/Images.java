package com.dill.agricola.view.utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.dill.agricola.Main;
import com.dill.agricola.common.Dir;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.Material;

public class Images {
	
	private final static int MISC_U_FENCE = 0;
	private final static int MISC_TROUGH = 1;
	private final static int MISC_U_TROUGH = 2;

	private final static Map<BuildingType, BufferedImage> buildings = new EnumMap<BuildingType, BufferedImage>(BuildingType.class);
	private final static Map<Animal, BufferedImage> animals = new EnumMap<Animal, BufferedImage>(Animal.class);
	private final static Map<Animal, ImageIcon> animalIcons = new EnumMap<Animal, ImageIcon>(Animal.class);
	private final static Map<Animal, ImageIcon> animalIconsBig = new EnumMap<Animal, ImageIcon>(Animal.class);
	private final static Map<Material, BufferedImage> materials = new EnumMap<Material, BufferedImage>(Material.class);
	private final static Map<Material, ImageIcon> materialIcons = new EnumMap<Material, ImageIcon>(Material.class);
	private final static Map<Dir, BufferedImage> fences = new EnumMap<Dir, BufferedImage>(Dir.class);
	private final static BufferedImage[] firstTokens = new BufferedImage[2];
	private final static BufferedImage[] farmsAndMargins = new BufferedImage[4];
	private final static BufferedImage[] misc = new BufferedImage[4];
	private static BufferedImage ext = null;

	private final static Map<Dir, ImageIcon> arrowIcons = new EnumMap<Dir, ImageIcon>(Dir.class);
	private final static Map<Dir, ImageIcon> redArrowIcons = new EnumMap<Dir, ImageIcon>(Dir.class);

	private Images() {
	}

	public static ImageIcon toIcon(BufferedImage image, int height) {
		float ratio = 1.0f * height / image.getHeight();
		int width = (int) (image.getWidth() * ratio);
		// TODO dont use getScaledInstance
		return new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
	}

	public static ImageIcon toIcon(BufferedImage image, float scale) {
		int height = (int) (image.getHeight() * scale);
		int width = (int) (image.getWidth() * scale);
		// TODO dont use getScaledInstance
		return new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
	}

	public static BufferedImage getFirstTokenImage(int id) {
		if (firstTokens[id] == null) {
			firstTokens[id] = createImage("first" + (id + 1));
		}
		return firstTokens[id];
	}

	public static ImageIcon getFirstTokenIcon(int id, int height) {
		return toIcon(getFirstTokenImage(id), height);
	}

	public static BufferedImage getFarmImage(int id) {
		id = id % 2;
		if (farmsAndMargins[id] == null) {
			farmsAndMargins[id] = createImage("farm" + (id + 1));
		}
		return farmsAndMargins[id];
	}

	public static BufferedImage getFarmMarginImage(Dir d) {
		int id = d == Dir.W ? 0 : 1;
		if (farmsAndMargins[2 + id] == null) {
			farmsAndMargins[2 + id] = createImage("b" + (id + 1));
		}
		return farmsAndMargins[2 + id];
	}

	public static BufferedImage getExtensionImage() {
		if (ext == null) {
			ext = createImage("ext1");
		}
		return ext;
	}

	public static BufferedImage getTroughImage() {
		if (misc[MISC_TROUGH] == null) {
			misc[MISC_TROUGH] = createImage("trough");
		}
		return misc[MISC_TROUGH];
	}
	
	public static BufferedImage getUnusedTroughImage() {
		if (misc[MISC_U_TROUGH] == null) {
			misc[MISC_U_TROUGH] = createImage("trough-unused");
		}
		return misc[MISC_U_TROUGH];
	}

	public static BufferedImage getFenceImage(Dir d) {
		if (fences.containsKey(d)) {
			return fences.get(d);
		} else {
			BufferedImage img = null;
			switch (d) {
			case N:
			case S:
				img = createImage("border1");
				break;
			case W:
			case E:
				img = createImage("border2");
				break;
			}
			fences.put(d, img);
			fences.put(d.opposite(), img);
			return img;
		}
	}
	
	public static BufferedImage getUnusedFenceImage() {
		if (misc[MISC_U_FENCE] == null) {
			misc[MISC_U_FENCE] = createImage("border-unused");
		}
		return misc[MISC_U_FENCE];
	}

	public static BufferedImage getAnimalImage(Animal type) {
		if (animals.containsKey(type)) {
			return animals.get(type);
		} else {
			BufferedImage img = createImage(type.toString().toLowerCase());
			animals.put(type, img);
			return img;
		}
	}

	public static ImageIcon getAnimalIcon(Animal type, IconSize size) {
		return getCachedIcon(size == IconSize.SMALL ? animalIcons : animalIconsBig, type, getAnimalImage(type), size == IconSize.SMALL ? 0.3f : 0.5f);
	}

	public static BufferedImage getMaterialImage(Material type) {
		if (materials.containsKey(type)) {
			return materials.get(type);
		} else {
			BufferedImage img = createImage(type.toString().toLowerCase());
			materials.put(type, img);
			return img;
		}
	}

	public static ImageIcon getMaterialIcon(Material type) {
		return getCachedIcon(materialIcons, type, getMaterialImage(type), 20);
	}

	public static BufferedImage getBuildingImage(BuildingType type) {
		if (buildings.containsKey(type)) {
			return buildings.get(type);
		} else {
			BufferedImage img = null;
			switch (type) {
			case BUILDING:
				img = createImage("special");
				break;
			case STALL:
				img = createImage("stall1");
				break;
			case STABLES:
				img = createImage("stables1");
				break;
			case COTTAGE:
				img = null;
				break;
			case HALF_TIMBERED_HOUSE:
				img = createImage("half-timbered-house");
				break;
			case STORAGE_BUILDING:
				img = createImage("storage-building");
				break;
			case SHELTER:
				img = createImage("shelter");
				break;
			case OPEN_STABLES:
				img = createImage("open-stables");
				break;
			}
			buildings.put(type, img);
			return img;
		}
	}

	public static ImageIcon getBuildingIcon(BuildingType type, int height) {
		return toIcon(getBuildingImage(type), height);
	}

	public static BufferedImage getArrowImage(Dir d, boolean red) {
		return createImage("arrow" + (red ? "-red" : "") + (d.ordinal() + 1));
	}
	
	public static ImageIcon getArrowIcon(Dir d, boolean red) {
		Map<Dir, ImageIcon> map = red ? redArrowIcons : arrowIcons;
		if (map.containsKey(d)) {
			return map.get(d);
		} else {
			BufferedImage img = createImage("arrow" + (red ? "-red" : "") + (d.ordinal() + 1));
			ImageIcon icon = toIcon(img, d == Dir.S || d == Dir.N ? 14 : 10);
			map.put(d, icon);
			return icon;
		}
	}
	
	private static <T extends Enum<T>> ImageIcon getCachedIcon(Map<T, ImageIcon> map, T type, BufferedImage img, int size){
		if (map.containsKey(type)) {
			return map.get(type);
		} else {
			ImageIcon icon = toIcon(img, size);
			map.put(type, icon);
			return icon;
		}
	}
	
	private static <T extends Enum<T>> ImageIcon getCachedIcon(Map<T, ImageIcon> map, T type, BufferedImage img, float ratio){
		if (map.containsKey(type)) {
			return map.get(type);
		} else {
			ImageIcon icon = toIcon(img, ratio);
			map.put(type, icon);
			return icon;
		}
	}

	private static BufferedImage createImage(String name) {
		String path = "images/" + name + ".png";
		BufferedImage img = null;
		URL url = Main.class.getResource(path);
		if (url != null)
			try {
				img = ImageIO.read(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		else
			System.err.println("Couldn't find file: " + path);
		return img;
	}

	public static enum IconSize {
		SMALL, BIG;
	}

}
