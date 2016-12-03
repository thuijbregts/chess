package com.thomas.chess.game;

import com.thomas.chess.game.pieces.Piece;

public class Square {

    public static final int DARK = 0;
    public static final int LIGHT = 1;

    private boolean mEmpty;
    private Piece mPiece;
    private int mRow;
    private int mColumn;
    private int mColor;

    public Square(int row, int column, int color) {
        mRow = row;
        mColumn = column;
        mEmpty = true;
        mColor = color;
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

    public int getColor() {
        return mColor;
    }
}
