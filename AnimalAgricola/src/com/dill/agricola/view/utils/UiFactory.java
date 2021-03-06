package com.dill.agricola.view.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Dir;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.Purchasable;
import com.dill.agricola.support.Fonts;
import com.dill.agricola.support.Msg;
import com.dill.agricola.view.utils.AgriImages.ImgSize;

public class UiFactory {

	public static final int ICON_LAST = 1;
	public static final int ICON_FIRST = 2;
	public static final int ICON_QUANTITY = 3;
	public static final int NO_NUMBER = 4;

	public static final int X_AXIS = 5;
	public static final int Y_AXIS = 6;
	public static final int NO_OPTION = -1;

	private UiFactory() {
	}

	public static Color makeTranslucent(Color c, int alpha) {
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
	}

	public static JPanel createFlowPanel() {
		return createFlowPanel(0, 0);
	}

	public static JPanel createFlowPanel(int hgap, int vgap) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, hgap, vgap));
		p.setOpaque(false);
		return p;
	}

	public static JPanel createBorderPanel() {
		return createBorderPanel(0, 0);
	}

	public static JPanel createBorderPanel(int hgap, int vgap) {
		JPanel p = new JPanel(new BorderLayout(hgap, vgap));
		p.setOpaque(false);
		return p;
	}

	public static JPanel createVerticalPanel() {
		JPanel p = new JPanel(/* BoxLayout */);
		p.setOpaque(false);
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		return p;
	}

	public static JPanel createHorizontalPanel() {
		JPanel p = new JPanel(/* BoxLayout */);
		p.setOpaque(false);
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		return p;
	}

	public static JLabel createAnimalLabel(Animal type, int count, int labelStyle) {
		Icon icon = labelStyle == ICON_QUANTITY
				? AgriImages.getAnimalMultiIcon(type, count, ImgSize.MEDIUM)
				: AgriImages.getAnimalIcon(type, ImgSize.MEDIUM);
		return createGeneralLabel(count, icon, labelStyle);
	}

	public static JLabel createMaterialLabel(Material type, int count, int labelStyle) {
		Icon icon = labelStyle == ICON_QUANTITY
				? AgriImages.getMaterialMultiIcon(type, count)
				: AgriImages.getMaterialIcon(type);
		return createGeneralLabel(count, icon, labelStyle);
	}

	public static JLabel createPurchasableLabel(Purchasable type, int count, int labelStyle) {
		Icon icon = AgriImages.getPurchasableIcon(type);
		return createGeneralLabel(count, icon, labelStyle);
	}

	public static JLabel createGeneralLabel(int count, Icon icon, int labelStyle) {
		String text = null;
		switch (labelStyle) {
		case NO_NUMBER:
			text = null;
			break;
		case ICON_QUANTITY:
			text = "+" + String.valueOf(count);
			break;
		default:
			text = String.valueOf(count);
			break;
		}
		JLabel l = createLabel(text, icon);
		if (labelStyle == ICON_FIRST) {
			l.setIconTextGap(4);
		}
		l.setHorizontalTextPosition(labelStyle != ICON_FIRST ? JLabel.LEFT : JLabel.RIGHT);
		return l;
	}

	public static JLabel createArrowLabel(Dir d, boolean red) {
		JLabel l = createLabel(AgriImages.getArrowIcon(d, red));
		l.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		return l;
	}

	public static JPanel createResourcesPanel(Materials materials, Animals animals, int orientation) {
		JPanel p = orientation == Y_AXIS ? UiFactory.createVerticalPanel() : UiFactory.createHorizontalPanel();
		p.setOpaque(false);
		updateResourcePanel(p, materials, animals, false, 0);
		return p;
	}

	public static void updateResourcePanel(JPanel panel, Materials materials, Animals animals, boolean usePlus, int gap) {
		panel.removeAll();
		int added = 0;
		if (materials != null) {
			for (Material m : Material.values()) {
				int count = materials.get(m);
				if (count > 0) {
					if (added > 0 && gap > 0) {
						panel.add(Box.createRigidArea(new Dimension(gap, gap)));
					}
					panel.add(createMaterialLabel(m, count, usePlus ? ICON_QUANTITY : ICON_LAST));
					added++;
				}
			}
		}
		if (animals != null) {
			for (Animal a : Animal.reversedValues()) {
				int count = animals.get(a);
				if (count > 0) {
					if (added > 0 && gap > 0) {
						panel.add(Box.createRigidArea(new Dimension(gap, gap)));
					}
					panel.add(createAnimalLabel(a, count, usePlus ? ICON_QUANTITY : ICON_LAST));
					added++;
				}
			}
		}
		if (added == 0) {
			panel.add(createLabel(Msg.get("empty")));
		}
		panel.revalidate();
	}

	public static JLabel createLabel(String text) {
		return createLabel(text, null);
	}

	public static JLabel createLabel(ImageIcon icon) {
		return createLabel(null, icon);
	}

	public static JLabel createLabel(String string, Icon icon, int width) {
		JLabel l = createLabel(string, icon);
		l.setPreferredSize(new Dimension(width, l.getPreferredSize().height));
		return l;
	}

	public static JLabel createLabel(String string, Icon icon) {
		JLabel l = new JLabel(string, icon, JLabel.CENTER);
		l.setHorizontalTextPosition(JLabel.LEFT);
		l.setIconTextGap(3);
		l.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		l.setAlignmentY(JLabel.CENTER_ALIGNMENT);
		if (string != null) {
			if (string.matches("[^a-z]+")) {
				l.setFont(Fonts.ACTION_NUMBER);
			} else {
				l.setFont(Fonts.ACTION_TEXT);
			}
		}
		l.setForeground(new Color(35, 35, 35));
		return l;
	}

	public static boolean showQuestionDialog(Component parent, String message, String title, Icon icon) {
		List<JComponent> opts = Arrays.asList(new JComponent[] {
				UiFactory.createLabel(Msg.get("yesBtn"), null, 50),
				UiFactory.createLabel(Msg.get("noBtn"), null, 50)
		});
		return 0 == showOptionDialog(parent, message, title, icon, opts, 0);
	}

	public static int showOptionDialog(Component parent, String message, String title, Icon icon, List<JComponent> opts, int cols) {
		final JOptionPane pane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, icon);
		JComponent[] buttons = new JComponent[opts.size()];
		int i = 0;
		for (JComponent opt : opts) {
			if (opt == null) {
				buttons[i] = new JLabel();
			} else {
				final JButton button = new JButton();
				button.setMargin(new Insets(2, 2, 2, 2));
				button.add(opt);
				button.setEnabled(opt.isEnabled());
				opt.setEnabled(true);
				final int value = i;
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						pane.setValue(value);
					}
				});
				buttons[i] = button;
			}
			i++;
		}
		pane.setOptions(buttons);
		if (cols > 0) {
			((JPanel) pane.getComponent(1)).setLayout(new GridLayout(0, cols, 5, 5));
		}
		JDialog dialog = pane.createDialog(parent, title);

		dialog.setVisible(true);
		Object retVal = pane.getValue();
		return retVal == null ? UiFactory.NO_OPTION : (Integer) retVal;
	}

	public static JButton createToolbarButton(String label, String iconName, String toolTip, ActionListener listener) {
		final JButton b = new JButton(label);
		b.setToolTipText(toolTip);
		b.setFont(Fonts.TOOLBAR_BTN);
		b.setContentAreaFilled(false);
		b.setBorderPainted(false);
		b.setFocusPainted(false); // not good for a11y, but rest of game is not navigable by keyboard anyway
		if (iconName != null) {
			b.setIcon(Images.createIcon(iconName, ImgSize.SMALL));
		}
		b.addActionListener(listener);

		b.addMouseListener(new MouseAdapter() {

			public void mouseExited(MouseEvent e) {
				b.setContentAreaFilled(false);
				b.setBorderPainted(false);
			}

			public void mouseEntered(MouseEvent e) {
				if (b.isEnabled()) {
					b.setContentAreaFilled(true);
					b.setBorderPainted(true);
				}
			}

		});
		return b;
	}

	public static JComponent createToolbarSeparator() {
		JLabel l = new JLabel(Images.createIcon("separator", ImgSize.SMALL));
		l.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 2));
		return l;
//		return new JToolBar.Separator(null);
	}

	public static JButton createTextButton(String text, ActionListener actionListener) {
		JButton b = new JButton(text);
		b.setMargin(new Insets(2, 8, 2, 8));
		b.setAlignmentX(JButton.CENTER_ALIGNMENT);
		b.addActionListener(actionListener);
		return b;
	}

	public static JButton createIconButton(String iconName, String toolTip, ActionListener actionListener) {
		JButton b = new JButton(Images.createIcon(iconName, ImgSize.SMALL));
		b.setMargin(new Insets(2, 8, 2, 8));
		b.setAlignmentX(JButton.CENTER_ALIGNMENT);
		b.setToolTipText(toolTip);
		
		b.addActionListener(actionListener);
		return b;
	}

	public static void mark(JComponent c) {
		// used for UI debugging
		c.setOpaque(true);
		c.setBackground(Color.RED);
	}
}
