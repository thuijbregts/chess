package com.thomas.chess.gui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.thomas.chess.R;
import com.thomas.chess.gui.utils.Utils;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeMenuButtons();
    }

    private void initializeMenuButtons() {
        Button playSolo = (Button) findViewById(R.id.play_solo);
        playSolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra(Utils.INTENT_GAME_TYPE, Utils.GAME_SOLO);
                startActivity(intent);
            }
        });

        Button playTwoPlayers = (Button) findViewById(R.id.play_two_players);
        playTwoPlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra(Utils.INTENT_GAME_TYPE, Utils.GAME_VERSUS);
                startActivity(intent);
            }
        });

        Button playOnline = (Button) findViewById(R.id.play_online);
        playOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra(Utils.INTENT_GAME_TYPE, Utils.GAME_ONLINE);
                //TODO connection activity
                startActivity(intent);
            }
        });
    }
}
