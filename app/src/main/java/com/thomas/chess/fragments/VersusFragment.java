package com.thomas.chess.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.thomas.chess.R;
import com.thomas.chess.activities.GameActivity;
import com.thomas.chess.game.Game;
import com.thomas.chess.game.p_children.RealPlayer;
import com.thomas.chess.views.HistoryDialog;

public class VersusFragment extends Fragment {

    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.versus_fragment, container, false);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeButtons();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initializeButtons() {
        final GameActivity gameActivity = (GameActivity) getActivity();
        final Game game = gameActivity.getGame();
        Button showHistory = (Button) mView.findViewById(R.id.game_show_history);
        showHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryDialog dialog = new HistoryDialog(gameActivity);
                dialog.setMoves(game.getMoves(), game.getMoveCount());
                dialog.show();
            }
        });

        Button undo = (Button) mView.findViewById(R.id.game_undo);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (game.getCurrentPlayer() instanceof RealPlayer) {
                    game.cancelMove();
                    gameActivity.clearSelection();
                    gameActivity.updateGameView();
                }
            }
        });

        Button forceDraw = (Button) mView.findViewById(R.id.game_force_draw);
        forceDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.claimDraw();
            }
        });

        Button surrender = (Button) mView.findViewById(R.id.game_surrender);
        surrender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.surrender();
            }
        });
    }
}
