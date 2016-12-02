package com.thomas.chess.game.pieces;

import com.thomas.chess.game.Board;
import com.thomas.chess.game.Game;
import com.thomas.chess.game.Move;
import com.thomas.chess.game.Square;
import com.thomas.chess.utils.Utils;

import java.util.ArrayList;

public class King extends Piece {

    public King(int color, Square square, Game game) {
        super(color, square, game);
    }

    @Override
    public ArrayList<Move> getMoves(boolean verification) {
        ArrayList<Move> possibleMoves = new ArrayList<>();

        Square[][] board = mGame.getBoard().getSquares();
        Square square;

        int row = mSquare.getRow();
        int column = mSquare.getColumn();

        int startRow = (row > 0 ? -1 : 0);
        int endRow = (row < Utils.ROWS-1 ? 1 : 0);
        int startColumn = (column > 0 ? -1 : 0);
        int endColumn = (column < Utils.COLUMNS-1 ? 1 : 0);

        for (int i = startRow; i <= endRow; i++) {
            for (int j = startColumn; j <= endColumn; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                square = board[row+i][column+j];
                if (square.isEmpty()) {
                    possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
                            mSquare,
                            square));
                }
                else if (!square.isEmpty() && square.getPiece().getColor() != mColor) {
                    possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
                            mSquare,
                            square));
                }
            }
        }
        if (!verification) {
            Move move;
            Square[][] castlingBoard = board;
            if (mColor == Utils.BLACK) {
                castlingBoard = Board.horizontalMirror(board);
            }
            if (canCastlingRight(castlingBoard)) {
                move = new Move(Utils.MOVE_TYPE_CASTLING,
                        mSquare,
                        castlingBoard[0][7]);
                possibleMoves.add(move);
                move.setCastling(castlingBoard[0][6], castlingBoard[0][5]);
            }
            if (canCastlingLeft(castlingBoard)) {
                move = new Move(Utils.MOVE_TYPE_CASTLING,
                        mSquare,
                        castlingBoard[0][0]);
                possibleMoves.add(move);
                move.setCastling(castlingBoard[0][2], castlingBoard[0][3]);

            }
            removeCheckMoves(possibleMoves);
        }
        return possibleMoves;
    }

    private boolean canCastlingRight(Square[][] board) {
        if (mMovements != 0) {
            return false;
        }
        if (board[0][7].getPiece() == null) {
            return false;
        }
        if (board[0][7].getPiece().getMovements() != 0) {
            return false;
        }
        if (!board[0][5].isEmpty() || !board[0][6].isEmpty()) {
            return false;
        }
        if (isSquareControlled(board[0][5]) || isSquareControlled(board[0][6])) {
            return false;
        }
        return true;
    }

    private boolean canCastlingLeft(Square[][] board) {
        if (mMovements != 0) {
            return false;
        }
        if (board[0][0].getPiece() == null) {
            return false;
        }
        if (board[0][0].getPiece().getMovements() != 0) {
            return false;
        }
        if (!board[0][1].isEmpty() || !board[0][2].isEmpty() || !board[0][3].isEmpty()) {
            return false;
        }
        if (isSquareControlled(board[0][1]) || isSquareControlled(board[0][2]) || isSquareControlled(board[0][3])) {
            return false;
        }
        return true;
    }

    private boolean isSquareControlled(Square square) {
        ArrayList<Piece> opponentPieces = mGame.getOpponent(mColor).getAlivePieces();

        for (Piece piece : opponentPieces) {
            for (Move move : piece.getMoves(true)) {
                if (square.equals(move.getDestinationSquare())) {
                    return true;
                }
            }
        }
        return false;
    }
}
