/*
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

public class HistoryFragment extends Fragment {

    private GameActivity mGameActivity;
    private Game mGame;
    private View mView;

    private Button mUndoButton;
    private Button mRedoButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.history_fragment, container, false);
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
        Button mScoreSheetButton = (Button) mView.findViewById(R.id.game_show_score_sheet);
        mScoreSheetButton.setOnClickListener(new View.OnClickListener() {
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
                mGame.cancelMove();
                mGameActivity.clearSelection();
                mGameActivity.updateGameView();
            }
        });

        mRedoButton = (Button) mView.findViewById(R.id.game_redo);
        mRedoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGame.getMoves().size() > mGame.getMoveCount()) {
                    mGameActivity.executeMove(mGame.getMoves().get(mGame.getMoveCount()), false);
                }
            }
        });
    }
    
    public void updateButtons() {
        mUndoButton.setEnabled(mGame.getMoveCount() != 0);
        mRedoButton.setEnabled(mGame.getMoveCount() < mGame.getMoves().size());
    }
}
*/
