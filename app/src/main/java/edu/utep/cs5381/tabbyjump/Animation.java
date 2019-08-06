package edu.utep.cs5381.tabbyjump;

import android.graphics.Bitmap;
import android.graphics.Rect;

class Animation {
    private Rect sourceRect;
    private int frameCount;
    private int currentFrame;
    private long frameTicker;
    private int framePeriod;
    private int frameWidth;

    Animation(int animFps, int frameCount, Bitmap sheet) {

        currentFrame = 0;
        this.frameCount = frameCount;

        // Each frame is the width in pixels divided by the number of frames
        // Images are placed in drawable-nodpi because the width of the bitmap depends
        // On the screen density, and we don't care about that
        frameWidth = sheet.getWidth() / frameCount;
        int frameHeight = sheet.getHeight();

        sourceRect = new Rect(0, 0, frameWidth, frameHeight);
        framePeriod = 1000 / animFps;
        frameTicker = 0L;
    }

    Rect getCurrentFrame(long time){

        if (time > frameTicker + framePeriod) {
            frameTicker = time;
            currentFrame++;
            if (currentFrame >= frameCount) {
                currentFrame = 0;
            }
        }

        // Update the left and right values of the source of
        // the next frame on the sprite sheet
        this.sourceRect.left = currentFrame * frameWidth;
        this.sourceRect.right = this.sourceRect.left + frameWidth;

        return sourceRect;
    }

}
