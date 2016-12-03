package com.thomas.chess.game;

import android.util.Log;

import com.thomas.chess.activities.GameActivity;
import com.thomas.chess.game.pieces.Bishop;
import com.thomas.chess.game.pieces.King;
import com.thomas.chess.game.pieces.Knight;
import com.thomas.chess.game.pieces.Pawn;
import com.thomas.chess.game.pieces.Piece;
import com.thomas.chess.game.p_children.AIPlayer;
import com.thomas.chess.game.p_children.RealPlayer;
import com.thomas.chess.game.pieces.Rook;
import com.thomas.chess.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private GameActivity mGameActivity;

    private Board mBoard;
    private Player mWhitePlayer;
    private Player mBlackPlayer;
    private Player mCurrentPlayer;

    private ArrayList<Move> mMoves;
    private int mMoveCount;

    private ArrayList<BoardThreefold> mBoardsForThreefold = new ArrayList<>();
    private int mBoardsForThreefoldCount;
    private int mPreviousBoardsCount;

    private int mMovesWithoutPawnOrTake;

    private boolean check;
    private boolean checkmate;
    private boolean stalemate;
    private boolean surrender;

    private Draw mDraw;
    private Draw mAllowedDraw;

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
        updateMovesList(move);
        move.make();
        updatePlayersPieces(move);
        updateFiftyMovesTracking(move);
        updateBoardThreefoldList(move);
        mMoveCount++;
        updateGameStatus();
        updateCurrentPlayer();
    }

    private void updateMovesList(Move move) {
        if (mMoveCount == mMoves.size()) {
            mMoves.add(move);
            move.checkAmbiguousMove(mCurrentPlayer);
        } else if (!mMoves.get(mMoveCount).equals(move)) {
            mMoves.set(mMoveCount, move);
            mMoves.subList(mMoveCount +1, mMoves.size()).clear();
            move.checkAmbiguousMove(mCurrentPlayer);
        }
    }

    private void updatePlayersPieces(Move move) {
        if (move.getMoveType() == Utils.MOVE_TYPE_PROMOTION) {
            ArrayList<Piece> alivePieces = mCurrentPlayer.getAlivePieces();
            alivePieces.set(alivePieces.indexOf(move.getPromotedPawn()), move.getPromotedPiece());
        }

        Player opponent = getOpponent(mCurrentPlayer.getColor());

        Piece deadPiece = move.getDeadPiece();
        if (deadPiece != null) {
            opponent.getDeadPieces().add(deadPiece);
            ArrayList<Piece> alivePieces = opponent.getAlivePieces();
            alivePieces.remove(deadPiece);
        }
    }

    private void updateFiftyMovesTracking(Move move) {
        Piece movedPiece = move.getMovedPiece();
        Piece deadPiece = move.getDeadPiece();
        if (movedPiece instanceof Pawn || deadPiece != null) {
            mMovesWithoutPawnOrTake = 0;
        } else {
            mMovesWithoutPawnOrTake++;
        }
    }

    private void updateBoardThreefoldList(Move move) {
        Piece movedPiece = move.getMovedPiece();
        Piece deadPiece = move.getDeadPiece();

        mPreviousBoardsCount = mBoardsForThreefoldCount;
        if (((movedPiece instanceof King || movedPiece instanceof Rook)
                && movedPiece.getMovements() == 1) || movedPiece instanceof Pawn
                || deadPiece != null) {
            mBoardsForThreefoldCount = 0;
        }
        if (mBoardsForThreefold.size() == mBoardsForThreefoldCount) {
            mBoardsForThreefold.add(new BoardThreefold(mBoard.getSquares()));
        } else {
            mBoardsForThreefold.set(mBoardsForThreefoldCount, new BoardThreefold(mBoard.getSquares()));
        }
        mBoardsForThreefoldCount++;
    }

    private void updateGameStatus() {
        Move lastMove = mMoves.get(mMoveCount - 1);
        if (!lastMove.isStatesSet()) {
            check = mCurrentPlayer.hasCheck();
            boolean hasNoLegalMoves = getOpponent(mCurrentPlayer.getColor()).hasNoLegalMove();
            checkmate = hasNoLegalMoves && check;
            stalemate = hasNoLegalMoves && !check;
            if (stalemate) {
                mDraw = new Draw(Draw.STALEMATE);
            } else {
                mDraw = checkmate ? null : Draw.checkMandatoryDraw(this);
                mAllowedDraw = checkmate ? null : Draw.checkAllowedDraw(this);
            }
            lastMove.setGameStates(check, checkmate, stalemate, mDraw, mAllowedDraw);
        } else {
            check = lastMove.isCheck();
            checkmate = lastMove.isCheckmate();
            stalemate = lastMove.isStalemate();
            mDraw = lastMove.getDraw();
            mAllowedDraw = lastMove.getAllowedDraw();
        }
    }

    private void updateCurrentPlayer() {
        mCurrentPlayer = (mWhitePlayer.equals(mCurrentPlayer) ? mBlackPlayer : mWhitePlayer);
    }

    public void cancelMove() {
        if (mMoveCount > 0) {
            Move move = mMoves.get(mMoveCount -1);
            resumePreviousPlayerPieces(move);
            move.unmake();
            if (mMovesWithoutPawnOrTake != 0) {
                mMovesWithoutPawnOrTake--;
            }
            mBoardsForThreefoldCount = mPreviousBoardsCount;
            mMoveCount--;
            resumePreviousGameStatus();
            updateCurrentPlayer();
        }
    }

    private void resumePreviousPlayerPieces(Move move) {
        if (move.getMoveType() == Utils.MOVE_TYPE_PROMOTION) {
            ArrayList<Piece> alivePieces = mCurrentPlayer.getAlivePieces();
            alivePieces.set(alivePieces.indexOf(move.getPromotedPiece()), move.getPromotedPawn());
        }

        Piece deadPiece = move.getDeadPiece();
        if (deadPiece != null) {
            mCurrentPlayer.getDeadPieces().remove(deadPiece);
            mCurrentPlayer.getAlivePieces().add(deadPiece);
        }
    }

    private void resumePreviousGameStatus() {
        checkmate = stalemate = false;
        mDraw = null;
        if (mMoveCount == 0) {
            check = false;
        } else {
            Move move = mMoves.get(mMoveCount-1);
            check = move.isCheck();
            mAllowedDraw = move.getAllowedDraw();
        }
    }

    public boolean isGameOver() {
        return checkmate || stalemate || mDraw != null || surrender;
    }

    public void surrender() {
        surrender = true;
        Move move = mMoves.get(mMoveCount-1);
        switch (mCurrentPlayer.getColor()) {
            case Utils.WHITE:
                move.setBlackWon(true);
                break;
            case Utils.BLACK:
                move.setWhiteWon(true);
                break;
        }
    }

    public void claimDraw() {
        if (mAllowedDraw != null) {
            mDraw = mAllowedDraw;
        }
    }

    public Player getOpponent(int color) {
        return color == Utils.WHITE ? mBlackPlayer : mWhitePlayer;
    }

    public ArrayList<BoardThreefold> getBoardsForThreefold() {
        return mBoardsForThreefold;
    }

    public int getBoardsForThreefoldCount() {
        return mBoardsForThreefoldCount;
    }

    public int getMovesWithoutPawnOrTake() {
        return mMovesWithoutPawnOrTake;
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

    public boolean isStalemate() {
        return stalemate;
    }

    public GameActivity getGameActivity() {
        return mGameActivity;
    }
}
