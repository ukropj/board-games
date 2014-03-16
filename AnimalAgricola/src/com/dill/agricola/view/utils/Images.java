package com.dill.agricola.view.utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.dill.agricola.Main;

public class Images {

	private Images() {
	}

//	public static ImageIcon toIcon(BufferedImage image, int height) {
//		float ratio = 1.0f * height / image.getHeight();
//		int width = (int) (image.getWidth() * ratio);
//		// TODO don't use getScaledInstance
//		return new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
//	}

	public static URL getImageUrl(String name) {
		return Main.class.getResource("images/" + name + ".png");
	}
	
	public static BufferedImage createImage(String name) {
		URL url = getImageUrl(name);
		BufferedImage img = null;
		if (url != null)
			try {
				img = ImageIO.read(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		else
			System.err.println("Couldn't find file: " + url);
		return img;
	}

	/*public BufferedImage getBestScaledInstance(BufferedImage img, int, Object hint, boolean higherQuality) {
		return getScaledInstance(img,
				(int) (img.getWidth() * ratio),
				(int) (img.getHeight() * ratio),
				RenderingHints.VALUE_INTERPOLATION_BILINEAR, higherQuality);
	}*/

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
