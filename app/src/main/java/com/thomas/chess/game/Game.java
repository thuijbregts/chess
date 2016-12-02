package com.thomas.chess.game;

import com.thomas.chess.activities.GameActivity;
import com.thomas.chess.game.pieces.Piece;
import com.thomas.chess.game.p_children.AIPlayer;
import com.thomas.chess.game.p_children.RealPlayer;
import com.thomas.chess.utils.Utils;

import java.util.ArrayList;

public class Game {

    private GameActivity mGameActivity;

    private Board mBoard;
    private Player mWhitePlayer;
    private Player mBlackPlayer;
    private Player mCurrentPlayer;

    private ArrayList<Move> mMoves;
    private int mMoveCount;

    private boolean check;
    private boolean checkmate;
    private boolean stalemate;
    private boolean draw;

    public Game(Player whitePlayer, Player blackPlayer, GameActivity gameActivity) {
        mGameActivity = gameActivity;
        mWhitePlayer = mCurrentPlayer = whitePlayer;
        mBlackPlayer = blackPlayer;
        mWhitePlayer.setGame(this);
        mBlackPlayer.setGame(this);
        initializeGame();
    }

    public void initializeGame() {
        mBoard = new Board(this);
        mMoves = new ArrayList<>();
        mBoard.placePieces();
    }

    public Board getBoard() {
        return mBoard;
    }

    public void executeMove(Move move) {
        if (mMoveCount == mMoves.size()) {
            mMoves.add(move);
            move.checkAmbiguousMove(mCurrentPlayer);
        } else if (!mMoves.get(mMoveCount).equals(move)) {
            mMoves.set(mMoveCount, move);
            mMoves.subList(mMoveCount +1, mMoves.size()).clear();
            move.checkAmbiguousMove(mCurrentPlayer);
        }

        move.make();

        if (move.getMoveType() == Utils.MOVE_TYPE_PROMOTION) {
            ArrayList<Piece> alivePieces = mCurrentPlayer.getAlivePieces();
            for (int i = 0; i < alivePieces.size(); i++) {
                if (alivePieces.get(i).equals(move.getPromotedPawn())) {
                    alivePieces.set(i, move.getPromotedPiece());
                    break;
                }
            }
        }

        mCurrentPlayer = (mWhitePlayer.equals(mCurrentPlayer) ? mBlackPlayer : mWhitePlayer);

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

        mMoveCount++;
        updateGameStatus();
    }

    public void cancelMove() {
        if (mMoveCount > 0) {
            Move move = mMoves.get(mMoveCount -1);
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

            mCurrentPlayer = (mWhitePlayer.equals(mCurrentPlayer) ? mBlackPlayer : mWhitePlayer);

            move.unmake();

            mMoveCount--;
            resumePreviousGameStatus();
        }
    }

    private void updateGameStatus() {
        Move lastMove = mMoves.get(mMoveCount-1);
        if (!lastMove.isStatesSet()) {
            check = getOpponent(mCurrentPlayer.getColor()).hasCheck();
            boolean hasNoLegalMoves = mCurrentPlayer.hasNoLegalMove();
            checkmate = hasNoLegalMoves && check;
            stalemate = hasNoLegalMoves && !check;
            draw = false;

            lastMove.setGameStates(check, checkmate, stalemate, draw);
        } else {
            check = lastMove.isCheck();
            checkmate = lastMove.isCheckmate();
            stalemate = lastMove.isStalemate();
            draw = lastMove.isDraw();
        }
    }

    private void resumePreviousGameStatus() {
        checkmate = stalemate = draw = false;
        if (mMoveCount == 0) {
            check = false;
        } else {
            Move move = mMoves.get(mMoveCount-1);
            check = move.isCheck();
        }
    }

    public Player getOpponent(int color) {
        return color == Utils.WHITE ? mBlackPlayer : mWhitePlayer;
    }

    public Player getWhitePlayer() {
        return mWhitePlayer;
    }

    public Player getBlackPlayer() {
        return mBlackPlayer;
    }

    public int getMoveCount() {
        return mMoveCount;
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

    public GameActivity getGameActivity() {
        return mGameActivity;
    }
}
