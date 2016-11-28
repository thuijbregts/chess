package com.thomas.chess.pieces;

import com.thomas.chess.game.Game;
import com.thomas.chess.game.Move;
import com.thomas.chess.game.Square;
import com.thomas.chess.game.Utils;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class Piece {

    protected Game mGame;
    protected int mColor;
    protected int mMovements;
    protected Square mSquare;

    public Piece(int color, Square square, Game game) {
        mGame = game;
        mColor = color;
        mSquare = square;
    }

    public abstract ArrayList<Move> getMoves(boolean verification);

    public int getColor() {
        return mColor;
    }

    public void setSquare(Square square) {
        mSquare = square;
    }

    public Square getSquare() {
        return mSquare;
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

    protected void removeCheckMoves(ArrayList<Move> possibleMoves) {

        Move move;
        ArrayList<Piece> opponentsPieces = mGame.getOpponent(mColor).getAlivePieces();

        Iterator<Move> iterator = possibleMoves.iterator();
        while (iterator.hasNext()) {
            move = iterator.next();

            if (move.getMoveType() != Utils.MOVE_TYPE_CASTLING) {
                move.make();

                for (Piece piece : opponentsPieces) {
                    if (!piece.equals(move.getDeadPiece()) && piece.hasCheck()) {
                        iterator.remove();
                        break;
                    }
                }

                move.unmake();
            }
        }
    }

    public boolean hasCheck() {
        Piece piece;
        for (Move move : getMoves(true)) {
            piece = move.getDestinationSquare().getPiece();
            if (piece != null && piece instanceof King) {
                return true;
            }
        }
        return false;
    }

}
