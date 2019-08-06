package edu.utep.cs5381.tabbyjump;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/** Representation of player. */
public class Player extends GameObject {

    private final float MAX_Y_VELOCITY = 45;

    private boolean isPressingRight = false;
    private boolean isPressingLeft = false;

    private double roll;

    private boolean isFalling;

    private Hitbox hitboxFeet;
    private Hitbox hitboxHead;

    private int minXPosition;
    private int maxXPosition;

    private boolean isAlive;

    private Context context;

    private boolean shieldActive;
    private long shieldStartTime;

    private int pixelsPerMeterX;

    void setRoll(double roll) {
        this.roll = roll;
    }

    double getRoll() {
        return roll;
    }

    Player(Context context, float worldStartX, float worldStartY, int pixelsPerMeterX,
           int minXPosition, int maxXPosition) {

        this.context = context;

        this.minXPosition = minXPosition;
        this.maxXPosition = maxXPosition;

        this.pixelsPerMeterX = pixelsPerMeterX;

        setHeight(4);
        setWidth(4);

        setXVelocity(0);
        setYVelocity(0);

        setFacing(Facing.LEFT);

        isFalling = true;

        setMoves(true);
        setActive(true);
        setVisible(true);

        setType(ObjectType.PLAYER);

        final int ANIMATION_FPS = 1;
        final int ANIM_FRAME_COUNT = 3;

        // Bitmap
        prepareSpriteSheet(context, R.drawable.player, pixelsPerMeterX, ANIM_FRAME_COUNT);
        setAnimated(ANIMATION_FPS, ANIM_FRAME_COUNT);

        setLocation(worldStartX, worldStartY, 1);

        hitboxFeet = new Hitbox();
        hitboxHead = new Hitbox();

        isAlive = true;

    }

    @Override
    public void update(long fps, float gravity) {

        // Set X velocity depending on direction press, zero if no direction
        float MAX_X_VELOCITY = 20;
        if (isPressingRight) {
            setXVelocity(MAX_X_VELOCITY);
        } else if (isPressingLeft) {
            setXVelocity(-MAX_X_VELOCITY);
        } else {
            setXVelocity(0);
        }

        if (roll != 0) {
            // Instead, set it using roll
            double sensitivity = 0.4; // Value of 0 to 1, higher is more sensitive
            float xVelocity = Math.min((float) (roll / sensitivity), MAX_X_VELOCITY);
            setXVelocity(xVelocity);
        }

        // Set facing depending on X velocity, unchanged if zero
        if (getXVelocity() > 0) {
            setFacing(Facing.RIGHT);
        } else if (getXVelocity() < 0) {
            setFacing(Facing.LEFT);
        }

        // Update Y velocity by adding gravity
        setYVelocity(getYVelocity() + gravity);

        // Player is falling if Y velocity is positive
        isFalling = getYVelocity() > 0;

        // Update x and y coordinates
        move(fps);

        float lx = getLocation().x;
        float ly = getLocation().y;

        // If player has moved past minXPosition or maxXPosition,
        // move player accordingly to give the illusion of looping
        if (lx + getWidth() < minXPosition) {
            setLocationX(maxXPosition - getWidth());
        } else if (lx > maxXPosition) {
            setLocationX(minXPosition);
        }

        // Update feet hit box
        hitboxFeet.top = ly + (getHeight() * .95f);
        hitboxFeet.left = lx + getWidth() * .2f;
        hitboxFeet.bottom = ly + getHeight();
        hitboxFeet.right = lx + getWidth() * .8f;

        // Update head hit box
        hitboxHead.top = ly + (getHeight() * .3f);
        hitboxHead.left = lx + (getWidth() * .2f);
        hitboxHead.bottom = ly + getHeight() * .95f;
        hitboxHead.right = lx + (getWidth() * .8f);

        // Check if shield should be disabled
        final int MAX_SHIELD_TIME = 7000; // ~7 seconds
        if (System.currentTimeMillis() - shieldStartTime >= MAX_SHIELD_TIME) {
            shieldActive = false;
        }
    }

    @Override
    public void draw(Context context, Canvas canvas, Rect drawRegion) {

        Paint paint = new Paint();

        Bitmap playerBitmap = getBitmap();

        // Draw a shield if needed
        if (shieldActive) {
            drawShield(canvas, drawRegion, playerBitmap);
        }

        // Draw the player using its bitmap
        canvas.drawBitmap(playerBitmap, drawRegion.left, drawRegion.top, paint);


        // If a part of the player is missing in the left side, draw it on the right side
        if (drawRegion.left < 0) {

            int cloneX = canvas.getWidth() - drawRegion.left * -1;

            // Draw shield if needed
            if (shieldActive) {
                Rect drawRegionCopy = drawRegion;
                drawRegionCopy.left = cloneX;
                drawShield(canvas, drawRegionCopy, playerBitmap);
            }

            canvas.drawBitmap(playerBitmap, cloneX, drawRegion.top, paint);

        // If a part of the player is missing on the right side, draw it on the left side
        } else if (drawRegion.right > canvas.getWidth()) {

            int cloneX = (canvas.getWidth() - drawRegion.left) * -1;

            // Draw shield if needed
            if (shieldActive) {
                Rect drawRegionCopy = drawRegion;
                drawRegionCopy.left = cloneX;
                drawShield(canvas, drawRegionCopy, playerBitmap);
            }

            canvas.drawBitmap(playerBitmap, cloneX, drawRegion.top, paint);
        }

    }

    private void drawShield(Canvas canvas, Rect drawRegion, Bitmap playerBitmap) {

        final float SHIELD_WIDTH = 5;
        final float SHIELD_HEIGHT = 5;

        Bitmap overlayBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.shield_overlay);
        overlayBitmap = Bitmap.createScaledBitmap(overlayBitmap, (int) SHIELD_WIDTH * pixelsPerMeterX,
                (int) SHIELD_HEIGHT * pixelsPerMeterX, false);

        Paint paint = new Paint();

        float x1 = drawRegion.left + (playerBitmap.getWidth() / 2f);
        float x2 = drawRegion.top + (playerBitmap.getHeight() / 2f);
        float left = x1 - (overlayBitmap.getWidth() / 2f);
        float top = x2 - (overlayBitmap.getHeight() / 2f);
        canvas.drawBitmap(overlayBitmap, left, top, paint);
    }

    public enum CollisionRegion {
        HEAD,
        FEET,
        NONE
    }

    boolean isFalling() {
        return isFalling;
    }

    CollisionRegion checkCollision(GameObject object) {

        Hitbox hitbox = object.getHitbox();

        if (this.hitboxFeet.intersects(hitbox)) {
            return CollisionRegion.FEET;
        }

        if (this.hitboxHead.intersects(hitbox)) {
            return CollisionRegion.HEAD;
        }

        return CollisionRegion.NONE;
    }

    void setPressingRight(boolean isPressingRight) {
        this.isPressingRight = isPressingRight;
    }

    void setPressingLeft(boolean isPressingLeft) {
        this.isPressingLeft = isPressingLeft;
    }

    void jump() {

        // Player cannot jump if already jumping
        if (!isFalling) {
            return;
        }

        setYVelocity(-MAX_Y_VELOCITY);

    }

    void superJump() {

        // Player cannot jump if already jumping
        if (!isFalling) {
            return;
        }

        setYVelocity(-MAX_Y_VELOCITY * 1.5f);
    }

    void kill() {
        SoundManager.instance(context).play(SoundManager.Sound.MEOW);
        setYVelocity(0);
        isAlive = false;
    }

    boolean isAlive() {
        return isAlive;
    }

    void activateShield() {
        shieldActive = true;
        shieldStartTime = System.currentTimeMillis();
    }

    boolean isShieldActive() {
        return shieldActive;
    }

}
