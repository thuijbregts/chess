package com.thomas.chess.engine.chess120;

public class Definitions {
	
	public static final String START_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public static final int CASTLING_K_WHITE = 1;
    public static final int CASTLING_Q_WHITE = 2;
    public static final int CASTLING_K_BLACK = 4;
    public static final int CASTLING_Q_BLACK = 8;
    
    public static final int NO_CASTLING_WHITE = 12;
    public static final int NO_CASTLING_BLACK = 3;
    public static final int NO_CASTLING_K_W = 14;
    public static final int NO_CASTLING_Q_W = 13;
    public static final int NO_CASTLING_K_B = 11;
    public static final int NO_CASTLING_Q_B = 7;
    
    //initial pieces for castling evalutions
    public static final int KING_INDEX = 15;
    public static final int LEFT_ROOK_INDEX = 12;
    public static final int RIGHT_ROOK_INDEX = 13;
	
	public static final int WHITE_PAWN = 0;
	public static final int BLACK_PAWN = 1;
	public static final int WHITE_KNIGHT = 2;
	public static final int BLACK_KNIGHT = 3;
	public static final int WHITE_BISHOP = 4;
	public static final int BLACK_BISHOP = 5;
	public static final int WHITE_ROOK = 6;
	public static final int BLACK_ROOK = 7;
	public static final int WHITE_QUEEN = 8;
	public static final int BLACK_QUEEN = 9;
	public static final int WHITE_KING = 10;
	public static final int BLACK_KING = 11;
	
	public static final int DIFF_PIECES = 12;
	
	public static final int SQ_EMPTY = 0;
	public static final int SQ_OFFBOARD = 100;
	
	public static final int LOWER_BOUND = 21;
	public static final int UPPER_BOUND = 98;
	
	public static final int PAWN = 0;
	public static final int KNIGHT = 1;
	public static final int BISHOP = 2;
	public static final int ROOK = 3;
	public static final int QUEEN = 4;
	public static final int KING = 5;
	
	//For the pieces count array: dark bishop = bishop, light bishop = king
	public static final int DARK_BISHOP = 2;
	public static final int LIGHT_BISHOP = 5;
	
	public static final int DIAGONAL_LEFT = 9;
	public static final int DIAGONAL_RIGHT = 11;
	public static final int HORIZONTAL = 1;
	public static final int VERTICAL = 10;
	
	public static final int SLIDE_HORIZONTAL = 1;
	public static final int SLIDE_DIAGONAL = 2;
	public static final int SLIDE_BOTH = 3;
	public static final int[] TYPE_SLIDE = new int[] { 0, 0, SLIDE_DIAGONAL, SLIDE_HORIZONTAL, SLIDE_BOTH, 0 };
	
	private static final int[] ROOK_DIR = new int[] { -10, -1, 1, 10 };
	private static final int[] BISHOP_DIR = new int[] { -11, -9, 9, 11 };
	private static final int[] KING_DIR = new int[] { -11, -10, -9, -1, 1, 9, 10, 11 };
	private static final int[] KNIGHT_DIR = new int[] { -21, -19, -12, -8, 8, 12, 19, 21 };
	
	public static final int[][] DIRECTIONS = new int[][] { new int[] {}, KNIGHT_DIR, BISHOP_DIR, ROOK_DIR, new int[] {}, KING_DIR };
	
	public static final int INITIAL_CASTLING_PERMS = 15;
	public static final int INITIAL_PASSANT = 0;
	public static final int INITIAL_HALFCLOCK = 0;
	
	public static final int[] RANKS = new int[] {
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
			3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
			4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
			5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
			6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
			7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	};
	
	public static final int[] FILES = new int[] {
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 1, 2, 3, 4, 5, 6, 7, 0,
			0, 0, 1, 2, 3, 4, 5, 6, 7, 0,
			0, 0, 1, 2, 3, 4, 5, 6, 7, 0,
			0, 0, 1, 2, 3, 4, 5, 6, 7, 0,
			0, 0, 1, 2, 3, 4, 5, 6, 7, 0,
			0, 0, 1, 2, 3, 4, 5, 6, 7, 0,
			0, 0, 1, 2, 3, 4, 5, 6, 7, 0,
			0, 0, 1, 2, 3, 4, 5, 6, 7, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	};
	
}
