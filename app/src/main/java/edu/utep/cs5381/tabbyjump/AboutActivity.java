package edu.utep.cs5381.tabbyjump;

import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

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
    }
}
