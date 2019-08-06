package edu.utep.cs5381.tabbyjump;

/**
 * A class for holding a location in the game world.
 * This location is independent to the coordinates of the pixels of the device the game runs on.
 */
class WorldPoint {

    /** x-axis position. */
    float x;

    /** y-axis position. */
    float y;

    /** Layer number. Lower layers will be drawn first. */
    int layer;

    WorldPoint() {}

    WorldPoint(float x, float y, int layer) {
        this.x = x;
        this.y = y;
        this.layer = layer;
    }

    WorldPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

}
