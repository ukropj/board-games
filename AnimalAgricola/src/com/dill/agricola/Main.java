package com.dill.agricola;

import java.util.Locale;

import javax.swing.SwingUtilities;

import com.dill.agricola.support.Msg;
import com.dill.agricola.view.Board;

public class Main {

	public static boolean DEBUG = true;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// TODO locale picker
				Msg.load(new Locale("en"));
				
				Game g = new Game(new Board());
				if (DEBUG) {
					g.start();					
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
