package com.thomas.chess.game;

import com.thomas.chess.activities.GameActivity;
import com.thomas.chess.game.pieces.King;
import com.thomas.chess.game.pieces.Pawn;
import com.thomas.chess.game.pieces.Piece;
import com.thomas.chess.game.pieces.Rook;
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

    private ArrayList<BoardThreefold> mBoardsForThreefold = new ArrayList<>();
    private int mBoardsForThreefoldCount;
    private int mPreviousBoardsCount;

    private int mMovesWithoutPawnOrTake;

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
        if (move.getMoveType() == Move.TYPE_DRAW || move.getMoveType() == Move.TYPE_RESIGN) {
            updateMovesList(move);
        } else {
            updateMovesListWithCheck(move);
            move.make();
            updatePlayersPieces(move);
            updateFiftyMovesTracking(move);
            updateBoardThreefoldList(move);
            updateGameStatus(move);
        }
        updateCurrentPlayer();
    }

    private void updateMovesListWithCheck(Move move) {
        if (mMoveCount == mMoves.size()) {
            mMoves.add(move);
            move.checkAmbiguousMove(mCurrentPlayer);
        } else if (!mMoves.get(mMoveCount).equals(move)) {
            mMoves.set(mMoveCount, move);
            mMoves.subList(mMoveCount +1, mMoves.size()).clear();
            move.checkAmbiguousMove(mCurrentPlayer);
        }
        mMoveCount++;
    }

    private void updateMovesList(Move move) {
        if (mMoveCount == mMoves.size()) {
            mMoves.add(move);
        } else if (!mMoves.get(mMoveCount).equals(move)) {
            mMoves.set(mMoveCount, move);
            mMoves.subList(mMoveCount +1, mMoves.size()).clear();
        }
        mMoveCount++;
    }

    private void updatePlayersPieces(Move move) {
        if (move.getMoveType() == Move.TYPE_PROMOTION) {
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

    private void updateGameStatus(Move move) {
        if (!move.isStatesSet()) {
            boolean check = mCurrentPlayer.hasCheck();
            boolean hasNoLegalMoves = getOpponent(mCurrentPlayer.getColor()).hasNoLegalMove();
            boolean checkmate = hasNoLegalMoves && check;
            boolean stalemate = hasNoLegalMoves && !check;
            Draw draw;
            if (stalemate) {
                draw = new Draw(Draw.STALEMATE);
            } else {
                draw = checkmate ? null : Draw.getMandatoryDraw(this);
            }
            move.setGameStates(check, checkmate, stalemate, draw);
        }
    }

    private void updateCurrentPlayer() {
        mCurrentPlayer = (mWhitePlayer.equals(mCurrentPlayer) ? mBlackPlayer : mWhitePlayer);
        mCurrentPlayer.setAllowedDraw(isGameOver() ? null : Draw.getAllowedDraw(this));
    }

    public void cancelMove() {
        if (mMoveCount > 0) {
            Move move = mMoves.get(mMoveCount - 1);
            if (!(move.getMoveType() == Move.TYPE_DRAW || move.getMoveType() == Move.TYPE_RESIGN)) {
                resumePreviousPlayerPieces(move);
                move.unmake();
                if (mMovesWithoutPawnOrTake != 0) {
                    mMovesWithoutPawnOrTake--;
                }
                mBoardsForThreefoldCount = mBoardsForThreefoldCount == 0 ? mPreviousBoardsCount
                        : mBoardsForThreefoldCount-1;
            }
            mMoveCount--;
            updateCurrentPlayer();
        }
    }

    private void resumePreviousPlayerPieces(Move move) {
        if (move.getMoveType() == Move.TYPE_PROMOTION) {
            ArrayList<Piece> alivePieces = mCurrentPlayer.getAlivePieces();
            alivePieces.set(alivePieces.indexOf(move.getPromotedPiece()), move.getPromotedPawn());
        }

        Piece deadPiece = move.getDeadPiece();
        if (deadPiece != null) {
            mCurrentPlayer.getDeadPieces().remove(deadPiece);
            mCurrentPlayer.getAlivePieces().add(deadPiece);
        }
    }

    public boolean isGameOver() {
        Move lastMove = getLastMove();
        if (lastMove == null) {
            return false;
        }
        return lastMove.isCheckmate() || lastMove.isStalemate()
                || lastMove.getDraw() != null || lastMove.isResign();
    }

    public Player getOpponent(int color) {
        return color == Utils.WHITE ? mBlackPlayer : mWhitePlayer;
    }

    public Move getLastMove() {
        return mMoveCount > 0 ? mMoves.get(mMoveCount-1) : null;
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

    public GameActivity getGameActivity() {
        return mGameActivity;
    }
}
