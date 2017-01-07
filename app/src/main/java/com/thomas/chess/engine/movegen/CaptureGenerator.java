package com.thomas.chess.engine.movegen;

import com.thomas.chess.engine.chess120.*;

public class CaptureGenerator {

	public static MoveArray generateMoves(Game game) {
		Move lastMove = game.getLastMove();
		int side = game.getCurrentSide();
		int[] pieces = game.getPieces()[side];
		int kingSquare = game.getFriendlyKingSquare(side);
		int type;
		int piece;
		
		MoveArray moves = new MoveArray();
		
		if (lastMove.isCheck()) {
			int[] checkPieces = lastMove.getCheckPieces();
			if (checkPieces[1] != 0) {
				kingMoves(moves, game.getPieces()[side][15], game);
				return moves;
			}
			
			int attackerSquare = checkPieces[0];
			int attType = game.getBoard()[attackerSquare] >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;

			for (int i = 0; i < 15; i++) {
				piece = pieces[i];
				if ((piece & Piece.DEAD_FLAG) == 0) {
					if (PinGenerator.isPinned(piece, kingSquare, game) != 0) {
						continue;
					}
					type = piece >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;
					if (attType == Definitions.PAWN || attType == Definitions.KNIGHT) {
						int square = (piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK);
						if (SquareAttack.attacksSquare(piece, attackerSquare, game)) {
							moves.add(Move.capture(square, attackerSquare, Move.CAPTURE));
						} else {
							if (type == Definitions.PAWN) {
								int left, right;
								if (side == Game.WHITE) {
									left = 9;
									right = 11;
								} else {
									left = -11;
									right = -9;
								}
								int enPassant = game.getEnPassant();
								int ePawnSquare = side == Game.WHITE ? enPassant - 10 : enPassant + 10;
								if (enPassant != 0 && attackerSquare == ePawnSquare 
										&& (square + left == enPassant || square + right == enPassant)) {
									moves.add(Move.capture(square, enPassant, Move.EN_PASSANT));
								}
							}
						}
					} else {
						checkMoves(moves, piece, type, attackerSquare, game);
					}
				}
			}
		
			kingMoves(moves, pieces[15], game);

			return moves;
		}
	
		
		int pinned;
		
		for (int i = 0; i < 16; i++) {
			piece = pieces[i];
			if ((piece & Piece.DEAD_FLAG) == 0) {
				type = piece >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;
				switch (type) {
					case Definitions.PAWN:
						pinned = PinGenerator.isPinned(piece, kingSquare, game);
						if (pinned != 0) {
							pinMoves(moves, piece, type, pinned, game);
						} else {
							pawnMoves(moves, piece, game);
						}
						break;
					case Definitions.KNIGHT:
						pinned = PinGenerator.isPinned(piece, kingSquare, game);
						if (pinned == 0) {
							knightMoves(moves, piece, game);
						}
						break;
					case Definitions.KING:
						kingMoves(moves, piece, game);
						break;
					case Definitions.ROOK:
						pinned = PinGenerator.isPinned(piece, kingSquare, game);
						if (pinned != 0) {
							pinMoves(moves, piece, type, pinned, game);
						} else {
							rookMoves(moves, piece, game);
						}
						break;
					case Definitions.BISHOP:
						pinned = PinGenerator.isPinned(piece, kingSquare, game);
						if (pinned != 0) {
							pinMoves(moves, piece, type, pinned, game);
						} else {
							bishopMoves(moves, piece, game);
						}
						break;
					case Definitions.QUEEN:
						pinned = PinGenerator.isPinned(piece, kingSquare, game);
						if (pinned != 0) {
							pinMoves(moves, piece, type, pinned, game);
						} else {
							rookMoves(moves, piece, game);
							bishopMoves(moves, piece, game);
						}
				}
			}
		}

		return moves;
	}
	
	private static void kingMoves(MoveArray moves, int piece, Game game) {
		int[] board = game.getBoard();
		int side = game.getCurrentSide();
		int[] directions = Definitions.DIRECTIONS[Definitions.KING];
		int square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		int toSquare;
		int toPiece;
		int pceSide;
		int length = directions.length;
		
		for (int i = 0; i < length; i++) {
			toSquare = square + directions[i];
			toPiece = board[toSquare];
			if (toPiece != Definitions.SQ_OFFBOARD) {
				if (toPiece != Definitions.SQ_EMPTY) {
					pceSide = (toPiece & Piece.SIDE_ONLY) == 0 ? 0 : 1;
					if (pceSide != side && !MoveGenerator.kingThreatened(piece, square, toSquare, toPiece, side, board, game)) {
						moves.add(Move.capture(square, toSquare, Move.CAPTURE));
					}
				}
			}
		}
	}
	
	private static void pawnMoves(MoveArray moves, int piece, Game game) {
		int[] board = game.getBoard();
		int square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		int side = game.getCurrentSide();
		int rank = Definitions.RANKS[square];
		int enPassant = game.getEnPassant();
		int toSquare;
		int toPiece;
		int promotionRank;
		int epRank;
		int left;
		int right;

		if (side == Game.WHITE) {
			promotionRank = 6;
			epRank = 4;
			left = 9;
			right = 11;
		} else {
			promotionRank = 1;
			epRank = 3;
			left = -11;
			right = -9;
		}

		toSquare = square + left;
		toPiece = board[toSquare];
		int ePawnSquare;
		if (toPiece != Definitions.SQ_OFFBOARD) {
			if (toPiece != Definitions.SQ_EMPTY) {
				if ((piece & Piece.SIDE_ONLY) != (toPiece & Piece.SIDE_ONLY)) {
					if (rank == promotionRank) {
						MoveGenerator.promotionMovesCapture(moves, square, toSquare);
					} else {
						moves.add(Move.capture(square, toSquare, Move.CAPTURE));
					}
				}
			} else {	
				ePawnSquare = side == Game.WHITE ? enPassant - 10 : enPassant + 10;
				if (toSquare == enPassant && epRank == rank
						&& !PinGenerator.isPinnedEnPAssant(piece, ePawnSquare, game.getFriendlyKingSquare(side), game)) {
					moves.add(Move.capture(square, toSquare, Move.EN_PASSANT));
				}
			}
		}
		
		toSquare = square + right;
		toPiece = board[toSquare];
		if (toPiece != Definitions.SQ_OFFBOARD) {
			if (toPiece != Definitions.SQ_EMPTY) {
				if ((piece & Piece.SIDE_ONLY) != (toPiece & Piece.SIDE_ONLY)) {
					if (rank == promotionRank) {
						MoveGenerator.promotionMovesCapture(moves, square, toSquare);
					} else {
						moves.add(Move.capture(square, toSquare, Move.CAPTURE));
					}
				}
			} else {			
				ePawnSquare = side == Game.WHITE ? enPassant - 10 : enPassant + 10;
				if (toSquare == enPassant && epRank == rank
						&& !PinGenerator.isPinnedEnPAssant(piece, ePawnSquare, game.getFriendlyKingSquare(side), game)) {
					moves.add(Move.capture(square, toSquare, Move.EN_PASSANT));
				}
			}
		}
	}
	
	private static void knightMoves(MoveArray moves, int piece, Game game) {
		int[] board = game.getBoard();
		int side = (piece & Piece.SIDE_ONLY);
		int[] directions = Definitions.DIRECTIONS[Definitions.KNIGHT];
		int square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		int toSquare;
		int toPiece;
		int pceSide;
		int length = directions.length;
		
		for (int i = 0; i < length; i++) {
			toSquare = square + directions[i];
			toPiece = board[toSquare];
			if (toPiece != Definitions.SQ_OFFBOARD) {
				if (toPiece != Definitions.SQ_EMPTY) {
					pceSide = (toPiece & Piece.SIDE_ONLY);
					if (pceSide != side) {
						moves.add(Move.capture(square, toSquare, Move.CAPTURE));
					}
				}
			}
		}
	}
 
	private static void rookMoves(MoveArray moves, int piece, Game game) {
		int[] board = game.getBoard();
		int side = piece & Piece.SIDE_ONLY;
		int[] directions = Definitions.DIRECTIONS[Definitions.ROOK];
		int square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		int toSquare;
		int toPiece;
		int pceSide;
		int direction;
		int length = directions.length;
		
		for (int i = 0; i < length; i++) {
			direction = directions[i];
			toSquare = square + direction;
			toPiece = board[toSquare];
			while (toPiece != Definitions.SQ_OFFBOARD) {
				if (toPiece != Definitions.SQ_EMPTY) {
					pceSide = (toPiece & Piece.SIDE_ONLY);
					if (pceSide != side) {
						moves.add(Move.capture(square, toSquare, Move.CAPTURE));
					}
					break;
				}
				toSquare += direction;
				toPiece = board[toSquare];
			}
		}
	}
	
	private static void bishopMoves(MoveArray moves, int piece, Game game) {
		int[] board = game.getBoard();
		int side = piece & Piece.SIDE_ONLY;
		int[] directions = Definitions.DIRECTIONS[Definitions.BISHOP];
		int square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		int toSquare;
		int toPiece;
		int pceSide;
		int direction;
		int length = directions.length;
		
		for (int i = 0; i < length; i++) {
			direction = directions[i];
			toSquare = square + direction;
			toPiece = board[toSquare];
			while (toPiece != Definitions.SQ_OFFBOARD) {
				if (toPiece != Definitions.SQ_EMPTY) {
					pceSide = (toPiece & Piece.SIDE_ONLY);
					if (pceSide != side) {
						moves.add(Move.capture(square, toSquare, Move.CAPTURE));
					}
					break;
				} 
				toSquare += direction;
				toPiece = board[toSquare];
			}
		}
	}
	
	
	private static void pinMoves(MoveArray moves, int piece, int type, int pinned, Game game) {
		int attackerSq = pinned >> PinGenerator.SQUARE_SHIFT & PinGenerator.SQUARE_MASK;
		int square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		switch (type) {
			case Definitions.PAWN:
				int dir = pinned & PinGenerator.DIR_MASK;
				if (dir == 9 || dir == 11) {
					PinGenerator.pawnMoves(moves, piece, dir, game);
				}
				break;
			case Definitions.BISHOP:
				if (SquareAttack.bishopAttacks(piece, attackerSq, game)) {
					moves.add(Move.capture(square, attackerSq, Move.CAPTURE));
				}
				break;
			case Definitions.ROOK:
				if (SquareAttack.rookAttacks(piece, attackerSq, game)) {
					moves.add(Move.capture(square, attackerSq, Move.CAPTURE));
				}
				break;
			case Definitions.QUEEN:
				if (SquareAttack.bishopAttacks(piece, attackerSq, game)) {
					moves.add(Move.capture(square, attackerSq, Move.CAPTURE));
				} else if (SquareAttack.rookAttacks(piece, attackerSq, game)) {
					moves.add(Move.capture(square, attackerSq, Move.CAPTURE));
				}
		}
	}
	
	private static void checkMoves(MoveArray moves, int piece, int type, int attackerSq, Game game) {
		int square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		switch (type) {
			case Definitions.PAWN:
				int promoRank;
				int rank = Definitions.RANKS[square];
				if ((piece & Piece.SIDE_ONLY) == Game.WHITE) {
					promoRank = 6;
				} else {
					promoRank = 1;
				}
				if (SquareAttack.pawnAttacks(piece, attackerSq, game)) {
					if (rank == promoRank) {
						MoveGenerator.promotionMovesCapture(moves, square, attackerSq);
					} else {
						moves.add(Move.capture(square, attackerSq, Move.CAPTURE));
					}
				}
				break;
			case Definitions.KNIGHT:
				if (SquareAttack.knightAttacks(piece, attackerSq, game)) {
					moves.add(Move.capture(square, attackerSq, Move.CAPTURE));
				}
				break;
			case Definitions.BISHOP:
				if (SquareAttack.bishopAttacks(piece, attackerSq, game)) {
					moves.add(Move.capture(square, attackerSq, Move.CAPTURE));
				}
				break;
			case Definitions.ROOK:
				if (SquareAttack.rookAttacks(piece, attackerSq, game)) {
					moves.add(Move.capture(square, attackerSq, Move.CAPTURE));
				}
				break;
			case Definitions.QUEEN:
				if (SquareAttack.bishopAttacks(piece, attackerSq, game)) {
					moves.add(Move.capture(square, attackerSq, Move.CAPTURE));
				} else if (SquareAttack.rookAttacks(piece, attackerSq, game)) {
					moves.add(Move.capture(square, attackerSq, Move.CAPTURE));
				}
		}
	}
}
