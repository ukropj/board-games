package com.dill.agricola;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.ActionPerformer;
import com.dill.agricola.actions.farm.BuildSpecial;
import com.dill.agricola.actions.farm.BuildStables;
import com.dill.agricola.actions.farm.BuildStalls;
import com.dill.agricola.actions.farm.Expand;
import com.dill.agricola.actions.farm.Fences;
import com.dill.agricola.actions.farm.Troughs;
import com.dill.agricola.actions.farm.Walls;
import com.dill.agricola.actions.simple.BuildingMaterial;
import com.dill.agricola.actions.simple.CowPigs;
import com.dill.agricola.actions.simple.HorseSheep;
import com.dill.agricola.actions.simple.Millpond;
import com.dill.agricola.actions.simple.OneStone;
import com.dill.agricola.actions.simple.PigSheep;
import com.dill.agricola.actions.simple.StartOneWood;
import com.dill.agricola.actions.simple.ThreeWood;
import com.dill.agricola.actions.simple.TwoStone;
import com.dill.agricola.common.Animals;
import com.dill.agricola.common.Bag;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ChangeType;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.support.Msg;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.TurnUndoManager;
import com.dill.agricola.undo.TurnUndoManager.UndoRedoListener;
import com.dill.agricola.view.Board;
import com.dill.agricola.view.NewGameDialog;

public class Game {

	public final static int ROUNDS = Main.DEBUG ? 5 : 8;

	private final Player[] players;
	private final Board board;

	private final ActionPerformer ap;
	private final TurnUndoManager undoManager;
	private SubmitListener submitListener;

	private int round = 0;
	private PlayerColor startPlayer;
	private Player currentPlayer;
	private boolean workPhase;

	private PlayerColor initialStartPlayer;

	private boolean breeding;
	private boolean ended;
	
	private Animals[] lastNewAnimals;

	public Game() {
		ap = new ActionPerformer();
		undoManager = new TurnUndoManager();
		ap.addUndoableEditListener(undoManager);

		players = new Player[2];
		players[PlayerColor.BLUE.ordinal()] = new Player(PlayerColor.BLUE);
		players[PlayerColor.RED.ordinal()] = new Player(PlayerColor.RED);
		this.board = new Board(this, ap, undoManager);

		undoManager.addUndoRedoListener(new UndoRedoListener() {

			public void undoOrRedoPerformed(boolean isUndo) {
				for (Player player : players) {
					player.notifyObservers(isUndo ? ChangeType.UNDO : ChangeType.REDO);
					board.updateState(round);
				}
			}

		});

		if (Main.DEBUG) {
			board.buildDebugPanel(players);
			board.setSize(1100, 640 + (Main.DEBUG ? 50 : 0));
		} else {
			board.pack();
			board.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
		board.setLocationRelativeTo(null);
		board.setVisible(true);
	}

	public Player getPlayer(PlayerColor color) {
		return color != null ? players[color.ordinal()] : null;
	}

	public Player[] getPlayers() {
		return players;
	}

	public PlayerColor getStartPlayer() {
		return startPlayer;
	}

	public PlayerColor getInitialStartPlayer() {
		return initialStartPlayer;
	}

	public void setStartingPlayer(PlayerColor startPlayerColor) {
		this.startPlayer = startPlayerColor;
		getPlayer(startPlayer).setStartingPlayer(true);
		getPlayer(startPlayer.other()).setStartingPlayer(false);
		board.startingPlayerChanged();
	}

	public ActionListener getSubmitListener() {
		if (submitListener == null) {
			submitListener = new SubmitListener();
		}
		return submitListener;
	}

	private void switchCurrentPlayer() {
		currentPlayer = getPlayer(currentPlayer.getColor().other());
	}

	public boolean isStarted() {
		return round > 0 && !ended;
	}

	public boolean isEnded() {
		return ended;
	}

	public void start() {
		NewGameDialog newDialog = new NewGameDialog(board);
		if (!newDialog.isDone()) {
			return;
		}

		for (Player p : players) {
			p.init();
		}
		breeding = false;
		ended = false;
		round = 0;
		workPhase = false;
		undoManager.discardAllEdits();
		ap.beginUpdate(""); // start "initial edit"
		ap.invalidateUpdated(); // which cannot be undone
		GeneralSupply.reset();

		setStartingPlayer(newDialog.getStartingPlayer());
		initialStartPlayer = startPlayer;

		board.start();
//		board.pack();
		startRound();
	}

	private void startRound() {
		ap.postEdit(new StartRound(round, currentPlayer != null ? currentPlayer.getColor() : null, startPlayer));
		round++;
		currentPlayer = getPlayer(startPlayer);
		// refill
		board.startRound(round);
		// start work
		workPhase = true;
		startTurn();
	}

	private void startTurn() {
		ap.postEdit(new StartTurn(ap.getPlayer(), currentPlayer));
		ap.endUpdate(); // end last "action/breeding edit"

		ap.setPlayer(currentPlayer);
		board.startTurn(currentPlayer);
	}

	private void endTurn() {
		// animals run away
		releaseAnimals();

		Player otherPlayer = getPlayer(currentPlayer.getColor().other());
		if (otherPlayer.hasWorkers()) {
			switchCurrentPlayer();
			// switch player
			ap.postEdit(new EndTurn());
			// if has workers continue with next turn
			startTurn();
		} else {
			// else end work phase II
			endWorkPhase();
		}
	}

	private void endWorkPhase() {
		// end work
		workPhase = false;

		Animals newAnimals[] = new Animals[2];
		for (Player p : players) {
			// return workers
			p.returnAllWorkers();
			// breed animals
			newAnimals[p.getColor().ordinal()] = p.breedAnimals();
			p.notifyObservers(ChangeType.ROUND_END);
		}
		ap.postEdit(new EndRound(currentPlayer, newAnimals));
		ap.setPlayer(null);
		board.endRound();
		lastNewAnimals = newAnimals;
		if (Bag.sumSize(newAnimals) == 0) {
			endRound();
		} else {
			ap.endUpdate(); // end current "action edit"
			// else wait for user confirmation
			submitListener.setBreedingCount(newAnimals);
			breeding = true;
		}
	}

	private void endRound() {
		if (breeding) {
			ap.beginUpdate(Msg.get("actBreedingDone")); // start "breeding edit"
			ap.postEdit(new BreedingDone(lastNewAnimals));
			breeding = false;
			// animals run away after breeding
			releaseAnimals();
		}
		System.out.println("");

		if (round < ROUNDS) {
			// either start new round
			startRound();
		} else {
			// or end game
			endGame();
		}
	}

	private void endGame() {
		ap.postEdit(new EndGame());
		ended = true;
		ap.endUpdate(); // end last "action/breeding edit"
		board.endGame();
	}

	private void releaseAnimals() {
		Animals lostAnimals[] = new Animals[2];
		for (Player p : players) {
			lostAnimals[p.getColor().ordinal()] = p.releaseAnimals();
			p.notifyObservers(ChangeType.TURN_END);
		}
		if (Bag.sumSize(lostAnimals) > 0) {
			ap.postEdit(new ReleaseAnimals(lostAnimals));
		}
	}

	public List<Action> getActions() {
		return new ArrayList<Action>(Arrays.asList(//
				new StartOneWood(this), new ThreeWood(), // 
				new OneStone(), new TwoStone(),
				new BuildingMaterial(),

				new Fences(), new Expand(), //
				new Walls(), new Troughs(), //

				new Millpond(), new PigSheep(), //
				new CowPigs(), new HorseSheep(), //

				new BuildStalls(), new BuildStables(),
				new BuildSpecial(), new BuildSpecial()//
				));
	}

	private class SubmitListener implements ActionListener {

		
		private int breedingCount = 0;
		private int submitCount = 0;

		public void actionPerformed(ActionEvent e) {
			ActionCommand cmd = ActionCommand.valueOf(e.getActionCommand());
			switch (cmd) {
			case SUBMIT:
				if (workPhase) {
					// turn end
					endTurn();
				} else if (breeding) {
					// one or both players must submit
					submitCount++;
					if (submitCount >= breedingCount) {
						submitCount = 0;
						// round end
						endRound();
					}
				}
				break;
			case CANCEL:
				if (workPhase) {
					undoManager.undo();
				}
			default:
				break;
			}
		}

		public void setBreedingCount(Animals[] newAnimals) {
			breedingCount = 0;
			for (Animals animals : newAnimals) {
				if (animals.size() > 0) {
					breedingCount++;
				}
			}
		}

	};

	public static enum ActionCommand {
		NEW, EXIT, UNDO, REDO, SUBMIT, CANCEL, ABOUT, SETTINGS;
	}

	private class StartRound extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final PlayerColor curPlayer;
		private final PlayerColor firstPlayer;

		public StartRound(int round, PlayerColor currentPlayer, PlayerColor startingPlayer) {
			this.curPlayer = currentPlayer;
			this.firstPlayer = startingPlayer;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			round--;
			workPhase = false;
			currentPlayer = getPlayer(curPlayer);
			board.endRound();
		}

		public void redo() throws CannotRedoException {
			super.redo();
			round++;
			workPhase = true;
			currentPlayer = getPlayer(firstPlayer);
		}

	}

	private class StartTurn extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		final Player prevPlayer;
		final Player curPlayer;

		public StartTurn(Player prevPlayer, Player curPlayer) {
			Main.asrtNotNull(curPlayer, "Cannot start turn with no player");
			this.prevPlayer = prevPlayer;
			this.curPlayer = curPlayer;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			ap.setPlayer(prevPlayer);
			if (prevPlayer != null) {
				board.startTurn(prevPlayer);
			}
		}

		public void redo() throws CannotRedoException {
			super.redo();
			ap.setPlayer(curPlayer);
			board.startTurn(curPlayer);
		}

	}

	private class EndTurn extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		public void undo() throws CannotUndoException {
			super.undo();
			switchCurrentPlayer();
		}

		public void redo() throws CannotRedoException {
			super.redo();
			switchCurrentPlayer();
		}
	}

	private class EndRound extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player curPlayer;
		private final Animals[] newAnimals;

		public EndRound(Player curPlayer, Animals[] newAnimals) {
			super(true);
			this.curPlayer = curPlayer;
			this.newAnimals = newAnimals;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			workPhase = true;
			for (Player p : players) {
				p.spendAllWorkers();
				p.unpurchaseAnimals(newAnimals[p.getColor().ordinal()]);
			}
			ap.setPlayer(curPlayer);
			board.startTurn(curPlayer);
			breeding = false;
		}

		public void redo() throws CannotRedoException {
			super.redo();
			workPhase = false;
			for (Player p : players) {
				p.returnAllWorkers();
				p.breedAnimals();
			}
			ap.setPlayer(null);
			board.endRound();
			submitListener.setBreedingCount(newAnimals);
			breeding = true;
		}

		public boolean isAnimalEdit() {
			return Bag.sumSize(newAnimals) > 0;
		}
	}

	private class BreedingDone extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Animals[] newAnimals;
		
		public BreedingDone(Animals[] lastNewAnimals) {
			super(true);
			newAnimals = lastNewAnimals;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			submitListener.setBreedingCount(newAnimals);
			breeding = true;
			for (Player p : players) {
				// move animals from farm to loose
				p.unpurchaseAnimals(newAnimals[p.getColor().ordinal()]);
				p.purchaseAnimals(newAnimals[p.getColor().ordinal()]);
			}
			board.endRound();
		}

		public void redo() throws CannotRedoException {
			super.redo();
			breeding = false;
			
		}
	}

	private class ReleaseAnimals extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Animals[] lostAnimals;

		public ReleaseAnimals(Animals[] lostAnimals) {
			this.lostAnimals = lostAnimals;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			for (Player p : players) {
				p.purchaseAnimals(lostAnimals[p.getColor().ordinal()]);
			}
		}

		public void redo() throws CannotRedoException {
			super.redo();
			for (Player p : players) {
				p.unpurchaseAnimals(lostAnimals[p.getColor().ordinal()]);
			}
		}
	}

	private class EndGame extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		public void undo() throws CannotUndoException {
			super.undo();
			ended = false;
		}

		public void redo() throws CannotRedoException {
			super.redo();
			ended = true;
			board.endGame();
		}

	}
}
