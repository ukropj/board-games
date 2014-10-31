package com.dill.agricola;

import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.dill.agricola.support.Msg;
import com.dill.agricola.view.SplashDialog;

public class Main {

	public static final String VERSION = "1.2.1";

	public static enum Lang {
		EN, DE, CZ, SK;

		public String toString() {
			return super.toString().toLowerCase();
		};
	};

	public static boolean DEBUG = false;
//	public static boolean DEBUG = true;
	public static boolean SKIP_LANG = false;
//	public static boolean SKIP_LANG = true;

	private static final Lang DEFAULT_LANG = Lang.EN; // 'en' is default

	public static void main(String[] args) {

		Lang lang = DEFAULT_LANG;

		if (!SKIP_LANG) {
			SplashDialog sd = new SplashDialog();
			lang = sd.getSelectedLang();
			if (lang == null) {
				// exit
				System.exit(1);
			}
		}
		Msg.load(new Locale(lang.toString()));

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setLookAndFeel();

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

		});
	}

	public static void asrtPositive(int i, String msg) throws IllegalArgumentException {
		if (i < 0) {
			throw new IllegalArgumentException(msg + " " + i);
		}
	}

	public static void asrtTrue(boolean b, String msg) throws IllegalStateException {
		if (!b) {
			throw new IllegalStateException(msg);
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

	public static void asrtNotEqual(Object o1, Object o2, String msg) throws IllegalArgumentException {
		if (o1 == o2 || o1 != null && o1.equals(o2)) {
			throw new IllegalArgumentException(msg + " " + o1);
		}
	}

}
