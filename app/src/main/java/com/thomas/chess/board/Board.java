package com.thomas.chess.board;

import com.thomas.chess.pieces.Bishop;
import com.thomas.chess.pieces.King;
import com.thomas.chess.pieces.Knight;
import com.thomas.chess.pieces.Pawn;
import com.thomas.chess.pieces.Piece;
import com.thomas.chess.pieces.Queen;
import com.thomas.chess.pieces.Rook;

public class Board {

    public static final int ROWS = 8;
    public static final int COLUMNS = 8;

    private Square[][] mBoard;

    public Board() {
        mBoard = new Square[ROWS][COLUMNS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                mBoard[i][j] = new Square();
            }
        }
    }

    public void placePieces() {
        mBoard[0][0].setPiece(new Rook(Piece.WHITE));
        mBoard[0][7].setPiece(new Rook(Piece.WHITE));
        mBoard[0][1].setPiece(new Knight(Piece.WHITE));
        mBoard[0][6].setPiece(new Knight(Piece.WHITE));
        mBoard[0][2].setPiece(new Bishop(Piece.WHITE));
        mBoard[0][5].setPiece(new Bishop(Piece.WHITE));
        mBoard[0][3].setPiece(new Queen(Piece.WHITE));
        mBoard[0][4].setPiece(new King(Piece.WHITE));
        for (int i = 0; i < COLUMNS; i++) {
            mBoard[1][i].setPiece(new Pawn(Piece.WHITE));
        }

        mBoard[7][0].setPiece(new Rook(Piece.BLACK));
        mBoard[7][7].setPiece(new Rook(Piece.BLACK));
        mBoard[7][1].setPiece(new Knight(Piece.BLACK));
        mBoard[7][6].setPiece(new Knight(Piece.BLACK));
        mBoard[7][2].setPiece(new Bishop(Piece.BLACK));
        mBoard[7][5].setPiece(new Bishop(Piece.BLACK));
        mBoard[7][3].setPiece(new Queen(Piece.BLACK));
        mBoard[7][4].setPiece(new King(Piece.BLACK));
        for (int i = 0; i < COLUMNS; i++) {
            mBoard[6][i].setPiece(new Pawn(Piece.BLACK));
        }
    }
}
