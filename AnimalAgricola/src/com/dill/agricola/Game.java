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
import com.dill.agricola.model.Building;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.BuildingType;
import com.dill.agricola.model.types.ChangeType;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.support.Msg;
import com.dill.agricola.undo.SimpleEdit;
import com.dill.agricola.undo.TurnUndoManager;
import com.dill.agricola.undo.TurnUndoManager.UndoRedoListener;
import com.dill.agricola.undo.UndoableFarmEdit;
import com.dill.agricola.view.Board;
import com.dill.agricola.view.NewGameDialog;

public class Game {

	private static enum Phase {
		CLEANUP, BEFORE_WORK, WORK, BEFORE_BREEDING, BREEDING/*, AFTER_BREEDING*/, EXTRA_BREEDING;
	}

	public final static int ROUNDS = Main.DEBUG ? 8 : 8;

	private final Player[] players;
	private final Board board;

	private final ActionPerformer ap;
	private final TurnUndoManager undoManager;
	private SubmitListener submitListener;

	private int round = 0;
	private Phase phase;
	private PlayerColor startPlayer;
	private Player currentPlayer;
	private int extraTurnNo;

	private PlayerColor initialStartPlayer;

	private Action breedingAction = new Breeding();
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
				}
				board.updateState(round, phase == Phase.BREEDING ? null : getCurrentPlayer());
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

	public PlayerColor getCurrentPlayer() {
		return currentPlayer.getColor();
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
		phase = Phase.CLEANUP;
		ended = false;
		round = 0;
		undoManager.discardAllEdits();
		ap.beginUpdate(null, ""); // start "initial edit"
		ap.invalidateUpdated(); // which cannot be undone
		GeneralSupply.reset(newDialog.getUseMoreBuildings(), newDialog.getUseEvenMoreBuildings());

		setStartingPlayer(newDialog.getStartingPlayer());
		initialStartPlayer = startPlayer;

		board.start();
		startRound();
//		if (!board.isMaximized()) {
//			board.pack();		
//		}
	}

	private void startRound() {
		ap.postEdit(new StartRound(round, currentPlayer != null ? currentPlayer.getColor() : null, startPlayer));
		round++;
		currentPlayer = getPlayer(startPlayer);
		// refill
		board.startRound(round);

		extraTurnNo = 0;
		if (startExtraTurn(Phase.BEFORE_WORK)) {
			return;
		}
		// start work
		startTurn();
	}

	private void startTurn() {
		ap.postEdit(new ChangePhase(phase, Phase.WORK));
		phase = Phase.WORK;

		ap.postEdit(new StartTurn(ap.getPlayer(), currentPlayer));
		ap.setPlayer(currentPlayer);
		board.startTurn(currentPlayer.getColor());

		ap.endUpdate(); // end last "action/breeding edit"
	}

	private void endTurn() {
		// animals run away
		releaseAnimals(currentPlayer);

		Player otherPlayer = getPlayer(currentPlayer.getColor().other());
		if (otherPlayer.hasWorkers()) {
			switchCurrentPlayer();
			// switch player
			ap.postEdit(new EndTurn());
			// if has workers continue with next turn
			startTurn();
		} else {
			extraTurnNo = 0;
			if (startExtraTurn(Phase.BEFORE_BREEDING)) {
				return;
			}
			// else end work phase II
			breedingPhase();
		}
	}

	private boolean startExtraTurn(Phase extraPhase) {
		// check if any extra turns are provided by buildings
		// TODO optimize - don't cycle through all the buildings every time
		int no = 0;
		for (Player p : players) {
			for (Building b : p.farm.getFarmBuildings()) {
				Action a = extraPhase == Phase.BEFORE_WORK ? b.getBeforeWorkAction(round) : b.getBeforeBreedingAction();
				if (a != null) {
					if (no < extraTurnNo) {
						// this extra action was already performed, skip it
						no++;
					} else {
						extraTurnNo++;
						no++;
						if (a.canDo(p)) {
							ap.postEdit(new ChangePhase(phase, extraPhase));
							phase = extraPhase;

							ap.postEdit(new StartTurn(ap.getPlayer(), p));
							ap.setPlayer(p);

							// TODO make extra actions undoable
							// also causes BUG - cannot undo on farm (since actual multiedit belongs to last player)
//							ap.endUpdate(); // end last "(extra)action edit"
							a.reset();
							if (ap.startAction(a, true)) {
								board.startTurn(p.getColor());
								if (ap.isFinished()) {
									submitListener.actionPerformed(
											new ActionEvent(p.getColor(), 0, ActionCommand.SUBMIT.toString()));
								}
								return true;
							} else {
								// TODO undo?
							}
						}
					}
				}
			}
		}
		return false;
	}

	private void endExtraTurn(Phase phase) {
		if (startExtraTurn(phase)) {
			return;
		}
		if (phase == Phase.BEFORE_WORK) {
			// else start work phase
			startTurn();
		} else if (phase == Phase.BEFORE_BREEDING) {
			// else end work phase
			breedingPhase();
		} else {
			throw new IllegalArgumentException(phase.toString());
		}
	}

	private void breedingPhase() {
		// end work
		ap.postEdit(new ChangePhase(phase, Phase.BREEDING));
		phase = Phase.BREEDING;

		ap.postEdit(new EndWorkPhase(currentPlayer));
		ap.setPlayer(null);

		for (Player p : players) {
			// return workers
			ap.postEdit(new ReturnWorkers(p));
			p.returnAllWorkers();

			startBreeding(p);
			p.notifyObservers(ChangeType.ROUND_END);
		}

		boolean isBreeding = submitListener.getBreedingCount() > 0;
		if (!isBreeding) {
			postBreedingPhase();
		}
	}

	private void startBreeding(Player player) {
		UndoableFarmEdit edit = breedingAction.doo(player);
		if (edit != null) {
			ap.postEdit(edit);

			ap.postEdit(new BreedingStart(player));
			submitListener.adjustBreedingCount(1);
			board.startBreeding(player.getColor());
		}
	}

	private void endBreeding(Player player) {
		ap.endUpdate(); // end last "action edit" or "breeding edit"
		// start "breeding edit" (will contain animal releasing)
		ap.beginUpdate(player.getColor(), Msg.get("actBreedingDone"));

		ap.postEdit(new BreedingEnd(player));
		submitListener.adjustBreedingCount(-1);

		// animals run away after breeding
		releaseAnimals(player);
	}

	private void postBreedingPhase() {
//		ap.postEdit(new ChangePhase(phase, Phase.AFTER_BREEDING));
//		phase = Phase.AFTER_BREEDING;
		if (handleExtraBreedingPhase()) {
			return;
		}
		endRound();
	}

	private void endRound() {
		ap.postEdit(new ChangePhase(phase, Phase.CLEANUP));
		phase = Phase.CLEANUP;

		if (round < ROUNDS) {
			// either start new round
			startRound();
		} else {
			// or end game
			endGame();
		}
	}

	private boolean handleExtraBreedingPhase() {
		if (round < ROUNDS) {
			// only for last round
			return false;
		}
		List<Player> extraBreeders = new ArrayList<Player>();
		for (Player p : players) {
			if (p.farm.hasBuilding(BuildingType.BREEDING_STATION)) {
				extraBreeders.add(p);
			}
		}
		if (extraBreeders.size() > 0) {
			ap.postEdit(new ChangePhase(phase, Phase.EXTRA_BREEDING));
			phase = Phase.EXTRA_BREEDING;
			for (Player p : extraBreeders) {
				startBreeding(p);
				p.notifyObservers(ChangeType.ROUND_END);
			}
			return true;
		}
		return false;
	}

	private void endGame() {
		ap.postEdit(new EndGame());
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

		private int breedingCount = 0;

		public void actionPerformed(ActionEvent e) {
			ActionCommand cmd = ActionCommand.valueOf(e.getActionCommand());
			PlayerColor color = (PlayerColor) e.getSource();
			switch (cmd) {
			case SUBMIT:
				switch (phase) {
				case BEFORE_WORK:
					// extra turn end
					endExtraTurn(phase);
					break;
				case WORK:
					// turn end
					endTurn();
					break;
				case BEFORE_BREEDING:
					// extra turn end
					endExtraTurn(phase);
					break;
				case BREEDING:
					// one or both players must submit
					endBreeding(players[color.ordinal()]);
					if (breedingCount == 0) {
						postBreedingPhase();
					}
					break;
				/*case AFTER_BREEDING:
					endRound();
					break;*/
				case EXTRA_BREEDING:
					endBreeding(players[color.ordinal()]);
					if (breedingCount == 0) {
						endRound();
					}
					break;
				default:
					break;
				}
				break;
			case CANCEL:
				if (phase == Phase.WORK) {
					undoManager.undo();
				}
			default:
				break;
			}
		}

		public void adjustBreedingCount(int d) {
			breedingCount += d;
		}

		public int getBreedingCount() {
			return breedingCount;
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
			currentPlayer = getPlayer(curPlayer);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			round++;
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
				board.startTurn(prevPlayer.getColor());
			}
		}

		public void redo() throws CannotRedoException {
			super.redo();
			ap.setPlayer(curPlayer);
			board.startTurn(curPlayer.getColor());
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

	private class EndWorkPhase extends SimpleEdit {
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

	}

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

	private class BreedingStart extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Player breedingPlayer;

		public BreedingStart(Player breedingPlayer) {
			this.breedingPlayer = breedingPlayer;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			submitListener.adjustBreedingCount(-1);
			board.deactivate(breedingPlayer.getColor());
		}

		public void redo() throws CannotRedoException {
			super.redo();
			submitListener.adjustBreedingCount(1);
			board.startBreeding(breedingPlayer.getColor());
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
			submitListener.adjustBreedingCount(1);
			// make last born animals loose
			breedingPlayer.unpurchaseAnimals(lastBorn);
			breedingPlayer.purchaseAnimals(lastBorn);

			board.startBreeding(breedingPlayer.getColor());
		}

		public void redo() throws CannotRedoException {
			super.redo();
			submitListener.adjustBreedingCount(-1);
			board.deactivate(breedingPlayer.getColor());
		}

	}

	private class ChangePhase extends SimpleEdit {
		private static final long serialVersionUID = 1L;

		private final Phase old;
		private final Phase current;

		public ChangePhase(Phase old, Phase current) {
			super(false);
			this.old = old;
			this.current = current;
		}

		public void undo() throws CannotUndoException {
			super.undo();
			phase = old;
//			updateBoard();
		}

		public void redo() throws CannotRedoException {
			super.redo();
			phase = current;
//			updateBoard();
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
