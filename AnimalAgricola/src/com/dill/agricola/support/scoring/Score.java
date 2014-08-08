package com.dill.agricola.support.scoring;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dill.agricola.Game;
import com.dill.agricola.model.Player;
import com.dill.agricola.model.types.PlayerColor;

public class Score implements Comparable<Score> {

	private static final DateFormat df = DateFormat.getDateInstance();

	private final long startTime;
	private final PlayerColor startingPlayer;
	private final List<PlayerScore> playerScores;

	public Score(Game game, Map<PlayerColor, String> nameMap) {
		this.startTime = game.getStartTime();
		this.startingPlayer = game.getInitialStartPlayer();
		List<PlayerScore> playerScores = new ArrayList<PlayerScore>();
		for (Player player : game.getPlayers()) {
			PlayerColor color = player.getColor();
			playerScores.add(new PlayerScore(color, nameMap.get(color), player.getScore()));
		}
		this.playerScores = Collections.unmodifiableList(playerScores);
	}

	private Score(long startTime, PlayerColor startingPlayer, List<PlayerScore> playerScores) {
		this.startTime = startTime;
		this.startingPlayer = startingPlayer;
		this.playerScores = Collections.unmodifiableList(playerScores);
	}

	public long getStartTime() {
		return startTime;
	}

	public Date getDate() {
		return new Date(startTime);
	}
	
	public PlayerColor getStartingPlayer() {
		return startingPlayer;
	}

	public List<PlayerScore> getPlayerScores() {
		return playerScores;
	}
	
	public PlayerColor getWinner() {
		float blueTotal = playerScores.get(0).getScore();
		float redTotal = playerScores.get(1).getScore();
		return blueTotal > redTotal ? PlayerColor.BLUE
				: blueTotal < redTotal ? PlayerColor.RED : startingPlayer.other();
	}

	public String toString() {
		return df.format(getDate()) + " s:" + startingPlayer+  " " + playerScores;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (startTime ^ (startTime >>> 32));
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Score other = (Score) obj;
		if (startTime != other.startTime)
			return false;
		return true;
	}

	public static String serialize(Score score) {
		StringBuilder sb = new StringBuilder();
		sb.append(score.getStartTime()).append(";");
		sb.append(score.getStartingPlayer()).append(";");
		for (PlayerScore ps : score.getPlayerScores()) {
			sb.append(ps.getColor()).append(";")
					.append(sanitize(ps.getName())).append(";")
					.append(ps.getScore()).append(";");
		}
		return sb.toString();
	}

	private static Object sanitize(String name) {
		return name != null ? name.replaceAll("[\r\n;]", "") : name;
	}

	public static Score parse(String str) throws ScoreParsingException {
		try {
			String[] parts = str.split(";");
			long time = Long.valueOf(parts[0]);
			PlayerColor starting = PlayerColor.valueOf(parts[1]);
			List<PlayerScore> playerScores = new ArrayList<PlayerScore>();
			for (int i = 2; i + 2 < parts.length; i += 3) {
				PlayerColor color = PlayerColor.valueOf(parts[i]);
				String name = parts[i + 1];
				float score = Float.valueOf(parts[i + 2]);
				playerScores.add(new PlayerScore(color, name, score));
			}
			return new Score(time, starting, playerScores);
		} catch (Exception e) {
			throw new ScoreParsingException(e);
		}
	}

	public static class PlayerScore {

		private final PlayerColor color;
		private final String name;
		private final float score;

		public PlayerScore(PlayerColor color, String name, float score) {
			this.color = color;
			this.name = name;
			this.score = score;
		}

		public PlayerColor getColor() {
			return color;
		}

		public String getName() {
			return name;
		}

		public float getScore() {
			return score;
		}

		public String toString() {
			return color.toString() + " " + score;
		}

	}

	public static class ScoreParsingException extends Exception {
		private static final long serialVersionUID = 1L;

		public ScoreParsingException(Exception e) {
			super(e);
		}

	}

	public int compareTo(Score otherScore) {
		// descending sort - earlier score is "bigger"
		long thisVal = this.startTime;
		long anotherVal = otherScore.startTime;
		return (thisVal < anotherVal ? 1 : (thisVal == anotherVal ? 0 : -1));
	}

}
