package com.thomas.chess.utils;

import android.widget.ImageView;

import com.thomas.chess.R;
import com.thomas.chess.game.pieces.Bishop;
import com.thomas.chess.game.pieces.King;
import com.thomas.chess.game.pieces.Knight;
import com.thomas.chess.game.pieces.Pawn;
import com.thomas.chess.game.pieces.Piece;
import com.thomas.chess.game.pieces.Queen;
import com.thomas.chess.game.pieces.Rook;

public class Utils {
    public static final int WHITE = 0;
    public static final int BLACK = 1;

    public static final String INTENT_GAME_TYPE = "GAME_TYPE";
    public static final int GAME_SOLO = 0;
    public static final int GAME_ONLINE = 1;
    public static final int GAME_VERSUS = 2;
    public static final int GAME_REVIEW = 3;

    public static final int ROWS = 8;
    public static final int COLUMNS = 8;

    public static final int MOVE_TYPE_NORMAL = 0;
    public static final int MOVE_TYPE_PASSANT = 1;
    public static final int MOVE_TYPE_CASTLING = 2;
    public static final int MOVE_TYPE_PROMOTION = 3;

    public static final int PIECE_ANIMATION_DURATION = 700;
    
    public static void setImageViewForPiece(ImageView imageView, Piece piece) {
        if (piece instanceof Pawn) {
            switch (piece.getColor()) {
                case Utils.WHITE:
                    imageView.setImageResource(R.drawable.pawn_w);
                    break;
                case Utils.BLACK:
                    imageView.setImageResource(R.drawable.pawn_b);
                    break;
            }
        }

        if (piece instanceof Rook) {
            switch (piece.getColor()) {
                case Utils.WHITE:
                    imageView.setImageResource(R.drawable.rook_w);
                    break;
                case Utils.BLACK:
                    imageView.setImageResource(R.drawable.rook_b);
                    break;
            }
        }

        if (piece instanceof Knight) {
            switch (piece.getColor()) {
                case Utils.WHITE:
                    imageView.setImageResource(R.drawable.knight_w);
                    break;
                case Utils.BLACK:
                    imageView.setImageResource(R.drawable.knigh_b);
                    break;
            }
        }

        if (piece instanceof Bishop) {
            switch (piece.getColor()) {
                case Utils.WHITE:
                    imageView.setImageResource(R.drawable.bishop_w);
                    break;
                case Utils.BLACK:
                    imageView.setImageResource(R.drawable.bishop_b);
                    break;
            }
        }

        if (piece instanceof Queen) {
            switch (piece.getColor()) {
                case Utils.WHITE:
                    imageView.setImageResource(R.drawable.queen_w);
                    break;
                case Utils.BLACK:
                    imageView.setImageResource(R.drawable.queen_b);
                    break;
            }
        }

        if (piece instanceof King) {
            switch (piece.getColor()) {
                case Utils.WHITE:
                    imageView.setImageResource(R.drawable.king_w);
                    break;
                case Utils.BLACK:
                    imageView.setImageResource(R.drawable.king_b);
                    break;
            }
        }
    }

    public static String getPieceCode(Piece piece) {
        if (piece instanceof Rook) {
            return "R";
        }
        if (piece instanceof Bishop) {
            return "B";
        }
        if (piece instanceof Knight) {
            return "N";
        }
        if (piece instanceof Queen) {
            return "Q";
        }
        if (piece instanceof King) {
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
}
