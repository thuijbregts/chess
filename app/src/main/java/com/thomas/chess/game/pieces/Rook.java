package com.thomas.chess.game.pieces;

import com.thomas.chess.game.Game;
import com.thomas.chess.game.Move;
import com.thomas.chess.game.Square;
import com.thomas.chess.utils.Utils;

import java.util.ArrayList;

public class Rook extends Piece {

    public static final int VALUE = 5;

    public Rook(int color, Square square, Game game) {
        super(color, square, game);
        mValue = VALUE;
    }

    @Override
    public ArrayList<Move> getMoves(boolean verification) {
        ArrayList<Move> possibleMoves = new ArrayList<>();

        Square[][] board = mGame.getBoard().getSquares();
        Square square;

        int row = mSquare.getRow();
        int column = mSquare.getColumn();

        for (int i = row+1; i < Utils.ROWS; i++) {
            square = board[i][column];
            if (square.isEmpty()) {
                possibleMoves.add(new Move(Move.TYPE_NORMAL,
                        mSquare,
                        square));
            } else {
                if (square.getPiece().getColor() != mColor) {
                    possibleMoves.add(new Move(Move.TYPE_NORMAL,
                            mSquare,
                            square));
                }
                break;
            }
        }

        for (int i = row-1; i >= 0; i--) {
            square = board[i][column];
            if (square.isEmpty()) {
                possibleMoves.add(new Move(Move.TYPE_NORMAL,
                        mSquare,
                        square));
            } else {
                if (square.getPiece().getColor() != mColor) {
                    possibleMoves.add(new Move(Move.TYPE_NORMAL,
                            mSquare,
                            square));
                }
                break;
            }
        }

        for (int i = column+1; i < Utils.COLUMNS; i++) {
            square = board[row][i];
            if (square.isEmpty()) {
                possibleMoves.add(new Move(Move.TYPE_NORMAL,
                        mSquare,
                        square));
            } else {
                if (square.getPiece().getColor() != mColor) {
                    possibleMoves.add(new Move(Move.TYPE_NORMAL,
                            mSquare,
                            square));
                }
                break;
            }
        }

        for (int i = column-1; i >= 0; i--) {
            square = board[row][i];
            if (square.isEmpty()) {
                possibleMoves.add(new Move(Move.TYPE_NORMAL,
                        mSquare,
                        square));
            } else {
                if (square.getPiece().getColor() != mColor) {
                    possibleMoves.add(new Move(Move.TYPE_NORMAL,
                            mSquare,
                            square));
                }
                break;
            }
        }

        if (!verification) {
            removeCheckMoves(possibleMoves);
        }
        return possibleMoves;
    }

}
