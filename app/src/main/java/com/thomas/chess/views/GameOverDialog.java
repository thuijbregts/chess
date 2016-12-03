package com.thomas.chess.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.thomas.chess.R;
import com.thomas.chess.activities.GameActivity;
import com.thomas.chess.game.Move;

import java.util.ArrayList;
import java.util.List;


public class GameOverDialog extends Dialog {

    private GameActivity mGameActivity;

    public GameOverDialog(GameActivity gameActivity) {
        super(gameActivity);
        mGameActivity = gameActivity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.history_dialog);
        setCancelable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
