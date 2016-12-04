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
import com.thomas.chess.game.Draw;
import com.thomas.chess.game.Move;

import java.util.ArrayList;
import java.util.List;


public class GameOverDialog extends Dialog {

    private GameActivity mGameActivity;
    private Move mLastMove;

    public GameOverDialog(GameActivity gameActivity, Move lastMove) {
        super(gameActivity);
        mGameActivity = gameActivity;
        mLastMove = lastMove;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game_over_dialog);
        setCancelable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeComponents();
    }


    private void initializeComponents() {
        TextView status = (TextView) findViewById(R.id.game_over_status);
        status.setText(getLastMoveStatus());
        TextView detail = (TextView) findViewById(R.id.game_over_detail);
        detail.setText(getLastMoveDetail());
    }

    private String getLastMoveStatus() {
        if (mLastMove.isCheckmate() || mLastMove.isResign()) {
            return mGameActivity.getResources().getString(R.string.game_over);
        }
        return mGameActivity.getResources().getString(R.string.game_draw);
    }

    private String getLastMoveDetail() {
        if (mLastMove.getDraw() != null) {
            switch (mLastMove.getDraw().getDrawType()) {
                case Draw.AGREEMENT:
                    return mGameActivity.getResources().getString(R.string.game_agreement);
                case Draw.FIFTY_MOVES:
                    return mGameActivity.getResources().getString(R.string.game_fifty_move);
                case Draw.SEVENTY_FIVE_MOVES:
                    return mGameActivity.getResources().getString(R.string.game_seventy_five);
                case Draw.NO_CHECKMATE:
                    return mGameActivity.getResources().getString(R.string.game_no_checkmate);
                case Draw.STALEMATE:
                    return mGameActivity.getResources().getString(R.string.game_stalemate);
                case Draw.THREEFOLD:
                    return mGameActivity.getResources().getString(R.string.game_threefold);
                default:
                    return "Reason unknown";
            }
        } else {
            if (mLastMove.isCheckmate()) {
                return mGameActivity.getResources().getString(R.string.game_checkmate);
            } else {
                return mGameActivity.getResources().getString(R.string.game_resign);
            }
        }
    }
}
