package com.artgames.games.gamesartgames;

/**
 * Created by shaharel on 12/03/2018.
 * Code for this class was taken from:
 * https://www.ntu.edu.sg/home/ehchua/programming/java/JavaGame_TicTacToe_AI.html
 */

public class AIPlayerTableLookup extends AIPlayer {
    // Moves {row, col} in order of preferences. {0, 0} at top-left corner
    private int[][] preferredMoves = {
            {1, 1}, {0, 0}, {0, 2}, {2, 0}, {2, 2},
            {0, 1}, {1, 0}, {1, 2}, {2, 1}};

    /** constructor */
    public AIPlayerTableLookup(int[][] board) {
        super(board);
    }

    /** Search for the first empty cell, according to the preferences
     *  Assume that next move is available, i.e., not gameover
     *  @return int[2] of {row, col}
     */
    @Override
    public int[] move() {
        int[] blockMove = checkSecondPlayerNextMoveWin(BoardActivity.deepCopy(_board));
        if (blockMove != null){
            return blockMove;
        }

        for (int[] move : preferredMoves) {
            if (_board[move[0]][move[1]] == Seed.EMPTY.getSeed()) {
                return move;
            }
        }
        return null;
    }

    private int[] checkSecondPlayerNextMoveWin(int[][] boardCopy) {
        int length = GameMain.COLS * GameMain.ROWS;
        for (int index = 0; index < length; index++){
            int row = 1 * index / 3;
            int col = index % 3;
            if (boardCopy[row][col] != Seed.EMPTY.getSeed())
                continue;
            boardCopy[row][col] = _oppSeed.getSeed();
            Seed result = checkWinState(boardCopy);
            if (result == Seed.EMPTY){
                boardCopy[row][col] = Seed.EMPTY.getSeed();
                continue;
            }
            return new int[] { row, col };
        }

        return null;
    }

    private Seed checkWinState(int[][] boardCopy) {
        for (int index = 0; index < GameMain.ROWS; index++){
            if (boardCopy[0][index] != 0 && boardCopy[0][index] == boardCopy[1][index] && boardCopy[0][index] == boardCopy[2][index]){
                return boardCopy[0][index] == 1 ? Seed.CROSS : Seed.NOUGHT;
            }
            if (boardCopy[index][0] != 0 && boardCopy[index][0] == boardCopy[index][1] && boardCopy[index][0] == boardCopy[index][2]){
                return boardCopy[index][0] == 1 ? Seed.CROSS : Seed.NOUGHT;
            }
        }
        if (boardCopy[0][0] != 0 && boardCopy[0][0] == boardCopy[1][1] && boardCopy[0][0] == boardCopy[2][2]){
            return boardCopy[0][0] == 1 ? Seed.CROSS : Seed.NOUGHT;
        }
        if (boardCopy[0][2] != 0 && boardCopy[0][2] == boardCopy[1][1] && boardCopy[0][2] == boardCopy[2][0]){
            return boardCopy[0][2] == 1 ? Seed.CROSS : Seed.NOUGHT;
        }
        return Seed.EMPTY;
    }

}
