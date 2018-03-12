package com.artgames.games.gamesartgames;

import android.content.DialogInterface;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Arrays;

public class BoardActivity extends AppCompatActivity implements View.OnClickListener{

    private int _movesCounter = 9;
    private GridLayout _mainGridLayout;
    private boolean _currentPlayer = true; // true => X, false => O
    private int[][] _gameBoard = new int[GameMain.ROWS][GameMain.COLS]; // 0 (zero) = cell empty, 1 = X, 2 = O (letter 'O')
    private TextView[][] _textViews = new TextView[GameMain.ROWS][GameMain.COLS];
    private boolean _againstComputer = false;
    private int _screenWidth;
    private int _cellSide;
    private int _level = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        MobileAds.initialize(this, "ca-app-pub-8402023979328526~3171238260");

        AdView adViewTop = findViewById(R.id.adGameViewBottom);
        AdRequest requestTop = new AdRequest.Builder().build();
        adViewTop.loadAd(requestTop);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        _screenWidth = size.x;
        _cellSide = _screenWidth / 9;

        _againstComputer = getIntent().getBooleanExtra(MainActivity.AgainstComputerParamName, false);
        _level = getIntent().getIntExtra(MainActivity.ComputerPlayerLevel, 1);
        _mainGridLayout = findViewById(R.id.mainGridLayout);
        setupGame();

        Button newGameButton = findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupGame();
            }
        });
    }

    @Override
    public void onClick(View view) {

        makeMove(view);

        Seed seed = checkWinState();
        if (seed == Seed.EMPTY){
            if (_movesCounter == 0){
                _movesCounter = 9;
                showTieDialog();
                return;
            }

            if (_againstComputer){
                makeComputerMove();
            }else{
                return;
            }
        }
        else{
            showWinLossDialog();
        }
    }

    private void makeComputerMove() {
        int[][] boardCopy = deepCopy(_gameBoard);
        AIPlayer player = null;
        if (_level == 1){
            player = new AIPlayerTableLookup(boardCopy);
        }else if (_level == 2){
            player = new AIPlayerMinimax(boardCopy);
        }
        player.setSeed(_currentPlayer ? Seed.CROSS : Seed.NOUGHT);
        int[] move = player.move();
        TextView cell = _textViews[move[0]][move[1]];
        makeMove(cell);

        Seed seed = checkWinState();
        if (seed == Seed.EMPTY){
            if (_movesCounter == 0){
                _movesCounter = 9;
                showTieDialog();
                return;
            }
        }
        else{
            showWinLossDialog();
        }
    }

    private void setupGame() {
        _currentPlayer = true;
        _movesCounter = 9;
        setupGameBoard();
        setupTextViews();
    }

    private void setupGameBoard() {
        for(int index = 0; index < GameMain.ROWS; index++){
            _gameBoard[0][index] = 0;
            _gameBoard[1][index] = 0;
            _gameBoard[2][index] = 0;
        }
    }

    private void setupTextViews() {
        int length = _mainGridLayout.getChildCount();
        for (int index = 0; index < length; index++){
            TextView child = (TextView)_mainGridLayout.getChildAt(index);
            if (!child.hasOnClickListeners()) {
                child.setOnClickListener(this);
                child.setWidth(_cellSide);
                child.setHeight(_cellSide);
            }
            child.setText("");

            String tag = String.valueOf(child.getTag());
            int row = Integer.valueOf(String.valueOf(tag.charAt(0)));
            int col = Integer.valueOf(String.valueOf(tag.charAt(1)));
            _textViews[row][col] = child;
        }
    }

    private void showWinLossDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        _currentPlayer = !_currentPlayer;
        String winningPlayer = _currentPlayer ? "X" : "O";
        String title = getString(R.string.playerWon).replace("[PLAYERNAME]", winningPlayer);
        builder.setTitle(title);
        final String[] items = new String[]{
                getString(R.string.newGame),
                getString(R.string.backButton)
        };
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selection = Arrays.asList(items).get(i);

                if (selection.equals(getString(R.string.newGame))){
                    setupGame();
                }
                if (selection.equals(getString(R.string.backButton))){
                    finish();
                }
            }
        });

        builder.create().show();
    }

    private void showTieDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.gameOver);
        final String[] items = new String[]{
                getString(R.string.newGame),
                getString(R.string.backButton)
        };
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selection = Arrays.asList(items).get(i);

                if (selection.equals(getString(R.string.newGame))){
                    setupGame();
                }
                if (selection.equals(getString(R.string.backButton))){
                    finish();
                }
            }
        });

        builder.create().show();
    }

    private void makeMove(View view) {
        _movesCounter--;
        TextView currentTextView = (TextView)view;
        if (_currentPlayer){
            currentTextView.setText("X");
        }else{
            currentTextView.setText("O");
        }

        String tag = String.valueOf(view.getTag());
        int row = Integer.valueOf(String.valueOf(tag.charAt(0)));
        int col = Integer.valueOf(String.valueOf(tag.charAt(1)));
        _gameBoard[row][col] = _currentPlayer ? 1 : 2;

        _currentPlayer = !_currentPlayer;
    }

    private Seed checkWinState() {
        for (int index = 0; index < GameMain.ROWS; index++){
            if (_gameBoard[0][index] != 0 && _gameBoard[0][index] == _gameBoard[1][index] && _gameBoard[0][index] == _gameBoard[2][index]){
                return _gameBoard[0][index] == 1 ? Seed.CROSS : Seed.NOUGHT;
            }
            if (_gameBoard[index][0] != 0 && _gameBoard[index][0] == _gameBoard[index][1] && _gameBoard[index][0] == _gameBoard[index][2]){
                return _gameBoard[index][0] == 1 ? Seed.CROSS : Seed.NOUGHT;
            }
        }
        if (_gameBoard[0][0] != 0 && _gameBoard[0][0] == _gameBoard[1][1] && _gameBoard[0][0] == _gameBoard[2][2]){
            return _gameBoard[0][0] == 1 ? Seed.CROSS : Seed.NOUGHT;
        }
        if (_gameBoard[0][2] != 0 && _gameBoard[0][2] == _gameBoard[1][1] && _gameBoard[0][2] == _gameBoard[2][0]){
            return _gameBoard[0][2] == 1 ? Seed.CROSS : Seed.NOUGHT;
        }
        return Seed.EMPTY;
    }

    public static int[][] deepCopy(int[][] original) {

        if (original == null) {
            return null;
        }

        final int[][] result = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return result;
    }
}
