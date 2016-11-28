package com.thomas.chess.game;

import com.thomas.chess.pieces.Piece;
import com.thomas.chess.player.Player;
import com.thomas.chess.player.RealPlayer;

import java.util.ArrayList;

public class Game {

    private int mGameType;
    private Board mBoard;
    private Player[] mPlayers;
    private Player mCurrentPlayer;

    private ArrayList<Piece> mAlivePieces;
    private ArrayList<Piece> mDeadPieces;
    private ArrayList<Move> mMoves;

    public Game(int gameType) {
        mGameType = gameType;
        initializeGame();
    }

    public void initializeGame() {
        mBoard = new Board(this);
        mAlivePieces = new ArrayList<>();
        mDeadPieces = new ArrayList<>();
        mMoves = new ArrayList<>();
        mPlayers = new Player[2];

        mBoard.placePieces(mAlivePieces);

        switch (mGameType) {
            case Utils.GAME_SOLO:
                mPlayers[0] = mCurrentPlayer = new RealPlayer("1", Utils.WHITE);
                mPlayers[1] = new RealPlayer("2", Utils.BLACK);
                break;
            case Utils.GAME_ONLINE:
                break;
        }
    }

    public Board getBoard() {
        return mBoard;
    }

    public void makeMove(Move move) {
        move.make();
        mMoves.add(move);
        mCurrentPlayer = (mPlayers[0].equals(mCurrentPlayer) ? mPlayers[1] : mPlayers[0]);

        Piece deadPiece = move.getDeadPiece();
        if (deadPiece != null) {
            mDeadPieces.add(deadPiece);
            for (int i = 0; i < mAlivePieces.size(); i++) {
                if (mAlivePieces.get(i).equals(deadPiece)) {
                    mAlivePieces.remove(i);
                    break;
                }
            }
        }
    }

    public Player[] getPlayers() {
        return mPlayers;
    }

    public Player getCurrentPlayer() {
        return mCurrentPlayer;
    }

    public ArrayList<Move> getMoves() {
        return mMoves;
    }
}
