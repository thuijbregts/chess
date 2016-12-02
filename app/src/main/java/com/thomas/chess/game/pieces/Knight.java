package com.thomas.chess.game.pieces;

import com.thomas.chess.game.Game;
import com.thomas.chess.game.Move;
import com.thomas.chess.game.Square;
import com.thomas.chess.utils.Utils;

import java.util.ArrayList;

public class Knight extends Piece {

    public Knight(int color, Square square, Game game) {
        super(color, square, game);
    }

    @Override
    public ArrayList<Move> getMoves(boolean verification) {
        ArrayList<Move> possibleMoves = new ArrayList<>();

        Square[][] board = mGame.getBoard().getSquares();
        Square square;

        int row = mSquare.getRow();
        int column = mSquare.getColumn();

        if (row < Utils.ROWS-1) {
            if (column > 1) {
                square = board[row+1][column-2];
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
                }
            }

            if (column < Utils.COLUMNS-2) {
                square = board[row+1][column+2];
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
                }
            }
        }

        if (row < Utils.ROWS-2) {
            if (column > 0) {
                square = board[row+2][column-1];
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
                }
            }

            if (column < Utils.COLUMNS-1) {
                square = board[row+2][column+1];
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
                }
            }
        }

        if (row > 0) {
            if (column > 1) {
                square = board[row-1][column-2];
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
                }
            }

            if (column < Utils.COLUMNS-2) {
                square = board[row-1][column+2];
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
                }
            }
        }

        if (row > 1) {
            if (column > 0) {
                square = board[row-2][column-1];
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
                }
            }

            if (column < Utils.COLUMNS-1) {
                square = board[row-2][column+1];
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
                }
            }
        }

        if (!verification) {
            removeCheckMoves(possibleMoves);
        }
        return possibleMoves;
    }


}
