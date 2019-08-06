package edu.utep.cs5381.tabbyjump;

import android.content.Context;
import android.media.SoundPool;

class SoundManager {

    public enum Sound {
        JUMP(R.raw.jump), // https://freesound.org/people/LloydEvans09/sounds/187024/
        PAUSE_ON(R.raw.pause_on), // https://freesound.org/people/jens.enk/sounds/434610/
        PAUSE_OFF(R.raw.pause_off), // https://freesound.org/people/jens.enk/sounds/434611/
        MEOW(R.raw.meow), // https://freesound.org/people/Department64/sounds/64017/
        SQUISH(R.raw.squish), // https://freesound.org/people/Zuzek06/sounds/353250/
        SHOOT(R.raw.shoot), // https://freesound.org/people/greenvwbeetle/sounds/244656/
        SPRING(R.raw.spring), // https://freesound.org/people/stomachache/sounds/29811/
        WIN(R.raw.win), // https://freesound.org/people/shinephoenixstormcrow/sounds/337049/
        SHIELD(R.raw.shield), // https://freesound.org/people/Cabeeno%20Rossley/sounds/126422/
        CLICK(R.raw.click), // https://freesound.org/people/TicTacShutUp/sounds/406/
        THUD(R.raw.thud); // https://freesound.org/people/InspectorJ/sounds/352180/

        public final int resourceId;

        private int soundId;

        Sound(int resourceId) {
            this.resourceId = resourceId;
        }
    }

    /** Singleton instance. */
    private static SoundManager theInstance;

    private final SoundPool soundPool;

    private SoundManager(Context ctx) {
        soundPool = new SoundPool.Builder().setMaxStreams(Sound.values().length).build();
        for (Sound sound: Sound.values()) {
            sound.soundId = soundPool.load(ctx, sound.resourceId, 1);
        }
    }

    static SoundManager instance(Context context) {
        if (theInstance == null) {
            theInstance = new SoundManager(context);
        }
        return theInstance;
    }

    void play(Sound sound) {
        soundPool.play(sound.soundId, 1, 1, 0, 0, 1);
    }
}