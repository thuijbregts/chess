package com.thomas.chess.game;

import android.util.Log;

import com.thomas.chess.game.pieces.Bishop;
import com.thomas.chess.game.pieces.King;
import com.thomas.chess.game.pieces.Knight;
import com.thomas.chess.game.pieces.Pawn;
import com.thomas.chess.game.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class Draw {

    public static final int STALEMATE = 0;
    public static final int FIFTY_MOVES = 1;
    public static final int THREEFOLD = 2;
    public static final int AGREEMENT = 3;
    public static final int NO_CHECKMATE = 4;
    public static final int SEVENTY_FIVE_MOVES = 1;

    private int mDrawType;

    public Draw(int drawType) {
        mDrawType = drawType;
    }

    public int getDrawType() {
        return mDrawType;
    }

    public static Draw checkAllowedDraw(Game game) {
        if (game.getMovesWithoutPawnOrTake()/2 == 50) {
            return new Draw(Draw.FIFTY_MOVES);
        }
        if (game.getMovesWithoutPawnOrTake()/2 == 49) {
            for (Piece piece : game.getCurrentPlayer().getAlivePieces()) {
                if (!(piece instanceof Pawn)) {
                    List<Move> moves = piece.getMoves(false);
                    for (Move move : moves) {
                        if (move.getDestinationSquare().getPiece() != null) {
                            return new Draw(Draw.FIFTY_MOVES);
                        }
                    }
                }
            }
        }
        if (isThreefold(game.getBoardsForThreefold(), game.getBoardsForThreefoldCount())) {
            return new Draw(Draw.THREEFOLD);
        }
        return null;
    }

    private static boolean isThreefold(List<BoardThreefold> boardThreefolds, int boardsCount) {
        if (boardThreefolds.size() < 3) {
            return false;
        }
        int lastIndex = boardsCount-1;
        BoardThreefold last = boardThreefolds.get(lastIndex);
        int sameStateCount = 0;
        for (int i = lastIndex-2; i >= 0; i = i-2) {
            if (last.isSameBoardState(boardThreefolds.get(i))) {
                sameStateCount++;
            }
        }
        return sameStateCount >= 3;
    }

    public static Draw checkMandatoryDraw(Game game) {
        if (game.getMovesWithoutPawnOrTake()/2 == 75) {
            return new Draw(Draw.SEVENTY_FIVE_MOVES);
        }
        if (hasNoCheckmatePossible(game)) {
            return new Draw(Draw.NO_CHECKMATE);
        }
        return null;
    }

    private static boolean hasNoCheckmatePossible(Game game) {
        List<Piece> blackPieces = game.getBlackPlayer().getAlivePieces();
        List<Piece> whitePieces = game.getWhitePlayer().getAlivePieces();
        if (blackPieces.size() == 1 && whitePieces.size() == 1) {
            return true;
        }
        if (blackPieces.size() == 1 && whitePieces.size() == 2) {
            for (Piece piece : whitePieces) {
                if (piece instanceof King) {
                    continue;
                }
                if (piece instanceof Bishop || piece instanceof Knight) {
                    return true;
                }
            }
        }
        if (blackPieces.size() == 2 && whitePieces.size() == 1) {
            for (Piece piece : blackPieces) {
                if (piece instanceof King) {
                    continue;
                }
                if (piece instanceof Bishop || piece instanceof Knight) {
                    return true;
                }
            }
        }
        ArrayList<Piece> bishops = new ArrayList<>();
        boolean onlyBishops = true;

        for (Piece blackPiece : blackPieces) {
            if (blackPiece instanceof King) {
                continue;
            }
            if (blackPiece instanceof Bishop) {
                bishops.add(blackPiece);
            } else {
                onlyBishops = false;
                break;
            }
        }
        if (onlyBishops) {
            for (Piece whitePiece : whitePieces) {
                if (whitePiece instanceof King) {
                    continue;
                }
                if (whitePiece instanceof Bishop) {
                    bishops.add(whitePiece);
                } else {
                    onlyBishops = false;
                    break;
                }
            }
            if (onlyBishops && allBishopsOnSameColor(bishops)) {
                return true;
            }
        }
        return false;
    }

    private static boolean allBishopsOnSameColor(List<Piece> bishops) {
        int color = bishops.get(0).getSquare().getColor();
        for (int i = 1; i < bishops.size(); i++) {
            if (bishops.get(i).getSquare().getColor() != color) {
                return false;
            }
        }
        return true;
    }

}
