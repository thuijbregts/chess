package com.thomas.chess.game;

import com.thomas.chess.pieces.Bishop;
import com.thomas.chess.pieces.King;
import com.thomas.chess.pieces.Knight;
import com.thomas.chess.pieces.Pawn;
import com.thomas.chess.pieces.Piece;
import com.thomas.chess.pieces.Queen;
import com.thomas.chess.pieces.Rook;

import java.util.ArrayList;

public class Board {

    private Game mGame;
    private Square[][] mSquares;

    public Board(Game game) {
        mGame = game;
        mSquares = new Square[Utils.ROWS][Utils.COLUMNS];
        for (int i = 0; i < Utils.ROWS; i++) {
            for (int j = 0; j < Utils.COLUMNS; j++) {
                mSquares[i][j] = new Square(i, j);
            }
        }
    }

    public void placePieces(ArrayList<Piece> alivePieces) {
        Piece piece;

        piece = new Rook(Utils.WHITE, mGame);
        alivePieces.add(piece);
        mSquares[0][0].setPiece(piece);

        piece = new Rook(Utils.WHITE, mGame);
        alivePieces.add(piece);
        mSquares[0][7].setPiece(piece);

        piece = new Knight(Utils.WHITE, mGame);
        alivePieces.add(piece);
        mSquares[0][1].setPiece(piece);

        piece = new Knight(Utils.WHITE, mGame);
        alivePieces.add(piece);
        mSquares[0][6].setPiece(piece);

        piece = new Bishop(Utils.WHITE, mGame);
        alivePieces.add(piece);
        mSquares[0][2].setPiece(piece);

        piece = new Bishop(Utils.WHITE, mGame);
        alivePieces.add(piece);
        mSquares[0][5].setPiece(piece);

        piece = new Queen(Utils.WHITE, mGame);
        alivePieces.add(piece);
        mSquares[0][3].setPiece(piece);

        piece = new King(Utils.WHITE, mGame);
        alivePieces.add(piece);
        mSquares[0][4].setPiece(piece);

        for (int i = 0; i < Utils.COLUMNS; i++) {
            piece = new Pawn(Utils.WHITE, mGame);
            alivePieces.add(piece);
            mSquares[1][i].setPiece(piece);
        }

        piece = new Rook(Utils.BLACK, mGame);
        alivePieces.add(piece);
        mSquares[7][0].setPiece(piece);

        piece = new Rook(Utils.BLACK, mGame);
        alivePieces.add(piece);
        mSquares[7][7].setPiece(piece);

        piece = new Knight(Utils.BLACK, mGame);
        alivePieces.add(piece);
        mSquares[7][1].setPiece(piece);

        piece = new Knight(Utils.BLACK, mGame);
        alivePieces.add(piece);
        mSquares[7][6].setPiece(piece);

        piece = new Bishop(Utils.BLACK, mGame);
        alivePieces.add(piece);
        mSquares[7][2].setPiece(piece);

        piece = new Bishop(Utils.BLACK, mGame);
        alivePieces.add(piece);
        mSquares[7][5].setPiece(piece);

        piece = new Queen(Utils.BLACK, mGame);
        alivePieces.add(piece);
        mSquares[7][3].setPiece(piece);

        piece = new King(Utils.BLACK, mGame);
        alivePieces.add(piece);
        mSquares[7][4].setPiece(piece);

        for (int i = 0; i < Utils.COLUMNS; i++) {
            piece = new Pawn(Utils.BLACK, mGame);
            alivePieces.add(piece);
            mSquares[6][i].setPiece(piece);
        }
    }

    public Square[][] getSquares() {
        return mSquares;
    }

    public static Square[][] rotate(Square[][] board) {
        Square[][] rotatedBoard = new Square[8][8];
        for (int i = 0; i < Utils.ROWS; i++) {
            for (int j = 0; j < Utils.COLUMNS; j++) {
                rotatedBoard[(Utils.ROWS-1)-i][(Utils.COLUMNS-1)-j] = board[i][j];
            }
        }
        return rotatedBoard;
    }
}
