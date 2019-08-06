package edu.utep.cs5381.tabbyjump;

/** A normal difficulty level. */
class LevelNormal extends LevelData{
    LevelNormal() {
        gravity = 1.0f;
        platformTypes = new ObjectType[] {
                ObjectType.PLATFORM_BASIC,
                ObjectType.PLATFORM_BREAKABLE
        };
    }
}


