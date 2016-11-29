package com.thomas.chess.activities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thomas.chess.R;
import com.thomas.chess.game.Game;
import com.thomas.chess.game.Move;
import com.thomas.chess.game.Square;
import com.thomas.chess.game.Utils;
import com.thomas.chess.overrides.HistoryDialog;
import com.thomas.chess.overrides.PromotionDialog;
import com.thomas.chess.overrides.SquareView;
import com.thomas.chess.pieces.Piece;

import java.util.ArrayList;

public class GameActivity extends Activity {

    private LinearLayout mGameLayout;
    private Game mGame;
    private SquareView[][] mSquareViews;
    private SquareView mSelectedSquareView;

    private TextView mGameStatus;

    private ArrayList<ImageView> mBlackDeadPieces;
    private ArrayList<ImageView> mWhiteDeadPieces;

    private ArrayList<Move> mPossibleMoves = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        Bundle extras = getIntent().getExtras();
        int gameType = extras.getInt(Utils.INTENT_GAME_TYPE);

        mGameLayout = (LinearLayout) findViewById(R.id.game_layout);
        mGameStatus = (TextView) findViewById(R.id.game_status);
        initializeGame(gameType);
    }

    private void initializeGame(int gameType) {
        Log.d("tag", "" + gameType);
        mGame = new Game(gameType);
        setUpPlayerContainers();
        setUpBoardViews();
        setUpButtons();
    }

    private void setUpPlayerContainers() {
        TextView blackName = (TextView) findViewById(R.id.player_pieces_header_black);
        TextView whiteName = (TextView) findViewById(R.id.player_pieces_header_white);

        whiteName.setText(mGame.getWhitePLayer().getName());
        blackName.setText(mGame.getBlackPlayer().getName());

        LinearLayout blackContainer = (LinearLayout) findViewById(R.id.player_pieces_container_black);
        LinearLayout whiteContainer = (LinearLayout) findViewById(R.id.player_pieces_container_white);

        mBlackDeadPieces = new ArrayList<>();
        mWhiteDeadPieces = new ArrayList<>();
        for (int i = 0; i < blackContainer.getChildCount(); i++) {
            mBlackDeadPieces.add((ImageView) blackContainer.getChildAt(i));
            mWhiteDeadPieces.add((ImageView) whiteContainer.getChildAt(i));
        }
    }

    private void setUpBoardViews() {
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

    private void setUpButtons() {
        Button showHistory = (Button) findViewById(R.id.game_show_history);
        showHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryDialog dialog = new HistoryDialog(GameActivity.this);
                dialog.setMoves(mGame.getMoves());
                dialog.show();
            }
        });
    }

    public void updateGameView(boolean hasMoved) {
        for (SquareView[] line : mSquareViews) {
            for (SquareView squareView : line) {
                squareView.update();
            }
        }

        if (hasMoved) {
            if (getGame().isCheckmate()) {
                mGameStatus.setText("Checkmate");
            } else if (getGame().isStalemate()) {
                mGameStatus.setText("Stalemate");
            } else if (getGame().isDraw()) {
                mGameStatus.setText("Draw");
            } else if (getGame().isCheck()) {
                mGameStatus.setText("Check");
            } else {
                mGameStatus.setText("");
            }
            updatePlayerContainers();
        }
    }

    private void updatePlayerContainers() {
        ArrayList<Piece> whiteDeadPieces = mGame.getWhitePLayer().getDeadPieces();
        for (int i = 0; i < whiteDeadPieces.size(); i++) {
            Utils.setImageViewForPiece(mWhiteDeadPieces.get(i), whiteDeadPieces.get(i));
        }
        for (int i = whiteDeadPieces.size(); i < mWhiteDeadPieces.size(); i++) {
            mWhiteDeadPieces.get(i).setImageResource(android.R.color.transparent);
        }

        ArrayList<Piece> blackDeadPieces = mGame.getBlackPlayer().getDeadPieces();
        for (int i = 0; i < blackDeadPieces.size(); i++) {
            Utils.setImageViewForPiece(mBlackDeadPieces.get(i), blackDeadPieces.get(i));
        }
        for (int i = blackDeadPieces.size(); i < mBlackDeadPieces.size(); i++) {
            mBlackDeadPieces.get(i).setImageResource(android.R.color.transparent);
        }
    }

    public void clearSelection() {
        mSelectedSquareView = null;
        mPossibleMoves.clear();
    }

    public void choosePromotionPiece(Move move) {
        PromotionDialog dialog = new PromotionDialog(this);
        dialog.setMove(move);
        dialog.show();
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
