package com.thomas.chess.game;

import android.util.Log;

import com.thomas.chess.game.pieces.Bishop;
import com.thomas.chess.game.pieces.King;
import com.thomas.chess.game.pieces.Knight;
import com.thomas.chess.game.pieces.Pawn;
import com.thomas.chess.game.pieces.Piece;
import com.thomas.chess.game.pieces.Queen;
import com.thomas.chess.game.pieces.Rook;
import com.thomas.chess.utils.Utils;

public class BoardThreefold {

    public static final int EMPTY = 0;

    public static final int WHITE_PAWN = 1;
    public static final int WHITE_ROOK = 2;
    public static final int WHITE_BISHOP = 3;
    public static final int WHITE_KNIGHT = 4;
    public static final int WHITE_QUEEN = 5;
    public static final int WHITE_KING = 6;
    public static final int WHITE_PAWN_EP = 7;

    public static final int BLACK_PAWN = 10;
    public static final int BLACK_ROOK = 12;
    public static final int BLACK_BISHOP = 13;
    public static final int BLACK_KNIGHT = 14;
    public static final int BLACK_QUEEN = 15;
    public static final int BLACK_KING = 16;
    public static final int BLACK_PAWN_EP = 17;

    int[][] mBoard;

    public BoardThreefold(Square[][] currentBoard) {
        mBoard = new int[Utils.ROWS][Utils.COLUMNS];
        Piece piece;
        for (int i = 0; i < currentBoard.length; i++) {
            for (int j = 0; j < currentBoard.length; j++) {
                if (currentBoard[i][j].isEmpty()) {
                    mBoard[i][j] = EMPTY;
                    continue;
                }
                piece = currentBoard[i][j].getPiece();
                if (piece instanceof Knight) {
                    mBoard[i][j] = (piece.getColor() == Utils.WHITE) ? WHITE_KNIGHT : BLACK_KNIGHT;
                    continue;
                }
                if (piece instanceof Bishop) {
                    mBoard[i][j] = (piece.getColor() == Utils.WHITE) ? WHITE_BISHOP : BLACK_BISHOP;
                    continue;
                }
                if (piece instanceof Queen) {
                    mBoard[i][j] = (piece.getColor() == Utils.WHITE) ? WHITE_QUEEN : BLACK_QUEEN;
                    continue;
                }
                if (piece instanceof Rook) {
                    mBoard[i][j] = (piece.getColor() == Utils.WHITE) ? WHITE_ROOK : BLACK_ROOK;
                    continue;
                }
                if (piece instanceof King) {
                    mBoard[i][j] = (piece.getColor() == Utils.WHITE) ? WHITE_KING : BLACK_KING;
                    continue;
                }
                if (((Pawn)piece).canEnPassant()) {
                    mBoard[i][j] = (piece.getColor() == Utils.WHITE) ? WHITE_PAWN_EP : BLACK_PAWN_EP;
                } else {
                    mBoard[i][j] = (piece.getColor() == Utils.WHITE) ? WHITE_PAWN : BLACK_PAWN;
                }
            }
        }
    }

    public boolean isSameBoardState(BoardThreefold board) {
        for (int i = 0; i < mBoard.length; i++) {
            for (int j = 0; j < mBoard.length; j++) {
                if (mBoard[i][j] != board.getBoard()[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public int[][] getBoard() {
        return mBoard;
    }
}
