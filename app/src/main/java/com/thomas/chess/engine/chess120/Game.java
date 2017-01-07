package com.thomas.chess.engine.chess120;

public class Game {
	
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	
	public static final int BOARD_SIZE = 120;
	
	public static final int MOVE_SIZE = 100000;
	
	private int[] mBoard = new int[BOARD_SIZE];
	
	private int[][] mPieces = new int[2][16];
	private int[] mWhitePieces = new int[16];
	private int[] mBlackPieces = new int[16];
	
	private int[][] mPiecesCount = new int[2][6];
	
	private long[] mPositions = new long[MOVE_SIZE];
	private int mPositionsCount;
	
	public static int[] mCastlingHash;
	public static int[][] mPiecesHash;
	public static int[] mEnPassantHash;
	public static int mSideHash;
	
	private Move[] mMoves = new Move[MOVE_SIZE];
	private int mMoveCount;
	private int mMoveIndex;
	
	private int mCurrentSide;
	
	public Game() {
		Utils.setFEN(this, Definitions.START_FEN);
	}

	public void initializeGame() {
		mPositionsCount = 0;
		initializeBoard();
		initializePieces();
		initializeMoves();
	}
	
	private void initializeBoard() {
		int i;
		int modulo;
		for (i = 0; i < 120; i++) {
			mBoard[i] = Definitions.SQ_OFFBOARD;
		}
		for (i = Definitions.LOWER_BOUND; i <= Definitions.UPPER_BOUND; i++) {
			modulo = i % 10;
			if (modulo != 0 && modulo != 9) {
				mBoard[i] = Definitions.SQ_EMPTY;
			}
		}
	}
	
	private void initializePieces() {
		mPieces[WHITE] = mWhitePieces;
		mPieces[BLACK] = mBlackPieces;
	}
	
	private void initializeMoves() {
		for (int i = 0; i < MOVE_SIZE; i++) {
			mMoves[i] = new Move(this);
		}
		mMoveCount = 0;
		mMoveIndex = 1;
	}
	
	public void makeMove(int move) {
		Move mv = mMoves[mMoveIndex];
		mv.setMove(move);
		mv.make();
		mMoveCount++;
		mMoveIndex++;
		changeSide();
		updatePiecesCount(mv);
		updatePositionHash(mv);
		findDraw(mv);
	}
	
	public void unmakeMove() {
		Move move = mMoves[--mMoveIndex];
		mMoveCount--;
		move.unmake();
		mPositionsCount--;
		changeSide();
		resumePiecesCount(move);
	}
	
	public void makeNullMove() {
		Move move = mMoves[mMoveIndex];
    	move.reset();
    	mMoveCount++;
		mMoveIndex++;
        changeSide();
        updatePositionHash(move);
    }
    
    public void unmakeNullMove() {
    	mMoveCount--;
    	mMoveIndex--;
    	mPositionsCount--;
    	changeSide();
    }
    
    private void updatePiecesCount(Move move) {
    	int type;
    	int side = move.getSide();
    	int square = move.getDestinationSquare();
    	if (move.getType() == Move.PROMOTION) {
    		int promoted = move.getPromotedPiece();
    		type = promoted >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;
			switch (type) {
				case Definitions.KNIGHT:
					mPiecesCount[side][Definitions.KNIGHT] = mPiecesCount[side][Definitions.KNIGHT] + 1;
					break;
				case Definitions.ROOK:
					mPiecesCount[side][Definitions.ROOK] = mPiecesCount[side][Definitions.ROOK] + 1;
					break;
				case Definitions.QUEEN:
					mPiecesCount[side][Definitions.QUEEN] = mPiecesCount[side][Definitions.QUEEN] + 1;
					break;
				default:
					if ((Definitions.RANKS[square] + Definitions.FILES[square]) % 2 == 0) {
						mPiecesCount[side][Definitions.DARK_BISHOP] = mPiecesCount[side][Definitions.DARK_BISHOP] + 1;
					} else {
						mPiecesCount[side][Definitions.LIGHT_BISHOP] = mPiecesCount[side][Definitions.LIGHT_BISHOP] + 1;
					}
			}
    		mPiecesCount[side][Definitions.PAWN] = mPiecesCount[side][Definitions.PAWN] - 1;
    	}
    	
    	if (move.isCapture()) {
    		int dead = move.getDeadPiece();
    		side = 1-side;
    		type = dead >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;
			switch (type) {
				case Definitions.KNIGHT:
					mPiecesCount[side][Definitions.KNIGHT] = mPiecesCount[side][Definitions.KNIGHT] - 1;
					break;
				case Definitions.ROOK:
					mPiecesCount[side][Definitions.ROOK] = mPiecesCount[side][Definitions.ROOK] - 1;
					break;
				case Definitions.QUEEN:
					mPiecesCount[side][Definitions.QUEEN] = mPiecesCount[side][Definitions.QUEEN] - 1;
					break;
				case Definitions.PAWN:
					mPiecesCount[side][Definitions.PAWN] = mPiecesCount[side][Definitions.PAWN] - 1;
					break;
				default:
					if ((Definitions.RANKS[square] + Definitions.FILES[square]) % 2 == 0) {
						mPiecesCount[side][Definitions.DARK_BISHOP] = mPiecesCount[side][Definitions.DARK_BISHOP] - 1;
					} else {
						mPiecesCount[side][Definitions.LIGHT_BISHOP] = mPiecesCount[side][Definitions.LIGHT_BISHOP] - 1;
					}
			}
    	}
    }
    
    private void resumePiecesCount(Move move) {
    	int type;
    	int side = move.getSide();
    	int square = move.getDestinationSquare();
    	if (move.getType() == Move.PROMOTION) {
    		int promoted = move.getPromotedPiece();
    		type = promoted >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;
			switch (type) {
				case Definitions.KNIGHT:
					mPiecesCount[side][Definitions.KNIGHT] = mPiecesCount[side][Definitions.KNIGHT] - 1;
					break;
				case Definitions.ROOK:
					mPiecesCount[side][Definitions.ROOK] = mPiecesCount[side][Definitions.ROOK] - 1;
					break;
				case Definitions.QUEEN:
					mPiecesCount[side][Definitions.QUEEN] = mPiecesCount[side][Definitions.QUEEN] - 1;
					break;
				default:
					if ((Definitions.RANKS[square] + Definitions.FILES[square]) % 2 == 0) {
						mPiecesCount[side][Definitions.DARK_BISHOP] = mPiecesCount[side][Definitions.DARK_BISHOP] - 1;
					} else {
						mPiecesCount[side][Definitions.LIGHT_BISHOP] = mPiecesCount[side][Definitions.LIGHT_BISHOP] - 1;
					}
			}
    		mPiecesCount[side][Definitions.PAWN] = mPiecesCount[side][Definitions.PAWN] + 1;
    	}
    	
    	if (move.isCapture()) {
    		int dead = move.getDeadPiece();
    		side = 1-side;
    		type = dead >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;
			switch (type) {
				case Definitions.KNIGHT:
					mPiecesCount[side][Definitions.KNIGHT] = mPiecesCount[side][Definitions.KNIGHT] + 1;
					break;
				case Definitions.ROOK:
					mPiecesCount[side][Definitions.ROOK] = mPiecesCount[side][Definitions.ROOK] + 1;
					break;
				case Definitions.QUEEN:
					mPiecesCount[side][Definitions.QUEEN] = mPiecesCount[side][Definitions.QUEEN] + 1;
					break;
				case Definitions.PAWN:
					mPiecesCount[side][Definitions.PAWN] = mPiecesCount[side][Definitions.PAWN] + 1;
				default:
					if ((Definitions.RANKS[square] + Definitions.FILES[square]) % 2 == 0) {
						mPiecesCount[side][Definitions.DARK_BISHOP] = mPiecesCount[side][Definitions.DARK_BISHOP] + 1;
					} else {
						mPiecesCount[side][Definitions.LIGHT_BISHOP] = mPiecesCount[side][Definitions.LIGHT_BISHOP] + 1;
					}
			}
    	}
    }
    
    private void findDraw(Move move) {
    	move.setPotentialDraw(potentialDraw(move));
    	move.setDraw(mandatoryDraw(move));
    }
    
    private Move.Draw potentialDraw(Move move) {
    	if (move.getHalfClockMoves() >= 100) {
    		return Move.Draw.FIFTY;
    	}
    	if (mPositionsCount >= 8) {  		
	    	long currentPosition = mPositions[mPositionsCount-1];
	    	int repeat = 1;
	    	for (int i = mPositionsCount-3; i >= 0; i -= 2) {
	    		if (mPositions[i] == currentPosition) {
	    			repeat++;
	    			if (repeat == 3) {
	    				return Move.Draw.THREEFOLD;
	    			}
	    		}
	    	}
    	}
    	return Move.Draw.NONE;
    }
    
    private Move.Draw mandatoryDraw(Move move) {
    	if (move.getHalfClockMoves() >= 150) {
    		return Move.Draw.SEVENTYFIVE;
    	}
    	int side = move.getSide();
    	if (mPiecesCount[side][Definitions.PAWN] != 0 
    			|| mPiecesCount[side][Definitions.QUEEN] != 0 
    			|| mPiecesCount[side][Definitions.ROOK] != 0) {
    		return Move.Draw.NONE;
    	}
    	
    	int knights = mPiecesCount[side][Definitions.KNIGHT];
    	int lightBishops = mPiecesCount[side][Definitions.LIGHT_BISHOP];
    	int darkBishops = mPiecesCount[side][Definitions.DARK_BISHOP];
    	
    	if (knights == 0) {
    		if (lightBishops == 0 || darkBishops == 0) {
    			return Move.Draw.NO_MATERIAL;
    		}
    		return Move.Draw.NONE;
    	}
    	if (knights == 1) {
    		return (lightBishops == 0 && darkBishops == 0) ? Move.Draw.NO_MATERIAL : Move.Draw.NONE;
    	}
    	return Move.Draw.NONE;
    }
    
    public boolean sufficientMaterial() {
    	int rooks = mPiecesCount[0][Definitions.ROOK] + mPiecesCount[1][Definitions.ROOK];
    	if (rooks > 0) {
    		return true;
    	}
    	int knights = mPiecesCount[0][Definitions.KNIGHT] + mPiecesCount[1][Definitions.KNIGHT];
    	if (knights > 0) {
    		return true;
    	}
    	int queens = mPiecesCount[0][Definitions.QUEEN] + mPiecesCount[1][Definitions.QUEEN];
    	if (queens > 0) {
    		return true;
    	}
    	int lBishops = mPiecesCount[0][Definitions.LIGHT_BISHOP] + mPiecesCount[1][Definitions.LIGHT_BISHOP];
    	if (lBishops > 0) {
    		return true;
    	}
    	int dBishops = mPiecesCount[0][Definitions.DARK_BISHOP] + mPiecesCount[1][Definitions.DARK_BISHOP];
    	return dBishops > 0;
    }
	
	public int getEnemyKingSquare(int side) {
		return (mPieces[1-side][15] >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK);
	}
	
	public int getFriendlyKingSquare(int side) {
		return (mPieces[side][15] >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK);
	}
	
	public void changeSide() {
		mCurrentSide = 1 - mCurrentSide;
	}
	
	public void generatePositionHash() {
		mPositions[mPositionsCount] = Hashing.generatePositionHash(this);
		mPositionsCount++;
	}

	public void updatePositionHash(Move move) {
		Move lastMove = mMoves[mMoveIndex-2];
		mPositions[mPositionsCount] = Hashing.updatePositionHash(this, move, lastMove.getEnPassant(), lastMove.getCastlingPermissions());
		mPositionsCount++;
	}

	public boolean canClaimDraw() {
        return mMoves[mMoveIndex-1].getPotentialDraw() != Move.Draw.NONE;
    }

    public void claimDraw() {
        Move lastMove = mMoves[mMoveIndex-1];
        lastMove.setDraw(lastMove.getPotentialDraw());
    }

    public void resign() {
        mMoves[mMoveIndex].setResign(true);
        mMoveIndex++;
        mMoveCount++;
        changeSide();
    }

    public boolean isGameOver() {
        if (mMoveCount == 0) {
            return false;
        }
        Move lastMove = mMoves[mMoveIndex-1];
        return lastMove.isCheckmate() || lastMove.isResign() || lastMove.getDraw() != Move.Draw.NONE;
    }
	
	public long getCurrentPosition() {
		return mPositions[mPositionsCount-1];
	}
	
	public void setSide(int side) {
		mCurrentSide = side;
	}
	
	public Move getLastMove() {
		return mMoves[mMoveIndex-1];
	}
	
	public int getEnPassant() {
		return mMoves[mMoveIndex-1].getEnPassant();
	}
	
	public int getCastlingPermissions() {
		return mMoves[mMoveIndex-1].getCastlingPermissions();
	}
	
	public int getHalfClockMoves() {
		return mMoves[mMoveIndex-1].getHalfClockMoves();
	}
	
	public int[] getBoard() {
		return mBoard;
	}
	
	public int[][] getPieces() {
		return mPieces;
	}
	
	public int getCurrentSide() {
		return mCurrentSide;
	}
	
	public Move[] getMoves() {
		return mMoves;
	}
	
	public int getMoveCount() {
		return mMoveCount;
	}
	
	public int getMoveIndex() {
		return mMoveIndex;
	}
	
	public long[] getPositions() {
		return mPositions;
	}

	public int getPositionsCount() {
		return mPositionsCount;
	}
	
	public int[][] getPiecesCount() {
		return mPiecesCount;
	}
	
}
