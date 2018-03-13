package com.artgames.games.gamesartgames;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public static String AgainstComputerParamName = "AgainstComputer";
    public static String ComputerPlayerLevel = "ComputerPlayerLevel";
    public static String PlaySounds = "PlayClickSound";
    public static String ConfirmExitGame = "ConfirmExitGame";
    public static String SETTINGS_FILENAME = "SUDOKU_SETTINGS";
    private SettingsData _settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-8402023979328526~6177870434");

        AdView adViewTop = findViewById(R.id.adViewTop);
        AdRequest requestTop = new AdRequest.Builder().build();
        adViewTop.loadAd(requestTop);

        AdView adViewBottom = findViewById(R.id.adViewBottom);
        AdRequest requestBottom = new AdRequest.Builder().build();
        adViewBottom.loadAd(requestBottom);

        Button humanBtn = findViewById(R.id.humanPlayer);
        Button easyComputerBtn = findViewById(R.id.easyComputerPlayer);
        Button hardComputerBtn = findViewById(R.id.hardComputerPlayer);
        Button gameSettings = findViewById(R.id.gameSettings);

        humanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewGameByLevel(false, 1);
            }
        });

        easyComputerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewGameByLevel(true, 1);
            }
        });

        hardComputerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewGameByLevel(true, 2);
            }
        });

        LoadSettingsData();
        final boolean[] checked = new boolean[3];
        checked[0] = _settings.getPlaySound();
        checked[1] = _settings.getConfirmExit();
        final SettingsData settings = new SettingsData();
        settings.setPlaySound(_settings.getPlaySound());
        settings.setConfirmExit(_settings.getConfirmExit());
        final Context context = this;
        gameSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.settings);
                final String[] items = new String[]{
                        getString(R.string.playSounds),
                        getString(R.string.confirmOnExit)
                };
                builder.setMultiChoiceItems(items, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int indexSelected, boolean isChecked) {
                        UpdateSettingsData(indexSelected, isChecked, items, settings);
                    }
                });
                builder.setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SaveSettingsData(settings);
                        _settings.setPlaySound(settings.getPlaySound());
                        _settings.setConfirmExit(settings.getConfirmExit());
                    }
                });
                builder.create().show();
            }
        });
    }

    private void LoadSettingsData() {
        BufferedReader br = null;
        StringBuilder builder = null;
        InputStream stream = null;

        try{
            stream = openFileInput(MainActivity.SETTINGS_FILENAME);
            br = new BufferedReader(new InputStreamReader(stream));
            builder = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null){
                builder.append(line);
            }
            stream.close();
        }
        catch (IOException ex){
        }
        finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        _settings = new SettingsData();
        _settings.setPlaySound(true);
        _settings.setConfirmExit(true);
        Gson gson = new GsonBuilder().create();
        if (builder != null) {
            _settings = gson.fromJson(builder.toString(), new TypeToken<SettingsData>() {}.getType());
        }
    }

    private void SaveSettingsData(SettingsData settings) {
        Gson gson = new GsonBuilder().create();
        String result = gson.toJson(settings);

        FileOutputStream fos = null;
        try{
            fos = openFileOutput(MainActivity.SETTINGS_FILENAME, Context.MODE_PRIVATE);
            fos.write(result.getBytes());
            fos.close();
        }
        catch (FileNotFoundException ex){
        }
        catch (IOException ex){
        }
        finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void UpdateSettingsData(int indexSelected, boolean isChecked, String[] items, SettingsData settings) {
        String selection = Arrays.asList(items).get(indexSelected);
        if (selection.equals(getString(R.string.playSounds))){
            settings.setPlaySound(isChecked);
        }
        if (selection.equals(getString(R.string.confirmOnExit))){
            settings.setConfirmExit(isChecked);
        }
    }

    private void startNewGameByLevel(boolean isAgainstComputer, int level) {
        Intent i = new Intent(MainActivity.this, BoardActivity.class);
        i.putExtra(AgainstComputerParamName, isAgainstComputer);
        i.putExtra(ComputerPlayerLevel, level);
        i.putExtra(PlaySounds, _settings.getPlaySound());
        i.putExtra(ConfirmExitGame, _settings.getConfirmExit());
        startActivity(i);
    }
}
