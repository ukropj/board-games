package com.dill.agricola.view.utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.dill.agricola.Main;
import com.dill.agricola.view.utils.AgriImages.ImgSize;

public class Images {

	private Images() {
	}

	public static String getImagePath(String name, boolean icon) {
		return "resources/" + (icon ? "icons/" : "images/") + name + ".png";
	}

	public static URL getImageUrl(String path) {
		return Main.class.getResource(path);
	}

	public static BufferedImage createImage(String name) {
		return createImageInner(name, false);
	}

	public static ImageIcon createIcon(String name, ImgSize size) {
		if (size == ImgSize.SMALL) {
			name = "small/" + name; 
		}
		return new ImageIcon(createImageInner(name, true));
	}

	private static BufferedImage createImageInner(String name, boolean icon) {
		String path = getImagePath(name, icon);
		URL url = getImageUrl(path);
		BufferedImage img = null;
		if (url != null) {
			try {
				img = ImageIO.read(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Couldn't find file: " + path);
		}
		return img;
	}

	/*public BufferedImage getBestScaledInstance(BufferedImage img, int, Object hint, boolean higherQuality) {
		return getScaledInstance(img,
				(int) (img.getWidth() * ratio),
				(int) (img.getHeight() * ratio),
				RenderingHints.VALUE_INTERPOLATION_BILINEAR, higherQuality);
	}*/
	
	public static BufferedImage getBestScaledInstance(BufferedImage img, int maxSize) {
		float ratio =  1f * maxSize / Math.max(img.getWidth(),  img.getHeight());
		return getBestScaledInstance(img, ratio);
	}

	public static BufferedImage getBestScaledInstance(BufferedImage img, float ratio) {
		return getScaledInstance(img,
				(int) (img.getWidth() * ratio),
				(int) (img.getHeight() * ratio),
				RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
	}

	public static BufferedImage getScaledInstance(BufferedImage img,
			int targetWidth,
			int targetHeight,
			Object hint,
			boolean higherQuality) {
		int type = getImageType(img);
		BufferedImage ret = (BufferedImage) img;
		int w, h;
		if (higherQuality) {
			// Use multi-step technique: start with original size, then
			// scale down in multiple passes with drawImage()
			// until the target size is reached
			w = Math.max(img.getWidth(), targetWidth);
			h = Math.max(img.getHeight(), targetHeight);
		} else {
			// Use one-step technique: scale directly from original
			// size to target size with a single drawImage() call
			w = targetWidth;
			h = targetHeight;
		}

		do {
			if (higherQuality && w > targetWidth) {
				w /= 2;
				if (w < targetWidth) {
					w = targetWidth;
				}
			}

			if (higherQuality && h > targetHeight) {
				h /= 2;
				if (h < targetHeight) {
					h = targetHeight;
				}
			}

			BufferedImage tmp = new BufferedImage(w, h, type);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
		} while (w != targetWidth || h != targetHeight);

		return ret;
	}

	public static int getImageType(BufferedImage img) {
		return (img.getTransparency() == Transparency.OPAQUE) ?
				BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
	}

}
