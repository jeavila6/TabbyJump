package edu.utep.cs5381.tabbyjump;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

// Font: https://www.dafont.com/vcr-osd-mono.font

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start game activity on start button click
        final Button buttonStart = findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(view -> {
            Intent gameActivityIntent = new Intent(this, GameActivity.class);
            startActivity(gameActivityIntent);
            finish();
        });

        // Start about activity on about button click
        final ImageButton imageButtonAbout = findViewById(R.id.imageButtonAbout);
        imageButtonAbout.setOnClickListener(view -> {
            Intent gameActivityIntent = new Intent(this, AboutActivity.class);
            startActivity(gameActivityIntent);
        });

        // Set high score text
        int score = ScoreManager.getInstance(this).getScore();
        TextView textViewHighScore = findViewById(R.id.textViewHighScore);
        textViewHighScore.setText(Integer.toString(score));

        // Set scrolling background (https://stackoverflow.com/a/36927302)
        final ImageView backgroundOne = findViewById(R.id.imageViewBg1);
        final ImageView backgroundTwo = findViewById(R.id.imageViewBg2);
        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(5000L);
        animator.addUpdateListener(animation -> {
            final float progress = (float) animation.getAnimatedValue();
            final float width = backgroundOne.getWidth();
            final float translationX = width * progress;
            backgroundOne.setTranslationX(translationX);
            backgroundTwo.setTranslationX(translationX - width);
        });
        animator.start();

        // Title animation
        TextView textViewTitle = findViewById(R.id.textViewTitle);
        android.view.animation.Animation anim = AnimationUtils.loadAnimation(this, R.anim.scale);
        textViewTitle.startAnimation(anim);
    }
}
