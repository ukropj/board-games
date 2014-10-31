package com.dill.agricola.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.dill.agricola.Main.Lang;
import com.dill.agricola.view.utils.Images;

public class SplashDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private Lang selectedLang = null;

	public SplashDialog() {
		setTitle("Agricola: All Creatures Big and Small");
		setIconImage(Images.getBestScaledInstance(Images.createImage("a"), 16));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		
		buildButtons();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void buildButtons() {
		Container pane = getContentPane();
		JPanel mainP = new ImagePanel("splash");
		pane.add(mainP);
		mainP.setLayout(new BorderLayout());
		mainP.add(Box.createGlue(), BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension(0, 60));
		buttonPanel.setOpaque(false);
		mainP.add(buttonPanel, BorderLayout.SOUTH);
		
		Insets i = new Insets(0,0,0,0);
		for (Lang lang : Lang.values()) {
			final JButton button = new JButton(getLangIcon(lang));
			button.setMargin(i);
			button.setActionCommand(lang.toString());
			button.addActionListener(this);
			buttonPanel.add(button);
		}
	}
	
	private ImageIcon getLangIcon(Lang lang) {
		BufferedImage img = Images.createImage("flags/lang_" + lang.toString());
		img = Images.getBestScaledInstance(img, 50);
		return new ImageIcon(img);
	}
	
	public Lang getSelectedLang() {
		return selectedLang;
	}

	public void actionPerformed(ActionEvent e) {
		selectedLang = Lang.valueOf(e.getActionCommand().toUpperCase());
		dispose();
	}
	
	private final class ImagePanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private final BufferedImage img;
		
		public ImagePanel(String imgName) {
			img = Images.createImage(imgName);
		    setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
		}

		public void paintComponent(Graphics g) {
			g.drawImage(img, 0, 0, null);
		}
		
	}

}
