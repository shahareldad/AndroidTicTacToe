package com.artgames.games.gamesartgames;

/**
 * Created by shaharel on 12/03/2018.
 */

public enum Seed {
    EMPTY(0),
    CROSS(1),
    NOUGHT(2);

    private int _seed;

    Seed(int seed){
        _seed = seed;
    }

    public void setSeed(int seed) {
        _seed = seed;
    }

    public int getSeed() {
        return _seed;
    }
}
