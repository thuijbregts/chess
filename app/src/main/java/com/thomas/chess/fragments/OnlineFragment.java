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

public class OnlineFragment extends Fragment {

    private GameActivity mGameActivity;
    private Game mGame;
    private View mView;

    private Button mForceDrawButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.online_fragment, container, false);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mGameActivity = (GameActivity) getActivity();
        mGame = mGameActivity.getGame();
        initializeButtons();
        //updateButtons();
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
    }

    //TODO code for online
    public void updateButtons() {
        mForceDrawButton.setEnabled(mGame.getCurrentPlayer().canClaimDraw());
    }
}
