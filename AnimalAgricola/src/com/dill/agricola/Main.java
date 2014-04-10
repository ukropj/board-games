package com.dill.agricola;

import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.dill.agricola.support.Msg;

public class Main {

	public static boolean DEBUG = true;
//	public static boolean DEBUG = false;

	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setLookAndFeel();
				
				// TODO locale picker
				Msg.load(new Locale("en"));
				try {
					Game g = new Game();
					g.start();
				} catch (Throwable e) {
					String msg = e.getMessage();
					JOptionPane.showMessageDialog(null, msg != null ? msg : Msg.get("unknownError"), Msg.get("error"), JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}

			private void setLookAndFeel() {
				/*try {
//					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				        if ("Nimbus".equals(info.getName())) {
				            UIManager.setLookAndFeel(info.getClassName());
				            break;
				        }
				    }
				} catch (Exception e) {*/
					try {
						UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
					} catch (Exception e1) {
						throw new RuntimeException(e1);
					}
//				}
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
