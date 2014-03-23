package com.dill.agricola.view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.view.utils.UiFactory;

public class DebugPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private Player[] players;
	private Player currentPlayer;

	public DebugPanel(Player[] players) {
		this.players = players;
		setLayout(new GridLayout(1, 0));
		initDebugActions();
	}

	public void setCurrentPlayer(PlayerColor currentPlayer) {
		this.currentPlayer = players[currentPlayer.ordinal()];
	}

	private void initDebugActions() {
		createDebugButton(UiFactory.createMaterialLabel(Material.WOOD, 1, UiFactory.ICON_LAST), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.addMaterial(Material.WOOD, 1);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton(UiFactory.createMaterialLabel(Material.STONE, 1, UiFactory.ICON_LAST), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.addMaterial(Material.STONE, 1);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton(UiFactory.createMaterialLabel(Material.REED, 1, UiFactory.ICON_LAST), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.addMaterial(Material.REED, 1);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton(UiFactory.createMaterialLabel(Material.BORDER, 1, UiFactory.ICON_LAST), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.addMaterial(Material.BORDER, 1);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton(UiFactory.createAnimalLabel(Animal.SHEEP, 1, UiFactory.ICON_LAST), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.purchaseAnimal(Animal.SHEEP, 1);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton(UiFactory.createAnimalLabel(Animal.PIG, 1, UiFactory.ICON_LAST), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.purchaseAnimal(Animal.PIG, 1);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton(UiFactory.createAnimalLabel(Animal.COW, 1, UiFactory.ICON_LAST), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.purchaseAnimal(Animal.COW, 1);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton(UiFactory.createAnimalLabel(Animal.HORSE, 1, UiFactory.ICON_LAST), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.purchaseAnimal(Animal.HORSE, 1);
				currentPlayer.notifyObservers();
			}
		});
		/*createDebugButton("Ext", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.purchaseExtension(Dir.W);
				currentPlayer.setActiveType(Purchasable.EXTENSION);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton("Fence", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.purchaseFence(null, pos, d)(Purchasable.FENCE, new Materials(Material.BORDER));
				currentPlayer.setActiveType(Purchasable.FENCE);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton("Trough", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.purchaseTrough(cost, pos);
				currentPlayer.setActiveType(Purchasable.TROUGH);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton("Stall", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.purchaseFreeBuilding(new Stall(0));
				currentPlayer.setActiveType(Purchasable.BUILDING);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton("Stables", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.purchaseFreeBuilding(new Stables());
				currentPlayer.setActiveType(Purchasable.BUILDING);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton("House", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.purchaseFreeBuilding(new HalfTimberedHouse());
				currentPlayer.setActiveType(Purchasable.BUILDING);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton("Storage", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.purchaseFreeBuilding(new StorageBuilding());
				currentPlayer.setActiveType(Purchasable.BUILDING);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton("Shelter", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.purchaseFreeBuilding(new Shelter());
				currentPlayer.setActiveType(Purchasable.BUILDING);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton("OpenSb", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.purchaseFreeBuilding(new OpenStables());
				currentPlayer.setActiveType(Purchasable.BUILDING);
				currentPlayer.notifyObservers();
			}
		});*/
		createDebugButton("Do Nothing", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.spendWorker();
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton("Done", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.setActiveType(null);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton("*Score*", new ScoreListener(this));
	}

	private JButton createDebugButton(JLabel label, ActionListener al) {
		JButton b = new JButton();
		b.add(label);
		b.addActionListener(al);
		this.add(b);
		return b;
	}

	private JButton createDebugButton(String label, ActionListener al) {
		JButton b = new JButton(label);
		b.addActionListener(al);
		this.add(b);
		return b;
	}

	private class ScoreListener implements ActionListener {

		private final JComponent c;

		public ScoreListener(JComponent c) {
			this.c = c;
		}

		public void actionPerformed(ActionEvent e) {
			ScoreDialog sd = new ScoreDialog(players, players[0].getColor() /*not true*/);
			sd.setLocationRelativeTo(SwingUtilities.windowForComponent(c));
			sd.setVisible(true);
		}
	}

}
