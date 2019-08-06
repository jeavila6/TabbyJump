package edu.utep.cs5381.tabbyjump;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Locale;

import static android.content.Context.SENSOR_SERVICE;


/** A custom view for handling the view aspect of the game. */
@SuppressLint("ViewConstructor")
public class GameView extends SurfaceView implements Runnable, SensorEventListener {

    /** A flag for controlling whether the game loop thread should do work. */
    private volatile boolean running;

    private boolean debugging;

    /** The game loop thread responsible for updating and drawing game objects. */
    private Thread gameThread = null;

    /** A Paint instance for holding style and color information when drawing. */
    private Paint paint;

    /** The drawing surface of the view. */
    private SurfaceHolder surfaceHolder;

    /** A Context instance for accessing application resources. */
    Context context;

    private LevelManager levelManager;

    private Viewport viewport;

    InputController inputController;

    SoundManager soundManager;

    /** The time just before drawing the last frame, in milliseconds. */
    long startFrameTime;

    /** The time just after drawing the last frame, in milliseconds. */
    long timeThisFrame;

    /** The number of frames per second ran at during the last frame. */
    long fps;

    private boolean hasSensor;

    SensorManager mSensorManager;
    Sensor mRotationVectorSensor;

    boolean runUpdates = true;

    GameView(Context context, int screenWidth, int screenHeight, boolean debugging) {
        super(context);

        this.context = context;
        this.debugging = debugging;

        // Initialize drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();

        // Initialize the viewport
        viewport = new Viewport(screenWidth, screenHeight);

        soundManager = SoundManager.instance(context);

        // Get an instance of the SensorManger
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        mRotationVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        hasSensor = (mRotationVectorSensor != null);

        // Load the first level
        loadLevel(Difficulty.NORMAL);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (levelManager != null) {
            inputController.handleInput(event, levelManager, viewport);
        }
        return true;
    }

    /** Sets the LevelManager according to level name and centers viewport on player. */
    public void loadLevel(Difficulty difficulty) {

        levelManager = new LevelManager(context, difficulty, viewport);

        inputController = new InputController(context, viewport.getScreenWidth(), viewport.getScreenHeight());

        // Set the players location as the world center of the viewport
        viewport.setFocusPoint(
                levelManager.getPlayer().getLocation().x,
                levelManager.getPlayer().getLocation().y
        );
    }

    @Override
    public void run() {
        while (running) {

            // Get the time just before updating and drawing game objects
            startFrameTime = System.currentTimeMillis();

            if (runUpdates) {
                update();
            }

            draw();

            // Get the time just after updating and drawing game objects
            timeThisFrame = System.currentTimeMillis() - startFrameTime;

            // Calculate the frames per second for this frame
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    /** Updates all game object levelData. */
    private void update() {

        // Do not update if game is not being played
        if (!levelManager.isPlaying()) {
            return;
        }

        for (GameObject object : levelManager.getGameObjects()) {

            // Do not update object if it is not currently active
            if (!object.isActive()) {
                continue;
            }

            // Set object's visibility depending if visible in viewport
            boolean inView = viewport.inView(object.getLocation().x, object.getLocation().y, object.getWidth(), object.getHeight());
            object.setVisible(inView);

            // Do not update object if it is not currently visible
            // With exception to the player, who can be outside view when looping through screen
            if (!object.isVisible() && object.getType() != ObjectType.PLAYER) {
                continue;
            }

            // Check collisions if player is is alive
            if (levelManager.getPlayer().isAlive()) {

                // Check object collisions with player
                Player.CollisionRegion collisionRegion = levelManager.getPlayer().checkCollision(object);

                // Handle collision with player
                if (collisionRegion == Player.CollisionRegion.FEET &&
                        levelManager.getPlayer().isFalling()) {

                    switch (object.getType()) {

                        case PLATFORM_BREAKABLE:
                            soundManager.play(SoundManager.Sound.THUD);
                            PlatformBreakable p = (PlatformBreakable) object;
                            p.setFalling();
                            levelManager.getPlayer().jump();
                            break;

                        case SLIME:
                            Slime s = (Slime) object;
                            s.kill();
                            levelManager.getPlayer().jump();
                            break;

                        case SPRING:
                            soundManager.play(SoundManager.Sound.SPRING);
                            levelManager.getPlayer().superJump();
                            break;

                        case SHIELD:
                            soundManager.play(SoundManager.Sound.SHIELD);
                            levelManager.getPlayer().activateShield();
                            Shield shield = (Shield) object;
                            shield.kill();
                            break;

                        default:
                            soundManager.play(SoundManager.Sound.JUMP);
                            levelManager.getPlayer().jump();
                            break;
                    }

                } else if (collisionRegion == Player.CollisionRegion.HEAD) {

                    switch (object.getType()) {
                        case SLIME:

                            // If shield is active, kill slime, else kill player
                            if (levelManager.getPlayer().isShieldActive()) {
                                Slime s = (Slime) object;
                                s.kill();
                            } else {
                                levelManager.getPlayer().kill();
                            }
                            break;

                        case SHIELD:
                            soundManager.play(SoundManager.Sound.SHIELD);
                            levelManager.getPlayer().activateShield();
                            Shield shield = (Shield) object;
                            shield.kill();
                            break;
                    }
                }
            }


            // Check bullet collisions
            if (object.getType() == ObjectType.BULLET) {
                Bullet bullet = (Bullet) object;

                for (GameObject otherObject : levelManager.getGameObjects()) {

                    // If bullet collides with slime, kill slime
                    if (otherObject.getType() == ObjectType.SLIME) {
                        Slime slime = (Slime) otherObject;
                        if (slime.checkCollision(bullet) && slime.isActive()) {
                            bullet.kill();
                            slime.kill();
                        }
                    }
                }
            }

            // Update active, in view object
            object.update(fps, levelManager.getGravity());

        }

        // Update the level if player is alive
        if (levelManager.getPlayer().isAlive()) {
            levelManager.updateLevel();
        }

        // Set the center of the viewport as the player's location
        viewport.setFocusPoint(viewport.getMetersToShowX() / 2f,
                levelManager.getHighestY());

        // If game is over, store high score if needed
        if (!levelManager.getPlayer().isAlive()) {
            int score = ScoreManager.getInstance(context).getScore();
            if (levelManager.getScore() > score) {
                ScoreManager.getInstance(context).setScore(levelManager.getScore());
            }
        }

    }

    /** Draws all game objects. */
    private void draw() {

        Typeface tf = Typeface.createFromAsset(context.getAssets(), "font/vcr_osd_mono.ttf");
        paint.setTypeface(tf);

        // Ignore if surface is not available
        if (!surfaceHolder.getSurface().isValid()) {
            return;
        }

        // Lock the area of memory drawing to
        Canvas canvas = surfaceHolder.lockCanvas();

        // Rub out the last frame with an arbitrary color
        canvas.drawColor(getResources().getColor(R.color.colorBackground));

        // Draw all game objects, one layer at a time
        for (int layer = -1; layer <= 1; layer++) {

            for (GameObject object : levelManager.getGameObjects()) {

                // Draw game object if it is visible and is in this layer
                if (object.isVisible() && object.getLocation().layer == layer) {

                    Rect drawRegion = viewport.worldToScreen(object.getLocation().x, object.getLocation().y, object.getWidth(),
                            object.getHeight());
                    object.draw(context, canvas, drawRegion);

                }
            }

        }

        // Draw text for debugging
        if (debugging) {
            int start = 25;
            int spacing = 45;
            paint.setTextSize(40);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setColor(Color.parseColor("#212121"));
            canvas.drawText("FPS: " + fps, 10, start + spacing, paint);
            canvas.drawText("Num objects: " + levelManager.getGameObjects().size(), 10, start + spacing * 2, paint);
            canvas.drawText("Num clipped: " + viewport.getNumClipped(), 10, start + spacing * 3, paint);
            canvas.drawText(String.format(Locale.getDefault(), "Player X: %.2f", levelManager.getPlayer().getLocation().x), 10, start + spacing * 4, paint);
            canvas.drawText(String.format(Locale.getDefault(), "Player Y: %.2f", levelManager.getPlayer().getLocation().y), 10, start + spacing * 5, paint);
            canvas.drawText("Gravity: " + levelManager.getGravity(), 10, start + spacing * 6, paint);
            canvas.drawText(String.format(Locale.getDefault(), "X velocity: %.2f", levelManager.getPlayer().getXVelocity()), 10, start + spacing * 7, paint);
            canvas.drawText("Y velocity: " + levelManager.getPlayer().getYVelocity(), 10, start + spacing * 8, paint);
            canvas.drawText(String.format(Locale.getDefault(), "Roll: %.3f", levelManager.getPlayer().getRoll()), 10, start + spacing * 9, paint);

            // Reset the number of clipped objects each frame
            viewport.resetNumClipped();

        }

        // Draw HUD
        if (levelManager.getPlayer().isAlive()) {

            // Draw a darker shade
            paint.setColor(Color.argb(30, 0, 0, 0));
            canvas.drawRect(0, 0, viewport.getScreenWidth(), 130, paint);

            // Draw score
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setColor(Color.parseColor("#212121"));
            paint.setTextSize(80);
            canvas.drawText("Score:" + levelManager.getScore(), 20, 95, paint);

        }

        // Draw paused text
        if (!levelManager.isPlaying()) {
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.parseColor("#212121"));
            paint.setTextSize(120);
            canvas.drawText("Paused", viewport.getScreenWidth() / 2f, viewport.getScreenHeight() / 2f, paint);
        }

        // Draw game over text
        if (!levelManager.getPlayer().isAlive()) {

            // Draw a lighter shade
            canvas.drawColor(Color.argb(140, 255, 255, 255));

            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.parseColor("#212121"));
            float centerX = viewport.getScreenWidth() / 2f;
            float centerY = viewport.getScreenHeight() / 2f;

            // Draw game over text
            paint.setTextSize(120);
            canvas.drawText("Game Over", centerX, centerY, paint);

            paint.setTextSize(60);

            // Draw high score text
            String text = "High Score: " + ScoreManager.getInstance(context).getScore();
            if (ScoreManager.getInstance(context).getScore() == levelManager.getScore()) {
                text += " (NEW!)";
            }
            canvas.drawText(text, centerX, centerY + 80, paint);

            // Draw score text
            canvas.drawText("Score: " + levelManager.getScore(), centerX, centerY + 150, paint);
        }

        // Draw buttons
        inputController.drawButtons(canvas, hasSensor, levelManager.getPlayer().isAlive(), levelManager.isPlaying());

        // Unlock and draw the scene
        surfaceHolder.unlockCanvasAndPost(canvas);

    }


    /** Pause game loop thread. */
    public void pause() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error", "Failed to pause thread");
        }

        // Turn off sensor
        mSensorManager.unregisterListener(this);
    }

    /** Resume game loop thread. Execution moves to run() method. */
    public void resume() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();

        // Enable sensor, ask for 10ms updates
        mSensorManager.registerListener(this, mRotationVectorSensor, 10000);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // Check that the event is what we're looking for, since this is called A LOT
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] R = new float[16];
            SensorManager.getRotationMatrixFromVector(R, event.values);
            float[] values = new float[3];
            SensorManager.getOrientation(R, values);
            double rollDegrees = Math.toDegrees(values[2]);
            levelManager.getPlayer().setRoll(rollDegrees);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
