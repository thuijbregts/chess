package com.thomas.chess.game;

import com.thomas.chess.game.pieces.Piece;
import com.thomas.chess.utils.Utils;

import java.util.ArrayList;

public class Move {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_PASSANT = 1;
    public static final int TYPE_CASTLING = 2;
    public static final int TYPE_PROMOTION = 3;
    public static final int TYPE_RESIGN = 4;
    public static final int TYPE_DRAW = 5;

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

    private boolean resign;
    private boolean whiteWon;
    private boolean blackWon;
    private Draw mDraw;

    //Empty constructor for resign only
    public Move() {
        mMoveType = TYPE_RESIGN;
        resign = true;
    }

    public Move(int moveType, Square sourceSquare, Square destinationSquare) {
        mMoveType = moveType;
        mSourceSquare = sourceSquare;
        mDestinationSquare = destinationSquare;
        mMovedPiece = sourceSquare.getPiece();
    }

    public Move(Draw draw) {
        mMoveType = TYPE_DRAW;
        mDraw = draw;
    }

    public void make() {
        switch (mMoveType) {
            case TYPE_NORMAL:
                makeNormalMove();
                break;
            case TYPE_PROMOTION:
                makePromotionMove();
                break;
            case TYPE_PASSANT:
                makeEnPassantMove();
                break;
            case TYPE_CASTLING:
                makeCastlingMove();
                break;
        }
    }

    public void unmake() {
        switch (mMoveType) {
            case TYPE_NORMAL:
                unmakeNormalMove();
                break;
            case TYPE_PROMOTION:
                unmakePromotionMove();
                break;
            case TYPE_PASSANT:
                unmakeEnPassantMove();
                break;
            case TYPE_CASTLING:
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

    public boolean isResign() {
        return resign;
    }

    public void setResign(boolean resign) {
        this.resign = resign;
    }

    public void setBlackWon(boolean blackWon) {
        resign = true;
        this.blackWon = blackWon;
    }

    public void setWhiteWon(boolean whiteWon) {
        resign = true;
        this.whiteWon = whiteWon;
    }

    public Draw getDraw() {
        return mDraw;
    }

    public void setGameStates(boolean check, boolean checkmate, boolean stalemate, Draw draw) {
        this.check = check;
        this.checkmate = checkmate;
        this.stalemate = stalemate;
        mDraw = draw;

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
        mSourceSquare.clear();
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
        mSourceSquare.clear();
        mEnPassantSquare.clear();
        mMovedPiece.addMovement();
    }

    private void unmakeEnPassantMove() {
        mEnPassantSquare.setPiece(mDeadPiece);
        movePiece(mSourceSquare, mMovedPiece);
        mDestinationSquare.clear();
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
        mSourceSquare.clear();
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
        mSourceSquare.clear();
        mDestinationSquare.clear();

        //TWICE BECAUSE KING MOVES TWICE DURING CASTLING
        mMovedPiece.addMovement();
        mMovedPiece.addMovement();

        rook.addMovement();
    }

    private void unmakeCastlingMove() {
        Piece rook = mCastlingRook.getPiece();
        movePiece(mSourceSquare, mMovedPiece);
        movePiece(mDestinationSquare, rook);
        mCastlingRook.clear();
        mCastlingKing.clear();

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
            case TYPE_NORMAL:
                result = getNormalMoveAsString();
                break;
            case TYPE_CASTLING:
                result = getCastlingAsString();
                break;
            case TYPE_PASSANT:
                result = getEnPassantAsString();
                break;
            case TYPE_PROMOTION:
                result = getPromotionAsString();
                break;
            case TYPE_RESIGN:
                return "resign";
        }
        if (checkmate) {
            result += "#";
        } else if (check) {
            result += "+";
        } else if (whiteWon) {
            result += "1-0";
        } else if (blackWon) {
            result += "0-1";
        } else if (mDraw != null) {
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
