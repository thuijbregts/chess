package com.thomas.chess.game;

import com.thomas.chess.game.pieces.Piece;

import java.util.ArrayList;

public abstract class Player {

    protected String mName;
    protected int mColor;

    protected Game mGame;

    protected ArrayList<Piece> mAlivePieces;
    protected ArrayList<Piece> mDeadPieces;

    public Player(String name, int color) {
        mName = name;
        mColor = color;
        initializeArrays();
    }

    public Player(int color) {
        mColor = color;
        initializeArrays();
    }

    public void play() {}

    public void setGame(Game game) {
        mGame = game;
    }

    public String getName() {
        return mName;
    }

    public int getColor() {
        return mColor;
    }

    private void initializeArrays() {
        mAlivePieces = new ArrayList<>();
        mDeadPieces = new ArrayList<>();
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

    public ArrayList<Piece> getAlivePieces() {
        return mAlivePieces;
    }

    public ArrayList<Piece> getDeadPieces() {
        return mDeadPieces;
    }
}
