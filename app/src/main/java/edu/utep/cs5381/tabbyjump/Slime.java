package edu.utep.cs5381.tabbyjump;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Random;

public class Slime extends GameObject {

    Context context;

    Slime(Context context, float worldStartX, float worldStartY, int pixelsPerMeterX) {

        this.context = context;

        setHeight(3);
        setWidth(3);

        setMoves(true);
        setActive(true);
        setVisible(true);

        setType(ObjectType.SLIME);

        final int ANIMATION_FPS = 6;
        final int ANIM_FRAME_COUNT = 4;

        // Choose random drawable
        Random rand = new Random();
        int[] drawables = {R.drawable.slime1, R.drawable.slime2, R.drawable.slime3};
        int selection = rand.nextInt(drawables.length);
        int drawable = drawables[selection];

        prepareSpriteSheet(context, drawable, pixelsPerMeterX, ANIM_FRAME_COUNT);
        setAnimated(ANIMATION_FPS, ANIM_FRAME_COUNT);

        setLocation(worldStartX, worldStartY, -1);

    }


    @Override
    public void update(long fps, float gravity) {

        // Update x and y coordinates
        move(fps);

        // Use the standard hitbox
        setHitBox();
    }

    @Override
    public void draw(Context context, Canvas canvas, Rect drawRegion) {
        Paint paint = new Paint();

        // Draw the player using its bitmap
        Bitmap bitmap = getBitmap();
        canvas.drawBitmap(bitmap, drawRegion.left, drawRegion.top, paint);
    }

    /** Returns true given object collides with slime. */
    boolean checkCollision(GameObject object) {
        return getHitbox().intersects(object.getHitbox());
    }

    void kill() {
        setVisible(false);
        setActive(false);
        SoundManager.instance(context).play(SoundManager.Sound.SQUISH);
    }
}
