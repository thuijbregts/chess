package com.thomas.chess.game.p_children;

import com.thomas.chess.game.Move;
import com.thomas.chess.game.Player;
import com.thomas.chess.game.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class AIPlayer extends Player {

    public AIPlayer(int color) {
        super(color);
        mName = "Computer";
    }

    @Override
    public void play() {
        List<Move> allMoves = new ArrayList<>();

        for (Piece piece : mAlivePieces) {
            allMoves.addAll(piece.getMoves(false));
        }

        Move bestMove = null;
        for (Move move : allMoves) {
            if (!move.getDestinationSquare().isEmpty() && move.getDestinationSquare().getPiece().getColor() != mColor) {
                if (bestMove == null) {
                    bestMove = move;
                }
                else {
                    Piece piece = move.getDestinationSquare().getPiece();
                    Piece bestPiece = bestMove.getDestinationSquare().getPiece();
                    if (piece.getValue() > bestPiece.getValue()) {
                        bestMove = move;
                    }
                }
            }
        }

        if (bestMove != null) {
            mGame.getGameActivity().executeMove(bestMove, true);
        } else {
            mGame.getGameActivity().executeMove(allMoves.get((int)Math.floor(Math.random()*allMoves.size())), true);
        }
    }
}
