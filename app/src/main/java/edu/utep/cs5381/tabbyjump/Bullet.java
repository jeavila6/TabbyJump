package edu.utep.cs5381.tabbyjump;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Bullet extends GameObject {

    Bullet(Context context, float worldStartX, float worldStartY, int pixelsPerMeterX, WorldPoint waypoint) {

        setHeight(1);
        setWidth(1);

        setMoves(true);
        setActive(true);
        setVisible(true);

        setType(ObjectType.BULLET);

        final int ANIMATION_FPS = 8;
        final int ANIM_FRAME_COUNT = 4;

        prepareSpriteSheet(context, R.drawable.bullet, pixelsPerMeterX, ANIM_FRAME_COUNT);
        setAnimated(ANIMATION_FPS, ANIM_FRAME_COUNT);

        setLocation(worldStartX, worldStartY, 1);

        // Find theta from polar coordinate
        double theta = Math.atan2(waypoint.y - worldStartY, waypoint.x - worldStartX);

        // Find X and Y component velocities
        final int SPEED = 36;
        double xVelocity = SPEED * Math.cos(theta);
        double yVelocity = SPEED * Math.sin(theta);

        setXVelocity((float) xVelocity);
        setYVelocity((float) yVelocity);

        setHitBox();

    }

    @Override
    public void update(long fps, float gravity) {
        move(fps);
        setHitBox();
    }

    @Override
    public void draw(Context context, Canvas canvas, Rect drawRegion) {
        Paint paint = new Paint();

        // Draw the bullet using its bitmap
        Bitmap bitmap = getBitmap();
        canvas.drawBitmap(bitmap, drawRegion.left, drawRegion.top, paint);
    }

    void kill() {
        setActive(false);
        setVisible(false);
    }
}
