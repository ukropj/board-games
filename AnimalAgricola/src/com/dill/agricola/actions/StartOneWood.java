package com.dill.agricola.actions;

import com.dill.agricola.Game;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;

public class StartOneWood extends RefillAction {

	public final static Materials REFILL = new Materials(Material.WOOD, 1);
	
	private final Game game;
	private Player previousStartingPlayer = null;
	
	public StartOneWood(Game game) {
		super(ActionType.STARTING_ONE_WOOD, REFILL);
		this.game = game;
	}
	
	public void init() {
		super.init();
		previousStartingPlayer = null;
	}

	public boolean doOnce(Player player) {
		previousStartingPlayer = game.getStartingPlayer();
		game.setStartingPlayer(player);
		return super.doOnce(player);
	}
	
	public boolean undoOnce(Player player) {
		game.setStartingPlayer(previousStartingPlayer);
		previousStartingPlayer = null;
		return super.undoOnce(player);
	}

}
