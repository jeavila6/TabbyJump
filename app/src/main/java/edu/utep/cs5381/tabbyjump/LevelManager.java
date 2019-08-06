package edu.utep.cs5381.tabbyjump;

import android.content.Context;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/** Loads and manages data for a level being played. */
class LevelManager {

    /** Context for accessing application resources. */
    private Context context;

    /** A flag for indicating whether the game is being played or is paused. */
    private boolean playing;

    /** The game objects in the level. */
    private CopyOnWriteArrayList<GameObject> gameObjects;

    /** A handle to the player. */
    private Player player;

    private int metersShownY;

    private float gravity;

    private int boundaryLeft;

    private int boundaryRight;

    private int lastRowGenerated;

    private int pixelsPerMeterX;

    private float startingY;
    private float highestY;

    private Viewport viewport;

    LevelManager(Context context, Difficulty difficulty, Viewport viewport) {

        this.context = context;
        this.viewport = viewport;

        pixelsPerMeterX = viewport.getPixelsPerMeterX();

        // Load level data
        LevelData levelData = difficulty.levelData;
        gravity = levelData.gravity;

        boundaryLeft = 0;
        boundaryRight = boundaryLeft + viewport.getMetersToShowX();

        this.metersShownY = viewport.getMetersToShowY();

        gameObjects = new CopyOnWriteArrayList<>();

        restartLevel();

    }

    void restartLevel() {

        gameObjects.clear();

        startingY = 5;
        highestY = startingY;

        // Add the player at the center
        float playerX = (boundaryRight - boundaryLeft) / 2f;
        float playerY = startingY;
        player = new Player(context, playerX, playerY, pixelsPerMeterX, boundaryLeft, boundaryRight);
        gameObjects.add(player);

        // Add a basic platform right below the player to start with
        Platform p = new PlatformBasic(context, playerX, playerY + player.getHeight() + 1,
                pixelsPerMeterX);
        gameObjects.add(p);

        lastRowGenerated = (int) playerY;

        playing = true; // Start un-paused
    }

    void updateLevel() {

        float playerY = player.getLocation().y;

        // Check if player is out of view
        if (playerY > highestY + (metersShownY / 2)) {
            player.kill();
        }

        // Set highest Y if needed
        highestY = Math.min(playerY, highestY);

        // Ignore if player is not one screen above last generated row
        if (playerY >= lastRowGenerated + metersShownY)
            return;

        Random rand = new Random();

        for (int y = (int) playerY; y >= playerY - metersShownY; y--) {

            if (y >= lastRowGenerated)
                continue;

            lastRowGenerated = y;

            // Skip rows
            if (y % 6 != 0)
                continue;

            int padding = (int) ((boundaryRight - boundaryLeft) * 0.05); // ~5% padding

            // Decide what kind of platform to add
            // Use a placeholder X position for now, reposition later
            Platform p;
            int selection = rand.nextInt(7);
            if (selection >= 4) { // 4..6 for basic platform
                p = new PlatformBasic(context, 0, y, pixelsPerMeterX);
            } else if (selection >= 2) { // 2..3 for breakable platform
                p = new PlatformBreakable(context, 0, y, pixelsPerMeterX);
            } else { // 0..1 for moving platform
                WorldPoint waypointLeft = new WorldPoint(boundaryLeft + padding, y, 0);
                WorldPoint waypointRight = new WorldPoint(boundaryRight - padding, y, 0);
                p = new PlatformMoving(context, waypointLeft, waypointRight, pixelsPerMeterX);
            }

            // Decide the X position of the platform
            // Consider padding and the width of the platform
            int min = boundaryLeft + padding;
            int max = (int) (boundaryRight - padding - p.getWidth());
            int randX = rand.nextInt((max - min) + 1) + min;

            // Reposition the platform
            p.setLocationX(randX);
            p.setHitBox();

            gameObjects.add(p);

            // Add a carrying object, exclude breakable platforms
            if (p.getType() != ObjectType.PLATFORM_BREAKABLE && rand.nextInt(4) == 0) {

                // Decide the object to add
                GameObject carryingObject;
                int objectSelection = rand.nextInt(6);
                if (objectSelection >= 4) { // 4 for spring
                    carryingObject = new Spring(context, 0, 0, pixelsPerMeterX);
                } else if (objectSelection >= 1) { // 1..3 for slime
                    carryingObject = new Slime(context, 0, 0, pixelsPerMeterX);
                } else { // 0 for shield
                    carryingObject = new Shield(context, 0, 0, pixelsPerMeterX);
                }
                p.setCarrying(carryingObject);
                gameObjects.add(carryingObject);
            }

        }

        // Remove objects that are more than one screen below player
        CopyOnWriteArrayList<GameObject> objectsForRemoval = new CopyOnWriteArrayList<>();
        for (GameObject object : gameObjects) {
            if (object.getLocation().y > playerY + metersShownY)
                objectsForRemoval.add(object);
        }
        gameObjects.removeAll(objectsForRemoval);

        // Remove bullets out of view
        CopyOnWriteArrayList<GameObject> bulletsForRemoval = new CopyOnWriteArrayList<>();
        for (GameObject object : gameObjects) {
            if (object.getType() == ObjectType.BULLET && !viewport.inView(object.getLocation().x, object.getLocation().y, object.getWidth(), object.getWidth()))
                bulletsForRemoval.add(object);
        }
        gameObjects.removeAll(bulletsForRemoval);

    }

    void addBullet(WorldPoint waypoint) {

        // Start bullet above the center of the player
        float startX = player.getLocation().x;
        float startY = player.getLocation().y + (player.getWidth() / 2);
        Bullet b = new Bullet(context, startX, startY, pixelsPerMeterX, waypoint);
        gameObjects.add(b);
    }

    CopyOnWriteArrayList<GameObject> getGameObjects() {
        return gameObjects;
    }

    float getGravity() {
        return gravity;
    }

    boolean isPlaying() {
        return playing;
    }

    void setPlaying(boolean playing) {
        this.playing = playing;
    }

    Player getPlayer() {
        return this.player;
    }

    int getScore() {
        return (int) (startingY - highestY);
    }

    float getHighestY() {
        return highestY;
    }
}
