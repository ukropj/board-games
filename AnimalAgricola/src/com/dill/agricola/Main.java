package com.dill.agricola;

import javax.swing.SwingUtilities;

import com.dill.agricola.view.Board;

public class Main {

	public static boolean DEBUG = true;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Game g = new Game(new Board());
				g.start();
			}
		});
	}

	public static void asrtPositive(int i, String msg) throws IllegalArgumentException {
		if (i < 0) {
			throw new IllegalArgumentException(msg);
		}
	}

	public static void asrtNotNull(Object o, String msg) throws IllegalArgumentException {
		if (o == null) {
			throw new IllegalArgumentException(msg);
		}
	}

}
