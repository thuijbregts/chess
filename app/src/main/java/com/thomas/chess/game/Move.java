package com.thomas.chess.game;

import com.thomas.chess.game.pieces.Piece;
import com.thomas.chess.utils.Utils;

import java.util.ArrayList;

public class Move {

    private int mMoveType;

    private Square mSourceSquare;
    private Square mDestinationSquare;
    private Piece mMovedPiece;
    private Piece mDeadPiece;

    private Square mEnPassantSquare;

    private Square mCastlingKing;
    private Square mCastlingRook;

    private Piece mPromotedPiece;
    private Piece mPromotedPawn;

    private boolean ambiguousMove;
    private boolean sameRowFound;
    private boolean sameColumnFound;

    private boolean statesSet;
    private boolean check;
    private boolean checkmate;
    private boolean stalemate;

    private boolean whiteWon;
    private boolean blackWon;
    private boolean draw;

    public Move(int moveType, Square sourceSquare, Square destinationSquare) {
        mMoveType = moveType;
        mSourceSquare = sourceSquare;
        mDestinationSquare = destinationSquare;
        mMovedPiece = sourceSquare.getPiece();
    }

    public void make() {
        switch (mMoveType) {
            case Utils.MOVE_TYPE_NORMAL:
                makeNormalMove();
                break;
            case Utils.MOVE_TYPE_PROMOTION:
                makePromotionMove();
                break;
            case Utils.MOVE_TYPE_PASSANT:
                makeEnPassantMove();
                break;
            case Utils.MOVE_TYPE_CASTLING:
                makeCastlingMove();
                break;
        }
    }

    public void unmake() {
        switch (mMoveType) {
            case Utils.MOVE_TYPE_NORMAL:
                unmakeNormalMove();
                break;
            case Utils.MOVE_TYPE_PROMOTION:
                unmakePromotionMove();
                break;
            case Utils.MOVE_TYPE_PASSANT:
                unmakeEnPassantMove();
                break;
            case Utils.MOVE_TYPE_CASTLING:
                unmakeCastlingMove();
                break;
        }
    }

    public Piece getMovedPiece() {
        return mMovedPiece;
    }

    public Piece getDeadPiece() {
        return mDeadPiece;
    }

    public Square getSourceSquare() {
        return mSourceSquare;
    }

    public int getMoveType() {
        return mMoveType;
    }

    public Piece getPromotedPawn() {
        return mPromotedPawn;
    }

    public Piece getPromotedPiece() {
        return mPromotedPiece;
    }

    public Square getDestinationSquare() {
        return mDestinationSquare;
    }

    public Square getCastlingKing() {
        return mCastlingKing;
    }

    public Square getCastlingRook() {
        return mCastlingRook;
    }

    public boolean isCheck() {
        return check;
    }

    public boolean isCheckmate() {
        return checkmate;
    }

    public boolean isStatesSet() {
        return statesSet;
    }

    public boolean isStalemate() {
        return stalemate;
    }

    public boolean isWhiteWon() {
        return whiteWon;
    }

    public boolean isBlackWon() {
        return blackWon;
    }

    public boolean isDraw() {
        return draw;
    }

    public void setGameStates(boolean check, boolean checkmate, boolean stalemate, boolean draw) {
        this.check = check;
        this.checkmate = checkmate;
        this.stalemate = stalemate;
        this.draw = draw;

        statesSet = true;
    }

    public void checkAmbiguousMove(Player player) {
        ArrayList<Piece> alivePieces = player.getAlivePieces();
        Piece piece;
        ArrayList<Move> pieceMoves;
        for (int i = 0; i < alivePieces.size(); i++) {
            piece = alivePieces.get(i);
            if (piece.getClass().equals(mMovedPiece.getClass()) && !piece.equals(mMovedPiece)) {
                pieceMoves = piece.getMoves(false);
                for (Move move : pieceMoves) {
                    if (move.getDestinationSquare().equals(mDestinationSquare)) {
                        if (move.getSourceSquare().getRow() == mSourceSquare.getRow()) {
                            sameRowFound = true;
                        } else if (move.getSourceSquare().getColumn() == mSourceSquare.getColumn()) {
                            sameColumnFound = true;
                        }
                        ambiguousMove = true;
                    }
                }
            }
        }
    }

    public void setEnPassant(Square enPassantSquare) {
        mEnPassantSquare = enPassantSquare;
        mDeadPiece = enPassantSquare.getPiece();
    }

    public void setCastling(Square king, Square rook) {
        mCastlingKing = king;
        mCastlingRook = rook;
    }

    public void setPromotion(Piece promotedPiece) {
        mPromotedPiece = promotedPiece;
        mPromotedPawn = mMovedPiece;
    }

    private void makeNormalMove() {
        mDeadPiece = mDestinationSquare.getPiece();
        movePiece(mDestinationSquare, mMovedPiece);
        mSourceSquare.setPiece(null);
        mMovedPiece.addMovement();
    }

    private void unmakeNormalMove() {
        movePiece(mSourceSquare, mMovedPiece);
        mDestinationSquare.setPiece(mDeadPiece);
        mMovedPiece.removeMovement();
        mDeadPiece = null;
    }

    private void makeEnPassantMove() {
        mDeadPiece = mEnPassantSquare.getPiece();
        movePiece(mDestinationSquare, mMovedPiece);
        mSourceSquare.setPiece(null);
        mEnPassantSquare.setPiece(null);
        mMovedPiece.addMovement();
    }

    private void unmakeEnPassantMove() {
        mEnPassantSquare.setPiece(mDeadPiece);
        movePiece(mSourceSquare, mMovedPiece);
        mDestinationSquare.setPiece(null);
        mMovedPiece.removeMovement();
        mDeadPiece = null;
    }

    private void makePromotionMove() {
        mDeadPiece = mDestinationSquare.getPiece();
        if (mPromotedPiece == null) {
            movePiece(mDestinationSquare, mMovedPiece);
        } else {
            movePiece(mDestinationSquare, mPromotedPiece);
        }
        mSourceSquare.setPiece(null);
    }

    private void unmakePromotionMove() {
        movePiece(mSourceSquare, mMovedPiece);
        mDestinationSquare.setPiece(mDeadPiece);
        mDeadPiece = null;
    }

    private void makeCastlingMove() {
        Piece rook = mDestinationSquare.getPiece();
        movePiece(mCastlingKing, mMovedPiece);
        movePiece(mCastlingRook, rook);
        mSourceSquare.setPiece(null);
        mDestinationSquare.setPiece(null);

        //TWICE BECAUSE KING MOVES TWICE DURING CASTLING
        mMovedPiece.addMovement();
        mMovedPiece.addMovement();

        rook.addMovement();
    }

    private void unmakeCastlingMove() {
        Piece rook = mCastlingRook.getPiece();
        movePiece(mSourceSquare, mMovedPiece);
        movePiece(mDestinationSquare, rook);
        mCastlingRook.setPiece(null);
        mCastlingKing.setPiece(null);

        //TWICE BECAUSE KING MOVES TWICE DURING CASTLING
        mMovedPiece.removeMovement();
        mMovedPiece.removeMovement();

        rook.removeMovement();
    }

    private void movePiece(Square square, Piece piece) {
        square.setPiece(piece);
        piece.setSquare(square);
    }

    public String getMoveAsString() {
        String result = "";
        switch (mMoveType) {
            case Utils.MOVE_TYPE_NORMAL:
                result = getNormalMoveAsString();
                break;
            case Utils.MOVE_TYPE_CASTLING:
                result = getCastlingAsString();
                break;
            case Utils.MOVE_TYPE_PASSANT:
                result = getEnPassantAsString();
                break;
            case Utils.MOVE_TYPE_PROMOTION:
                result = getPromotionAsString();
        }
        if (checkmate) {
            result += "#";
        } else if (check) {
            result += "+";
        } else if (whiteWon) {
            result += "1-0";
        } else if (blackWon) {
            result += "0-1";
        } else if (draw) {
            result += "1⁄2– 1⁄2";
        }
        return result;
    }

    private String getNormalMoveAsString() {
        String result = "";
        result += Utils.getPieceCode(mMovedPiece);
        if (ambiguousMove) {
            if (!sameColumnFound) {
                result += Utils.getColumnCode(mSourceSquare.getColumn());
            } else if (!sameRowFound) {
                result += (mSourceSquare.getRow() + 1);
            } else {
                result += Utils.getColumnCode(mSourceSquare.getColumn());
                result += mSourceSquare.getRow() + 1;
            }
        }
        if (mDeadPiece != null) {
            result += (result.length() == 0 ? Utils.getColumnCode(mSourceSquare.getColumn()):"");
            result += "x";
        }
        result += Utils.getColumnCode(mDestinationSquare.getColumn());
        result += (mDestinationSquare.getRow() + 1);
        return result;
    }

    private String getCastlingAsString() {
        return mCastlingRook.getColumn() == 0 ? "0-0-0":"0-0";
    }

    private String getEnPassantAsString() {
        return getNormalMoveAsString() + " e.p.";
    }

    private String getPromotionAsString() {
        return getNormalMoveAsString() + Utils.getPieceCode(mPromotedPiece);
    }
}
