package com.thomas.chess.pieces;

import com.thomas.chess.game.Board;
import com.thomas.chess.game.Game;
import com.thomas.chess.game.Move;
import com.thomas.chess.game.Square;
import com.thomas.chess.game.Utils;

import java.util.ArrayList;

public class Pawn extends Piece {

    public Pawn(int color, Game game) {
        super(color, game);
    }

    @Override
    public ArrayList<Move> getMoves(Square currentSquare) {
        ArrayList<Move> possibleMoves = new ArrayList<>();

        Square[][] board = mGame.getBoard().getSquares();
        Square square;

        int row = currentSquare.getRow();
        int column = currentSquare.getColumn();

        if (mColor == Utils.BLACK) {
            board = Board.rotate(board);
            row = (Utils.ROWS-1) - currentSquare.getRow();
            column = (Utils.COLUMNS-1) - currentSquare.getColumn();
        }

        if (board[row+1][column].isEmpty()) {
            possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
                    currentSquare,
                    board[row+1][column]));
        }
        if (column > 0) {
            square = board[row+1][column-1];
            if (!square.isEmpty() && square.getPiece().getColor() != mColor) {
                possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
                        currentSquare,
                        square));
            }
        }
        if (column < Utils.COLUMNS-1) {
            square = board[row+1][column+1];
            if (!square.isEmpty() && square.getPiece().getColor() != mColor) {
                possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
                        currentSquare,
                        square));
            }
        }

        if (mMovements == 0) {
            if (board[row+2][column].isEmpty()) {
                possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
                        currentSquare,
                        board[row+2][column]));
            }
        }

        if (row == 4) {
            enPassant(currentSquare, possibleMoves, board, row, column);
        }

        return possibleMoves;
    }

    private void enPassant(Square currentSquare, ArrayList<Move> possibleMoves,
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
                        currentSquare,
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
                        currentSquare,
                        board[row + 1][column + 1]);
                move.setEnPassant(square);
                possibleMoves.add(move);
            }
        }
    }
}
