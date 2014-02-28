package com.dill.agricola;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.dill.agricola.actions.Action;
import com.dill.agricola.actions.BuildSpecial;
import com.dill.agricola.actions.BuildStables;
import com.dill.agricola.actions.BuildStalls;
import com.dill.agricola.actions.BuildTrough;
import com.dill.agricola.actions.BuildingMaterial;
import com.dill.agricola.actions.CowPigs;
import com.dill.agricola.actions.Expand;
import com.dill.agricola.actions.Fences;
import com.dill.agricola.actions.HorseSheep;
import com.dill.agricola.actions.Millpond;
import com.dill.agricola.actions.OneStone;
import com.dill.agricola.actions.PigSheep;
import com.dill.agricola.actions.StartOneWood;
import com.dill.agricola.actions.ThreeWood;
import com.dill.agricola.actions.TwoStone;
import com.dill.agricola.actions.Walls;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.ChangeType;
import com.dill.agricola.model.types.PlayerColor;
import com.dill.agricola.view.Board;
import com.dill.agricola.view.utils.AgriImages;
import com.dill.agricola.view.utils.AgriImages.ImgSize;
import com.dill.agricola.view.utils.SwingUtils;

public class Game {

	public final static int ROUNDS = Main.DEBUG ? 5 : 8;

	private final Player[] players;
	private final Board board;

	private int round = 0;
	private int turn = 0;
	private Player startingPlayer;
	private Player currentPlayer;
	private boolean workPhase;

	private PlayerColor initialStartingPlayer;

	public Game(Board board) {
		this.board = board;
		players = new Player[2];
		players[PlayerColor.BLUE.ordinal()] = new Player(PlayerColor.BLUE);
		players[PlayerColor.RED.ordinal()] = new Player(PlayerColor.RED);

		board.init(this);
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

	private Player chooseStaringPlayer() {
		if (Main.DEBUG) {
			return players[0];			
		} else {			
			List<JComponent> opts = new ArrayList<JComponent>();
			for (PlayerColor color : PlayerColor.values()) {
				Icon icon = AgriImages.getFirstTokenIcon(color.ordinal(), ImgSize.BIG);
				JLabel l = new JLabel(icon);
				l.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				l.setOpaque(true);
				l.setBackground(color.getRealColor());
				opts.add(l);	
			}
			int result = SwingUtils.showOptionDialog("Choose starting player", "Game starts", null, opts);
			return players[Math.max(0, result)];
		}
	}

	public void start() {
		for (Player p : players) {
			p.init();
		}
		turn = 0;
		round = 0;
		workPhase = false;

		setStartingPlayer(chooseStaringPlayer());
		initialStartingPlayer = startingPlayer.getColor();
		GeneralSupply.reset();
		board.start();

		startRound();
	}

	private void startRound() {
		round++;
		System.out.println("Round: " + round + "(" + startingPlayer.getColor() + " is first)");
		turn = 0;
		currentPlayer = startingPlayer;
		// refill
		board.startRound(round);
		// start work
		workPhase = true;
		startTurn();
	}

	private void startTurn() {
		turn++;
		System.out.println("Turn: " + turn + " (" + currentPlayer.getColor() + ")");
		board.startTurn(currentPlayer);
	}

	private void endTurn() {
		// animals run away
		releaseAnimals();
		// switch player
		currentPlayer = getPlayer(currentPlayer.getColor().other());
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
		float[] scores = new float[]{players[0].getScore(), players[1].getScore()};
		int winnerNo = scores[0] > scores[1] ? 0 : scores[0] < scores[1] ? 1 : initialStartingPlayer.other().ordinal();
		board.showScoring(players, players[winnerNo].getColor());
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
				
				new Fences(),new Expand(), //
				new Walls(), new BuildTrough(), //
				
				new Millpond(), new PigSheep(), //
				new CowPigs(), new HorseSheep(), //
				
				new BuildStalls(), new BuildStables(),
				new BuildSpecial(),new BuildSpecial()//
				));
	}

}
