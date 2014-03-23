package com.dill.agricola;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.UiFactory;

public class Game {

	public final static int ROUNDS = Main.DEBUG ? 5 : 8;

	private final Player[] players;
	private final Board board;

	private final ActionPerformer ap;
	private final TurnUndoManager undoManager;

	private int round = 0;
	private Player startingPlayer;
	private Player currentPlayer;
	private boolean workPhase;

	private PlayerColor initialStartingPlayer;

	private boolean breeding;
	private boolean ended;

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
		} else {
			board.setExtendedState(JFrame.MAXIMIZED_BOTH);			
		}
		board.pack();
		board.setLocationRelativeTo(null);
		board.setVisible(true);
	}

	public Player getPlayer(PlayerColor color) {
		return players[color.ordinal()];
	}

	public Player getStartingPlayer() {
		return startingPlayer;
	}

	public void setStartingPlayer(Player startingPlayer) {
		this.startingPlayer = startingPlayer;
		startingPlayer.setStartingPlayer(true);
		getPlayer(startingPlayer.getColor().other()).setStartingPlayer(false);
		board.startingPlayerChanged();
	}

	public ActionListener getSubmitListener() {
		return new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (workPhase) {
					// turn end
					endTurn();
				} else {
					// round end
					endRound();
				}
			}

		};
	}

	private Player chooseStartingPlayer() {
		if (Main.DEBUG) {
			return players[0];
		} else {
			List<JComponent> opts = new ArrayList<JComponent>();
			for (PlayerColor color : PlayerColor.values()) {
				JLabel l = UiFactory.createLabel(AgriImages.getFirstTokenIcon(color.ordinal(), ImgSize.BIG));
				l.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				l.setOpaque(true);
				l.setBackground(color.getRealColor());
				opts.add(l);
			}
			int result = UiFactory.showOptionDialog(Msg.get("chooseStarting"), Msg.get("gameStart"), null, opts);
			return players[Math.max(0, result)];
		}
	}

	private void switchCurrentPlayer() {
		currentPlayer = getPlayer(currentPlayer.getColor().other());
	}

	public boolean isStarted() {
		return round > 0 && !ended;
	}

	public void start() {
		for (Player p : players) {
			p.init();
		}
		breeding = false;
		ended = false;
		round = 0;
		workPhase = false;
		undoManager.discardAllEdits();
		ap.beginUpdate(); // start "initial edit"
		ap.invalidateUpdated(); // which cannot be undone

		setStartingPlayer(chooseStartingPlayer());
		initialStartingPlayer = startingPlayer.getColor();
		GeneralSupply.reset();
		board.start();

		startRound();
	}

	private void startRound() {
		ap.postEdit(new StartRound(round, currentPlayer, startingPlayer));
		round++;
		currentPlayer = startingPlayer;
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
		if (Bag.sumSize(newAnimals) == 0) {
			endRound();
		} else {
			// else wait for user confirmation
			ap.endUpdate(); // end current "action edit"
			breeding = true;
		}
	}

	private void endRound() {
		if (breeding) {
			ap.beginUpdate(); // start "breeding edit"
			ap.postEdit(new BreedingDone());
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
		ap.endUpdate(); // end last "action/breeding edit"
		ap.postEdit(new EndGame());
		ended = true;
		board.showScoring(players, initialStartingPlayer);
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

	private class StartRound extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player curPlayer;
		private final Player firstPlayer;

		public StartRound(int round, Player currentPlayer, Player startingPlayer) {
			this.curPlayer = currentPlayer;
			this.firstPlayer = startingPlayer;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			round--;
			workPhase = false;
			currentPlayer = curPlayer;
		}

		public void redo() throws CannotRedoException {
			super.redo();
			round++;
			workPhase = true;
			currentPlayer = firstPlayer;
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
			} else {
				board.endRound();
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
		}
	}
	
	private class BreedingDone extends SimpleEdit {
		private static final long serialVersionUID = 1L;
		
		public BreedingDone() {
			super(true);
		}
		
		public void undo() throws CannotUndoException {
			super.undo();
			breeding = true;
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

		public EndGame() {
			super(true);
		}
		
		public void undo() throws CannotUndoException {
			super.undo();
			ended = false;
		}

		public void redo() throws CannotRedoException {
			super.redo();
			ended = true;
		}

	}
}
