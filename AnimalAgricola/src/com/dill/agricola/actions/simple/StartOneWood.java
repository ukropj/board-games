package com.dill.agricola.actions.simple;

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

	public boolean doo(Player player, int doneSoFar) {
		previousStartingPlayer = game.getStartingPlayer();
		game.setStartingPlayer(player);
		return super.doo(player, doneSoFar);
	}
	
	public boolean undo(Player player, int doneSoFar) {
		game.setStartingPlayer(previousStartingPlayer);
		previousStartingPlayer = null;
		return super.undo(player, doneSoFar);
	}

}
