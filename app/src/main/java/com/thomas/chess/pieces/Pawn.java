package com.thomas.chess.pieces;

import com.thomas.chess.game.Board;
import com.thomas.chess.game.Game;
import com.thomas.chess.game.Move;
import com.thomas.chess.game.Square;
import com.thomas.chess.game.Utils;

import java.util.ArrayList;

public class Pawn extends Piece {

    public Pawn(int color, Square square, Game game) {
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

        if (row < Utils.ROWS-1) {
            if (board[row + 1][column].isEmpty()) {
                possibleMoves.add(new Move((row == Utils.ROWS-2 ?Utils.MOVE_TYPE_PROMOTION:Utils.MOVE_TYPE_NORMAL),
                        mSquare,
                        board[row + 1][column]));
            }

            if (column > 0) {
                square = board[row + 1][column - 1];
                if (!square.isEmpty() && square.getPiece().getColor() != mColor) {
                    possibleMoves.add(new Move((row == Utils.ROWS-2?Utils.MOVE_TYPE_PROMOTION:Utils.MOVE_TYPE_NORMAL),
                            mSquare,
                            square));
                }
            }
            if (column < Utils.COLUMNS - 1) {
                square = board[row + 1][column + 1];
                if (!square.isEmpty() && square.getPiece().getColor() != mColor) {
                    possibleMoves.add(new Move((row == Utils.ROWS-2?Utils.MOVE_TYPE_PROMOTION:Utils.MOVE_TYPE_NORMAL),
                            mSquare,
                            square));
                }
            }
        }
        if (mMovements == 0) {
            if (board[row+2][column].isEmpty() && board[row+1][column].isEmpty()) {
                possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
                        mSquare,
                        board[row+2][column]));
            }
        }

        if (row == 4) {
            enPassant(possibleMoves, board, row, column);
        }

        if (!verification) {
            removeCheckMoves(possibleMoves);
        }
        return possibleMoves;
    }

    private void enPassant(ArrayList<Move> possibleMoves,
                           Square[][] board, int row, int column) {
        Square square;
        Piece piece;
        Move lastMove = mGame.getMoves().get(mGame.getMoves().size()-1);
        Move move;

        if (column > 0) {
            square = board[row][column - 1];
            piece = square.getPiece();
            if (!square.isEmpty() && piece.getColor() != mColor
                    && piece instanceof Pawn && piece.getMovements() == 1
                    && piece.equals(lastMove.getMovedPiece())) {
                move = new Move(Utils.MOVE_TYPE_PASSANT,
                        mSquare,
                        board[row+1][column-1]);
                move.setEnPassant(square);
                possibleMoves.add(move);
            }
        }
        if (column < Utils.COLUMNS - 1) {
            square = board[row][column + 1];
            piece = square.getPiece();
            if (!square.isEmpty() && piece.getColor() != mColor
                    && piece instanceof Pawn && piece.getMovements() == 1
                    && piece.equals(lastMove.getMovedPiece())) {
                move = new Move(Utils.MOVE_TYPE_PASSANT,
                        mSquare,
                        board[row + 1][column + 1]);
                move.setEnPassant(square);
                possibleMoves.add(move);
            }
        }
    }
}
