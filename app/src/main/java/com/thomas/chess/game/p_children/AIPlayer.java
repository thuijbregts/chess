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

        mGame.getGameActivity().executeMove(allMoves.get((int)Math.floor(Math.random()*allMoves.size())));
    }
}
