package com.thomas.chess.pieces;

import com.thomas.chess.game.Board;
import com.thomas.chess.game.Game;
import com.thomas.chess.game.Move;
import com.thomas.chess.game.Square;
import com.thomas.chess.game.Utils;

import java.util.ArrayList;

public class Rook extends Piece {

    public Rook(int color, Square square, Game game) {
        super(color, square, game);
    }

    @Override
    public ArrayList<Move> getMoves(boolean verification) {
        ArrayList<Move> possibleMoves = new ArrayList<>();

        Square[][] board = mGame.getBoard().getSquares();
        Square square;

        int row = mSquare.getRow();
        int column = mSquare.getColumn();

        if (mColor == Utils.BLACK) {
            board = Board.rotate(board);
            row = (Utils.ROWS-1) - mSquare.getRow();
            column = (Utils.COLUMNS-1) - mSquare.getColumn();
        }

        for (int i = row+1; i < Utils.ROWS; i++) {
            square = board[i][column];
            if (square.isEmpty()) {
                possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
                        mSquare,
                        square));
            } else {
                if (square.getPiece().getColor() != mColor) {
                    possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
                            mSquare,
                            square));
                }
                break;
            }
        }

        for (int i = row-1; i >= 0; i--) {
            square = board[i][column];
            if (square.isEmpty()) {
                possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
                        mSquare,
                        square));
            } else {
                if (square.getPiece().getColor() != mColor) {
                    possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
                            mSquare,
                            square));
                }
                break;
            }
        }

        for (int i = column+1; i < Utils.COLUMNS; i++) {
            square = board[row][i];
            if (square.isEmpty()) {
                possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
                        mSquare,
                        square));
            } else {
                if (square.getPiece().getColor() != mColor) {
                    possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
                            mSquare,
                            square));
                }
                break;
            }
        }

        for (int i = column-1; i >= 0; i--) {
            square = board[row][i];
            if (square.isEmpty()) {
                possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
                        mSquare,
                        square));
            } else {
                if (square.getPiece().getColor() != mColor) {
                    possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
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
