package com.thomas.chess.gui.views;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.thomas.chess.R;
import com.thomas.chess.gui.activities.GameActivity;
import com.thomas.chess.gui.utils.Utils;

import com.thomas.chess.engine.chess120.*;

import java.util.ArrayList;

public class SquareView extends ImageView{

    private GameActivity mGameActivity;
    private Game mGame;
    private int mRank;
    private int mFile;

    private int[] mBoard;
    private int mSquare;

    private boolean sourceSquare;

    private ArrayList<SquareView> mDestinationSquares = new ArrayList<>();

    private int[] mMoves = new int[200];
    private int mMoveCount;
    private boolean clickable;

    private int mGameType;

    public SquareView(GameActivity gameActivity, int row, int column, int gameType) {
        super(gameActivity);
        mGameActivity = gameActivity;
        mGame = mGameActivity.getGame();
        mRank = row;
        mFile = column;
        mBoard = mGame.getBoard();
        mSquare = 21 + mFile + (10 * mRank);
        mGameType = gameType;
        if (gameType != Utils.GAME_REVIEW) {
            setOnClickListener(new OnSquareClickListener());
        }
    }

    public void addMove(int move) {
        mMoves[mMoveCount++] = move;
    }

    public void addSquare(SquareView squareView) {
        mDestinationSquares.add(squareView);
    }

    public void clear() {
        mMoveCount = 0;
        mDestinationSquares.clear();
        sourceSquare = false;
        clickable = false;
    }

    public void update() {
        int piece = mBoard[mSquare];

        if (piece == Definitions.SQ_EMPTY) {
            setImageResource(android.R.color.transparent);
        } else {
            Utils.setImageViewForPiece(this, piece);
        }
        clear();
        clearBackground();
    }

    public void setLastMove() {
        setBackgroundResource(R.drawable.moved_square_frame);
    }

    public void setSelected() {
        setBackgroundResource(R.drawable.selected_square_frame);
    }

    public void setPossibleMove() {
        clickable = true;
        setBackgroundResource(R.drawable.possible_square_frame);
    }

    public void clearBackground() {
        clickable = false;
        setBackgroundColor(Color.TRANSPARENT);
    }

    private class OnSquareClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (canClick()) {
                if (sourceSquare) {
                    mGameActivity.updateSelectedSquare(SquareView.this);
                } else {
                    SquareView square = mGameActivity.getSelectedSquareView();
                    if (square != null) {
                        if (clickable) {
                            int rank = square.getRank();
                            int file = square.getFile();

                            int move, src, dst, type;
                            for (int i = 0; i < mMoveCount; i++) {
                                move = mMoves[i];
                                src = move & Move.SQUARE_MASK;
                                dst = move >> Move.TO_SHIFT & Move.SQUARE_MASK;
                                if (Definitions.RANKS[src] == rank && Definitions.FILES[src] == file
                                        && Definitions.RANKS[dst] == mRank && Definitions.FILES[dst] == mFile) {
                                    type = move >> Move.TYPE_SHIFT & Move.TYPE_MASK;
                                    if (type == Move.PROMOTION) {
                                        mGameActivity.showPromotionDialog(move);
                                    } else {
                                        mGameActivity.makeMove(move);
                                    }
                                    break;
                                }
                            }
                        } else {
                            mGameActivity.clearSelection();
                        }
                    }
                }
            }
        }

        private boolean canClick() {
            if (mGameActivity.getGame().isGameOver()) {
                return false;
            }
            if (mGameActivity.isAnimating()) {
                return false;
            }
            if (sourceSquare || mMoveCount > 0) {
                return true;
            }
            return false;
        }
    }

    public void setSourceSquare(boolean sourceSquare) {
        this.sourceSquare = sourceSquare;
    }

    public int[] getMoves() {
        return mMoves;
    }

    public int getRank() {
        return mRank;
    }

    public int getFile() {
        return mFile;
    }

    public ArrayList<SquareView> getDestinationSquares() {
        return mDestinationSquares;
    }
}
