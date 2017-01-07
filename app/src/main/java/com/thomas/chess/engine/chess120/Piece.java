package com.thomas.chess.engine.chess120;

public class Piece {
	
	public static final int INDEX_MASK = 15;
	
	public static final int RESET_SQUARE = 260111;
	public static final int SQUARE_MASK = 127;
	public static final int SQUARE_SHIFT = 4;
	
	public static final int TYPE_MASK = 7;
	public static final int TYPE_SHIFT = 12;
	
	public static final int SLIDE_MASK = 3;
	public static final int SLIDE_SHIFT = 15;
	public static final int HORIZONTAL_ONLY = 32768;
	public static final int DIAGONAL_ONLY = 65536;
	public static final int SLIDE_ONLY = 98304;
	
	public static final int SIDE_SHIFT = 11;

	public static final int SIDE_ONLY = 2048;
	public static final int TYPE_ONLY = 28672;
	public static final int SQUARE_ONLY = 2032;
	
	public static final int DEAD_FLAG = 131072;
	
	public static int toInt(int index, int square, int side, int type) {
		int piece = index;
		piece |= (square << SQUARE_SHIFT);
		piece |= (side << SIDE_SHIFT);
		piece |= (type << TYPE_SHIFT);
		switch (type) {
			case Definitions.ROOK:
				piece |= Definitions.SLIDE_HORIZONTAL << SLIDE_SHIFT;
				break;
			case Definitions.BISHOP:
				piece |= Definitions.SLIDE_DIAGONAL << SLIDE_SHIFT;
				break;
			case Definitions.QUEEN:
				piece |= Definitions.SLIDE_BOTH << SLIDE_SHIFT;
		}
		return piece;
	}
	
}

