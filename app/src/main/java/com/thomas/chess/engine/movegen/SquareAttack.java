package com.thomas.chess.engine.movegen;

import com.thomas.chess.engine.chess120.*;

public class SquareAttack {

	public static boolean attacksSquare(int piece, int square, Game game) {
		int type = piece >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;
		switch (type) {
			case Definitions.PAWN:
				return pawnAttacks(piece, square, game);
			case Definitions.KNIGHT:
				return knightAttacks(piece, square, game);
			case Definitions.BISHOP:
				return bishopAttacks(piece, square, game);
			case Definitions.ROOK:
				return rookAttacks(piece, square, game);
			case Definitions.QUEEN:
				if (bishopAttacks(piece, square, game)) {
					return true;
				}
				return rookAttacks(piece, square, game);
			default:
				return kingAttacks(piece, square, game);
		}
	}
	
	public static boolean pawnAttacks(int piece, int square, Game game) {
		int side = piece >> Piece.SIDE_SHIFT & 1;
		int pSquare = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		int left, right;
		
		if (side == Game.WHITE) {
			left = 9;
			right = 11;
		} else {
			left = -9;
			right = -11;
		}
		return pSquare + left == square || pSquare + right == square;
	}
	
	public static boolean knightAttacks(int piece, int square, Game game) {
		int pSquare = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		int direction = Math.abs(square - pSquare);
		return direction == 8 || direction == 12 || direction == 19 || direction == 21;
	}
	
	public static boolean bishopAttacks(int piece, int square, Game game) {
		int pSquare = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		int[] board = game.getBoard();
		int direction = square - pSquare;
		if (direction % 9 == 0) {
			direction = square > pSquare ? 9 : -9;
		} else if (direction % 11 == 0) {
			direction = square > pSquare ? 11 : -11;
		} else {
			return false;
		}
		
		int nextSquare = pSquare + direction;
		int nextPiece = board[nextSquare];
		while (nextPiece != Definitions.SQ_OFFBOARD) {
			if (nextSquare == square) {
				return true;
			}
			if (nextPiece != Definitions.SQ_EMPTY) {
				return false;
			}
			nextSquare += direction;
			nextPiece = board[nextSquare];
		}
		return false;
	}
	
	public static boolean rookAttacks(int piece, int square, Game game) {
		int pSquare = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		int[] board = game.getBoard();
		int direction = square - pSquare;
		
		if (direction % 10 == 0) {
			direction = square > pSquare ? 10 : -10;
		} else if (Definitions.RANKS[pSquare] == Definitions.RANKS[square]) {
			direction = square > pSquare ? 1 : -1;
		} else {
			return false;
		}
		int nextSquare = pSquare + direction;
		int nextPiece = board[nextSquare];
		while (nextPiece != Definitions.SQ_OFFBOARD) {
			if (nextSquare == square) {
				return true;
			}
			if (nextPiece != Definitions.SQ_EMPTY) {
				return false;
			}
			nextSquare += direction;
			nextPiece = board[nextSquare];
		}
		return false;
	}
	
	public static boolean kingAttacks(int piece, int square, Game game) {
		int pSquare = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		int direction = Math.abs(pSquare - square);
		return direction == 1 || direction == 9 || direction == 10 || direction == 11;
	}
}
