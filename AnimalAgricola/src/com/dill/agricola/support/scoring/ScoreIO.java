package com.dill.agricola.support.scoring;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.dill.agricola.support.scoring.Score.PlayerScore;
import com.dill.agricola.support.scoring.Score.ScoreParsingException;

public class ScoreIO {

	public static final String FILE_NAME = "_scores.txt";

	private Collection<Score> scores = null;

	public void appendScore(Score score) {
		Collection<Score> scores = getScores();
		scores.add(score);
		writeScores(scores);
	}

	public Collection<Score> getScores() {
		if (scores == null) {
			scores = readScores();
		}
		return scores;
	}

	public void writeScores(Collection<Score> scores) {

		BufferedWriter scoreOut = null;
		try {
			File f = new File(FILE_NAME);
			f.createNewFile(); // creates only if does not exist			

			scoreOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
			for (Score score : scores) {
				scoreOut.append(Score.serialize(score)).append("\r\n");
			}
			scoreOut.flush();
		} catch (Exception e) {
			throw new RuntimeException(e);
//			e.printStackTrace();
		} finally {
			if (scoreOut != null) {
				try {
					scoreOut.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public Collection<Score> readScores() {
		List<Score> scores = new ArrayList<Score>();
		File f = new File(FILE_NAME);
		if (!f.exists() || !f.canRead()) {
			return scores;
		}

		BufferedReader scoreIn = null;
		try {
			scoreIn = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
			String line = null;
			while ((line = scoreIn.readLine()) != null) {
				scores.add(Score.parse(line));
			}
		} catch (ScoreParsingException e) {
			System.err.println("Score file " + FILE_NAME + " is broken");
//			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
//			e.printStackTrace();
		} finally {
			if (scoreIn != null) {
				try {
					scoreIn.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return new TreeSet<Score>(scores);
	}

	/**
	 * @return All names present in saved scores, sorted by frequency
	 */
	public List<String> getNamesInScores() {
		Collection<Score> scores = getScores();
		final Map<String, Integer> nameCount = new HashMap<String, Integer>();
		for (Score score : scores) {
			for (PlayerScore ps : score.getPlayerScores()) {
				String name = ps.getName();
				if (!nameCount.containsKey(name)) {
					nameCount.put(name, 0);
				} else {
					nameCount.put(name, nameCount.get(name) + 1);
				}
			}
		}
		List<String> names = new ArrayList<String>(nameCount.keySet());
		Collections.sort(names, new Comparator<String>() {
			public int compare(String name1, String name2) {
				// descending sort
				return nameCount.get(name2).compareTo(nameCount.get(name1));
			}
		});
		return names;
	}

	public boolean isSaved(long startTime) {
		Collection<Score> scores = getScores();
		for (Score score : scores) {
			if (score.getStartTime() == startTime) {
				return true;
			}
		}
		return false;
	}
}
