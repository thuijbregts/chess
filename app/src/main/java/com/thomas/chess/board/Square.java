package com.thomas.chess.board;

import com.thomas.chess.pieces.Piece;

public class Square {

    private boolean mEmpty;
    private Piece mPiece;

    public Square() {
        mEmpty = true;
    }

    public boolean isEmpty() {
        return mEmpty;
    }

    public Piece getPiece() {
        return mPiece;
    }

    public void setPiece(Piece piece) {
        if (piece != null) {
            mPiece = piece;
            mEmpty = false;
        }
    }

    public void clear() {
        mPiece = null;
        mEmpty = true;
    }
}
