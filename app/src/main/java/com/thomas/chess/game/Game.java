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

    private ArrayList<Move> mMoves;

    private boolean promotion;
    private boolean check;
    private boolean checkmate;
    private boolean stalemate;
    private boolean draw;

    public Game(int gameType) {
        mGameType = gameType;
        initializeGame();
    }

    public void initializeGame() {
        mBoard = new Board(this);
        mMoves = new ArrayList<>();
        mPlayers = new Player[2];

        switch (mGameType) {
            case Utils.GAME_SOLO:
                mPlayers[0] = mCurrentPlayer = new RealPlayer("1", Utils.WHITE);
                mPlayers[1] = new RealPlayer("2", Utils.BLACK);
                break;
            case Utils.GAME_ONLINE:
                break;
        }

        mBoard.placePieces();
    }

    public Board getBoard() {
        return mBoard;
    }

    public void executeMove(Move move) {
        move.make();
        mMoves.add(move);
        if (move.getMoveType() == Utils.MOVE_TYPE_PROMOTION) {
            promotion = true;
            ArrayList<Piece> alivePieces = mCurrentPlayer.getAlivePieces();
            for (int i = 0; i < alivePieces.size(); i++) {
                if (alivePieces.get(i).equals(move.getPromotedPawn())) {
                    alivePieces.set(i, move.getPromotedPiece());
                    break;
                }
            }
        }

        mCurrentPlayer = (mPlayers[0].equals(mCurrentPlayer) ? mPlayers[1] : mPlayers[0]);

        Piece deadPiece = move.getDeadPiece();
        if (deadPiece != null) {
            mCurrentPlayer.getDeadPieces().add(deadPiece);
            ArrayList<Piece> alivePieces = mCurrentPlayer.getAlivePieces();
            for (int i = 0; i < alivePieces.size(); i++) {
                if (alivePieces.get(i).equals(deadPiece)) {
                    alivePieces.remove(i);
                    break;
                }
            }
        }

        updateGameStatus();
    }

    private void updateGameStatus() {
        check = isInCheck();
        checkmate = false;
        stalemate = false;
        draw = false;

        boolean hasNoLegalMoves = hasNoLegalMove();

        if (check) {
            checkmate = hasNoLegalMoves;
        } else {
            stalemate = hasNoLegalMoves;
        }
    }

    private boolean isInCheck() {
        ArrayList<Piece> opponentPieces = getOpponent(mCurrentPlayer.getColor()).getAlivePieces();
        for (Piece piece : opponentPieces) {
            if (piece.hasCheck()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasNoLegalMove() {
        ArrayList<Piece> myPieces = mCurrentPlayer.getAlivePieces();
        for (Piece piece : myPieces) {
            if (!piece.getMoves(false).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public Player[] getPlayers() {
        return mPlayers;
    }

    public Player getOpponent(int color) {
        return (mPlayers[0].getColor() == color ? mPlayers[1] : mPlayers[0]);
    }

    public Player getWhitePLayer() {
        return (mPlayers[0].getColor() == Utils.WHITE ? mPlayers[0] : mPlayers[1]);
    }

    public Player getBlackPlayer() {
        return (mPlayers[0].getColor() == Utils.WHITE ? mPlayers[1] : mPlayers[0]);
    }

    public Player getCurrentPlayer() {
        return mCurrentPlayer;
    }

    public ArrayList<Move> getMoves() {
        return mMoves;
    }

    public boolean isCheck() {
        return check;
    }

    public boolean isCheckmate() {
        return checkmate;
    }

    public boolean isDraw() {
        return draw;
    }

    public boolean isStalemate() {
        return stalemate;
    }

    public boolean isPromotion() {
        return promotion;
    }
}
