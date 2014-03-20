package com.dill.agricola;

import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.dill.agricola.support.Msg;

public class Main {

	public static boolean DEBUG = true;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// TODO locale picker
				Msg.load(new Locale("en"));
				try {
					Game g = new Game();
					if (DEBUG) {
						g.start();					
					}					
				} catch(Throwable e) {
					String msg = e.getMessage();
					JOptionPane.showMessageDialog(null, msg != null ? msg : Msg.get("unknownError"), Msg.get("error"), JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
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
