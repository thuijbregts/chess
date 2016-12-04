package com.thomas.chess.game.pieces;

import com.thomas.chess.game.Board;
import com.thomas.chess.game.Game;
import com.thomas.chess.game.Move;
import com.thomas.chess.game.Square;
import com.thomas.chess.utils.Utils;

import java.util.ArrayList;

public class Pawn extends Piece {

    public static final int VALUE = 1;

    public Pawn(int color, Square square, Game game) {
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

        if (mColor == Utils.BLACK) {
            board = Board.rotate(board);
            row = (Utils.ROWS-1) - mSquare.getRow();
            column = (Utils.COLUMNS-1) - mSquare.getColumn();
        }

        if (row < Utils.ROWS-1) {
            if (board[row + 1][column].isEmpty()) {
                possibleMoves.add(new Move((row == Utils.ROWS-2 ?Move.TYPE_PROMOTION:Move.TYPE_NORMAL),
                        mSquare,
                        board[row + 1][column]));
            }

            if (column > 0) {
                square = board[row + 1][column - 1];
                if (!square.isEmpty() && square.getPiece().getColor() != mColor) {
                    possibleMoves.add(new Move((row == Utils.ROWS-2?Move.TYPE_PROMOTION:Move.TYPE_NORMAL),
                            mSquare,
                            square));
                }
            }
            if (column < Utils.COLUMNS - 1) {
                square = board[row + 1][column + 1];
                if (!square.isEmpty() && square.getPiece().getColor() != mColor) {
                    possibleMoves.add(new Move((row == Utils.ROWS-2?Move.TYPE_PROMOTION:Move.TYPE_NORMAL),
                            mSquare,
                            square));
                }
            }
        }
        if (mMovements == 0) {
            if (board[row+2][column].isEmpty() && board[row+1][column].isEmpty()) {
                possibleMoves.add(new Move(Move.TYPE_NORMAL,
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
        Move lastMove = mGame.getMoves().get(mGame.getMoveCount()-1);
        Move move;

        if (column > 0) {
            square = board[row][column - 1];
            piece = square.getPiece();
            if (!square.isEmpty() && piece.getColor() != mColor
                    && piece instanceof Pawn && piece.getMovements() == 1
                    && piece.equals(lastMove.getMovedPiece())) {
                move = new Move(Move.TYPE_PASSANT,
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
                move = new Move(Move.TYPE_PASSANT,
                        mSquare,
                        board[row + 1][column + 1]);
                move.setEnPassant(square);
                possibleMoves.add(move);
            }
        }
    }

    public boolean canEnPassant() {
        Square[][] board = mGame.getBoard().getSquares();

        Square square;
        Piece piece;

        int row = mSquare.getRow();
        int column = mSquare.getColumn();

        if (mColor == Utils.BLACK) {
            board = Board.rotate(board);
            row = (Utils.ROWS-1) - mSquare.getRow();
            column = (Utils.COLUMNS-1) - mSquare.getColumn();
        }

        if (row == 4) {
            Move lastMove = mGame.getMoves().get(mGame.getMoveCount()-1);
            if (column > 0) {
                square = board[row][column - 1];
                piece = square.getPiece();
                if (!square.isEmpty() && piece.getColor() != mColor
                        && piece instanceof Pawn && piece.getMovements() == 1
                        && piece.equals(lastMove.getMovedPiece())) {
                    return true;
                }
            }
            if (column < Utils.COLUMNS - 1) {
                square = board[row][column + 1];
                piece = square.getPiece();
                if (!square.isEmpty() && piece.getColor() != mColor
                        && piece instanceof Pawn && piece.getMovements() == 1
                        && piece.equals(lastMove.getMovedPiece())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isBlockedByEnemyPawn() {
        Square[][] board = mGame.getBoard().getSquares();

        int row = mSquare.getRow();
        int column = mSquare.getColumn();

        if (mColor == Utils.BLACK) {
            board = Board.rotate(board);
            row = (Utils.ROWS-1) - mSquare.getRow();
            column = (Utils.COLUMNS-1) - mSquare.getColumn();
        }

        Square square = board[row + 1][column];
        if (square.getPiece() == null) {
            return false;
        }
        Piece piece = square.getPiece();
        if (!(piece instanceof Pawn)) {
            return false;
        }
        return piece.getColor() != mColor;
    }
}
