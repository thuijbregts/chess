package com.thomas.chess.engine.search;

import com.thomas.chess.engine.chess120.*;

public class Evaluator {
	public static final int PAWN = 100;
	public static final int KNIGHT = 310;
	public static final int BISHOP = 330;
	public static final int ROOK = 500;
	public static final int QUEEN = 1000;
	public static final int KING = 50000;

    public static final int FORK_BONUS = 40;
    public static final int PASSED_PAWN_BONUS = 40;
    public static final int KING_CHECK_BONUS = 40;
    public static final int BISHOP_PAIR = 40;

    public static final int INFINITE = 50000;
    public static final int CHECKMATE = 49000;
    
    //Most Valuable Victim - Least Valuable Attacker
    //100 = pawn, 600 = king
    public static final int[] MVV_LVA = { 100, 200, 300, 400, 500, 600 };
    public static final int MVV_LVA_LENGTH = 6;

    public static final int[] PAWN_TABLE = {
            0, 0, 0, 0, 0, 0, 0, 0,
            10, 10, 0, -10, -10, 0, 10, 10,
            5, 0, 0, 5, 5, 0, 0, 5,
            0, 0, 10, 20, 20, 10, 0, 0,
            5, 5, 5, 10, 10, 5, 5, 5,
            10, 10, 10, 20, 20, 10, 10, 10,
            20, 20, 20, 30, 30, 20, 20, 20,
            0, 0, 0, 0, 0, 0, 0, 0
    };


    public static final int[] KNIGHT_TABLE = {
            0, -10, 0, 0, 0, 0, -10, 0,
            0, 0, 0, 5, 5, 0, 0, 0,
            0, 0, 10, 10, 10, 10, 0, 0,
            0, 0, 10, 20, 20, 10, 5, 0,
            5, 10, 15, 20, 20, 15, 10, 5,
            5, 10, 10, 20, 20, 10, 10, 5,
            0, 0, 5, 10, 10, 5, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0
    };

    public static final int[] BISHOP_TABLE = {
            0, 0, -10, 0, 0, -10, 0, 0,
            0, 0, 0, 10, 10, 0, 0, 0,
            0, 0, 10, 15, 15, 10, 0, 0,
            0, 10, 15, 20, 20, 15, 10, 0,
            0, 10, 15, 20, 20, 15, 10, 0,
            0, 0, 10, 15, 15, 10, 0, 0,
            0, 0, 0, 10, 10, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0
    };

    public static final int[] ROOK_TABLE = {
            0, 0, 5, 10, 10, 5, 0, 0,
            0, 0, 5, 10, 10, 5, 0, 0,
            0, 0, 5, 10, 10, 5, 0, 0,
            0, 0, 5, 10, 10, 5, 0, 0,
            0, 0, 5, 10, 10, 5, 0, 0,
            0, 0, 5, 10, 10, 5, 0, 0,
            25, 25, 25, 25, 25, 25, 25, 25,
            0, 0, 5, 10, 10, 5, 0, 0
    };
    
    public static final int[] QUEEN_TABLE = {
            0, 0, 5, 10, 10, 5, 0, 0,
            0, 0, 5, 10, 10, 5, 0, 0,
            0, 0, 5, 10, 10, 5, 0, 0,
            0, 0, 5, 10, 10, 5, 0, 0,
            0, 0, 5, 10, 10, 5, 0, 0,
            0, 0, 5, 10, 10, 5, 0, 0,
            25, 25, 25, 25, 25, 25, 25, 25,
            0, 0, 5, 10, 10, 5, 0, 0
    };

    public static final int[] KING_EARLY = {
            10, 10, 50, -10, 0, -10, 50, 10,
            -10, -10, -10, -10, -10, -10, -10, -10,
            -10, -10, -10, -10, -10, -10, -10, -10,
            -10, -10, -10, -10, -10, -10, -10, -10,
            -10, -10, -10, -10, -10, -10, -10, -10,
            -10, -10, -10, -10, -10, -10, -10, -10,
            -10, -10, -10, -10, -10, -10, -10, -10,
            -10, -10, -10, -10, -10, -10, -10, -10
    };
    
    public static int pieceValue(int piece) {
    	int type = piece >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;
        switch (type) {
	        case Definitions.PAWN:
	            return PAWN;
	        case Definitions.KNIGHT:
	            return KNIGHT;
	        case Definitions.BISHOP:
	            return BISHOP;
	        case Definitions.ROOK:
	            return ROOK;
	        case Definitions.QUEEN:
	        	return QUEEN;
	        default:
	        	return KING;
        }
    }

    public static int positionValue(int square, int type, int side) {
        int rank = Definitions.RANKS[square];
        int file = Definitions.FILES[square];
        int index = (side == Game.WHITE) ? file+8*rank : file + 8*(7-rank);
        switch (type) {
	        case Definitions.PAWN:
	            return PAWN + PAWN_TABLE[index];
	        case Definitions.KNIGHT:
	            return KNIGHT + KNIGHT_TABLE[index];
	        case Definitions.BISHOP:
	            return BISHOP + BISHOP_TABLE[index];
	        case Definitions.ROOK:
	            return ROOK + ROOK_TABLE[index];
	        case Definitions.QUEEN:
	        	return QUEEN + QUEEN_TABLE[index];
	        default:
	        	return KING + KING_EARLY[index];
        }
    }
    
  //TODO : king defense, static evaluation, pawn structure, forks...
    public static int evaluateBoard(Game game) {
        int whiteScore = 0;
        int blackScore = 0;

        int[][] pieces = game.getPieces();
        int[] whitePieces = pieces[Game.WHITE];
        int[] blackPieces = pieces[Game.BLACK];
        int piece;
        int whiteBishopsLight = 0;
        int whiteBishopsDark = 0;
        int blackBishopsLight = 0;
        int blackBishopsDark= 0;
        
        int type, square;
        
        for (int i = 0; i < 16; i++) {
            piece = whitePieces[i];
            if ((piece & Piece.DEAD_FLAG) == 0) {
            	type = piece >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;
        		square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
        		
            	whiteScore += Evaluator.positionValue(square, type, Game.WHITE);
            	       	
	            if (type == Definitions.BISHOP) {
	            	if (Definitions.RANKS[square]+Definitions.FILES[square] % 2 == 0) {
	            		whiteBishopsDark++;
	            	} else {
	             		whiteBishopsLight++;
	             	}
	            }
            }
            
            piece = blackPieces[i];
            if ((piece & Piece.DEAD_FLAG) == 0) {
            	type = piece >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;
        		square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
        		
            	blackScore -= Evaluator.positionValue(square, type, Game.BLACK);

	            if (type == Definitions.BISHOP) {
	            	if ((Definitions.RANKS[square]+Definitions.FILES[square]) % 2 == 0) {
	            		blackBishopsDark++;
	            	} else {
	             		blackBishopsLight++;
	             	}
	            }
            }
        }

        if(whiteBishopsLight > 0 && whiteBishopsDark > 0) {
            whiteScore += Evaluator.BISHOP_PAIR;
        }

        if(blackBishopsLight > 0 && blackBishopsDark > 0) {
            blackScore -= Evaluator.BISHOP_PAIR;
        }

        if(game.getCurrentSide() == Game.WHITE) {
            return whiteScore+blackScore;
        }
        return -(whiteScore+blackScore);
    }
}
