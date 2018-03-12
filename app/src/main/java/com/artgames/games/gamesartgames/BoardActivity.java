package com.artgames.games.gamesartgames;

import android.content.DialogInterface;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
    private int[][] _gameBoard = new int[3][3]; // 0 (zero) = cell empty, 1 = X, 2 = O (letter 'O')
    private boolean _againstComputer = false;
    private int _screenWidth;
    private int _cellSide;
    private AppCompatActivity _activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        MobileAds.initialize(this, "ca-app-pub-8402023979328526~3171238260");

        AdView adViewTop = findViewById(R.id.adGameViewBottom);
        AdRequest requestTop = new AdRequest.Builder().build();
        adViewTop.loadAd(requestTop);

        _activity = this;

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        _screenWidth = size.x;
        _cellSide = _screenWidth / 9;

        _againstComputer = getIntent().getIntExtra(MainActivity.AgainstComputerParamName, 1) == 1 ? false : true;
        _mainGridLayout = findViewById(R.id.mainGridLayout);
        setupGame();
    }

    private void setupGame() {
        _currentPlayer = true;
        _movesCounter = 9;
        setupGameBoard();
        setupTextViews();
    }

    private void setupGameBoard() {
        for(int index = 0; index < 3; index++){
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
        }
    }

    @Override
    public void onClick(View view) {

        MakeMove(view);

        boolean isWinState = checkWinState();
        if (!isWinState){
            if (_movesCounter == 0){
                _movesCounter = 9;
                showTieDialog();
                return;
            }

            if (_againstComputer){
                //TODO: if computer then make next move
                Toast.makeText(_activity, "Not impl. yet", Toast.LENGTH_LONG).show();
            }else{
                return;
            }
        }
        else{
            showWinLossDialog();
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

    private void MakeMove(View view) {
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
        _gameBoard[row][col] = _currentPlayer ? 1 :2;

        _currentPlayer = !_currentPlayer;
    }

    private boolean checkWinState() {
        for (int index = 0; index < 3; index++){
            if (_gameBoard[0][index] != 0 && _gameBoard[0][index] == _gameBoard[1][index] && _gameBoard[0][index] == _gameBoard[2][index]){
                return true;
            }
            if (_gameBoard[index][0] != 0 && _gameBoard[index][0] == _gameBoard[index][1] && _gameBoard[index][0] == _gameBoard[index][2]){
                return true;
            }
        }
        if (_gameBoard[0][0] != 0 && _gameBoard[0][0] == _gameBoard[1][1] && _gameBoard[0][0] == _gameBoard[2][2]){
            return true;
        }
        if (_gameBoard[0][2] != 0 && _gameBoard[0][2] == _gameBoard[1][1] && _gameBoard[0][2] == _gameBoard[2][0]){
            return true;
        }
        return false;
    }
}
