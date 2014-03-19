package com.dill.agricola;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.dill.agricola.actions.Action;
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
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ChangeType;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.support.Msg;
import com.dill.agricola.support.Namer;
import com.dill.agricola.undo.LoggingUndoableEdit;
import com.dill.agricola.undo.TurnUndoManager;
import com.dill.agricola.undo.TurnUndoableEditSupport;
import com.dill.agricola.view.Board;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.UiFactory;

public class Game extends TurnUndoableEditSupport {

	public final static int ROUNDS = Main.DEBUG ? 5 : 8;

	private final Player[] players;
	private final Board board;
	private final TurnUndoManager undoManager;

	private int round = 0;
	private Player startingPlayer;
	private Player currentPlayer;
	private boolean workPhase;

	private PlayerColor initialStartingPlayer;

	private boolean ended;

	public Game() {
		undoManager = new TurnUndoManager();
		addUndoableEditListener(undoManager);
		
		players = new Player[2];
		players[PlayerColor.BLUE.ordinal()] = new Player(PlayerColor.BLUE);
		players[PlayerColor.RED.ordinal()] = new Player(PlayerColor.RED);
		this.board = new Board(this, undoManager);

		if (Main.DEBUG) {
			board.buildDebugPanel(players);
		}

//		board.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
		ended = false;
		round = 0;
		workPhase = false;
		undoManager.discardAllEdits();

		setStartingPlayer(chooseStartingPlayer());
		initialStartingPlayer = startingPlayer.getColor();
		GeneralSupply.reset();
		board.start();

		startRound();
	}

	private void startRound() {
		round++;
		System.out.println("Round: " + round + "(" + startingPlayer.getColor() + " is first)");
		currentPlayer = startingPlayer;
		// refill
		board.startRound(round);
		// start work
		workPhase = true;
		startTurn();
	}

	private void startTurn() {
		beginUpdate(round, currentPlayer.getColor());
		postEdit(new StartTurn());
		System.out.println("Turn: " + currentPlayer.getColor());
		board.startTurn(currentPlayer);
	}

	private void endTurn() {
		// animals run away
		releaseAnimals();
		// switch player
		postEdit(new EndTurn());
		endUpdate();
		
		switchCurrentPlayer();
		if (currentPlayer.hasWorkers()) {
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

		System.out.println("Animals breed now.");
		int newAnimals = 0;
		for (Player p : players) {
			// return workers
			p.returnAllWorkers();
			// breed animals
			newAnimals += p.breedAnimals();
			p.notifyObservers(ChangeType.ROUND_END);
		}
		board.endRound();
		if (newAnimals == 0) {
			endRound();
		} // else wait for user confirmation
	}

	private void endRound() {
		// animals run away after breeding
		releaseAnimals();
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
		ended = true;
		board.showScoring(players, initialStartingPlayer);
	}

	private void releaseAnimals() {
		for (Player p : players) {
			int a = p.releaseAnimals();
			if (a > 0) {
				System.out.println(p.getColor() + ": " + a + " animals run away!");
			}
			p.notifyObservers(ChangeType.TURN_END);
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

	@SuppressWarnings("serial")
	private class StartTurn extends LoggingUndoableEdit {

		public void undo() throws CannotUndoException {
			super.undo();
			board.startTurn(currentPlayer);
		}

		public void redo() throws CannotRedoException {
			super.redo();
			board.startTurn(currentPlayer);
		}

		public String getPresentationName() {
			return Namer.getName(this);
		}

	}

	@SuppressWarnings("serial")
	private class EndTurn extends LoggingUndoableEdit {

		public void undo() throws CannotUndoException {
			super.undo();
			switchCurrentPlayer();
		}

		public void redo() throws CannotRedoException {
			super.redo();
			switchCurrentPlayer();
		}

		public String getPresentationName() {
			return Namer.getName(this);
		}

	}
}
