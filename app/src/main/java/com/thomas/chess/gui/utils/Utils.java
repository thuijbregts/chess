package com.thomas.chess.gui.utils;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.thomas.chess.R;
import com.thomas.chess.engine.chess120.*;
import com.thomas.chess.engine.movegen.SquareAttack;
import com.thomas.chess.gui.views.SquareView;

public class Utils {

    public static final String INTENT_GAME_TYPE = "GAME_TYPE";
    public static final int GAME_SOLO = 0;
    public static final int GAME_ONLINE = 1;
    public static final int GAME_VERSUS = 2;
    public static final int GAME_REVIEW = 3;

    public static final int ROWS = 8;
    public static final int COLUMNS = 8;

    public static final int PIECE_ANIMATION_DURATION = 700;
    
    public static void setImageViewForPiece(ImageView imageView, int piece) {
        int type = piece >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;
        int side = piece >> Piece.SIDE_SHIFT & 1;

        switch (type) {
            case Definitions.PAWN:
                switch (side) {
                    case Game.WHITE:
                        imageView.setImageResource(R.drawable.pawn_w);
                        break;
                    case Game.BLACK:
                        imageView.setImageResource(R.drawable.pawn_b);
                        break;
                }
                break;
            case Definitions.KNIGHT:
                switch (side) {
                    case Game.WHITE:
                        imageView.setImageResource(R.drawable.knight_w);
                        break;
                    case Game.BLACK:
                        imageView.setImageResource(R.drawable.knight_b);
                        break;
                }
                break;
            case Definitions.BISHOP:
                switch (side) {
                    case Game.WHITE:
                        imageView.setImageResource(R.drawable.bishop_w);
                        break;
                    case Game.BLACK:
                        imageView.setImageResource(R.drawable.bishop_b);
                        break;
                }
                break;
            case Definitions.ROOK:
                switch (side) {
                    case Game.WHITE:
                        imageView.setImageResource(R.drawable.rook_w);
                        break;
                    case Game.BLACK:
                        imageView.setImageResource(R.drawable.rook_b);
                        break;
                }
                break;
            case Definitions.QUEEN:
                switch (side) {
                    case Game.WHITE:
                        imageView.setImageResource(R.drawable.queen_w);
                        break;
                    case Game.BLACK:
                        imageView.setImageResource(R.drawable.queen_b);
                        break;
                }
                break;
            default:
                switch (side) {
                    case Game.WHITE:
                        imageView.setImageResource(R.drawable.king_w);
                        break;
                    case Game.BLACK:
                        imageView.setImageResource(R.drawable.king_b);
                        break;
                }
        }

    }

    public static String getPieceCode(int piece) {
        int type = piece >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;
        switch (type) {
            case Definitions.ROOK:
                return "R";
            case Definitions.BISHOP:
                return "B";
            case Definitions.KNIGHT:
                return "N";
            case Definitions.QUEEN:
                return "Q";
            case Definitions.KING:
                return "K";
        }
        return "";
    }

    public static char getColumnCode(int column) {
        switch (column) {
            case 0:
                return 'a';
            case 1:
                return 'b';
            case 2:
                return 'c';
            case 3:
                return 'd';
            case 4:
                return 'e';
            case 5:
                return 'f';
            case 6:
                return 'g';
            case 7:
                return 'h';
            default:
                return 'x';
        }
    }

    public static TranslateAnimation getAnimation(SquareView source, SquareView destination) {
        int[] coordinates = new int[2];
        source.getLocationOnScreen(coordinates);
        int sourceX = coordinates[0];
        int sourceY = coordinates[1];
        destination.getLocationOnScreen(coordinates);
        int destinationX = coordinates[0];
        int destinationY = coordinates[1];
        TranslateAnimation animation = new TranslateAnimation(Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, destinationX-sourceX,
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, destinationY-sourceY);
        animation.setDuration(Utils.PIECE_ANIMATION_DURATION);
        animation.setRepeatCount(0);
        animation.setZAdjustment(Animation.ZORDER_TOP);
        animation.setFillAfter(false);

        return animation;
    }

    public static String getMoveAsString(Move move, Game game) {
        if (move.isResign()) {
            return "Resign";
        }
        String result;
        switch (move.getType()) {
            default:
                result = getNormalMoveAsString(move, game);
                break;
            case Move.CASTLING:
                result = getCastlingAsString(move);
                break;
            case Move.EN_PASSANT:
                result = getEnPassantAsString(move, game);
                break;
            case Move.PROMOTION:
                result = getPromotionAsString(move, game);
                break;
        }
        if (move.isCheckmate()) {
            result += "#";
        } else if (move.isCheck()) {
            result += "+";
        } /*else if (mDraw != null) {
            result += "1â�„2â€“â€Š1â�„2";
        }*/
        return result;
    }

    private static String getNormalMoveAsString(Move move, Game game) {
        String result = "";
        int movedPiece = move.getMovedPiece();
        result += Utils.getPieceCode(movedPiece);

        int src = move.getSourceSquare();
        int dst = move.getDestinationSquare();
        int srcRank = Definitions.RANKS[src];
        int srcFile = Definitions.FILES[src];
        int dstRank = Definitions.RANKS[dst];
        int dstFile = Definitions.FILES[dst];

        int ambiguousType = isAmbiguousMove(src, dst, movedPiece, game.getPieces()[move.getSide()], game.getBoard(), game);
        switch (ambiguousType) {
            case 1:
                result += Utils.getColumnCode(srcFile);
                break;
            case 2:
                result += (srcRank + 1);
                break;
            case 3:
                result += Utils.getColumnCode(srcFile);
                result += (srcRank+ 1);
        }
        if (move.isCapture()) {
            result += (result.length() == 0 ? Utils.getColumnCode(srcFile):"");
            result += "x";
        }
        result += Utils.getColumnCode(dstFile);
        result += (dstRank + 1);
        return result;
    }

    private static String getCastlingAsString(Move move) {
        return Definitions.FILES[move.getDestinationRook()] == 0 ? "0-0-0":"0-0";
    }

    private static String getEnPassantAsString(Move move, Game game) {
        return getNormalMoveAsString(move, game) + " e.p.";
    }

    private static String getPromotionAsString(Move move, Game game) {
        String result = "";
        int movedPiece = move.getPromotedPawn();
        result += Utils.getPieceCode(movedPiece);

        int src = move.getSourceSquare();
        int dst = move.getDestinationSquare();
        int srcRank = Definitions.RANKS[src];
        int srcFile = Definitions.FILES[src];
        int dstRank = Definitions.RANKS[dst];
        int dstFile = Definitions.FILES[dst];

        int ambiguousType = isAmbiguousMove(src, dst, movedPiece, game.getPieces()[move.getSide()], game.getBoard(), game);
        switch (ambiguousType) {
            case 1:
                result += Utils.getColumnCode(srcFile);
                break;
            case 2:
                result += (srcRank + 1);
                break;
            case 3:
                result += Utils.getColumnCode(srcFile);
                result += (srcRank + 1);
        }
        if (move.getDeadPiece() != -1) {
            result += (result.length() == 0 ? Utils.getColumnCode(srcFile):"");
            result += "x";
        }
        result += Utils.getColumnCode(dstFile);
        result += (dstRank + 1);
        result += Utils.getPieceCode(move.getPromotedPiece());
        return result;
    }

    //returns 0 if not ambiguous - 1 if not same column, 2 if not same rank, 3 if both
    private static int isAmbiguousMove(int src, int dest, int movedPiece,
                                       int[] pieces, int[] board, Game game) {
        int result = 0;
        int srcFile = Definitions.FILES[src];
        int srcRank = Definitions.RANKS[src];
        int destFile = Definitions.FILES[dest];
        int type = movedPiece >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;
        if (type == Definitions.PAWN) {
            if (destFile != srcFile) {
                int piece;
                if (destFile < srcFile) {
                    piece = board[src-2];
                    if (piece != Definitions.SQ_OFFBOARD && piece != Definitions.SQ_EMPTY
                            && (piece & Piece.TYPE_ONLY) == (movedPiece & Piece.TYPE_ONLY)
                            && (piece & Piece.SIDE_ONLY) == (movedPiece & Piece.SIDE_ONLY)) {
                        return 1;
                    }
                } else if (destFile > srcFile){
                    piece = board[src+2];
                    if (piece != Definitions.SQ_OFFBOARD && piece != Definitions.SQ_EMPTY
                            && (piece & Piece.TYPE_ONLY) == (movedPiece & Piece.TYPE_ONLY)
                            && (piece & Piece.SIDE_ONLY) == (movedPiece & Piece.SIDE_ONLY)) {
                        return 1;
                    }
                }
            }
            return 0;
        }
        for (int piece : pieces) {
            int pType = piece >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;
            if ((piece & Piece.DEAD_FLAG) == 0 && pType == type && piece != movedPiece) {
                if (SquareAttack.attacksSquare(piece, dest, game)) {
                    int square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
                    if (srcFile != Definitions.FILES[square]) {
                        result += 1;
                    } else if (srcRank != Definitions.RANKS[square]) {
                        result += 2;
                    }
                    if (result == 3) {
                        return result;
                    }
                }
            }
        }
        return result;
    }

}
