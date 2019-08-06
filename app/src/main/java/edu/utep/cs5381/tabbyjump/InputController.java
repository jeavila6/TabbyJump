package edu.utep.cs5381.tabbyjump;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

/** Processes user input. */
class InputController {

    private InputButton left;
    private InputButton right;
    private InputButton pause;
    private InputButton start;
    private InputButton restart;

    private Context context;

    class InputButton {

        private Rect region;
        private int bitmapResourceId;

        InputButton(Rect region, int bitmapResourceId) {
            this.region = region;
            this.bitmapResourceId = bitmapResourceId;
        }

        InputButton(Rect region) {
            this.region = region;
        }
    }

    InputController(Context context, int screenWidth, int screenHeight) {

        this.context = context;

        //Configure the player buttons
        int buttonWidth = screenWidth / 4;
        int buttonHeight = screenHeight / 8;
        int buttonPadding = screenWidth / 80;

        left = new InputButton(
                new Rect(buttonPadding, screenHeight - buttonHeight - buttonPadding, buttonWidth, screenHeight - buttonPadding)
        );

        right = new InputButton(
                new Rect(buttonWidth + buttonPadding, screenHeight - buttonHeight - buttonPadding, buttonWidth + buttonPadding + buttonWidth, screenHeight - buttonPadding)
        );

        int buttonSize = 95;
        pause = new InputButton(
                new Rect(screenWidth - buttonPadding - buttonSize, buttonPadding, screenWidth - buttonPadding, buttonPadding + buttonSize),
                R.drawable.pause
        );

        start = new InputButton(pause.region, R.drawable.start);

        restart = new InputButton(
                new Rect(screenWidth / 2 - buttonSize / 2, screenHeight - 1100, screenWidth / 2 + buttonSize / 2, screenHeight - 1100 + buttonSize),
                R.drawable.restart
        );

    }

    void handleInput(MotionEvent motionEvent, LevelManager levelManager, Viewport viewport) {

        int pointerCount = motionEvent.getPointerCount();

        for (int i = 0; i < pointerCount; i++) {

            int x = (int) motionEvent.getX(i);
            int y = (int) motionEvent.getY(i);

            // If the game is being played and the player is alive
            if (levelManager.isPlaying() && levelManager.getPlayer().isAlive()) {

                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:


                        if (right.region.contains(x, y)) {
                            levelManager.getPlayer().setPressingRight(true);
                            levelManager.getPlayer().setPressingLeft(false);
                        } else if (left.region.contains(x, y)) {
                            levelManager.getPlayer().setPressingLeft(true);
                            levelManager.getPlayer().setPressingRight(false);
                        } else if (pause.region.contains(x, y)) {
                            SoundManager.instance(context).play(SoundManager.Sound.PAUSE_ON);
                            levelManager.setPlaying(false);
                        } else { // Bullet
                            SoundManager.instance(context).play(SoundManager.Sound.SHOOT);
                            WorldPoint waypoint = viewport.screenToWorldPoint(x, y);
                            levelManager.addBullet(waypoint);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        if (right.region.contains(x, y)) {
                            levelManager.getPlayer().setPressingRight(false);
                        } else if (left.region.contains(x, y)) {
                            levelManager.getPlayer().setPressingLeft(false);
                        }
                        break;

                }
            }

            // If the game is not being played, but the player is alive
            else if (!levelManager.isPlaying() && levelManager.getPlayer().isAlive()) {

                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        if (pause.region.contains(x, y)) {
                            levelManager.setPlaying(true);
                            SoundManager.instance(context).play(SoundManager.Sound.PAUSE_OFF);
                        }
                        break;

                }

            } else {

                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        if (restart.region.contains(x, y)) {
                            levelManager.restartLevel();
                            SoundManager.instance(context).play(SoundManager.Sound.CLICK);
                        }
                        break;

                }
            }
        }
    }

    void drawButtons(Canvas canvas, boolean hasSensor, boolean playerIsAlive, boolean isPlaying) {
        Paint paint = new Paint();
        paint.setColor(Color.argb(80, 0, 0, 0));

        if (!hasSensor) {

            // Draw left button
            RectF rf = new RectF(left.region.left, left.region.top, left.region.right, left.region.bottom);
            canvas.drawRoundRect(rf, 15f, 15f, paint);

            // Draw right button
            rf = new RectF(right.region.left, right.region.top, right.region.right, right.region.bottom);
            canvas.drawRoundRect(rf, 15f, 15f, paint);
        }

        paint = new Paint();

        if (playerIsAlive) {

            if (isPlaying) {

                // Draw pause button
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), pause.bitmapResourceId);
                Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                canvas.drawBitmap(bitmap, src, pause.region, paint);
            } else {

                // Draw play button
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), start.bitmapResourceId);
                Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                canvas.drawBitmap(bitmap, src, start.region, paint);
            }

        } else {

            // Draw win button
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), restart.bitmapResourceId);
            Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            canvas.drawBitmap(bitmap, src, restart.region, paint);
        }

    }
}
