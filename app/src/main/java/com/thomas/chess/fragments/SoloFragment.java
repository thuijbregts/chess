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
import com.thomas.chess.views.ScoreSheetDialog;

public class SoloFragment extends Fragment {

    private GameActivity mGameActivity;
    private Game mGame;
    private View mView;

    private Button mForceDrawButton;
    private Button mUndoButton;
    private Button mSurrenderButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.solo_fragment, container, false);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mGameActivity = (GameActivity) getActivity();
        mGame = mGameActivity.getGame();
        initializeButtons();
        updateButtons();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initializeButtons() {
        Button scoreSheet = (Button) mView.findViewById(R.id.game_show_score_sheet);
        scoreSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScoreSheetDialog dialog = new ScoreSheetDialog(mGameActivity);
                dialog.setMoves(mGame.getMoves(), mGame.getMoveCount());
                dialog.show();
            }
        });

        mForceDrawButton = (Button) mView.findViewById(R.id.game_force_draw);
        mForceDrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGame.getCurrentPlayer().canClaimDraw()) {
                    mGame.getCurrentPlayer().claimDraw();
                    updateButtons();
                    mGameActivity.showGameOverDialog();
                }
            }
        });

        mUndoButton = (Button) mView.findViewById(R.id.game_undo);
        mUndoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGame.getCurrentPlayer() instanceof RealPlayer) {
                    mGame.cancelMove();
                    mGame.cancelMove();
                    mGameActivity.clearSelection();
                    mGameActivity.updateGameView();
                } else if (mGame.isGameOver()) {
                    mGame.cancelMove();
                    mGameActivity.updateGameView();
                }
            }
        });

        mSurrenderButton = (Button) mView.findViewById(R.id.game_surrender);
        mSurrenderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGame.getCurrentPlayer().resign();
                updateButtons();
                mGameActivity.showGameOverDialog();
            }
        });
    }

    public void updateButtons() {
        mUndoButton.setEnabled(mGame.getMoveCount() != 0);
        mForceDrawButton.setEnabled((mGame.getCurrentPlayer() instanceof RealPlayer)
                && mGame.getCurrentPlayer().canClaimDraw());
        mSurrenderButton.setEnabled(mGame.getCurrentPlayer() instanceof RealPlayer);
    }
}
