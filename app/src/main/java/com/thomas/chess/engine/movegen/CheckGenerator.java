package com.thomas.chess.engine.movegen;

import com.thomas.chess.engine.chess120.*;

public class CheckGenerator {

	public static MoveArray generateMoves(Game game, int attackerSquare) {
		MoveArray moves = new MoveArray();

		int side = game.getCurrentSide();
 		int[] pieces = game.getPieces()[side];
		int type;
		int piece;
		int kingSquare = game.getFriendlyKingSquare(side);
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
					switch (type) {
						case Definitions.PAWN:
							pawnMoves(moves, piece, attackerSquare, kingSquare, game);
							break;
						case Definitions.KNIGHT:
							knightMoves(moves, piece, attackerSquare, kingSquare, game);
							break;
						case Definitions.ROOK:
							rookMoves(moves, piece, attackerSquare, kingSquare, game);
							break;
						case Definitions.BISHOP:
							bishopMoves(moves, piece, attackerSquare, kingSquare, game);
							break;
						case Definitions.QUEEN:
							rookMoves(moves, piece, attackerSquare, kingSquare, game);
							bishopMoves(moves, piece, attackerSquare, kingSquare, game);
					}
				}
			}
		}
	
		kingMoves(moves, pieces[15], game);

		return moves;
	}
	
	private static void pawnMoves(MoveArray moves, int piece, int attackerSquare, int kingSquare, Game game) {
		int[] board = game.getBoard();
		int square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		int side = game.getCurrentSide();
		int rank = Definitions.RANKS[square];
		int file = Definitions.FILES[square];
		int firstRank;
		int promotionRank;
		int direction;
		int front;
		
		if (side == Game.WHITE) {
			firstRank = 1;
			promotionRank = 6;
			direction = attackerSquare - square;
			front = 10;
		} else {
			firstRank = 6;
			promotionRank = 1;
			direction = square - attackerSquare;
			front = -10;
		}
		
		if (direction == 9 || direction == 11) {
			if (rank == promotionRank) {
				MoveGenerator.promotionMovesCapture(moves, square, attackerSquare);
			} else {
				moves.add(Move.capture(square, attackerSquare, Move.CAPTURE));
			}
		}
		
		int aFile = Definitions.FILES[attackerSquare];
		int kFile = Definitions.FILES[kingSquare];
		
		if (betweenFiles(file, aFile, kFile)) {
			int aRank = Definitions.RANKS[attackerSquare];
			int kRank = Definitions.RANKS[kingSquare];
			int toSquare = square+front;
			
			if (kRank == aRank) {
				if (aRank == Definitions.RANKS[toSquare]) {
					moves.add(Move.quiet(square, toSquare, Move.QUIET));
				} else if (aRank == Definitions.RANKS[toSquare+front]) {
					if (rank == firstRank && board[toSquare] == Definitions.SQ_EMPTY) {
						moves.add(Move.pawnDoubleMove(square, toSquare+front, Move.QUIET, toSquare));
					}
				}
			} else {
				int diff;
		        int intRank;
		        int dir = attackerSquare - kingSquare;
		        if (dir % 9 == 0) {
		            if (aFile > kFile) {
		                diff = aFile - file;
		                intRank = aRank + diff;
		            } else {
		                diff = kFile - file;
		                intRank = kRank + diff;
		            }
		        } else {
		            if (aFile > kFile) {
		                diff = aFile - file;
		                intRank = aRank - diff;
		            } else {
		                diff = kFile - file;
		                intRank = kRank - diff;
		            }
		        }
		        if (intRank == Definitions.RANKS[toSquare]) {
					moves.add(Move.quiet(square, toSquare, Move.QUIET));
				} else if (intRank == Definitions.RANKS[toSquare+front]) {
					if (rank == firstRank && board[toSquare] == Definitions.SQ_EMPTY) {
						moves.add(Move.pawnDoubleMove(square, toSquare+front, Move.QUIET, toSquare));
					}
				}
			}
		}
		
	}
	
	public static void kingMoves(MoveArray moves, int piece, Game game) {
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
				if (toPiece == Definitions.SQ_EMPTY
						&& !MoveGenerator.kingThreatened(piece, square, toSquare, toPiece, side, board, game)) {
					moves.add(Move.quiet(square, toSquare, Move.QUIET));
				} else {
					pceSide = (toPiece & Piece.SIDE_ONLY) == 0 ? 0 : 1;
					if (pceSide != side
							&& !MoveGenerator.kingThreatened(piece, square, toSquare, toPiece, side, board, game)) {
						moves.add(Move.capture(square, toSquare, Move.CAPTURE));
					}
				}
			}
		}
	}

	private static void knightMoves(MoveArray moves, int piece, int attackerSquare, int kingSquare, Game game) {
		int square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		
		if (SquareAttack.knightAttacks(piece, attackerSquare, game)) {
			moves.add(Move.capture(square, attackerSquare, Move.CAPTURE));
		}
		
		int mRank = Definitions.RANKS[square];
		int mFile = Definitions.FILES[square];
		int aRank = Definitions.RANKS[attackerSquare];
		int aFile = Definitions.FILES[attackerSquare];
		int kRank = Definitions.RANKS[kingSquare];
		int kFile = Definitions.FILES[kingSquare];
		
		int toSquare;
		
		if (sameDiagonal(aRank, aFile, kRank, kFile)) {
            if ((mRank+mFile)%2 != (aRank+aFile)%2 && Math.abs(aFile-kFile)-1 > 0) {
            	int destRank;
                int destFile;
                if ((attackerSquare-kingSquare) % 9 == 0) {
                    if (isUnderDiagonalLeft(mRank, mFile, aRank, aFile)) {
                        destRank = mRank-1;
                        destFile = mFile+2;
                        toSquare = (21 + destFile) + (10 * destRank);
                        if (betweenRanks(destRank, aRank, kRank) &&
                                betweenFiles(destFile, aFile, kFile) &&
                                sameDiagonal(destRank, destFile, aRank, aFile) &&
                                (attackerSquare-toSquare) % 9 == 0) {
                        	 moves.add(Move.quiet(square, toSquare, Move.QUIET));
                        }
                        destRank = mRank+1;
                        destFile = mFile+2;
                        toSquare = (21 + destFile) + (10 * destRank);
                        if (betweenRanks(destRank, aRank, kRank) &&
                                betweenFiles(destFile, aFile, kFile) &&
                                sameDiagonal(destRank, destFile, aRank, aFile) &&
                                (attackerSquare-toSquare) % 9 == 0) {
                        	 moves.add(Move.quiet(square, toSquare, Move.QUIET));
                        }
                        destRank = mRank+2;
                        destFile = mFile+1;
                        toSquare = (21 + destFile) + (10 * destRank);
                        if (betweenRanks(destRank, aRank, kRank) &&
                                betweenFiles(destFile, aFile, kFile) &&
                                sameDiagonal(destRank, destFile, aRank, aFile) &&
                                (attackerSquare-toSquare) % 9 == 0) {
                        	
                        	 moves.add(Move.quiet(square, toSquare, Move.QUIET));
                        }
                        destRank = mRank+2;
                        destFile = mFile-1;
                        toSquare = (21 + destFile) + (10 * destRank);
                        if (betweenRanks(destRank, aRank, kRank) &&
                                betweenFiles(destFile, aFile, kFile) &&
                                sameDiagonal(destRank, destFile, aRank, aFile) &&
                                (attackerSquare-toSquare) % 9 == 0) {
                        	
                        	 moves.add(Move.quiet(square, toSquare, Move.QUIET));
                        }
                    } else {
                        destRank = mRank-2;
                        destFile = mFile+1;
                        toSquare = (21 + destFile) + (10 * destRank);
                        if (betweenRanks(destRank, aRank, kRank) &&
                                betweenFiles(destFile, aFile, kFile) &&
                                sameDiagonal(destRank, destFile, aRank, aFile) &&
                                (attackerSquare-toSquare) % 9 == 0) {
                        	 moves.add(Move.quiet(square, toSquare, Move.QUIET));
                        }
                        destRank = mRank-2;
                        destFile = mFile-1;
                        toSquare = (21 + destFile) + (10 * destRank);
                        if (betweenRanks(destRank, aRank, kRank) &&
                                betweenFiles(destFile, aFile, kFile) &&
                                sameDiagonal(destRank, destFile, aRank, aFile) &&
                                (attackerSquare-toSquare) % 9 == 0) {
                        	 moves.add(Move.quiet(square, toSquare, Move.QUIET));
                        }
                        destRank = mRank-1;
                        destFile = mFile-2;
                        toSquare = (21 + destFile) + (10 * destRank);
                        if (betweenRanks(destRank, aRank, kRank) &&
                                betweenFiles(destFile, aFile, kFile) &&
                                sameDiagonal(destRank, destFile, aRank, aFile) &&
                                (attackerSquare-toSquare) % 9 == 0) {
                        	 moves.add(Move.quiet(square, toSquare, Move.QUIET));
                        }
                        destRank = mRank+1;
                        destFile = mFile-2;
                        toSquare = (21 + destFile) + (10 * destRank);
                        if (betweenRanks(destRank, aRank, kRank) &&
                                betweenFiles(destFile, aFile, kFile) &&
                                sameDiagonal(destRank, destFile, aRank, aFile) &&
                                (attackerSquare-toSquare) % 9 == 0) {
                        	 moves.add(Move.quiet(square, toSquare, Move.QUIET));
                        }
                    }
                } else {
                    if (isOverDiagonalRight(mRank, mFile, aRank, aFile)) {
                        destRank = mRank+1;
                        destFile = mFile+2;
                        toSquare = (21 + destFile) + (10 * destRank);
                        if (betweenRanks(destRank, aRank, kRank) &&
                                betweenFiles(destFile, aFile, kFile) &&
                                sameDiagonal(destRank, destFile, aRank, aFile) &&
                                (attackerSquare-toSquare) % 11 == 0) {
                        	 moves.add(Move.quiet(square, toSquare, Move.QUIET));
                        }
                        destRank = mRank-1;
                        destFile = mFile+2;
                        toSquare = (21 + destFile) + (10 * destRank);
                        if (betweenRanks(destRank, aRank, kRank) &&
                                betweenFiles(destFile, aFile, kFile) &&
                                sameDiagonal(destRank, destFile, aRank, aFile) &&
                                (attackerSquare-toSquare) % 11 == 0) {
                        	 moves.add(Move.quiet(square, toSquare, Move.QUIET));
                        }
                        destRank = mRank-2;
                        destFile = mFile+1;
                        toSquare = (21 + destFile) + (10 * destRank);
                        if (betweenRanks(destRank, aRank, kRank) &&
                                betweenFiles(destFile, aFile, kFile) &&
                                sameDiagonal(destRank, destFile, aRank, aFile) &&
                                (attackerSquare-toSquare) % 11 == 0) {
                        	 moves.add(Move.quiet(square, toSquare, Move.QUIET));
                        }
                        destRank = mRank-2;
                        destFile = mFile-1;
                        toSquare = (21 + destFile) + (10 * destRank);
                        if (betweenRanks(destRank, aRank, kRank) &&
                                betweenFiles(destFile, aFile, kFile) &&
                                sameDiagonal(destRank, destFile, aRank, aFile) &&
                                (attackerSquare-toSquare) % 11 == 0) {
                        	 moves.add(Move.quiet(square, toSquare, Move.QUIET));
                        }
                    } else {
                        destRank =  (mRank-1);
                        destFile =  (mFile-2);
                        toSquare = (21 + destFile) + (10 * destRank);
                        if (betweenRanks(destRank, aRank, kRank) &&
                                betweenFiles(destFile, aFile, kFile) &&
                                sameDiagonal(destRank, destFile, aRank, aFile) &&
                                (attackerSquare-toSquare) % 11 == 0) {
                        	 moves.add(Move.quiet(square, toSquare, Move.QUIET));
                        }
                        destRank =  (mRank+1);
                        destFile =  (mFile-2);
                        toSquare = (21 + destFile) + (10 * destRank);
                        if (betweenRanks(destRank, aRank, kRank) &&
                                betweenFiles(destFile, aFile, kFile) &&
                                sameDiagonal(destRank, destFile, aRank, aFile) &&
                                (attackerSquare-toSquare) % 11 == 0) {
                        	 moves.add(Move.quiet(square, toSquare, Move.QUIET));
                        }
                        destRank =  (mRank+2);
                        destFile =  (mFile-1);
                        toSquare = (21 + destFile) + (10 * destRank);
                        if (betweenRanks(destRank, aRank, kRank) &&
                                betweenFiles(destFile, aFile, kFile) &&
                                sameDiagonal(destRank, destFile, aRank, aFile) &&
                                (attackerSquare-toSquare) % 11 == 0) {
                        	 moves.add(Move.quiet(square, toSquare, Move.QUIET));
                        }
                        destRank =  (mRank+2);
                        destFile =  (mFile+1);
                        toSquare = (21 + destFile) + (10 * destRank);
                        if (betweenRanks(destRank, aRank, kRank) &&
                                betweenFiles(destFile, aFile, kFile) &&
                                sameDiagonal(destRank, destFile, aRank, aFile) &&
                                (attackerSquare-toSquare) % 11 == 0) {
                        	 moves.add(Move.quiet(square, toSquare, Move.QUIET));
                        }
                    }
                }
            }
        } else {
        	if (aRank == kRank) {
                if (aRank == mRank || Math.abs(mRank - aRank) > 2) {
                    return;
                }
                int left = aFile < kFile ? aFile : kFile;
                int right = aFile < kFile ? kFile : aFile;
                if (Math.abs(mRank - aRank) == 1) {
                    int newFile = mFile-2;
                    if (newFile > left && newFile < right) {
        	            toSquare = (21 + newFile) + (10 * aRank);
        	            moves.add(Move.quiet(square, toSquare, Move.QUIET));
                    }
                    newFile = mFile+2;
                    if (newFile > left && newFile < right) {
           	            toSquare = (21 + newFile) + (10 * aRank);
        	            moves.add(Move.quiet(square, toSquare, Move.QUIET));
                    }
                } else {
                    int newFile = mFile-1;
                    if (newFile > left && newFile < right) {
           	            toSquare = (21 + newFile) + (10 * aRank);
        	            moves.add(Move.quiet(square, toSquare, Move.QUIET));
                    }
                    newFile = mFile+1;
                    if (newFile > left && newFile < right) {
           	            toSquare = (21 + newFile) + (10 * aRank);
        	            moves.add(Move.quiet(square, toSquare, Move.QUIET));
                    }
                }
            } else {
                if (aFile == mFile || Math.abs(mFile - aFile) > 2) {
                    return;
                }
                int top = aRank < kRank ? kRank : aRank;
                int bottom = aRank < kRank ? aRank : kRank;
                if (Math.abs(mFile - aFile) == 1) {
                    int newRank = mRank-2;
                    if (newRank > bottom && newRank < top) {
           	            toSquare = (21 + aFile) + (10 * newRank);
        	            moves.add(Move.quiet(square, toSquare, Move.QUIET));
                    }
                    newRank = mRank+2;
                    if (newRank > bottom && newRank < top) {
                    	toSquare = (21 + aFile) + (10 * newRank);
        	            moves.add(Move.quiet(square, toSquare, Move.QUIET));
                    }
                } else {
                    int newRank = mRank-1;
                    if (newRank > bottom && newRank < top) {
                    	toSquare = (21 + aFile) + (10 * newRank);
        	            moves.add(Move.quiet(square, toSquare, Move.QUIET));
                    }
                    newRank = mRank+1;
                    if (newRank > bottom && newRank < top) {
                    	toSquare = (21 + aFile) + (10 * newRank);
        	            moves.add(Move.quiet(square, toSquare, Move.QUIET));
                    }
                }
            }
        }
	}
 
	private static void rookMoves(MoveArray moves, int piece, int attackerSquare, int kingSquare, Game game) {
		int square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		
		if (SquareAttack.rookAttacks(piece, attackerSquare, game)) {
			moves.add(Move.capture(square, attackerSquare, Move.CAPTURE));
		}
		
		int mRank = Definitions.RANKS[square];
		int mFile = Definitions.FILES[square];
		int aRank = Definitions.RANKS[attackerSquare];
		int aFile = Definitions.FILES[attackerSquare];
		int kRank = Definitions.RANKS[kingSquare];
		int kFile = Definitions.FILES[kingSquare];
		
		int toSquare;
		
		if (sameDiagonal(aRank, aFile, kRank, kFile)) {
			if (betweenRanks(mRank, aRank, kRank)) {
	            int diff;
	            int intFile;
	            if ((attackerSquare-kingSquare) % 9 == 0) {
	                if (aRank > kRank) {
	                    diff = aRank - mRank;
	                    intFile = aFile + diff;
	                } else {
	                    diff = kRank - mRank;
	                    intFile = kFile + diff;
	                }
	            } else {
	                if (aRank > kRank) {
	                    diff = aRank - mRank;
	                    intFile = aFile - diff;
	                } else {
	                    diff = kRank - mRank;
	                    intFile = kFile - diff;
	                }
	            }
	            
	            toSquare = (21 + intFile) + (10 * mRank);
	            
	            if (SquareAttack.rookAttacks(piece, toSquare, game)){
	                moves.add(Move.quiet(square, toSquare, Move.QUIET));
	            }
	        }
	        if (betweenFiles(mFile, aFile, kFile)) {
	            int diff;
	            int intRank;
	            if ((attackerSquare-kingSquare) % 9 == 0) {
	                if (aFile > kFile) {
	                    diff = aFile - mFile;
	                    intRank = aRank + diff;
	                } else {
	                    diff = kFile - mFile;
	                    intRank = kRank + diff;
	                }
	            } else {
	                if (aFile > kFile) {
	                    diff = aFile - mFile;
	                    intRank = aRank - diff;
	                } else {
	                    diff = kFile - mFile;
	                    intRank = kRank - diff;
	                }
	            }

	            toSquare = (21 + mFile) + (10 * intRank);
	            
	            if (SquareAttack.rookAttacks(piece, toSquare, game)){
	                moves.add(Move.quiet(square, toSquare, Move.QUIET));
	            }
	        }
        } else {
        	if (aRank == kRank && mRank != aRank) {
        		if (betweenFiles(mFile, aFile, kFile)) {
                    int dist = Math.abs(mRank - aRank);
                    int intRank = mRank > aRank ? mRank-dist : mRank+dist;
                    
                    toSquare = (21 + mFile) + (10 * intRank);
    	            
    	            if (SquareAttack.rookAttacks(piece, toSquare, game)){
    	                moves.add(Move.quiet(square, toSquare, Move.QUIET));
    	            }
                }
            } else if (aFile == kFile && mFile != aFile){
                if (betweenRanks(mRank, aRank, kRank)) {
                    int dist = Math.abs(mFile - aFile);
                    int intFile = mFile > aFile ? mFile-dist : mFile+dist;

    	            toSquare = (21 + intFile) + (10 * mRank);
    	            
    	            if (SquareAttack.rookAttacks(piece, toSquare, game)){
    	                moves.add(Move.quiet(square, toSquare, Move.QUIET));
    	            }
                }
            }
        }
	}
	
	private static void bishopMoves(MoveArray moves, int piece, int attackerSquare, int kingSquare, Game game) {
		int square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
		
		if (SquareAttack.bishopAttacks(piece, attackerSquare, game)) {
			moves.add(Move.capture(square, attackerSquare, Move.CAPTURE));
		}
		
		int mRank = Definitions.RANKS[square];
		int mFile = Definitions.FILES[square];
		int aRank = Definitions.RANKS[attackerSquare];
		int aFile = Definitions.FILES[attackerSquare];
		int kRank = Definitions.RANKS[kingSquare];
		int kFile = Definitions.FILES[kingSquare];
		
		int toSquare;
		
		if (sameDiagonal(aRank, aFile, kRank, kFile)) {
			if (!sameDiagonal(mRank, mFile, aRank, aFile) && (mRank+mFile)%2 == (aRank+aFile)%2) {
                if ((attackerSquare-kingSquare) % 9 == 0) {
                    if (aRank < kRank && aFile > kFile) {
                        int dist = Math.abs(((aRank + aFile) - (mRank + mFile)) / 2);
                        int intRank = mRank;
                        int intFile = mFile;
                        if (isUnderDiagonalLeft(mRank, mFile, aRank, aFile)) {
                            intRank += dist;
                            intFile += dist;
                        } else {
                            intRank -= dist;
                            intFile -= dist;
                        }
                        if (intRank > aRank && intRank < kRank) {
            	            toSquare = (21 + intFile) + (10 * intRank);
            	            
            	            if (SquareAttack.bishopAttacks(piece, toSquare, game)){
            	                moves.add(Move.quiet(square, toSquare, Move.QUIET));
            	            }
                        }
                    } else {
                        int dist = Math.abs(((aRank + aFile) - (mRank + mFile)) / 2);
                        int intRank = mRank;
                        int intFile = mFile;
                        if (isUnderDiagonalLeft(mRank, mFile, aRank, aFile)) {
                            intRank += dist;
                            intFile += dist;
                        } else {
                            intRank -= dist;
                            intFile -= dist;
                        }
                        if (intRank > kRank && intRank < aRank) {
                        	toSquare = (21 + intFile) + (10 * intRank);
            	            
            	            if (SquareAttack.bishopAttacks(piece, toSquare, game)){
            	                moves.add(Move.quiet(square, toSquare, Move.QUIET));
            	            }
                        }
                    }
                } else {
                    //diagonal line bottom left to top right
                    if (aRank < kRank && aFile < kFile) {
                        int dist = Math.abs(((mRank - kRank) - (mFile - kFile)) / 2);
                        int intRank = mRank;
                        int intFile = mFile;
                        if (isOverDiagonalRight(mRank, mFile, kRank, kFile)) {
                            intRank -= dist;
                            intFile += dist;
                        } else {
                            intRank += dist;
                            intFile -= dist;
                        }
                        if (intRank > aRank && intRank < kRank) {
                        	toSquare = (21 + intFile) + (10 * intRank);
            	            
            	            if (SquareAttack.bishopAttacks(piece, toSquare, game)){
            	                moves.add(Move.quiet(square, toSquare, Move.QUIET));
            	            }
                        }
                    } else {
                        int dist = Math.abs(((mRank - kRank) - (mFile - kFile)) / 2);
                        int intRank = mRank;
                        int intFile = mFile;
                        if (isOverDiagonalRight(mRank, mFile, kRank, kFile)) {
                            intRank -= dist;
                            intFile += dist;
                        } else {
                            intRank += dist;
                            intFile -= dist;
                        }
                        if (intRank > kRank && intRank < aRank) {
                        	toSquare = (21 + intFile) + (10 * intRank);
            	            
            	            if (SquareAttack.bishopAttacks(piece, toSquare, game)){
            	                moves.add(Move.quiet(square, toSquare, Move.QUIET));
            	            }
                        }
                    }
                }
	        }
        } else {
        	if (aRank == kRank) {
                if (aRank != mRank) {
                    int dist = Math.abs(mRank - aRank);
                    int intRank, intFileLeft, intFileRight;
                    if (mRank > aRank) {
                        intRank = mRank - dist;
                    } else {
                        intRank = mRank + dist;
                    }
                    intFileLeft = mFile - dist;
                    intFileRight = mFile + dist;
                    if (aFile < kFile) {
                        if (intFileLeft > aFile && intFileLeft < kFile) {
                        	toSquare = (21 + intFileLeft) + (10 * intRank);
            	            
            	            if (SquareAttack.bishopAttacks(piece, toSquare, game)){
            	                moves.add(Move.quiet(square, toSquare, Move.QUIET));
            	            }
                        }
                        if (intFileRight > aFile && intFileRight < kFile) {
                        	toSquare = (21 + intFileRight) + (10 * intRank);
            	            
            	            if (SquareAttack.bishopAttacks(piece, toSquare, game)){
            	                moves.add(Move.quiet(square, toSquare, Move.QUIET));
            	            }
                        }
                    } else {
                        if (intFileLeft > kFile && intFileLeft < aFile) {
                        	toSquare = (21 + intFileLeft) + (10 * intRank);
            	            
            	            if (SquareAttack.bishopAttacks(piece, toSquare, game)){
            	                moves.add(Move.quiet(square, toSquare, Move.QUIET));
            	            }
                        }
                        if (intFileRight > kFile && intFileRight < aFile) {
                        	toSquare = (21 + intFileRight) + (10 * intRank);
            	            
            	            if (SquareAttack.bishopAttacks(piece, toSquare, game)){
            	                moves.add(Move.quiet(square, toSquare, Move.QUIET));
            	            }
                        }
                    }
                }
            } else {
                if (mFile != aFile) {
                    int dist = Math.abs(mFile - aFile);
                    int intRankUp, intRankDown, intFile;
                    if (mFile > aFile) {
                        intFile = mFile - dist;
                    } else {
                        intFile = mFile + dist;
                    }
                    intRankUp = mRank + dist;
                    intRankDown = mRank - dist;
                    if (aRank < kRank) {
                        if (intRankDown > aRank && intRankDown < kRank) {
                        	toSquare = (21 + intFile) + (10 * intRankDown);
            	            
            	            if (SquareAttack.bishopAttacks(piece, toSquare, game)){
            	                moves.add(Move.quiet(square, toSquare, Move.QUIET));
            	            }
                        }
                        if (intRankUp > aRank && intRankUp < kRank) {
                        	toSquare = (21 + intFile) + (10 * intRankUp);
            	            
            	            if (SquareAttack.bishopAttacks(piece, toSquare, game)){
            	                moves.add(Move.quiet(square, toSquare, Move.QUIET));
            	            }
                        }
                    } else {
                        if (intRankDown > kRank && intRankDown < aRank) {
                        	toSquare = (21 + intFile) + (10 * intRankDown);
            	            
            	            if (SquareAttack.bishopAttacks(piece, toSquare, game)){
            	                moves.add(Move.quiet(square, toSquare, Move.QUIET));
            	            }
                        }
                        if (intRankUp > kRank && intRankUp < aRank) {
                        	toSquare = (21 + intFile) + (10 * intRankUp);
            	            
            	            if (SquareAttack.bishopAttacks(piece, toSquare, game)){
            	                moves.add(Move.quiet(square, toSquare, Move.QUIET));
            	            }
                        }
                    }
                }
            }
        }
	}
	
	
	private static boolean betweenFiles(int file, int aFile, int kFile) {
		if (aFile > kFile) {
			return (file < aFile && file > kFile);
		}
		return (file > aFile && file < kFile);
	}
	
	private static boolean betweenRanks(int rank, int aRank, int kRank) {
		if (aRank > kRank) {
			return (rank < aRank && rank > kRank);
		}
		return (rank > aRank && rank < kRank);
	}
	
	private static boolean sameDiagonal(int aRank, int aFile, int bRank, int bFile) {
		return Math.abs(aRank - bRank) == Math.abs(aFile - bFile);
	}

    private static boolean isUnderDiagonalLeft(int mRank, int mFile, int aRank, int aFile) {
        return aRank + aFile > mRank + mFile;
    }

    private static boolean isOverDiagonalRight(int mRank, int mFile, int aRank, int aFile) {
        return mRank - aRank > mFile - aFile;
    }
}
