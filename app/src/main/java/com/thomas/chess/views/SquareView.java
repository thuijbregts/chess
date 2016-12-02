package com.thomas.chess.views;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.thomas.chess.R;
import com.thomas.chess.activities.GameActivity;
import com.thomas.chess.game.Move;
import com.thomas.chess.game.Square;
import com.thomas.chess.game.p_children.AIPlayer;
import com.thomas.chess.utils.Utils;
import com.thomas.chess.game.pieces.Piece;

import java.util.ArrayList;

public class SquareView extends ImageView{

    private GameActivity mGameActivity;
    private int mRow;
    private int mColumn;

    private int mGameType;

    public SquareView(GameActivity gameActivity, int row, int column, int gameType) {
        super(gameActivity);
        mGameActivity = gameActivity;
        mRow = row;
        mColumn = column;
        mGameType = gameType;
        if (gameType != Utils.GAME_REVIEW) {
            setOnClickListener(new OnSquareClickListener());
        }
    }

    public void updateImage() {
        Square square = mGameActivity.getGame().getBoard().getSquares()[mRow][mColumn];

        if (square.isEmpty()) {
            setImageResource(android.R.color.transparent);
        } else {
            Piece piece = square.getPiece();
            Utils.setImageViewForPiece(this, piece);
        }
    }

    public void setSelected() {
        setBackgroundColor(ContextCompat.getColor(mGameActivity, R.color.board_selected));
    }

    public void setPossibleMove() {
        setBackgroundColor(ContextCompat.getColor(mGameActivity, R.color.board_possible_move));
    }

    public void clearBackground() {
        setBackgroundColor(Color.TRANSPARENT);
    }

    private class OnSquareClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (canClick()) {
                Square square = mGameActivity.getGame().getBoard().getSquares()[mRow][mColumn];
                SquareView selectedSquare = mGameActivity.getSelectedSquareView();
                if (isValid(square) || selectedSquare != null) {
                    Move move = mGameActivity.getMoveForSquare(square);
                    if (move != null) {
                        if (move.getMoveType() == Utils.MOVE_TYPE_PROMOTION) {
                            mGameActivity.choosePromotionPiece(move);
                        } else {
                            mGameActivity.executeMove(move);
                        }
                    } else {
                        treatSquare(square);
                    }
                }
            }
        }

        private boolean isValid(Square square) {
            if (mGameActivity.isMoving()) {
                return false;
            }
            if (square.isEmpty()) {
                return false;
            }
            if (square.getPiece().getColor() !=
                    mGameActivity.getGame().getCurrentPlayer().getColor()) {
                return false;
            }
            return true;
        }

        private void treatSquare(Square square) {
            if (!square.isEmpty() && square.getPiece().getColor() ==
                    mGameActivity.getGame().getCurrentPlayer().getColor()) {

                if (mGameActivity.getSelectedSquareView() != null) {
                    mGameActivity.clearSelection();
                }

                ArrayList<Move> possibleMoves = mGameActivity.getMovesForSquare(square);
                if (possibleMoves == null) {
                    mGameActivity.addPossibleMoves(square, square.getPiece().getMoves(false));
                } else {
                    mGameActivity.setPossibleMoves(possibleMoves);
                }
                mGameActivity.updateSelectedSquare(SquareView.this);
            } else {
                mGameActivity.clearSelection();
            }
        }

        private boolean canClick() {
            switch (mGameType) {
                case Utils.GAME_TWO_PLAYERS:
                    return true;
                case Utils.GAME_ONLINE:
                    //TODO online game
                    return true;
                case Utils.GAME_SOLO:
                    return !(mGameActivity.getGame().getCurrentPlayer() instanceof AIPlayer);
            }
            return false;
        }
    }
}
