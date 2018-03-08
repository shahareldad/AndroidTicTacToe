package com.artgames.games.gamesartgames;

/**
 * Created by shaharel on 22/01/2018.
 */

public class SettingsData {
    private boolean PlaySound;
    private boolean ConfirmExit;

    public boolean getPlaySound() {
        return PlaySound;
    }

    public void setPlaySound(boolean playSound) {
        PlaySound = playSound;
    }

    public boolean getConfirmExit() {
        return ConfirmExit;
    }

    public void setConfirmExit(boolean confirmExit) {
        ConfirmExit = confirmExit;
    }
}
