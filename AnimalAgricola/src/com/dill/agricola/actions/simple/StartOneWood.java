package com.dill.agricola.actions.simple;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import com.dill.agricola.Game;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.support.Namer;
import com.dill.agricola.undo.SimpleEdit;

public class StartOneWood extends RefillAction {

	public final static Materials REFILL = new Materials(Material.WOOD, 1);
	
	private final Game game;
	
	public StartOneWood(Game game) {
		super(ActionType.STARTING_ONE_WOOD, REFILL);
		this.game = game;
	}

	public UndoableEdit doo(Player player, int doneSoFar) {
		Player previousPlayer = game.getStartingPlayer();
		game.setStartingPlayer(player);
		return joinEdits(super.doo(player, doneSoFar), new TakeStartPlayer(previousPlayer, player));
	}
	
	protected class TakeStartPlayer extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player previousPlayer;
		private final Player player;
		
		public TakeStartPlayer(Player previousPlayer, Player player) {
			this.previousPlayer = previousPlayer;
			this.player = player;
		}
		
		public void undo() throws CannotUndoException {
			super.undo();
			game.setStartingPlayer(previousPlayer);
		}
		
		public void redo() throws CannotRedoException {
			super.redo();
			game.setStartingPlayer(player);
		}
		
		public String getPresentationName() {
			return Namer.getName(this);
		}
		
	}

}
