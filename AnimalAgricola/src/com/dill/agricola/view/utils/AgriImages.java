package com.dill.agricola.view.utils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import com.dill.agricola.Main;
import com.dill.agricola.common.Dir;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.BuildingType.BuildingText;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.support.Fonts;
import com.dill.agricola.support.Msg;

public class AgriImages {
	
	public final static Color OVERLAY_COLOR = new Color(255, 255, 255, 100);
	public static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);

	private final static BufferedImage[] buildings = new BufferedImage[BuildingType.values().length];
	private final static BufferedImage[] animals = new BufferedImage[ImgSize.values().length * (Animal.values().length + 1)];
	private final static BufferedImage[] materials = new BufferedImage[Material.values().length + 1];
	private final static BufferedImage[] fences = new BufferedImage[Dir.values().length];
	private final static BufferedImage[] troughs = new BufferedImage[ImgSize.values().length];
	private final static BufferedImage[] firstTokens = new BufferedImage[ImgSize.values().length * PlayerColor.values().length];
	private final static BufferedImage[] farmsAndMargins = new BufferedImage[4];
	private final static BufferedImage[] workers = new BufferedImage[ImgSize.values().length * (PlayerColor.values().length + 1)];
	private final static BufferedImage[] cottages = new BufferedImage[PlayerColor.values().length];
	private final static BufferedImage[] stallsAndStables = new BufferedImage[12];
	private final static BufferedImage[] exts = new BufferedImage[6];
	private final static BufferedImage[] pads = new BufferedImage[3];

	private final static BufferedImage[] arrowsMedium = new BufferedImage[Dir.values().length];
	private final static BufferedImage[] arrowsBig = new BufferedImage[Dir.values().length];
	private final static BufferedImage[] redArrowsMedium = new BufferedImage[Dir.values().length];
	private final static BufferedImage[] redArrowsBig = new BufferedImage[Dir.values().length];
	private final static BufferedImage[] symbols = new BufferedImage[4];


	private AgriImages() {
	}
	
	public static BufferedImage getFirstTokenImage(int id, ImgSize size) {
		int arrId = id + size.ordinal() * PlayerColor.values().length;
		Main.asrtInRange(arrId, 0, firstTokens.length, "Invalid img id");
		if (firstTokens[arrId] != null) {
			return firstTokens[arrId];
		} else {
			BufferedImage img = Images.createImage("first" + (id + 1));
			img = Images.getBestScaledInstance(img, size == ImgSize.BIG ? 0.3f : size == ImgSize.MEDIUM ? 0.17f : 0.1f);
			firstTokens[arrId] = img;
			return img;
		}
	}

	public static ImageIcon getFirstTokenIcon(ImgSize size) {
		return new ImageIcon(getFirstTokenImage(Math.random() < 0.5 ? 0 : 1, size));
	}

	public static ImageIcon getFirstTokenIcon(int id, ImgSize size) {
		return new ImageIcon(getFirstTokenImage(id, size));
	}

	public static BufferedImage getWorkerImage(PlayerColor color, ImgSize size) {
		int id = color != null ? color.ordinal() : 2;
		int arrId = id + size.ordinal() * PlayerColor.values().length;
		if (workers[arrId] != null) {
			return workers[arrId];
		} else {
			BufferedImage img = Images.createImage("worker" + (id + 1));
			img = Images.getBestScaledInstance(img, size != ImgSize.SMALL ? 0.3f : 0.2f);
			workers[arrId] = img;
			return img;
		}
	}

	public static ImageIcon getWorkerIcon(PlayerColor color, ImgSize size) {
		return new ImageIcon(getWorkerImage(color, size));
	}

	public static BufferedImage getFarmImage(PlayerColor color) {
		int i = color.ordinal();
		Main.asrtInRange(i, 0, 2, "Invalid img id");
		if (farmsAndMargins[i] == null) {
			farmsAndMargins[i] = Images.createImage("f_farm" + (i + 1));
		}
		return farmsAndMargins[i];
	}

	public static BufferedImage getFarmMarginImage(Dir d) {
		int id = d == Dir.W ? 0 : 1;
		int arrId = id + 2;
		if (farmsAndMargins[arrId] == null) {
			farmsAndMargins[arrId] = Images.createImage("f_margin" + (id + 1));
		}
		return farmsAndMargins[arrId];
	}

	public static BufferedImage getFarmPadImage(int id) {
		Main.asrtInRange(id, 0, pads.length, "Invalid img id");
		if (pads[id] == null) {
			pads[id] = Images.createImage("f_pad" + (id + 1));
		}
		return pads[id];
	}

	public static BufferedImage getExtensionImage(int id) {
		Main.asrtInRange(id, 0, exts.length, "Invalid img id");
		if (exts[id] == null) {
			exts[id] = Images.createImage("f_ext" + (id + 1));
		}
		return exts[id];
	}

	public static BufferedImage getTroughImage(ImgSize size) {
		if (troughs[size.ordinal()] == null) {
			BufferedImage img = Images.createImage("trough");
			img = Images.getBestScaledInstance(img, size == ImgSize.BIG ? 0.6f : 0.5f);
			troughs[size.ordinal()] = img;
		}
		return troughs[size.ordinal()];
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

	public static BufferedImage getAnimalImage(Animal type, ImgSize size) {
		int index = (type != null ? type.ordinal() : Animal.values().length) + size.ordinal() * (Animal.values().length + 1);
		float[] ratios = new float[] { 0.25f, 0.3f, 0.5f };
		if (animals[index] == null) {
			BufferedImage img = Images.createImage("a_" + (type != null ? type.toString().toLowerCase() : "all") + (size != ImgSize.BIG ? "" : "1"));
			img = Images.getBestScaledInstance(img, ratios[size.ordinal()]);
			animals[index] = img;
		}
		return animals[index];
	}

	public static ImageIcon getAnimalIcon(Animal type, ImgSize size) {
		return new ImageIcon(getAnimalImage(type, size));
	}

	public static ImageIcon getAnimalMultiIcon(Animal type, int count, ImgSize size) {
		return new ImageIcon(getMultiImage(getAnimalImage(type, size), count));
	}

	public static BufferedImage getMaterialImage(Material type) {
		int index = type != null ? type.ordinal() : Material.values().length;
		if (materials[index] == null) {
			BufferedImage img = Images.createImage("m_" + (type != null ? type.toString().toLowerCase() : "all"));
			img = Images.getBestScaledInstance(img, 0.4f);
			materials[index] = img;
			return img;
		}
		return materials[index];
	}

	public static ImageIcon getMaterialIcon(Material type) {
		return new ImageIcon(getMaterialImage(type));
	}

	public static ImageIcon getMaterialMultiIcon(Material type, int count) {
		return new ImageIcon(getMultiImage(getMaterialImage(type), count));
	}

	public static BufferedImage getMultiImage(BufferedImage img, int count) {
		if (count < 2) {
			return img;
		}
		float d = Math.min(4.5f, 30f / count);
		BufferedImage multiImg = new BufferedImage(img.getWidth() + (int) (d * (count - 1)),
				img.getHeight(), Images.getImageType(img));
		Graphics2D g2 = multiImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		for (int i = 0; i < count; i++) {
			g2.drawImage(img, (int) (d * (count - 1 - i)), 0, img.getWidth(), img.getHeight(), null);
		}
		g2.dispose();
		return multiImg;
	}

	public static String getBuildingImageName(BuildingType type) {
		String dashedName = type.toString().toLowerCase().replaceAll("_", "-");
		switch (type) {
		case STALL:
		case STABLES:
		case COTTAGE:
			dashedName += "1";
		default:
			break;
		}
		return "b" + (type.set > 0 ? type.set : "") + "_" + dashedName;
	}

	public static BufferedImage getBuildingImage(BuildingType type) {
		int i = type.ordinal();
		if (buildings[i] == null) {
			BufferedImage img = Images.createImage(getBuildingImageName(type));
			buildings[i] = img;
		}
		return buildings[i];
	}

	public static BufferedImage getCottageImage(PlayerColor color) {
		int i = color.ordinal();
		if (cottages[i] == null) {
			cottages[i] = Images.createImage("b_cottage" + (i + 1));
		}
		return cottages[i];
	}

	public static BufferedImage getStallImage(int id) {
		if (stallsAndStables[id] == null) {
			stallsAndStables[id] = Images.createImage("b_stall" + (id + 1));
		}
		return stallsAndStables[id];
	}

	public static BufferedImage getStableImage(int id) {
		int arrId = id + stallsAndStables.length / 2;
		if (stallsAndStables[arrId] == null) {
			stallsAndStables[arrId] = Images.createImage("b_stables" + (id + 1));
		}
		return stallsAndStables[arrId];
	}

	private static BufferedImage addBuildingText(BuildingType type, BufferedImage orig) {
		int w = orig.getWidth(), h = orig.getHeight();
		BufferedImage img = new BufferedImage(w, h, Images.getImageType(orig));
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g.drawImage(orig, 0, 0, w, h, null);
		g.setFont(Fonts.BUILDING_ICON);
		g.setColor(Color.BLACK);
		// name
		int x = 46, y = 78, maxw = 160;
		if (type == BuildingType.COTTAGE) {
			y -= 2;
		}
		Fonts.updateFontToFit(g, type.name, maxw);
		int nameW = g.getFontMetrics().stringWidth(type.name);
		g.drawString(type.name, x + (maxw - nameW) / 2, y);
		// text
		for (BuildingText bText : type.texts) {
			if (bText.text != null) {
				g.setFont(Fonts.BUILDING_ICON);
				Fonts.updateFontToFit(g, bText.text, (int) (w * bText.textWidth));
				float ty = h * bText.y, textH = g.getFontMetrics().getHeight();
				for (String line : bText.text.split("[\r\n]+")) {
					g.drawString(line, w * bText.x, ty);
					ty += textH;
				}
			}
		}
		g.dispose();
		return img;
	}

	
	public static ImageIcon getBuildingIcon(BuildingType type, ImgSize size) {
		return getBuildingIcon(type, size, true);
	}
	
	public static ImageIcon getBuildingIcon(BuildingType type, ImgSize size, boolean enabled) {
		BufferedImage img = getBuildingImage(type);
		img = addBuildingText(type, img);
		switch (size) {
		case SMALL:
			img = Images.getBestScaledInstance(img, 0.15f);
			break;
		case MEDIUM:
			img = Images.getBestScaledInstance(img, 0.3f);
			break;
		case BIG:
			img = Images.getBestScaledInstance(img, 0.7f);
			break;
		}
		if (!enabled) {
			Graphics2D g2 = img.createGraphics();
			// overlay
			g2.setColor(OVERLAY_COLOR);
			g2.fillRect(0, 0, img.getWidth(), img.getHeight());
			g2.dispose();
		}
		return new ImageIcon(img);
	}

	public static ImageIcon getStallIcon() {
		BufferedImage orig = Images.createImage("b_stall1");
		orig = Images.getBestScaledInstance(orig, 0.3f);
		int w = orig.getWidth(), h = orig.getHeight();
		BufferedImage img = new BufferedImage(w, h, Images.getImageType(orig));
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g.drawImage(orig, 0, 0, w, h, null);
		g.setFont(Fonts.BUILDING_ICON_MEDIUM);
		g.setColor(Color.BLACK);
		// names
		String stall = Msg.get("stall");
		int x = 28, y = 24;
		g.drawString(stall, x, y);
		g.dispose();
		return new ImageIcon(img);
	}

	public static ImageIcon getStallToStablesIcon() {
		BufferedImage orig = Images.createImage("stall-to-stables");
		orig = Images.getBestScaledInstance(orig, 0.3f);
		int w = orig.getWidth(), h = orig.getHeight();
		BufferedImage img = new BufferedImage(w, h, Images.getImageType(orig));
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g.drawImage(orig, 0, 0, w, h, null);
		g.setFont(Fonts.BUILDING_ICON_MEDIUM);
		g.setColor(Color.BLACK);
		// names
		String stall = Msg.get("stall");
		String stables = Msg.get("stables");
		int x1 = 14, x2 = 55, y = 24;
		g.drawString(stall, x1, y);
		g.drawString(stables, x2, y);
		g.dispose();
		return new ImageIcon(img);
	}
	
	public static ImageIcon getButtonImage(String name) {
		BufferedImage img = Images.createImage("btn_" + name);
		return new ImageIcon(Images.getBestScaledInstance(img, 0.26f));
	}

	public static ImageIcon getPurchasableIcon(Purchasable type) {
		switch (type) {
		case BUILDING:
			return getBuildingIcon(BuildingType.HALF_TIMBERED_HOUSE, ImgSize.SMALL);
		case EXTENSION:
			return new ImageIcon(Images.getBestScaledInstance(getExtensionImage(0), 0.06f));
		case FENCE:
			return new ImageIcon(Images.getBestScaledInstance(AgriImages.getFenceImage(Dir.N), 0.5f));
		case TROUGH:
			return new ImageIcon(AgriImages.getTroughImage(ImgSize.MEDIUM));
		default:
			return null;
		}
	}

	public static BufferedImage getArrowImage(Dir d, boolean red, ImgSize size) {
		BufferedImage[] imgs = null;
		float ratio = 1.0f;
		switch (size) {
		case SMALL:
		case MEDIUM:
			imgs = red ? redArrowsMedium : arrowsMedium;
			ratio = 0.2f;
			break;
		case BIG:
			imgs = red ? redArrowsBig : arrowsBig;
			ratio = 0.5f;
			break;
		}

		if (imgs[d.ordinal()] == null) {
			BufferedImage img = Images.createImage("arrow" + (red ? "-red" : "") + (d.ordinal() + 1));
			img = Images.getBestScaledInstance(img, ratio);
			imgs[d.ordinal()] = img;
		}
		return imgs[d.ordinal()];
	}

	public static ImageIcon getArrowIcon(Dir d, boolean red) {
		return new ImageIcon(getArrowImage(d, red, ImgSize.MEDIUM));
	}

	public static BufferedImage getSymbolImage(int id) {
		if (symbols[id] == null) {
			BufferedImage img = Images.createImage(id == 0 ? "no" : "yes");
			img = Images.getBestScaledInstance(img, 0.15f);
			symbols[id] = img;
		}
		return symbols[id];
	}

	public static ImageIcon getYesIcon() {
		return new ImageIcon(getSymbolImage(1));
	}

	public static ImageIcon getNoIcon() {
		return new ImageIcon(getSymbolImage(0));
	}

	/*public static ImageIcon getDisabledIcon(ImageIcon icon) {
		GrayFilter filter = new GrayFilter(true, 40);
	    ImageProducer prod = new FilteredImageSource(icon.getImage().getSource(), filter);
	    Image grayImage = Toolkit.getDefaultToolkit().createImage(prod);
		return new ImageIcon(grayImage);
	}*/

	public static enum ImgSize {
		SMALL, MEDIUM, BIG;
	}

}
