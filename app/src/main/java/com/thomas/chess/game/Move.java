package com.thomas.chess.game;

import com.thomas.chess.pieces.Piece;

import java.util.ArrayList;

public class Move {

    private int mMoveType;

    private Square mSourceSquare;
    private Square mDestinationSquare;
    private Piece mMovedPiece;
    private Piece mDeadPiece;

    private boolean isCastling;
    private boolean isEnPassant;

    private Square mEnPassantSquare;

    public Move(int moveType, Square sourceSquare, Square destinationSquare) {
        mMoveType = moveType;
        mSourceSquare = sourceSquare;
        mDestinationSquare = destinationSquare;
        mMovedPiece = sourceSquare.getPiece();
    }

    public void make() {
        switch (mMoveType) {
            case Utils.MOVE_TYPE_NORMAL:
                makeNormalMove();
                break;
            case Utils.MOVE_TYPE_PASSANT:
                makeEnPassantMove();
                break;
        }
    }

    public void unmake() {

    }

    public Piece getMovedPiece() {
        return mMovedPiece;
    }

    public Piece getDeadPiece() {
        return mDeadPiece;
    }

    public Square getSourceSquare() {
        return mSourceSquare;
    }

    public Square getDestinationSquare() {
        return mDestinationSquare;
    }

    public void setEnPassant(Square enPassantSquare) {
        mEnPassantSquare = enPassantSquare;
        mDeadPiece = enPassantSquare.getPiece();
    }

    private void makeNormalMove() {
        mDeadPiece = mDestinationSquare.getPiece();
        mDestinationSquare.setPiece(mSourceSquare.getPiece());
        mSourceSquare.setPiece(null);
        mDestinationSquare.getPiece().addMovement();
    }

    private void unmakeNormalMove() {
        mSourceSquare.setPiece(mDestinationSquare.getPiece());
        mDestinationSquare.setPiece(mDeadPiece);
        mSourceSquare.getPiece().removeMovement();
        mDeadPiece = null;
    }

    private void makeEnPassantMove() {
        mDeadPiece = mEnPassantSquare.getPiece();
        mDestinationSquare.setPiece(mSourceSquare.getPiece());
        mSourceSquare.setPiece(null);
        mEnPassantSquare.setPiece(null);
        mDestinationSquare.getPiece().addMovement();
    }

    private void unmakeEnPassantMove() {
        mEnPassantSquare.setPiece(mDeadPiece);
        mSourceSquare.setPiece(mDestinationSquare.getPiece());
        mDestinationSquare.setPiece(null);
        mSourceSquare.getPiece().removeMovement();
        mDeadPiece = null;
    }
}
