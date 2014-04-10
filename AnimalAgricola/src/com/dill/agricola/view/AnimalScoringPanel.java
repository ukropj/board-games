package com.dill.agricola.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dill.agricola.model.types.Animal;
import com.dill.agricola.support.Fonts;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.UiFactory;

public class AnimalScoringPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final Color LESS_COLOR = new Color(150, 23, 23);
	private static final Color MOST_COLOR = new Color(23, 133, 23);
	private static final Color MORE_COLOR = new Color(218, 165, 32);
	private static final Color BORDER_COLOR = Color.LIGHT_GRAY;
	private static final Font COUNT_FONT = Fonts.TEXT_FONT.deriveFont(14f);

	public AnimalScoringPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

		add(setFont(UiFactory.createLabel(Msg.get("animalBonusPointsPerCount")), COUNT_FONT));
		add(Box.createVerticalStrut(5));
		add(buildAnimalScoring());
		add(Box.createVerticalStrut(20));
		add(setFont(UiFactory.createLabel(Msg.get("animalCountPerBonusPoints")), COUNT_FONT));
		add(Box.createVerticalStrut(5));
		add(buildAnimalScoring2());
		add(Box.createVerticalGlue());
	}

	private JComponent buildAnimalScoring() {
		JPanel p = UiFactory.createHorizontalPanel();
		JPanel p0 = new JPanel(new GridLayout(5, 0));
		JPanel p1 = new JPanel(new GridLayout(5, 0));
		p.add(p0);
		p.add(Box.createHorizontalStrut(-3));
		p.add(p1);
		int l = 14;
		// header
		JPanel emptyP = new JPanel();
		emptyP.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, BORDER_COLOR));
		p0.add(emptyP);
		JLabel lab = addBorder(UiFactory.createLabel("[0-3]"));
		lab.setFont(COUNT_FONT);
		p0.add(lab);
		for (int i = 4; i < l; i++) {
			lab = addBorder(UiFactory.createLabel("[" + String.valueOf(i) + "]"));
			lab.setFont(COUNT_FONT);
			p1.add(lab);
		}
		// animals
		for (Animal a : Animal.values()) {
			JLabel al = UiFactory.createAnimalLabel(a, 0, UiFactory.NO_NUMBER);
			al.setToolTipText(a.getName(true));
			p0.add(addBorder(al));
			for (int i = 3; i < l; i++) {
				int b = a.getBonusPoints(i);
				lab = addBorder(UiFactory.createLabel(String.valueOf(b)));
				if (b < 0) {
					lab.setForeground(LESS_COLOR);
				} else if (b == 0) {
					lab.setForeground(Color.GRAY);
				} else if (i <= a.getDoublePointsTreshold()) {
					lab.setForeground(MORE_COLOR);
				} else {
					lab.setForeground(MOST_COLOR);
				}
				if (i == 3) {
					p0.add(lab);										
				} else {
					p1.add(lab);					
				}
			}
		}
		return p;
	}

	private JComponent buildAnimalScoring2() {
		JPanel p = new JPanel(new GridLayout(5, 0));
		// header
		JPanel emptyP = new JPanel();
		emptyP.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, BORDER_COLOR));
		p.add(emptyP);
		int[] points = { -3, 0, 1, 2, 3, 4, 5 };
		for (int i = 0; i < points.length; i++) {
			int b = points[i];
			JLabel lab = addBorder(UiFactory.createLabel(String.valueOf(b)));
			if (b < 0) {
				lab.setForeground(LESS_COLOR);
			} else if (b == 0) {
				lab.setForeground(Color.GRAY);
			} else if (b > 2) {
				lab.setForeground(MOST_COLOR);
			} else {
				lab.setForeground(MORE_COLOR);
			}
			p.add(lab);
		}
		// animals
		int l = 30;
		for (Animal a : Animal.values()) {
			JLabel al = UiFactory.createAnimalLabel(a, 0, UiFactory.NO_NUMBER);
			al.setToolTipText(a.getName(true));
			p.add(addBorder(al));
			int from, to = 0;
			for (int i = 0; i < points.length; i++) {
				from = -1;
				int b = points[i];
				for (int j = to; j < l; j++) {
					int bp = a.getBonusPoints(j);
					if (from == -1 && bp == b) {
						from = j;
					} else if (bp > b) {
						to = j - 1;
						break;
					}
					if (j > a.getDoublePointsTreshold()) {
						to = from = b + a.getDoublePointsTreshold() - 2;
						break;
					}
				}
				String str = "[" + (from == to ? from : from + "-" + to) + "]";
				JLabel lab = addBorder(UiFactory.createLabel(str));
				lab.setFont(COUNT_FONT);
				p.add(lab);
			}
		}
		return p;
	}

	private <T extends JComponent> T addBorder(T component) {
		component.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1),
				BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		return component;
	}

	private <T extends JComponent> T setFont(T component, Font font) {
		component.setFont(font);
		return component;
	}
}
