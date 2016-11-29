package com.thomas.chess.game;

import com.thomas.chess.pieces.Piece;
import com.thomas.chess.player.AIPlayer;
import com.thomas.chess.player.Player;
import com.thomas.chess.player.RealPlayer;

import java.util.ArrayList;

public class Game {

    private int mGameType;
    private Board mBoard;
    private Player[] mPlayers;
    private Player mCurrentPlayer;

    private ArrayList<Move> mMoves;

    private boolean check;
    private boolean checkmate;
    private boolean stalemate;
    private boolean draw;

    private boolean waitForOpponent;

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
                mPlayers[0] = mCurrentPlayer = new RealPlayer("You", Utils.WHITE);
                mPlayers[1] = new AIPlayer(Utils.BLACK);
                break;
            case Utils.GAME_ONLINE:
                mPlayers[0] = mCurrentPlayer = new RealPlayer("1", Utils.WHITE);
                mPlayers[1] = new RealPlayer("2", Utils.BLACK);
                break;
            case Utils.GAME_TWO_PLAYERS:
                mPlayers[0] = mCurrentPlayer = new RealPlayer("Player 1", Utils.WHITE);
                mPlayers[1] = new RealPlayer("Player 2", Utils.BLACK);
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
<<<<<<< HEAD
            promotion = true;
=======
>>>>>>> develop
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

    public void cancelMove() {
        if (!mMoves.isEmpty()) {
            Move move = mMoves.get(mMoves.size() - 1);
            if (move.getMoveType() == Utils.MOVE_TYPE_PROMOTION) {
                ArrayList<Piece> alivePieces = mCurrentPlayer.getAlivePieces();
                for (int i = 0; i < alivePieces.size(); i++) {
                    if (alivePieces.get(i).equals(move.getPromotedPiece())) {
                        alivePieces.set(i, move.getPromotedPawn());
                        break;
                    }
                }
            }

            Piece deadPiece = move.getDeadPiece();
            if (deadPiece != null) {
                mCurrentPlayer.getDeadPieces().remove(deadPiece);
                mCurrentPlayer.getAlivePieces().add(deadPiece);
            }

            mCurrentPlayer = (mPlayers[0].equals(mCurrentPlayer) ? mPlayers[1] : mPlayers[0]);

            move.unmake();
            mMoves.remove(move);

            updateGameStatus();
        }
    }

    private void updateGameStatus() {
        check = getOpponent(mCurrentPlayer.getColor()).isInCheck();
        checkmate = false;
        stalemate = false;
        draw = false;

        boolean hasNoLegalMoves = mCurrentPlayer.hasNoLegalMove();

        if (check) {
            checkmate = hasNoLegalMoves;
        } else {
            stalemate = hasNoLegalMoves;
        }

        waitForOpponent = (mGameType != Utils.GAME_TWO_PLAYERS
                && mCurrentPlayer.getColor() == Utils.BLACK);
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

    public boolean isWaitForOpponent() {
        return waitForOpponent;
    }
}
