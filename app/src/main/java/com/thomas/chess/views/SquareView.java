package com.thomas.chess.views;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.thomas.chess.R;
import com.thomas.chess.activities.GameActivity;
import com.thomas.chess.game.Move;
import com.thomas.chess.game.Square;
import com.thomas.chess.game.Utils;
import com.thomas.chess.pieces.Piece;

import java.util.ArrayList;

public class SquareView extends ImageView implements View.OnClickListener {

    private GameActivity mGameActivity;
    private int mRow;
    private int mColumn;

    public SquareView(GameActivity gameActivity) {
        super(gameActivity);
        mGameActivity = gameActivity;
        setOnClickListener(this);
    }

    public void setRow(int row) {
        this.mRow = row;
    }

    public void setColumn(int column) {
        this.mColumn = column;
    }

    public int getRow() {
        return mRow;
    }

    public int getColumn() {
        return mColumn;
    }

    @Override
    public void onClick(View v) {
        if (!mGameActivity.getGame().isWaitForOpponent()) {
            boolean hasMoved = false;
            Square square = mGameActivity.getGame().getBoard().getSquares()[mRow][mColumn];
            if (!(square.isEmpty() && mGameActivity.getSelectedSquareView() == null)) {
                Move move = mGameActivity.getMoveForSquare(square);
                if (move != null) {
                    if (move.getMoveType() == Utils.MOVE_TYPE_PROMOTION) {
                        mGameActivity.choosePromotionPiece(move);
                    } else {
                        mGameActivity.getGame().executeMove(move);
                        mGameActivity.clearSelection();
                    }
                    hasMoved = true;
                } else {
                    checkSquareValidity(square);
                }
                mGameActivity.updateGameView(hasMoved);
            }
        }
    }

    private void checkSquareValidity(Square square) {
        if (!square.isEmpty() && square.getPiece().getColor() ==
                mGameActivity.getGame().getCurrentPlayer().getColor()) {

            ArrayList<Move> possibleMoves = mGameActivity.getMovesForSquare(square);
            if (possibleMoves == null) {
                mGameActivity.addPossibleMoves(square, square.getPiece().getMoves(false));
            } else {
                mGameActivity.setPossibleMoves(possibleMoves);
            }
            mGameActivity.setSelectedSquareView(this);
        } else {
            mGameActivity.clearSelection();
        }
    }

    public void update() {
        Square square = mGameActivity.getGame().getBoard().getSquares()[mRow][mColumn];
        SquareView selectedSquare = mGameActivity.getSelectedSquareView();
        if (this == selectedSquare) {
            setBackgroundColor(ContextCompat.getColor(mGameActivity, R.color.board_selected));
        } else {
            ArrayList<Move> possibleMoves = mGameActivity.getPossibleMoves();
            if (possibleMoves != null && mGameActivity.getMoveForSquare(square) != null) {
                setBackgroundColor(ContextCompat.getColor(mGameActivity, R.color.board_possible_move));
            }
            else {
                setBackgroundColor(Color.TRANSPARENT);
            }
        }

        if (square.isEmpty()) {
            setImageResource(android.R.color.transparent);
        } else {
            Piece piece = square.getPiece();
            Utils.setImageViewForPiece(this, piece);
        }
    }
}