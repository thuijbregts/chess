package com.thomas.chess.pieces;

import com.thomas.chess.game.Board;
import com.thomas.chess.game.Game;
import com.thomas.chess.game.Move;
import com.thomas.chess.game.Square;
import com.thomas.chess.game.Utils;

import java.util.ArrayList;

public class Bishop extends Piece {


    public Bishop(int color, Game game) {
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

        int maxMoves = (row < column ? row : column);
        int i = 1;
        while (i <= maxMoves) {
            square = board[row-i][column-i];
            if (square.isEmpty()) {
                possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
                        currentSquare,
                        square));
            } else {
                if (square.getPiece().getColor() != mColor) {
                    possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
                            currentSquare,
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
                possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
                        currentSquare,
                        square));
            } else {
                if (square.getPiece().getColor() != mColor) {
                    possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
                            currentSquare,
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
                possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
                        currentSquare,
                        square));
            } else {
                if (square.getPiece().getColor() != mColor) {
                    possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
                            currentSquare,
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
                possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
                        currentSquare,
                        square));
            } else {
                if (square.getPiece().getColor() != mColor) {
                    possibleMoves.add(new Move(Utils.MOVE_TYPE_NORMAL,
                            currentSquare,
                            square));
                }
                break;
            }
            i++;
        }
        return possibleMoves;
    }


}
