package com.dill.agricola;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.Images;
import com.dill.agricola.view.utils.UiFactory;

public class Main {

	public static boolean DEBUG = false;
	public static boolean MORE_BUILDINGS = false;

	private static String[] LANGS = { "en", "de", "cz" };
	private static int DEFAULT_LANG = 0; // 'en' is default

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setLookAndFeel();
				chooseLanguage();

				try {
					Game g = new Game();
					g.start();
				} catch (Throwable e) {
					String msg = e.getMessage();
					JOptionPane.showMessageDialog(null, msg != null ? msg : Msg.get("unknownErrorMsg"), Msg.get("errorTitle"), JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}

			private void setLookAndFeel() {
				try {
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				} catch (Exception e1) {
					throw new RuntimeException(e1);
				}
			}

			private void chooseLanguage() {
				List<JComponent> opts = new ArrayList<JComponent>();
				for (String lang : LANGS) {
					JComponent opt = UiFactory.createLabel(getLangIcon(lang));
					opts.add(opt);
				}
				int chosenLang = Main.DEBUG ? DEFAULT_LANG :
						UiFactory.showOptionDialog(null, "Select language", "Agricola: All Creatures Big and Small", null, opts);
				if (chosenLang == UiFactory.NO_OPTION) {
					chosenLang = DEFAULT_LANG;
				}
				Msg.load(new Locale(LANGS[chosenLang]));
			}

			private ImageIcon getLangIcon(String lang) {
				BufferedImage img = Images.createImage("lang_" + lang);
				img = Images.getBestScaledInstance(img, 50);
				return new ImageIcon(img);
			}
		});
	}

	public static void asrtPositive(int i, String msg) throws IllegalArgumentException {
		if (i < 0) {
			throw new IllegalArgumentException(msg + " " + i);
		}
	}

	public static void asrtNotNull(Object o, String msg) throws IllegalArgumentException {
		if (o == null) {
			throw new IllegalArgumentException(msg);
		}
	}

	public static void asrtInRange(int i, int min, int max, String msg) throws IllegalArgumentException {
		if (i < min || i >= max) {
			throw new IllegalArgumentException(msg + " " + i);
		}
	}

}
