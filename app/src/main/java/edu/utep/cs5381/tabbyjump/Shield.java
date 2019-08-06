package edu.utep.cs5381.tabbyjump;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Shield extends GameObject {

    Context context;

    Shield(Context context, float worldStartX, float worldStartY, int pixelsPerMeterX) {

        this.context = context;

        setHeight(2);
        setWidth(2);

        setActive(true);
        setVisible(true);

        setType(ObjectType.SHIELD);

        final int ANIM_FRAME_COUNT = 1;

        prepareSpriteSheet(context, R.drawable.shield, pixelsPerMeterX, ANIM_FRAME_COUNT);

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

    void kill() {
        setActive(false);
        setVisible(false);
    }

}
