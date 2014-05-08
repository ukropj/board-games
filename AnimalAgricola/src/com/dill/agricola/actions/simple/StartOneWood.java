package com.dill.agricola.actions.simple;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.Game;
import com.dill.agricola.common.Materials;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ActionType;
import com.dill.agricola.model.types.Material;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.support.Namer;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.UndoableFarmEdit;

public class StartOneWood extends MaterialRefillAction {

	public final static Materials REFILL = new Materials(Material.WOOD, 1);
	
	private final Game game;
	
	public StartOneWood(Game game) {
		super(ActionType.STARTING_ONE_WOOD, REFILL);
		this.game = game;
	}

	public UndoableFarmEdit doo(Player player) {
		PlayerColor previousPlayer = game.getStartPlayer();
		game.setStartingPlayer(player.getColor());
		return joinEdits(super.doo(player), new TakeStartPlayer(previousPlayer, player.getColor()));
	}
	
	protected class TakeStartPlayer extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final PlayerColor previousPlayer;
		private final PlayerColor player;
		
		public TakeStartPlayer(PlayerColor previousPlayer, PlayerColor playerColor) {
			super(true);
			this.previousPlayer = previousPlayer;
			this.player = playerColor;
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
