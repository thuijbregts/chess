package com.thomas.chess.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.thomas.chess.R;
import com.thomas.chess.game.Game;
import com.thomas.chess.game.Move;
import com.thomas.chess.game.Square;
import com.thomas.chess.game.Utils;
import com.thomas.chess.overrides.SquareView;

import java.util.ArrayList;

public class GameActivity extends Activity {

    private LinearLayout mGameLayout;
    private Game mGame;
    private SquareView[][] mSquareViews;
    private SquareView mSelectedSquareView;

    private ArrayList<Move> mPossibleMoves = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        mGameLayout = (LinearLayout) findViewById(R.id.game_layout);
        initializeGame();
    }

    private void initializeGame() {
        mGame = new Game(Utils.GAME_SOLO);
        setUpViews();
    }

    private void setUpViews() {
        mSquareViews = new SquareView[8][8];

        LinearLayout rowLayout;
        SquareView squareView;
        for (int i = Utils.ROWS-1; i >= 0; i--) {
            rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            layoutParams.weight = 1;
            rowLayout.setLayoutParams(layoutParams);
            for (int j = 0; j < Utils.COLUMNS; j++) {
                squareView = new SquareView(this);
                final LinearLayout.LayoutParams squareParams
                        = new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.MATCH_PARENT);
                squareParams.weight = 1;
                squareView.setLayoutParams(squareParams);

                squareView.setRow(i);
                squareView.setColumn(j);
                squareView.update();
                mSquareViews[i][j] = squareView;

                rowLayout.addView(squareView);
            }
            mGameLayout.addView(rowLayout);
        }
    }

    public void updateBoardView() {
        for (SquareView[] line : mSquareViews) {
            for (SquareView squareView : line) {
                squareView.update();
            }
        }
    }

    public void clearSelection() {
        mSelectedSquareView = null;
        mPossibleMoves.clear();
    }

    public Game getGame() {
        return mGame;
    }

    public SquareView[][] getSquareViews() {
        return mSquareViews;
    }

    public ArrayList<Move> getPossibleMoves() {
        return mPossibleMoves;
    }

    public void setPossibleMoves(ArrayList<Move> possibleMoves) {
        mPossibleMoves = possibleMoves;
    }

    public SquareView getSelectedSquareView() {
        return mSelectedSquareView;
    }

    public void setSelectedSquareView(SquareView selectedSquareView) {
        mSelectedSquareView = selectedSquareView;
    }

    public Move getMoveForSquare(Square square) {
        for (Move move : mPossibleMoves) {
            if (move.getDestinationSquare().equals(square)) {
                return move;
            }
        }
        return null;
    }
}
