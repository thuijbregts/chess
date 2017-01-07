package com.thomas.chess.engine.movegen;

import com.thomas.chess.engine.chess120.*;

public class MoveGenerator {

	public static MoveArray generateMoves(Game game) {
		Move lastMove = game.getLastMove();
		int side = game.getCurrentSide();
		
		if (lastMove.isCheck()) {
			int[] checkPieces = lastMove.getCheckPieces();
			if (checkPieces[1] != 0) {
				MoveArray moves = new MoveArray();
				CheckGenerator.kingMoves(moves, game.getPieces()[side][15], game);
				return moves;
			}
			return CheckGenerator.generateMoves(game, checkPieces[0]);
		}
		
		MoveArray moves = new MoveArray();
	
		int[] pieces = game.getPieces()[side];
		int type;
		int piece;
		int pinned;
		int kingSquare = game.getFriendlyKingSquare(side);
		
		for (int i = 0; i < 16; i++) {
			piece = pieces[i];
			if ((piece & Piece.DEAD_FLAG) == 0) {
				type = piece >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;
				switch (type) {
					case Definitions.PAWN:
						pinned = PinGenerator.isPinned(piece, kingSquare, game);
						if (pinned != 0) {
							PinGenerator.generateMoves(moves, piece, type, (pinned & PinGenerator.DIR_MASK), game);
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
							PinGenerator.generateMoves(moves, piece, type, (pinned & PinGenerator.DIR_MASK), game);
						} else {
							rookMoves(moves, piece, game);
						}
						break;
					case Definitions.BISHOP:
						pinned = PinGenerator.isPinned(piece, kingSquare, game);
						if (pinned != 0) {
							PinGenerator.generateMoves(moves, piece, type, (pinned & PinGenerator.DIR_MASK), game);
						} else {
							bishopMoves(moves, piece, game);
						}
						break;
					case Definitions.QUEEN:
						pinned = PinGenerator.isPinned(piece, kingSquare, game);
						if (pinned != 0) {
							PinGenerator.generateMoves(moves, piece, type, (pinned & PinGenerator.DIR_MASK), game);
						} else {
							rookMoves(moves, piece, game);
							bishopMoves(moves, piece, game);
						}
				}
			}
		}

		return moves;
	}
	
	private static void pawnMoves(MoveArray moves, int piece, Game game) {
		int[] board = game.getBoard();
		int square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		int side = game.getCurrentSide();
		int rank = Definitions.RANKS[square];
		int enPassant = game.getEnPassant();
		int toSquare;
		int toPiece;
		int firstRank;
		int promotionRank;
		int epRank;
		int left;
		int right;
		int front;
		
		if (side == Game.WHITE) {
			firstRank = 1;
			promotionRank = 6;
			epRank = 4;
			left = 9;
			right = 11;
			front = 10;
		} else {
			firstRank = 6;
			promotionRank = 1;
			epRank = 3;
			left = -11;
			right = -9;
			front = -10;
		}
		toSquare = square + front;
		if (board[toSquare] == Definitions.SQ_EMPTY) {
			if (rank == promotionRank) {
				promotionMovesQuiet(moves, square, toSquare);
			} else {
				moves.add(Move.quiet(square, toSquare, Move.QUIET));
				if (rank == firstRank) {
					toSquare += front;
					if (board[toSquare] == Definitions.SQ_EMPTY) {
						moves.add(Move.pawnDoubleMove(square, toSquare, Move.QUIET, toSquare-front));
					}
				}
			}
		}
		
		toSquare = square + left;
		toPiece = board[toSquare];
		int ePawnSquare;
		if (toPiece != Definitions.SQ_OFFBOARD) {
			if (toPiece != Definitions.SQ_EMPTY) {
				if ((piece & Piece.SIDE_ONLY) != (toPiece & Piece.SIDE_ONLY)) {
					if (rank == promotionRank) {
						promotionMovesCapture(moves, square, toSquare);
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
						promotionMovesCapture(moves, square, toSquare);
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
	
	public static void promotionMovesQuiet(MoveArray moves, int square, int toSquare) {
		moves.add(Move.promotionQuiet(square, toSquare, Move.PROMOTION, Definitions.QUEEN));
		moves.add(Move.promotionQuiet(square, toSquare, Move.PROMOTION, Definitions.ROOK));
		moves.add(Move.promotionQuiet(square, toSquare, Move.PROMOTION, Definitions.KNIGHT));
		moves.add(Move.promotionQuiet(square, toSquare, Move.PROMOTION, Definitions.BISHOP));
	}
	
	public static void promotionMovesCapture(MoveArray moves, int square, int toSquare) {
		moves.add(Move.promotionCapture(square, toSquare, Move.PROMOTION, Definitions.QUEEN));
		moves.add(Move.promotionCapture(square, toSquare, Move.PROMOTION, Definitions.ROOK));
		moves.add(Move.promotionCapture(square, toSquare, Move.PROMOTION, Definitions.KNIGHT));
		moves.add(Move.promotionCapture(square, toSquare, Move.PROMOTION, Definitions.BISHOP));
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
				if (toPiece == Definitions.SQ_EMPTY ) {
					if (!kingThreatened(piece, square, toSquare, toPiece, side, board, game)) {
						moves.add(Move.quiet(square, toSquare, Move.QUIET));
					}
				} else {
					pceSide = (toPiece & Piece.SIDE_ONLY) == 0 ? 0 : 1;
					if (pceSide != side
							&& !kingThreatened(piece, square, toSquare, toPiece, side, board, game)) {
						moves.add(Move.capture(square, toSquare, Move.CAPTURE));
					}
				}
			}
		}
		
		int castlingPermissions = game.getCastlingPermissions();
		if (side == Game.WHITE) {
			if ((castlingPermissions & (Definitions.CASTLING_K_WHITE + Definitions.CASTLING_Q_WHITE)) != 0) {
				if (board[35] == Definitions.SQ_EMPTY || (board[35] & Piece.SIDE_ONLY) == 0) { 
					int[] enemyPieces = game.getPieces()[1-side];
					if ((castlingPermissions & Definitions.CASTLING_K_WHITE) != 0 
							&& kingCastle(side, 26, 27, enemyPieces, board, game)) {
						moves.add(Move.quiet(square, 27, Move.CASTLING));
					}
					if ((castlingPermissions & Definitions.CASTLING_Q_WHITE) != 0
							&& queenCastle(side, 22, 23, 24, enemyPieces, board, game)) {
						moves.add(Move.quiet(square, 23, Move.CASTLING));
					}
				}
			}
		} else {
			if ((castlingPermissions & (Definitions.CASTLING_K_BLACK + Definitions.CASTLING_Q_BLACK)) != 0) {
				if (board[85] == Definitions.SQ_EMPTY || (board[85] & Piece.SIDE_ONLY) != 0) { 
					int[] enemyPieces = game.getPieces()[1-side];
					if ((castlingPermissions & Definitions.CASTLING_K_BLACK) != 0 
							&& kingCastle(side, 96, 97, enemyPieces, board, game)) {
						moves.add(Move.quiet(square, 97, Move.CASTLING));
					}
					if ((castlingPermissions & Definitions.CASTLING_Q_BLACK) != 0
							&& queenCastle(side, 92, 93, 94, enemyPieces, board, game)) {
						moves.add(Move.quiet(square, 93, Move.CASTLING));
					}
				}
			}
		}
	}
	
	public static boolean kingThreatened(int piece, int square, int toSquare, int toPiece, int side, int[] board, Game game) {
		int i, ePiece;
		int[] enemyPieces = game.getPieces()[1-side];
        board[toSquare] = piece;
        board[square] = Definitions.SQ_EMPTY;
        for (i = 0; i < 16; i++) {
        	ePiece = enemyPieces[i];
            if ((ePiece & Piece.DEAD_FLAG) == 0 && ePiece != toPiece) {
                if (SquareAttack.attacksSquare(ePiece, toSquare, game)) {
                    board[square] = piece;
                    board[toSquare] = toPiece;
                    return true;
                }
            }
        }
        board[square] = piece;
        board[toSquare] = toPiece;
        return false;
	}
	
	private static boolean kingCastle(int side, int firstSq, int secondSq, int[] enemyPieces, int[] board, Game game) {
		if (board[firstSq] != Definitions.SQ_EMPTY) {
			return false;
		}
		if (board[secondSq] != Definitions.SQ_EMPTY) {
			return false;
		}

		int i;
		int piece;
		for (i = 0; i < 16; i++) {
			piece = enemyPieces[i];
			if ((piece & Piece.DEAD_FLAG) == 0) {
				if (SquareAttack.attacksSquare(piece, firstSq, game)) {
					return false;
				}
				if (SquareAttack.attacksSquare(piece, secondSq, game)) {
					return false;
				}
			}
		}
		return true;
	}

	private static boolean queenCastle(int side, int firstSq, int secondSq, int thirdSq, int[] enemyPieces, int[] board, Game game) {
		if (board[firstSq] != Definitions.SQ_EMPTY) {
			return false;
		}
		if (board[secondSq] != Definitions.SQ_EMPTY) {
			return false;
		}
		if (board[thirdSq] != Definitions.SQ_EMPTY) {
			return false;
		}
		int i;
		int piece;
		for (i = 0; i < 16; i++) {
			piece = enemyPieces[i];
			if ((piece & Piece.DEAD_FLAG) == 0) {
				if (SquareAttack.attacksSquare(piece, secondSq, game)) {
					return false;
				}
				if (SquareAttack.attacksSquare(piece, thirdSq, game)) {
					return false;
				}
			}
		}
		return true;
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
				if (toPiece == Definitions.SQ_EMPTY) {
					moves.add(Move.quiet(square, toSquare, Move.QUIET));
				} else {
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
				if (toPiece == Definitions.SQ_EMPTY) {
					moves.add(Move.quiet(square, toSquare, Move.QUIET));
				} else {
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
				if (toPiece == Definitions.SQ_EMPTY) {
					moves.add(Move.quiet(square, toSquare, Move.QUIET));
				} else {
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
	
}
