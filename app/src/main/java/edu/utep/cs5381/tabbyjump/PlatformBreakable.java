package edu.utep.cs5381.tabbyjump;

import android.content.Context;

public class PlatformBreakable extends Platform {

    private boolean isFalling;

    PlatformBreakable(Context context, float worldStartX, float worldStartY, int pixelsPerMeterX) {
        super(worldStartX, worldStartY);
        setType(ObjectType.PLATFORM_BREAKABLE);
        prepareSpriteSheet(context, R.drawable.platform_breakable, pixelsPerMeterX, 1);
        setMoves(true);
    }

    public void update(long fps, float gravity) {
        super.update(fps, gravity);
        if (isFalling) {
            setYVelocity(getYVelocity() + gravity);
            move(fps);
        }
    }

    void setFalling() {
        isFalling = true;
    }
}
