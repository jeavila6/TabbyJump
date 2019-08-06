package edu.utep.cs5381.tabbyjump;

import android.graphics.Rect;

/** The region of the game world to be shown on screen. */
class Viewport {

    /** The display width in pixels. */
    private int displayX;

    /** The display height in pixels. */
    private int displayY;

    /** The center of the display width in pixels. */
    private int displayCenterX;

    /** The center of the display height in pixels. */
    private int displayCenterY;

    /** The number of game world meters to show in the X-axis. */
    private int metersToShowX;

    /** The number of game world meters to show in the Y-axis. */
    private int metersToShowY;

    /** The number of pixels per game world meters in the X-axis. */
    private int pixelsPerMeterX;

    /** The number of pixels per game world meter in the Y-axis. */
    private int pixelsPerMeterY;

    /** The point in the game world to focus on. */
    private WorldPoint focusPoint;

    /** The number of game objects clipped. Used in debugging. */
    private int numClipped;

    Viewport(int displayWidth, int displayHeight){

        displayX = displayWidth;
        displayY = displayHeight;

        displayCenterX = displayX / 2;
        displayCenterY = displayY / 2;

        metersToShowX = 32;
        metersToShowY = 64;

        pixelsPerMeterX = displayX / metersToShowX;
        pixelsPerMeterY = displayY / metersToShowY;

        focusPoint = new WorldPoint();
    }

    /** Returns an object's region on screen given its location and dimensions. */
    Rect worldToScreen(float objectX, float objectY, float objectWidth, float objectHeight) {

        int left = (int) (displayCenterX - ((focusPoint.x - objectX) * pixelsPerMeterX));
        int top =  (int) (displayCenterY - ((focusPoint.y - objectY) * pixelsPerMeterY));
        int right = (int) (left + (objectWidth * pixelsPerMeterX));
        int bottom = (int) (top + (objectHeight * pixelsPerMeterY));

        return new Rect(left, top, right, bottom);
    }

    /** Returns a world point given a screen point. */
    WorldPoint screenToWorldPoint(float screenX, float screenY) {
        float x = focusPoint.x - (metersToShowX / 2f - screenX / pixelsPerMeterX);
        float y = focusPoint.y - (metersToShowY / 2f - screenY / pixelsPerMeterY);
        return new WorldPoint(x, y);
    }

    /** Returns true if a game object is in view given its location and dimensions. */
    boolean inView(float objectX, float objectY, float objectWidth, float objectHeight) {

        boolean inView =
                (objectX < focusPoint.x + (metersToShowX / 2)) &&
                        (objectX + objectWidth> focusPoint.x - (metersToShowX / 2)) &&
                        (objectY < focusPoint.y + (metersToShowY / 2)) &&
                        (objectY + objectHeight > focusPoint.y - (metersToShowY / 2));

        if (!inView) {
            numClipped++;
        }

        return inView;
    }

    int getScreenWidth(){
        return displayX;
    }

    int getScreenHeight(){
        return displayY;
    }

    int getMetersToShowX() {
        return metersToShowX;
    }

    int getMetersToShowY() {
        return metersToShowY;
    }

    int getPixelsPerMeterX(){
        return pixelsPerMeterX;
    }

    void setFocusPoint(float x, float y){
        focusPoint.x  = x;
        focusPoint.y  = y;
    }

    int getNumClipped(){
        return numClipped;
    }

    void resetNumClipped(){
        numClipped = 0;
    }
}
