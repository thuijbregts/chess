package com.thomas.chess.game.pieces;

import com.thomas.chess.game.Game;
import com.thomas.chess.game.Move;
import com.thomas.chess.game.Square;
import com.thomas.chess.utils.Utils;

import java.util.ArrayList;

public class Bishop extends Piece {

    public static final int VALUE = 3;

    public Bishop(int color, Square square, Game game) {
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

        int maxMoves = (row < column ? row : column);
        int i = 1;
        while (i <= maxMoves) {
            square = board[row-i][column-i];
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
            i++;
        }

        maxMoves = ((Utils.ROWS-1)-row < column ? (Utils.ROWS-1)-row : column);
        i = 1;
        while (i <= maxMoves) {
            square = board[row+i][column-i];
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
            i++;
        }

        maxMoves = ((Utils.ROWS-1)-row < (Utils.COLUMNS-1)-column ?
                (Utils.ROWS-1)-row : (Utils.COLUMNS-1)-column);
        i = 1;
        while (i <= maxMoves) {
            square = board[row+i][column+i];
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
            i++;
        }

        maxMoves = (row < (Utils.COLUMNS-1)-column ? row : (Utils.COLUMNS-1)-column);
        i = 1;
        while (i <= maxMoves) {
            square = board[row-i][column+i];
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
            i++;
        }

        if (!verification) {
            removeCheckMoves(possibleMoves);
        }
        return possibleMoves;
    }
}
