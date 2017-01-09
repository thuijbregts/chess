package com.thomas.chess.gui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.thomas.chess.R;
import com.thomas.chess.gui.activities.GameActivity;
import com.thomas.chess.gui.views.ScoreSheetDialog;

import com.thomas.chess.engine.chess120.*;

public class VersusFragment extends Fragment {

    private GameActivity mGameActivity;
    private Game mGame;
    private View mView;

    private Button mForceDrawButton;
    private Button mUndoButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.versus_fragment, container, false);
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

        mUndoButton = (Button) mView.findViewById(R.id.game_undo);
        mUndoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameActivity.unmakeMove();
            }
        });

        mForceDrawButton = (Button) mView.findViewById(R.id.game_force_draw);
        mForceDrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGame.canClaimDraw()) {
                    mGame.claimDraw();
                    updateButtons();
                    mGameActivity.showGameOverDialog();
                }
            }
        });

        Button surrender = (Button) mView.findViewById(R.id.game_surrender);
        surrender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGame.resign();
                updateButtons();
                mGameActivity.showGameOverDialog();
            }
        });
    }

    public void updateButtons() {
        mUndoButton.setEnabled(mGame.getMoveCount() != 0);
        mForceDrawButton.setEnabled(mGame.canClaimDraw());
    }
}
