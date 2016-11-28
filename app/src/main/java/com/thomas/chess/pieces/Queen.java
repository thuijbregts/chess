package com.thomas.chess.pieces;

import com.thomas.chess.game.Board;
import com.thomas.chess.game.Game;
import com.thomas.chess.game.Move;
import com.thomas.chess.game.Square;
import com.thomas.chess.game.Utils;

import java.util.ArrayList;

public class Queen extends Piece {

    public Queen(int color, Square square, Game game) {
        super(color, square, game);
    }

    @Override
    public ArrayList<Move> getMoves(boolean verification) {
        ArrayList<Move> possibleMoves = new ArrayList<>();

        Rook rook = new Rook(mColor, mSquare, mGame);
        possibleMoves.addAll(rook.getMoves(verification));

        Bishop bishop = new Bishop(mColor, mSquare, mGame);
        possibleMoves.addAll(bishop.getMoves(verification));

        return possibleMoves;
    }


}
