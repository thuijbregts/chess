package com.thomas.chess.game;

import com.thomas.chess.game.pieces.Piece;

public class Square {

    private boolean mEmpty;
    private Piece mPiece;
    private int mRow;
    private int mColumn;

    public Square(int row, int column) {
        mRow = row;
        mColumn = column;
        mEmpty = true;
    }

    public boolean isEmpty() {
        return mEmpty;
    }

    public Piece getPiece() {
        return mPiece;
    }

    public void setPiece(Piece piece) {
        mPiece = piece;
        mEmpty = (piece == null);
    }

    public void clear() {
        mPiece = null;
        mEmpty = true;
    }

    public int getColumn() {
        return mColumn;
    }

    public int getRow() {
        return mRow;
    }
}
