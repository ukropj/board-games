package com.dill.agricola.view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.buildings.HalfTimberedHouse;
import com.dill.agricola.model.buildings.OpenStables;
import com.dill.agricola.model.buildings.Shelter;
import com.dill.agricola.model.buildings.Stables;
import com.dill.agricola.model.buildings.Stall;
import com.dill.agricola.model.buildings.StorageBuilding;
import com.dill.agricola.model.types.Animal;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.model.types.Purchasable;

@SuppressWarnings("serial")
public class DebugPanel extends JPanel {

	private Player[] players;
	private Player currentPlayer;
	
	public DebugPanel(Player[] players) {
		this.players = players;
		setLayout(new GridLayout(2, 0));
		initDebugActions();
	}
	
	public void setCurrentPlayer(PlayerColor currentPlayer) {
		this.currentPlayer = players[currentPlayer.ordinal()];
	}
	
	private void initDebugActions() {
		createDebugButton("+Wood", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.addMaterial(Material.WOOD, 1);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton("+Stone", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.addMaterial(Material.STONE, 1);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton("+Reed", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.addMaterial(Material.REED, 1);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton("+Border", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.addMaterial(Material.BORDER, 1);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton("+Sheep", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.purchaseAnimal(Animal.SHEEP, 1);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton("+Pig", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.purchaseAnimal(Animal.PIG, 1);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton("+Cow", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.purchaseAnimal(Animal.COW, 1);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton("+Horse", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.purchaseAnimal(Animal.HORSE, 1);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton("Ext", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.purchase(Purchasable.EXTENSION);
				currentPlayer.setActiveType(Purchasable.EXTENSION);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton("Fence", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.purchase(Purchasable.FENCE, new Materials(Material.BORDER));
				currentPlayer.setActiveType(Purchasable.FENCE);
				currentPlayer.notifyObservers();
			}
		});
		createDebugButton("Trough", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.purchase(Purchasable.TROUGH);
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
		});
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
		createDebugButton("*Score*", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ScoreDialog sd = new ScoreDialog(players, null);
				sd.setVisible(true);
			}
		});
	}

	private JButton createDebugButton(String label, ActionListener al) {
		JButton b = new JButton(label);
		b.addActionListener(al);
		this.add(b);
		return b;
	}
	
}
