package edu.utep.cs5381.tabbyjump;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Platform extends GameObject {

    /** A game object to place on top of the platform. */
    private GameObject carrying;

    Platform(float worldStartX, float worldStartY) {

        setHeight(1);
        setWidth(4);

        setLocation(worldStartX, worldStartY, 0);

        setHitBox();
    }

    @Override
    public void update(long fps, float gravity) {
        if (carrying != null) {
            updateCarryLocation();
        }
    }

    @Override
    public void draw(Context context, Canvas canvas, Rect drawRegion) {
        Paint paint = new Paint();
        Bitmap b = getBitmap();
        canvas.drawBitmap(b, drawRegion.left, drawRegion.top, paint);
    }

    void setCarrying(GameObject object) {
        carrying = object;
        updateCarryLocation();
    }

    // Reposition carrying location
    private void updateCarryLocation() {

        // Add a tiny bit more to avoid unsightly gaps
        float space = 0.1f;

        carrying.setLocationY(getLocation().y - carrying.getHeight() + space);
        carrying.setLocationX((getWidth() - carrying.getWidth()) / 2 + getLocation().x);
    }
}
