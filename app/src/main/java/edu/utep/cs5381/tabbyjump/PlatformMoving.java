package edu.utep.cs5381.tabbyjump;

import android.content.Context;

import java.util.Random;

public class PlatformMoving extends Platform {

    private WorldPoint waypointLeft;
    private WorldPoint waypointRight;

    PlatformMoving(Context context, WorldPoint waypointLeft, WorldPoint waypointRight, int pixelsPerMeterX) {
        super(waypointLeft.x, waypointLeft.y);

        this.waypointLeft = waypointLeft;
        this.waypointRight = waypointRight;

        setType(ObjectType.PLATFORM_MOVING);
        prepareSpriteSheet(context, R.drawable.platform_moving, pixelsPerMeterX, 1);
        setMoves(true);

        // Random X velocity
        Random rand = new Random();
        final int MIN_SPEED = 4;
        final int MAX_SPEED = 6;
        int speed = rand.nextInt((MAX_SPEED - MIN_SPEED) + 1) + MIN_SPEED;
        int velocity = rand.nextInt(2) == 0 ? speed : -speed;
        setXVelocity(velocity);

    }

    @Override
    public void update(long fps, float gravity) {
        super.update(fps, gravity);

        if (getLocation().x <= waypointLeft.x) { // If reached left waypoint
            setXVelocity(-getXVelocity());
            setLocationX(getLocation().x + 0.1f);
        } else if (getLocation().x + getWidth() >= waypointRight.x) { // If reached right waypoint
            setXVelocity(-getXVelocity());
            setLocationX(getLocation().x - 0.1f);
        }

        move(fps);
        setHitBox();
    }
}
