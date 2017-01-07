package com.thomas.chess.engine.movegen;

import com.thomas.chess.engine.chess120.*;

public class PinGenerator {
	
	public static final int DIR_MASK = 15;
	
	public static final int SQUARE_MASK = 127;
	public static final int SQUARE_SHIFT = 4;
	
	//for friendly en passant, in rare cases where both pieces cause check
	public static final int SECOND_PIN_SHIFT = 7;
	
	//friendly king
	//returns direction and threatening square
	public static int isPinned(int piece, int kingSquare, Game game) {
		int[] board = game.getBoard();
		int square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		int direction = kingSquare - square;
		int side = piece & Piece.SIDE_ONLY;
		boolean horizontal;
		
		if (direction % 10 == 0) {
			direction = kingSquare > square ? 10 : -10;
			horizontal = true;
		} else if (Definitions.RANKS[square] == Definitions.RANKS[kingSquare]) {
			direction = kingSquare > square ? 1 : -1;
			horizontal = true;
		} else if (direction % 11 == 0) {
			direction = kingSquare > square ? 11 : -11;
			horizontal = false;
		} else if (direction % 9 == 0) {
			direction = kingSquare > square ? 9 : -9;
			horizontal = false;
		} else {
			return 0;
		}
		
		int toSquare = square + direction;
		while (toSquare != kingSquare) {
			if (board[toSquare] != Definitions.SQ_EMPTY) {
				return 0;
			}
			toSquare += direction;
		}
		
		toSquare = square - direction;
		int toPiece = board[toSquare];
		while (toPiece != Definitions.SQ_OFFBOARD) {
			if (toPiece != Definitions.SQ_EMPTY) {
				if (side == (toPiece & Piece.SIDE_ONLY)) {
					return 0;
				}
				if (horizontal) {
					if ((toPiece & Piece.HORIZONTAL_ONLY) == 0) {
						return 0;
					}					
				} else {
					if ((toPiece & Piece.DIAGONAL_ONLY) == 0) {
						return 0;
					}
				}
				return Math.abs(direction) | toSquare << SQUARE_SHIFT;
			}
			toSquare -= direction;
			toPiece = board[toSquare];
		}
		return 0;
	}
	
	//enemy king
	public static int isFriendlyPinned(int piece, int kingSquare, int moveDir, Game game) {
		int[] board = game.getBoard();
		int square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		int direction = kingSquare - square;
		int side = piece & Piece.SIDE_ONLY;
		boolean horizontal;
		if (direction % 10 == 0) {
			if (moveDir == 10) {
				return 0;
			}
			direction = kingSquare > square ? 10 : -10;
			horizontal = true;
		} else if (Definitions.RANKS[square] == Definitions.RANKS[kingSquare]) {
			if (moveDir == 1) {
				return 0;
			}
			direction = kingSquare > square ? 1 : -1;
			horizontal = true;
		} else if (direction % 11 == 0) {
			if (moveDir == 11) {
				return 0;
			}
			direction = kingSquare > square ? 11 : -11;
			horizontal = false;
		} else if (direction % 9 == 0) {
			if (moveDir == 9) {
				return 0;
			}
			direction = kingSquare > square ? 9 : -9;
			horizontal = false;
		} else {
			return 0;
		}
		
		int toSquare = square + direction;
		while (toSquare != kingSquare) {
			if (board[toSquare] != Definitions.SQ_EMPTY) {
				return 0;
			}
			toSquare += direction;
		}
		
		toSquare = square - direction;
		int toPiece = board[toSquare];
		while (toPiece != Definitions.SQ_OFFBOARD) {
			if (toPiece != Definitions.SQ_EMPTY) {
				if (side != (toPiece & Piece.SIDE_ONLY)) {
					return 0;
				}
				if (horizontal) {
					if ((toPiece & Piece.HORIZONTAL_ONLY) == 0) {
						return 0;
					}					
				} else {
					if ((toPiece & Piece.DIAGONAL_ONLY) == 0) {
						return 0;
					}
				}
				return toSquare;
			}
			toSquare -= direction;
			toPiece = board[toSquare];
		}
		return 0;
	}
	
	//friendly king
	public static boolean isPinnedEnPAssant(int piece, int ePawnSquare, int kingSquare, Game game) {
		int[] board = game.getBoard();
		int square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		int side = piece & Piece.SIDE_ONLY;
		
		if (Definitions.RANKS[square] != Definitions.RANKS[kingSquare]) {
			return false;
		}
		
		int direction = kingSquare > square ? 1 : -1;
		
		int toSquare = square + direction;
		while (toSquare != kingSquare) {
			if (board[toSquare] != Definitions.SQ_EMPTY && toSquare != ePawnSquare) {
				return false;
			}
			toSquare += direction;
		}
		
		toSquare = square - direction;
		int toPiece = board[toSquare];
		while (toPiece != Definitions.SQ_OFFBOARD) {
			if (toPiece != Definitions.SQ_EMPTY && toSquare != ePawnSquare) {
				if (side == (toPiece & Piece.SIDE_ONLY)) {
					return false;
				}
				return (toPiece & Piece.HORIZONTAL_ONLY) != 0;
			}
			toSquare -= direction;
			toPiece = board[toSquare];
		}
		return false;
	}

	//enemy king
	public static int isFriendlyPinnedEnPassant(int piece, int ePawnSquare, int kingSquare, int moveDir, Game game) {
		int[] board = game.getBoard();
		int square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		int direction = kingSquare - square;
		int side = piece & Piece.SIDE_ONLY;

		if (Definitions.RANKS[square] != Definitions.RANKS[kingSquare]) {
			int pinned = isPinnedDiagonal(ePawnSquare, Piece.SIDE_ONLY^side, kingSquare, game);
			
			boolean diagonal;
			
			if (direction % 11 == 0) {
				if (moveDir == 11) {
					return pinned;
				}
				direction = kingSquare > square ? 11 : -11;
				diagonal = true;
			} else if (direction % 9 == 0) {
				if (moveDir == 9) {
					return pinned;
				}
				direction = kingSquare > square ? 9 : -9;
				diagonal = true;
			} else if (direction % 10 == 0) {
				if (moveDir == 10) {
					return pinned;
				}
				direction = kingSquare > square ? 10 : -10;
				diagonal = false;
			} else {
				return pinned;
			}
			
			int toSquare = square + direction;
			while (toSquare != kingSquare) {
				if (board[toSquare] != Definitions.SQ_EMPTY) {
					return pinned;
				}
				toSquare += direction;
			}
			
			toSquare = square - direction;
			int toPiece = board[toSquare];
			while (toPiece != Definitions.SQ_OFFBOARD) {
				if (toPiece != Definitions.SQ_EMPTY) {
					if (side != (toPiece & Piece.SIDE_ONLY)) {
						return pinned;
					}
					if (diagonal) {
						if ((toPiece & Piece.DIAGONAL_ONLY) == 0) {
							return pinned;
						}
					} else {
						if ((toPiece & Piece.HORIZONTAL_ONLY) == 0) {
							return pinned;
						}
					}
					
					int res = toSquare;
					if (pinned == 0) {
						return res;
					}
					return pinned | (res << SECOND_PIN_SHIFT);
				}
				toSquare -= direction;
				toPiece = board[toSquare];
			}
			return pinned;
		}
		
		direction = kingSquare > square ? 1 : -1;
		
		int toSquare = square + direction;
		while (toSquare != kingSquare) {
			if (board[toSquare] != Definitions.SQ_EMPTY && toSquare != ePawnSquare) {
				return 0;
			}
			toSquare += direction;
		}
		
		toSquare = square - direction;
		int toPiece = board[toSquare];
		while (toPiece != Definitions.SQ_OFFBOARD) {
			if (toPiece != Definitions.SQ_EMPTY && toSquare != ePawnSquare) {
				if (side != (toPiece & Piece.SIDE_ONLY)) {
					return 0;
				}
				if ((toPiece & Piece.HORIZONTAL_ONLY) == 0) {
					return 0;
				}
				return toSquare;
			}
			toSquare -= direction;
			toPiece = board[toSquare];
		}
		
		return 0;
	}
	
	//only for checking if the dead en passant piece is pinned (used in isFriendlyPinnedEnPassant)
	//so kingSquare is same side as square
	private static int isPinnedDiagonal(int square, int side, int kingSquare, Game game) {
		int[] board = game.getBoard();
		int direction = kingSquare - square;

		if (direction % 11 == 0) {
			direction = kingSquare > square ? 11 : -11;
		} else if (direction % 9 == 0) {
			direction = kingSquare > square ? 9 : -9;
		} else {
			return 0;
		}
		
		int toSquare = square + direction;
		while (toSquare != kingSquare) {
			if (board[toSquare] != Definitions.SQ_EMPTY) {
				return 0;
			}
			toSquare += direction;
		}
		
		toSquare = square - direction;
		int toPiece = board[toSquare];
		while (toPiece != Definitions.SQ_OFFBOARD) {
			if (toPiece != Definitions.SQ_EMPTY) {
				if (side == (toPiece & Piece.SIDE_ONLY)) {
					return 0;
				}
				if ((toPiece & Piece.DIAGONAL_ONLY) == 0) {
					return 0;
				}
				return toSquare;
			}
			toSquare -= direction;
			toPiece = board[toSquare];
		}
		return 0;
	}
	
	//king can't be pinned, and knight can't move if pinned
	public static void generateMoves(MoveArray moves, int piece, int type, int direction, Game game) {
		switch (type) {
			case Definitions.PAWN:
				pawnMoves(moves, piece, direction, game);
				break;
			case Definitions.BISHOP:
				bishopMoves(moves, piece, direction, game);
				break;
			case Definitions.ROOK:
				rookMoves(moves, piece, direction, game);
				break;
			case Definitions.QUEEN:
				bishopMoves(moves, piece, direction, game);
				rookMoves(moves, piece, direction, game);
				break;
		}
	}
	
	public static void pawnMoves(MoveArray moves, int piece, int direction, Game game) {
		if (direction == Definitions.HORIZONTAL) {
			return;
		}
		int[] board = game.getBoard();
		int square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		int side = piece & Piece.SIDE_ONLY;
		int rank = Definitions.RANKS[square];
		int enPassant = game.getEnPassant();
		int toSquare;
		int toPiece;
		int firstRank;
		int promotionRank;
		int diagLeft;
		int diagRight;
		int front;

		if (side == Game.WHITE) {
			firstRank = 1;
			promotionRank = 6;
			diagLeft = 9;
			diagRight = 11;
			front = 10;
		} else {
			firstRank = 6;
			promotionRank = 1;
			diagLeft = -9;
			diagRight = -11;
			front = -10;
		}
		
		switch (direction) {
			case Definitions.VERTICAL:
				toSquare = square + front;
				if (board[toSquare] == Definitions.SQ_EMPTY) {
					if (rank == promotionRank) {
						MoveGenerator.promotionMovesQuiet(moves, square, toSquare);
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
				break;
			case Definitions.DIAGONAL_LEFT:
				toSquare = square + diagLeft;
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
						if (toSquare == enPassant) {
							moves.add(Move.capture(square, toSquare, Move.EN_PASSANT));
						}
					}
				}
				break;
			case Definitions.DIAGONAL_RIGHT:
				toSquare = square + diagRight;
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
						if (toSquare == enPassant) {
							moves.add(Move.capture(square, toSquare, Move.EN_PASSANT));
						}
					}
				}
				break;
		}
	}
	
	private static void bishopMoves(MoveArray moves, int piece, int direction, Game game) {
		if (direction == Definitions.HORIZONTAL || direction == Definitions.VERTICAL) {
			return;
		}
		
		int[] board = game.getBoard();
		int side = piece & Piece.SIDE_ONLY;
		int square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		int toSquare;
		int toPiece;
		int pceSide;

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
		
		toSquare = square - direction;
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
			toSquare -= direction;
			toPiece = board[toSquare];
		}
	}

	private static void rookMoves(MoveArray moves, int piece, int direction, Game game) {
		if (direction == Definitions.DIAGONAL_LEFT || direction == Definitions.DIAGONAL_RIGHT) {
			return;
		}
		
		int[] board = game.getBoard();
		int side = piece & Piece.SIDE_ONLY;
		int square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		int toSquare;
		int toPiece;
		int pceSide;

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
		
		toSquare = square - direction;
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
			toSquare -= direction;
			toPiece = board[toSquare];
		}
	}

}
