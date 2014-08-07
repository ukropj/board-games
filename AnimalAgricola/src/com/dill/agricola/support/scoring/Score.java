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
	private final List<PlayerScore> playerScores;

	public Score(Game game, Map<PlayerColor, String> nameMap) {
		this.startTime = game.getStartTime();
		List<PlayerScore> playerScores = new ArrayList<PlayerScore>();
		PlayerColor startingPlayer = game.getInitialStartPlayer();
		for (Player player : game.getPlayers()) {
			PlayerColor color = player.getColor();
			playerScores.add(new PlayerScore(color, nameMap.get(color), player.getScore(), color == startingPlayer));
		}
		this.playerScores = Collections.unmodifiableList(playerScores);
	}

	private Score(long startTime, List<PlayerScore> playerScores) {
		this.startTime = startTime;
		this.playerScores = Collections.unmodifiableList(playerScores);
	}

	public long getStartTime() {
		return startTime;
	}

	private Date getDate() {
		return new Date(startTime);
	}

	public List<PlayerScore> getPlayerScores() {
		return playerScores;
	}

	public String toString() {
		return df.format(getDate()) + " " + playerScores;
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
		sb.append(score.getStartTime()).append(";"); // TODO use ISO
		for (PlayerScore ps : score.getPlayerScores()) {
			sb.append(ps.getColor()).append(";")
					.append(sanitize(ps.getName())).append(";")
					.append(ps.getScore()).append(";")
					.append(ps.isStarting()).append(";");
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
			List<PlayerScore> playerScores = new ArrayList<PlayerScore>();
			for (int i = 1; i + 3 < parts.length; i += 4) {
				PlayerColor color = PlayerColor.valueOf(parts[i]);
				String name = parts[i + 1];
				float score = Float.valueOf(parts[i + 2]);
				boolean isStarting = Boolean.valueOf(parts[i + 3]);
				playerScores.add(new PlayerScore(color, name, score, isStarting));
			}
			return new Score(time, playerScores);
		} catch (Exception e) {
			throw new ScoreParsingException(e);
		}
	}

	public static class PlayerScore {

		private final PlayerColor color;
		private final String name;
		private final float score;
		private final boolean isStarting;

		public PlayerScore(PlayerColor color, String name, float score, boolean isStarting) {
			this.color = color;
			this.name = name;
			this.score = score;
			this.isStarting = isStarting;
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

		public boolean isStarting() {
			return isStarting;
		}

		@Override
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
