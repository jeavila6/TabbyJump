package edu.utep.cs5381.tabbyjump;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/** A class to facilitate storing and restoring game high scores. */
class ScoreManager {

    private SharedPreferences prefs;

    /** Singleton instance. */
    private static ScoreManager instance;

    /** Key for score value in preferences. */
    private final String SCORE_KEY = "highScore";

    private ScoreManager(Context context) {
        prefs = context.getSharedPreferences("highScores", MODE_PRIVATE);
    }

    static ScoreManager getInstance(Context context) {
        if (instance == null) {
            instance = new ScoreManager(context);
        }
        return instance;
    }

    /** Returns score retrieved from preferences. */
    int getScore() {
        return prefs.getInt(SCORE_KEY, 0);
    }

    /** Sets score in preferences. */
    void setScore(int score) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(SCORE_KEY, score);
        editor.apply();
    }
}