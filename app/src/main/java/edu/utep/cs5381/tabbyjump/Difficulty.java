package edu.utep.cs5381.tabbyjump;

enum Difficulty {
    NORMAL(new LevelNormal());

    LevelData levelData;

    Difficulty(LevelData levelData) {
        this.levelData = levelData;
    }
}
