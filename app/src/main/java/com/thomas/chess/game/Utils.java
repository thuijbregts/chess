package com.thomas.chess.game;

import android.widget.ImageView;

import com.thomas.chess.R;
import com.thomas.chess.pieces.Bishop;
import com.thomas.chess.pieces.King;
import com.thomas.chess.pieces.Knight;
import com.thomas.chess.pieces.Pawn;
import com.thomas.chess.pieces.Piece;
import com.thomas.chess.pieces.Queen;
import com.thomas.chess.pieces.Rook;

public class Utils {
    public static final int WHITE = 0;
    public static final int BLACK = 1;

    public static final int GAME_SOLO = 0;
    public static final int GAME_ONLINE = 1;

    public static final int ROWS = 8;
    public static final int COLUMNS = 8;

    public static final int MOVE_TYPE_NORMAL = 0;
    public static final int MOVE_TYPE_PASSANT = 1;
    public static final int MOVE_TYPE_CASTLING = 2;
    public static final int MOVE_TYPE_PROMOTION = 3;
    
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
}
