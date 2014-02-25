package com.dill.agricola.view;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.dill.agricola.Main;
import com.dill.agricola.model.buildings.BuildingType;
import com.dill.agricola.model.enums.Animal;
import com.dill.agricola.model.enums.Dir;
import com.dill.agricola.model.enums.Material;

public class Images {

	private final static Map<BuildingType, BufferedImage> buildings = new EnumMap<BuildingType, BufferedImage>(BuildingType.class);
	private final static Map<Animal, BufferedImage> animals = new EnumMap<Animal, BufferedImage>(Animal.class);
	private final static Map<Animal, ImageIcon> animalIcons = new EnumMap<Animal, ImageIcon>(Animal.class);
	private final static Map<Animal, ImageIcon> animalIconsBig = new EnumMap<Animal, ImageIcon>(Animal.class);
	private final static Map<Material, BufferedImage> materials = new EnumMap<Material, BufferedImage>(Material.class);
	private final static Map<Material, ImageIcon> materialIcons = new EnumMap<Material, ImageIcon>(Material.class);
	private final static Map<Dir, BufferedImage> fences = new EnumMap<Dir, BufferedImage>(Dir.class);
	private final static BufferedImage[] firstTokens = new BufferedImage[2];
	private final static BufferedImage[] farms = new BufferedImage[2];
	private final static BufferedImage[] margins = new BufferedImage[2];
	private static BufferedImage ext = null;
	private static BufferedImage trough = null;

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
		if (farms[id] == null) {
			farms[id] = createImage("farm" + (id + 1));
		}
		return farms[id];
	}

	public static BufferedImage getExtensionImage() {
		if (ext == null) {
			ext = createImage("ext1");
		}
		return ext;
	}

	public static BufferedImage getFarmMarginImage(Dir d) {
		int id = d == Dir.W ? 0 : 1;
		if (margins[id] == null) {
			margins[id] = createImage("b" + (id + 1));
		}
		return margins[id];
	}

	public static BufferedImage getThroughImage() {
		if (trough == null) {
			trough = createImage("trough");
		}
		return trough;
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
