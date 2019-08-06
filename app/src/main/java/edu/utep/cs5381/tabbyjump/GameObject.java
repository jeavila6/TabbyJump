package edu.utep.cs5381.tabbyjump;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

/**
 * A class for holding game object data.
 * This class can be extended each time a new game object is created.
 */
public abstract class GameObject {

    /** The object's location in the game world. */
    private WorldPoint location;

    /** The object's width in game world meters. */
    private float width;

    /** The object's height in game world meters. */
    private float height;

    /** A flag for controlling whether the object should be updated. */
    private boolean active = true;

    /** A flag for controlling whether the object should be drawn. */
    private boolean visible = true;

    private boolean moves = false;

    /** The object's type. */
    private ObjectType type;

    /** The image used to draw the object. This image may contain more than one frame. */
    private Bitmap spriteSheet;

    private Hitbox hitBox = new Hitbox();

    private float xVelocity;

    private float yVelocity;

    private Facing facing;

    private Animation anim;

    private boolean animated;

    enum Facing {
        LEFT,
        RIGHT
    }

    /** Method objects should implement to update their data. */
    public abstract void update(long fps, float gravity);

    /** Method objects should implement to draw themselves. */
    public abstract void draw(Context context, Canvas canvas, Rect drawRegion);

    void prepareSpriteSheet(Context context, int bitmapResourceId, int pixelsPerMeterX, int animFrameCount) {
        Bitmap spriteSheet = BitmapFactory.decodeResource(context.getResources(), bitmapResourceId);
        this.spriteSheet = Bitmap.createScaledBitmap(
                spriteSheet,
                (int) width * pixelsPerMeterX * animFrameCount,
                (int) (height * pixelsPerMeterX), // TODO use pixelsPerMeterY for height
                false
        );
    }

    Bitmap getBitmap() {

        // If the object is not animated, then return the entire sprite sheet
        if (!animated) {
            return spriteSheet;
        }

        Rect frameRegion = anim.getCurrentFrame(System.currentTimeMillis());

        Bitmap bitmap = Bitmap.createBitmap(spriteSheet, frameRegion.left, frameRegion.top, frameRegion.width()-1, frameRegion.height()-1);

        // Flip if needed
        if (this.facing == Facing.RIGHT) {
            Matrix matrix = new Matrix();
            matrix.setScale(-1, 1);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        }
        return bitmap;
    }

    /** Moves object in the game world according to its velocity in the x and y axis. */
    void move(long fps) {

        // Avoid nasty division by zero
        if (fps == 0) {
            return;
        }

        // Distance to move per frame should be the same regardless of the current frame rate
        if (xVelocity != 0) {
            location.x += xVelocity / fps;
        }

        if (yVelocity != 0) {
            location.y += yVelocity / fps;
        }
    }

    void setHitBox() {
        hitBox.setTop(location.y);
        hitBox.setLeft(location.x);
        hitBox.setBottom(location.y + height);
        hitBox.setRight(location.x + width);
    }

    WorldPoint getLocation() {
        return location;
    }

    Hitbox getHitbox() {
        return hitBox;
    }

    void setAnimated(int animFps, int animFrameCount) {
        this.animated = true;
        this.anim = new Animation(animFps, animFrameCount, spriteSheet);
    }

    void setLocation(float x, float y, int layer) {
        this.location = new WorldPoint();
        this.location.x = x;
        this.location.y = y;
        this.location.layer = layer;
    }

    void setLocationX(float x) {
        location.x = x;
    }

    void setLocationY(float y) {
        location.y = y;
    }

    float getWidth() {
        return width;
    }

    void setWidth(float width) {
        this.width = width;
    }

    float getHeight() {
        return height;
    }

    void setHeight(float height) {
        this.height = height;
    }

    boolean isActive() {
        return active;
    }

    boolean isVisible() {
        return visible;
    }

    void setVisible(boolean visible) {
        this.visible = visible;
    }

    ObjectType getType() {
        return type;
    }

    void setType(ObjectType type) {
        this.type = type;
    }

    void setFacing(Facing facing) {
        this.facing = facing;
    }

    float getXVelocity() {
        return xVelocity;
    }

    void setXVelocity(float xVelocity) {

        // Only allow for objects that can move
        if (moves) {
            this.xVelocity = xVelocity;
        }
    }

    float getYVelocity() {
        return yVelocity;
    }

    void setYVelocity(float yVelocity) {

        // Only allow for objects that can move
        if (moves) {
            this.yVelocity = yVelocity;
        }
    }

    void setMoves(boolean moves) {
        this.moves = moves;
    }

    void setActive(boolean active) {
        this.active = active;
    }

}
