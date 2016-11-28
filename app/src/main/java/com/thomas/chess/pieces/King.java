package com.thomas.chess.pieces;

import com.thomas.chess.game.Game;
import com.thomas.chess.game.Move;
import com.thomas.chess.game.Square;

import java.util.ArrayList;

public class King extends Piece {

    public King(int color, Game game) {
        super(color, game);
    }

    @Override
    public ArrayList<Move> getMoves(Square currentSquare) {
        ArrayList<Move> possibleMoves = new ArrayList<>();

        Square[][] board = mGame.getBoard().getSquares();

        return possibleMoves;
    }


}
