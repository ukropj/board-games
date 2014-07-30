package com.dill.agricola;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.ActionPerformer;
import com.dill.agricola.actions.extra.Breeding;
import com.dill.agricola.actions.farm.BordersExpand;
import com.dill.agricola.actions.farm.BuildSpecial;
import com.dill.agricola.actions.farm.BuildStables;
import com.dill.agricola.actions.farm.BuildStall;
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
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.ChangeType;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.TurnUndoManager;
import com.dill.agricola.undo.TurnUndoManager.UndoRedoListener;
import com.dill.agricola.view.Board;
import com.dill.agricola.view.BuildingOverviewDialog;
import com.dill.agricola.view.NewGameDialog;

public class Game {

	public static enum Phase {
		CLEANUP, BEFORE_WORK, WORK, EXTRA_WORK, BEFORE_BREEDING, BREEDING;
	}

	public final static int ROUNDS = Main.DEBUG ? 3 : 8;

	private final Player[] players;
	private final Board board;

	private final ActionPerformer ap;
	private final TurnUndoManager undoManager;
	private SubmitListener submitListener;

	private int round = 0;
	private Phase phase;
	private PlayerColor startPlayer;

	private PlayerColor initialStartPlayer;

	private Action breedingAction = new Breeding();
	private boolean ended;

	Deque<Player> playerQueue = new ArrayDeque<Player>();

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
				if (ap.hasPlayer()) {
					ap.beginUpdate(ap.getPlayer().getColor(), ap.hasExtraAction() ? ap.getAction().getType() : null);
				}

				for (Player player : players) {
					player.notifyObservers(isUndo ? ChangeType.UNDO : ChangeType.REDO);
				}
				board.refresh();
			}

		});

		board.pack();
		if (Main.DEBUG) {
			board.buildDebugPanel(players);
			board.setExtendedState(JFrame.MAXIMIZED_BOTH);
//			board.setSize(1100, 640 + (Main.DEBUG ? 50 : 0));
		} else {
//			board.setExtendedState(JFrame.MAXIMIZED_BOTH);
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

	public int getRound() {
		return round;
	}

	public Phase getPhase() {
		return phase;
	}

	public PlayerColor getWinner() {
		float blueTotal = players[0].getScore();
		float redTotal = players[1].getScore();
		return blueTotal > redTotal ? PlayerColor.BLUE
				: blueTotal < redTotal ? PlayerColor.RED : getInitialStartPlayer().other();
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

//	private void switchCurrentPlayer() {
//		currentPlayer = getPlayer(currentPlayer.getColor().other());
//	}

	private Player getOtherPlayer(Player p) {
		return getPlayer(p.getColor().other());
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

		GeneralSupply.reset(newDialog.getUseMoreBuildings(), newDialog.getUseEvenMoreBuildings());
		new BuildingOverviewDialog(board, newDialog.getUseMoreBuildings() || newDialog.getUseEvenMoreBuildings());

		for (Player p : players) {
			p.init();
		}
		phase = Phase.CLEANUP;
		ended = false;
		round = 0;
		playerQueue.clear();
		undoManager.discardAllEdits();
		ap.reset();
		ap.beginUpdate(null); // start "initial edit"
		ap.invalidateUpdated(); // which cannot be undone

		setStartingPlayer(newDialog.getStartingPlayer());
		initialStartPlayer = startPlayer;

		board.startGame();
		startRound();
//		if (!board.isMaximized()) {
//			board.pack();		
//		}
	}

	private void startRound() {
		ap.postEdit(new StartRound());
		round++;
		// refill
		board.startRound();

		// start pre-work phase
		interPhase(Phase.BEFORE_WORK);
	}

	private void interPhase(Phase interPhase) {
		ap.postEdit(new ChangePhase(phase, interPhase, true));
		phase = interPhase;
		playerQueue.clear();

		for (Player p : players) {
			boolean hasExtraActions = p.initExtraActions(phase, round);
			if (hasExtraActions) {
				addToQueue(p);
				ap.postEdit(new StartInterPhase(p, phase, round));
			}
		}

		Player currentPlayer = takeFromQueue();
		if (currentPlayer != null) {
			startExtraTurn(currentPlayer);
		} else {
			if (phase == Phase.BEFORE_WORK) {
				// start work phase
				workPhase();
			} else if (phase == Phase.BEFORE_BREEDING) {
				// start breeding phase
				breedingPhase();
			}
		}

	}

	private void startExtraTurn(Player currentPlayer) {
		// check if any extra turns are provided by buildings

		Action extraAction = currentPlayer.getNextExtraAction();
		if (extraAction != null) {
			ap.postEdit(new CheckExtraAction(currentPlayer, extraAction));
			ap.postEdit(extraAction.init());
			if (extraAction.canDo(currentPlayer)) {
				ap.postEdit(new StartTurn(ap.getPlayer(), currentPlayer));
				ap.setPlayer(currentPlayer);
				board.refresh();

				if (ap.startAction(extraAction, true)) {
					// end last "action/breeding edit"
					ap.endUpdate();
					// start "breeding edit"
					ap.beginUpdate(currentPlayer.getColor(), extraAction.getType());

					if (ap.isFinished()) {
						endExtraTurn();
					}
				} else {
					throw new IllegalStateException(extraAction.getType().shortDesc + " error");
				}
			} else {
				// invalid action, try next for same player
				startExtraTurn(currentPlayer);
			}
		} else {
			// no more actions, try other player
			currentPlayer = takeFromQueue();
			if (currentPlayer != null) {
				startExtraTurn(currentPlayer);
			} else {
				if (phase == Phase.BEFORE_WORK) {
					// start work phase
					workPhase();
				} else if (phase == Phase.BEFORE_BREEDING) {
					// start breeding phase
					breedingPhase();
				}
			}
		}
	}

	private void endExtraTurn() {
		ap.postEdit(new Noop());
		Player currentPlayer = ap.getPlayer();
		
		// animals run away
		releaseAnimals(currentPlayer);
		
		// try another extra turn
		startExtraTurn(currentPlayer);
	}

	private void workPhase() {
		ap.postEdit(new ChangePhase(phase, Phase.WORK, false));
		phase = Phase.WORK;
		// this phase does not use player queue

		startTurn(getPlayer(startPlayer));
	}

	private void startTurn(Player currentPlayer) {
		ap.postEdit(new StartTurn(ap.getPlayer(), currentPlayer));
		ap.setPlayer(currentPlayer);
		board.refresh();

		// end last "action edit"
		ap.endUpdate();
		// start "action edit"
		ap.beginUpdate(currentPlayer.getColor());
	}

	private void endTurn() {
		Player currentPlayer = ap.getPlayer();

		// animals run away
		releaseAnimals(currentPlayer);

		// switch player
		currentPlayer = getOtherPlayer(currentPlayer);

		if (currentPlayer.hasWorkers()) {
			// if has workers continue with next turn
			startTurn(currentPlayer);
		} else {
			// start pre-breeding phase
			interPhase(Phase.BEFORE_BREEDING);
		}
	}

	private void breedingPhase() {
		// end work
		ap.postEdit(new ChangePhase(phase, Phase.BREEDING, true));
		phase = Phase.BREEDING;
		playerQueue.clear();

		for (Player p : players) {
			// return workers
			ap.postEdit(new ReturnWorkers(p));
			p.returnAllWorkers();

			p.notifyObservers(ChangeType.ROUND_END);
			if (breedingAction.canDo(p)) {
				addToQueue(p);
				if (hasExtraBreeding(p)) {
					addToQueue(p);
				}
			}
		}
		Player currentPlayer = takeFromQueue();
		if (currentPlayer != null) {
			startBreedingTurn(currentPlayer);
		} else {
			endRound();
		}
	}

	private boolean hasExtraBreeding(Player player) {
		if (round == ROUNDS && player.farm.hasBuilding(BuildingType.BREEDING_STATION)) {
			// only for last round
			return true;
		}
		return false;
	}

	private void startBreedingTurn(Player currentPlayer) {
		ap.postEdit(new StartTurn(ap.getPlayer(), currentPlayer));
		ap.setPlayer(currentPlayer);
		board.refresh();

		ap.postEdit(breedingAction.init());
		if (ap.startAction(breedingAction, true)) {
			// end last "action/breeding edit"
			ap.endUpdate();
			// start "breeding edit"
			ap.beginUpdate(currentPlayer.getColor(), breedingAction.getType());
		} else {
			throw new IllegalStateException("Breeding error");
		}
	}

	private void endBreedingTurn() {
		Player currentPlayer = ap.getPlayer();
		ap.postEdit(new BreedingEnd(currentPlayer));

		// animals run away after breeding
		releaseAnimals(currentPlayer);

		currentPlayer = takeFromQueue();
		if (currentPlayer != null) {
			// switch player
			startBreedingTurn(currentPlayer);
		} else {
			endRound();
		}
	}

	private void endRound() {
		ap.postEdit(new ChangePhase(phase, Phase.CLEANUP, false));
		phase = Phase.CLEANUP;

		if (round < ROUNDS) {
			// either start new round
			startRound();
		} else {
			// or end game
			endGame();
		}
	}

	private void endGame() {
		ap.postEdit(new EndGame(ap.getPlayer()));
		ap.setPlayer(null);
		ended = true;
		ap.endUpdate(); // end last "action/breeding edit"
		board.endGame();
	}

	private void releaseAnimals(Player p) {
		Animals lostAnimals = p.releaseAnimals();
		p.notifyObservers(ChangeType.TURN_END);

		if (lostAnimals.size() > 0) {
			ap.postEdit(new ReleaseAnimals(p, lostAnimals));
		}
	}

	private void addToQueue(Player p) {
		ap.postEdit(new ChangeQueue(p));
		playerQueue.addLast(p);
	}

	private Player takeFromQueue() {
		if (playerQueue.isEmpty()) {
			return null;
		}
		ap.postEdit(new ChangeQueue());
		return playerQueue.removeFirst();
	}

	public List<Action> getActions() {
		return new ArrayList<Action>(Arrays.asList(//
				new StartOneWood(this), new ThreeWood(), // 
				new OneStone(), new TwoStone(),
				new BuildingMaterial(),

				new Fences(), new BordersExpand(), //
				new Walls(), new Troughs(), //

				new Millpond(), new PigSheep(), //
				new CowPigs(), new HorseSheep(), //

				new BuildStall(), new BuildStables(),
				new BuildSpecial(), new BuildSpecial()//
				));
	}

	private class SubmitListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			FarmActionCommand cmd = FarmActionCommand.valueOf(e.getActionCommand());
			switch (cmd) {
			case SUBMIT:
				switch (phase) {
				case BEFORE_WORK:
					// extra turn end
					endExtraTurn();
					break;
				case WORK:
					// work end
					endTurn();
					break;
				case EXTRA_WORK:
					// special action done - continue with work
					ap.postEdit(new ChangePhase(phase, Phase.WORK, false));
					phase = Phase.WORK;
					startTurn(ap.getPlayer());
					break;
				case BEFORE_BREEDING:
					// extra turn end
					endExtraTurn();
					break;
				case BREEDING:
					// breeding end
					endBreedingTurn();
					break;
				default:
					break;
				}
				break;
			case CANCEL:
				if (phase == Phase.WORK) {
					undoManager.undo();
				}
				break;
			case START_EXTRA_WORK:
				switch (phase) {
				case WORK:
					// special action start
					ap.postEdit(new ChangePhase(phase, Phase.EXTRA_WORK, false));
					phase = Phase.EXTRA_WORK;
					board.refresh();
					break;
				case EXTRA_WORK:
					// special action done - continue with work
					ap.postEdit(new ChangePhase(phase, Phase.WORK, false));
					phase = Phase.WORK;
					startTurn(ap.getPlayer());
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
		}
	};

	public static enum FarmActionCommand {
		SUBMIT, CANCEL, START_EXTRA_WORK;
	}

	private class StartRound extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		public StartRound() {
		}

		public void undo() throws CannotUndoException {
			super.undo();
			round--;
		}

		public void redo() throws CannotRedoException {
			super.redo();
			round++;
		}

	}

	private class StartTurn extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player prevPlayer;
		private final Player curPlayer;

		public StartTurn(Player prevPlayer, Player curPlayer) {
			Main.asrtNotNull(curPlayer, "Cannot start turn with no player");
			this.prevPlayer = prevPlayer;
			this.curPlayer = curPlayer;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			ap.setPlayer(prevPlayer);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			ap.setPlayer(curPlayer);
		}

	}

	private class StartInterPhase extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player currentPlayer;
		private final Phase currentPhase;
		private final int round;

		public StartInterPhase(Player currentPlayer, Phase phase, int round) {
			Main.asrtNotNull(currentPlayer, "Cannot start interphase with no player");
			this.currentPlayer = currentPlayer;
			this.currentPhase = phase;
			this.round = round;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			currentPlayer.clearExtraActions();
		}

		public void redo() throws CannotRedoException {
			super.redo();
			currentPlayer.initExtraActions(currentPhase, round);
		}

	}

	private class CheckExtraAction extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player currentPlayer;
		private final Action action;

		public CheckExtraAction(Player currentPlayer, Action action) {
			Main.asrtNotNull(currentPlayer, "Cannot check extra action with no player");
			Main.asrtNotNull(action, "Cannot check extra action with no action");
			this.currentPlayer = currentPlayer;
			this.action = action;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			currentPlayer.returnExtraAction(action);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			currentPlayer.getNextExtraAction();
		}

	}

	/*private class EndWorkPhase extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player curPlayer;

		public EndWorkPhase(Player curPlayer) {
			super(true);
			this.curPlayer = curPlayer;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			ap.setPlayer(curPlayer);
			board.startTurn(curPlayer.getColor());
		}

		public void redo() throws CannotRedoException {
			super.redo();
			ap.setPlayer(null);
		}

	}*/

	private class ReturnWorkers extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player curPlayer;

		public ReturnWorkers(Player curPlayer) {
			super(false);
			this.curPlayer = curPlayer;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			curPlayer.spendAllWorkers();
		}

		public void redo() throws CannotRedoException {
			super.redo();
			curPlayer.returnAllWorkers();
		}

	}

	private class BreedingEnd extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player breedingPlayer;
		private final Animals lastBorn;

		public BreedingEnd(Player breedingPlayer) {
			super(true);
			this.breedingPlayer = breedingPlayer;
			this.lastBorn = new Animals(breedingPlayer.getLastBornAnimals());
		}

		public void undo() throws CannotUndoException {
			super.undo();
			breedingPlayer.unpurchaseAnimals(lastBorn);
			breedingPlayer.purchaseAnimals(lastBorn);
		}

		public void redo() throws CannotRedoException {
			super.redo();
		}

	}

	private class ChangePhase extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Phase old;
		private final Phase current;
		private final List<Player> q;

		public ChangePhase(Phase old, Phase current, boolean affectsQueue) {
			this.old = old;
			this.current = current;
			if (affectsQueue) {
				q = new ArrayList<Player>(playerQueue);
			} else {
				q = null;
			}
		}

		public void undo() throws CannotUndoException {
			super.undo();
			phase = old;
			if (q != null) {
				playerQueue.clear();
				playerQueue.addAll(q);
			}
		}

		public void redo() throws CannotRedoException {
			super.redo();
			phase = current;
			if (q != null) {
				playerQueue.clear();
			}
		}

	}

	private class ChangeQueue extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player addedPlayer;
		private final Player removedPlayer;

		public ChangeQueue() {
			this(null);
		}

		public ChangeQueue(Player player) {
			this.addedPlayer = player;
			removedPlayer = addedPlayer == null ? playerQueue.getFirst() : null;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			if (addedPlayer != null) {
				playerQueue.removeLast();
			} else {
				playerQueue.addFirst(removedPlayer);
			}
		}

		public void redo() throws CannotRedoException {
			super.redo();
			if (addedPlayer != null) {
				playerQueue.addLast(addedPlayer);
			} else {
				playerQueue.removeFirst();
			}
		}

	}

	private class ReleaseAnimals extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player player;
		private final Animals lostAnimals;

		public ReleaseAnimals(Player player, Animals lostAnimals) {
			this.player = player;
			this.lostAnimals = lostAnimals;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			player.purchaseAnimals(lostAnimals);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			player.unpurchaseAnimals(lostAnimals);
		}
	}

	private class EndGame extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player last;

		public EndGame(Player player) {
			last = player;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			ap.setPlayer(last);
			ended = false;
		}

		public void redo() throws CannotRedoException {
			super.redo();
			ap.setPlayer(null);
			ended = true;
			board.endGame();
		}

	}

	private class Noop extends SimpleEdit {
		private static final long serialVersionUID = 1L;
	}

}
