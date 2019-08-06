package edu.utep.cs5381.tabbyjump;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class GameActivity extends Activity {

    /** PlatformView instance to set as the activity's content. */
    private GameView mGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;

        // Set the activity's content to an instance of GameView
        mGameView = new GameView(this, displayWidth, displayHeight, false);
        setContentView(mGameView);

    }

    // If the Activity is resumed, resume the game loop thread
    @Override
    protected void onResume() {
        super.onResume();
        mGameView.resume();
    }

    // If the Activity is paused, pause the game loop thread
    @Override
    protected void onPause() {
        super.onPause();
        mGameView.pause();
    }

}
