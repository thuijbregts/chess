package com.thomas.chess.pieces;

public abstract class Piece {

    public static final int WHITE = 0;
    public static final int BLACK = 1;

    private int color;
    private int movements;

    public Piece(int color) {
        this.color = color;
    }

    public abstract void GetMoves();
}
