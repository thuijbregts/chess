package com.thomas.chess.pieces;

import com.thomas.chess.game.Game;
import com.thomas.chess.game.Move;
import com.thomas.chess.game.Square;

import java.util.ArrayList;

public abstract class Piece {

    protected Game mGame;
    protected int mColor;
    protected int mMovements;

    public Piece(int color, Game game) {
        mGame = game;
        this.mColor = color;
    }

    public abstract ArrayList<Move> getMoves(Square currentSquare);

    public int getColor() {
        return mColor;
    }

    public int getMovements() {
        return mMovements;
    }

    public void addMovement() {
        mMovements++;
    }

    public void removeMovement() {
        mMovements--;
    }
}
