package com.thomas.chess.pieces;

import com.thomas.chess.game.Board;
import com.thomas.chess.game.Game;
import com.thomas.chess.game.Move;
import com.thomas.chess.game.Square;
import com.thomas.chess.game.Utils;

import java.util.ArrayList;

public class Queen extends Piece {

    public Queen(int color, Game game) {
        super(color, game);
    }

    @Override
    public ArrayList<Move> getMoves(Square currentSquare) {
        ArrayList<Move> possibleMoves = new ArrayList<>();

        Rook rook = new Rook(mColor, mGame);
        possibleMoves.addAll(rook.getMoves(currentSquare));

        Bishop bishop = new Bishop(mColor, mGame);
        possibleMoves.addAll(bishop.getMoves(currentSquare));

        return possibleMoves;
    }


}
