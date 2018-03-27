package com.artgames.games.gamesartgames;

import android.content.DialogInterface;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.Arrays;

public class BoardActivity extends AppCompatActivity implements View.OnClickListener, RewardedVideoAdListener {

    private static final int DEBUG_MODE = 0;
    private String TAG = "BoardActivity";

    private static int _videoAdCounter = 10;
    private int _movesCounter = 9;
    private GridLayout _mainGridLayout;
    private boolean _currentPlayer = true; // true => X, false => O
    private int[][] _gameBoard = new int[GameMain.ROWS][GameMain.COLS]; // 0 (zero) = cell empty, 1 = X, 2 = O (letter 'O')
    private TextView[][] _textViews = new TextView[GameMain.ROWS][GameMain.COLS];
    private boolean _againstComputer = false;
    private int _cellSide;
    private int _level = 1;
    private TextView _XPlayerScore;
    private TextView _OPlayerScore;
    private boolean _playSound;
    private boolean _confirmExit;
    private RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        MobileAds.initialize(this, "ca-app-pub-8402023979328526~6177870434");

        AdView adViewTop = findViewById(R.id.adGameViewBottom);
        AdRequest requestTop = new AdRequest.Builder().build();
        adViewTop.loadAd(requestTop);

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int _screenWidth = size.x;
        _cellSide = _screenWidth / 9;

        _playSound = getIntent().getBooleanExtra(MainActivity.PlaySounds, true);
        _confirmExit = getIntent().getBooleanExtra(MainActivity.ConfirmExitGame, true);
        _againstComputer = getIntent().getBooleanExtra(MainActivity.AgainstComputerParamName, false);
        _level = getIntent().getIntExtra(MainActivity.ComputerPlayerLevel, 1);
        _mainGridLayout = findViewById(R.id.mainGridLayout);
        setupGame();
        resetScore();

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

        if (_playSound){
            MediaPlayer ring= MediaPlayer.create(BoardActivity.this, R.raw.click_sound);
            ring.start();
            try{
                Thread.sleep(100);
            }catch (InterruptedException ex){
                Log.e(TAG, ex.getMessage());
            }
        }

        TextView cell = (TextView)view;
        String textViewText = String.valueOf(cell.getText());
        if (!textViewText.equals(""))
            return;

        makeMove(view);

        Seed seed = checkWinState();
        if (seed == Seed.EMPTY){
            if (_movesCounter == 0){
                _movesCounter = 9;
                showTieDialog();
                return;
            }

            if (_againstComputer){
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        makeComputerMove();
                    }
                }, 100);
            }else{
                return;
            }
        }
        else{
            showWinLossDialog();
        }
    }

    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }

    @Override
    protected void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }

    private void ShowVideoAd() {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }else{
            loadRewardedVideoAd();
            if (mRewardedVideoAd.isLoaded()) {
                mRewardedVideoAd.show();
            }
        }
    }

    private void loadRewardedVideoAd() {
        if (DEBUG_MODE == 1){
            mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                    new AdRequest.Builder().build());
        }else{
            mRewardedVideoAd.loadAd("ca-app-pub-8402023979328526/7221119671",
                    new AdRequest.Builder().build());
        }
    }

    private void showExitConfirmationDialog() {
        if (!_confirmExit) {
            super.onBackPressed();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.yesOption, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.noOption, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setTitle(R.string.exitConfirm);
        builder.create().show();
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

    private void resetScore() {
        if (_XPlayerScore == null)
            _XPlayerScore = findViewById(R.id.XPlayerScore);
        _XPlayerScore.setText("0");
        if (_OPlayerScore == null)
            _OPlayerScore = findViewById(R.id.OPlayerScore);
        _OPlayerScore.setText("0");
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
        updateScoreForWinner();

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

        if (_videoAdCounter == 0){
            _videoAdCounter = 10;
            ShowVideoAd();
        }
        else{
            _videoAdCounter--;
        }
    }

    private void updateScoreForWinner() {
        // remark: in method make move the last step is to reverse the
        // current player so here we make a reverse check
        TextView scoreTextToUpdate = !_currentPlayer ? _XPlayerScore : _OPlayerScore;
        int currentScore = Integer.valueOf(String.valueOf(scoreTextToUpdate.getText())) + 1;
        scoreTextToUpdate.setText(String.valueOf(currentScore));
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

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        loadRewardedVideoAd();
    }
}
