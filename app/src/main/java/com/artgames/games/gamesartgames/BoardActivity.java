package com.artgames.games.gamesartgames;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class BoardActivity extends AppCompatActivity implements View.OnClickListener{

    private GridLayout _mainGridLayout;
    private boolean _currentPlayer = true; // true => X, false => O

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        MobileAds.initialize(this, "ca-app-pub-8402023979328526~3171238260");

        AdView adViewTop = findViewById(R.id.adGameViewBottom);
        AdRequest requestTop = new AdRequest.Builder().build();
        adViewTop.loadAd(requestTop);

        _mainGridLayout = findViewById(R.id.mainGridLayout);

        SetupTextViews();
    }

    private void SetupTextViews() {
        int length = _mainGridLayout.getChildCount();
        for (int index = 0; index < length; index++){
            TextView child = (TextView)_mainGridLayout.getChildAt(index);
            child.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {

        

        _currentPlayer = !_currentPlayer;
    }
}
