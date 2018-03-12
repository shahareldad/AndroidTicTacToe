package com.artgames.games.gamesartgames;

/**
 * Created by shaharel on 12/03/2018.
 * Code for this class was taken from:
 * https://www.ntu.edu.sg/home/ehchua/programming/java/JavaGame_TicTacToe_AI.html
 */

public abstract class AIPlayer {
    protected int ROWS = GameMain.ROWS;  // number of rows
    protected int COLS = GameMain.COLS;  // number of columns

    protected int[][] _board; // the board's ROWS-by-COLS array of Cells
    protected Seed _mySeed;    // computer's seed
    protected Seed _oppSeed;   // opponent's seed

    /** Constructor with reference to game board */
    public AIPlayer(int[][] board) {
        _board = board;
    }

    /** Set/change the seed used by computer and opponent */
    public void setSeed(Seed seed) {
        this._mySeed = seed;
        _oppSeed = (_mySeed == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
    }

    /** Abstract method to get next move. Return int[2] of {row, col} */
    abstract int[] move();  // to be implemented by subclasses
}
