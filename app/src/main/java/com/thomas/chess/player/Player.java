package com.thomas.chess.player;

import com.thomas.chess.pieces.Piece;

import java.util.ArrayList;

public abstract class Player {

    protected String mName;
    protected int mColor;

    protected ArrayList<Piece> mAlivePieces;
    protected ArrayList<Piece> mDeadPieces;

    public Player(String name, int color) {
        mName = name;
        mColor = color;
        mAlivePieces = new ArrayList<>();
        mDeadPieces = new ArrayList<>();
    }

    public Player(int color) {
        mColor = color;
        mAlivePieces = new ArrayList<>();
        mDeadPieces = new ArrayList<>();
    }

    public String getName() {
        return mName;
    }

    public int getColor() {
        return mColor;
    }

    public boolean hasCheck() {
        for (Piece piece : mAlivePieces) {
            if (piece.hasCheck()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasNoLegalMove() {
        for (Piece piece : mAlivePieces) {
            if (!piece.getMoves(false).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void setAlivePieces(ArrayList<Piece> alivePieces) {
        this.mAlivePieces = alivePieces;
    }

    public ArrayList<Piece> getAlivePieces() {
        return mAlivePieces;
    }

    public void setDeadPieces(ArrayList<Piece> deadPieces) {
        this.mDeadPieces = deadPieces;
    }

    public ArrayList<Piece> getDeadPieces() {
        return mDeadPieces;
    }
}
