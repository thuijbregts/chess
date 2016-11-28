package com.thomas.chess.pieces;

import com.thomas.chess.game.Board;
import com.thomas.chess.game.Game;
import com.thomas.chess.game.Move;
import com.thomas.chess.game.Square;
import com.thomas.chess.game.Utils;

import java.util.ArrayList;

public class Knight extends Piece {

    public Knight(int color, Game game) {
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

        if (row < Utils.ROWS-1) {
            if (column > 1) {
                square = board[row+1][column-2];
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
                }
            }

            if (column < Utils.COLUMNS-2) {
                square = board[row+1][column+2];
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
                }
            }
        }

        if (row < Utils.ROWS-2) {
            if (column > 0) {
                square = board[row+2][column-1];
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
                }
            }

            if (column < Utils.COLUMNS-1) {
                square = board[row+2][column+1];
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
                }
            }
        }

        if (row > 0) {
            if (column > 1) {
                square = board[row-1][column-2];
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
                }
            }

            if (column < Utils.COLUMNS-2) {
                square = board[row-1][column+2];
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
                }
            }
        }

        if (row > 1) {
            if (column > 0) {
                square = board[row-2][column-1];
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
                }
            }

            if (column < Utils.COLUMNS-1) {
                square = board[row-2][column+1];
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
                }
            }
        }

        return possibleMoves;
    }


}
