package edu.utep.cs5381.tabbyjump;

import android.content.Context;

/** A basic platform the player can jump on. */
public class PlatformBasic extends Platform {

    PlatformBasic(Context context, float worldStartX, float worldStartY, int pixelsPerMeterX) {
        super(worldStartX, worldStartY);
        setType(ObjectType.PLATFORM_BASIC);
        prepareSpriteSheet(context, R.drawable.platform_basic, pixelsPerMeterX, 1);
    }

    public void update(long fps, float gravity) {
        super.update(fps, gravity);
    }
}
