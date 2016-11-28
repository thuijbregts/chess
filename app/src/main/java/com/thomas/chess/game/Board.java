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

    public void placePieces() {
        Piece piece;
        Square square;

        square = mSquares[0][0];
        piece = new Rook(Utils.WHITE, square, mGame);
        mGame.getPlayers()[0].getAlivePieces().add(piece);
        square.setPiece(piece);

        square = mSquares[0][7];
        piece = new Rook(Utils.WHITE, square, mGame);
        mGame.getPlayers()[0].getAlivePieces().add(piece);
        square.setPiece(piece);

        square = mSquares[0][1];
        piece = new Knight(Utils.WHITE, square, mGame);
        mGame.getPlayers()[0].getAlivePieces().add(piece);
        square.setPiece(piece);

        square = mSquares[0][6];
        piece = new Knight(Utils.WHITE, square, mGame);
        mGame.getPlayers()[0].getAlivePieces().add(piece);
        square.setPiece(piece);

        square = mSquares[0][2];
        piece = new Bishop(Utils.WHITE, square, mGame);
        mGame.getPlayers()[0].getAlivePieces().add(piece);
        square.setPiece(piece);

        square = mSquares[0][5];
        piece = new Bishop(Utils.WHITE, square, mGame);
        mGame.getPlayers()[0].getAlivePieces().add(piece);
        square.setPiece(piece);

        square = mSquares[0][3];
        piece = new Queen(Utils.WHITE, square, mGame);
        mGame.getPlayers()[0].getAlivePieces().add(piece);
        square.setPiece(piece);

        square = mSquares[0][4];
        piece = new King(Utils.WHITE, square, mGame);
        mGame.getPlayers()[0].getAlivePieces().add(piece);
        square.setPiece(piece);

        for (int i = 0; i < Utils.COLUMNS; i++) {
            square = mSquares[1][i];
            piece = new Pawn(Utils.WHITE, square, mGame);
            mGame.getPlayers()[0].getAlivePieces().add(piece);
            square.setPiece(piece);
        }

        square = mSquares[7][0];
        piece = new Rook(Utils.BLACK, square, mGame);
        mGame.getPlayers()[1].getAlivePieces().add(piece);
        square.setPiece(piece);

        square = mSquares[7][7];
        piece = new Rook(Utils.BLACK, square, mGame);
        mGame.getPlayers()[1].getAlivePieces().add(piece);
        square.setPiece(piece);

        square = mSquares[7][1];
        piece = new Knight(Utils.BLACK, square, mGame);
        mGame.getPlayers()[1].getAlivePieces().add(piece);
        square.setPiece(piece);

        square = mSquares[7][6];
        piece = new Knight(Utils.BLACK, square, mGame);
        mGame.getPlayers()[1].getAlivePieces().add(piece);
        square.setPiece(piece);

        square = mSquares[7][2];
        piece = new Bishop(Utils.BLACK, square, mGame);
        mGame.getPlayers()[1].getAlivePieces().add(piece);
        square.setPiece(piece);

        square = mSquares[7][5];
        piece = new Bishop(Utils.BLACK, square, mGame);
        mGame.getPlayers()[1].getAlivePieces().add(piece);
        square.setPiece(piece);

        square = mSquares[7][3];
        piece = new Queen(Utils.BLACK, square, mGame);
        mGame.getPlayers()[1].getAlivePieces().add(piece);
        square.setPiece(piece);

        square = mSquares[7][4];
        piece = new King(Utils.BLACK, square, mGame);
        mGame.getPlayers()[1].getAlivePieces().add(piece);
        square.setPiece(piece);

        for (int i = 0; i < Utils.COLUMNS; i++) {
            square = mSquares[6][i];
            piece = new Pawn(Utils.BLACK, square, mGame);
            mGame.getPlayers()[1].getAlivePieces().add(piece);
            square.setPiece(piece);
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
