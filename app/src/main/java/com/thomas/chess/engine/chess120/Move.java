package com.thomas.chess.engine.chess120;

import com.thomas.chess.engine.movegen.*;

public class Move {
	
	public enum Draw { STALEMATE, FIFTY, SEVENTYFIVE, THREEFOLD, NO_MATERIAL, AGREEMENT, NONE };
	
	public static final int QUIET = 0;
	public static final int CAPTURE = 1;
	public static final int EN_PASSANT = 2;
	public static final int CASTLING = 3;
	public static final int PROMOTION = 4;
	
	public static final int SQUARE_MASK = 127;
	
	public static final int TO_SHIFT = 7;
	
	public static final int TYPE_MASK = 7;
	public static final int TYPE_SHIFT = 15;
	
	public static final int PROMO_MASK = 7;
	public static final int PROMO_SHIFT = 18;
    public static final int NO_PROMO = 266600447;
	
	public static final int EN_PASSANT_SHIFT = 21;
	
	public static final int CAPTURE_FLAG = 16384;
	
	private Game mGame;
	private int[] mBoard;
	private int[][] mPieces;
	
	private int mMove;
	
	private int mSide;
	private int mOppositeSide;
	
	private int mSourceSquare;
	private int mDestinationSquare;
	
	private int mEnPassantDead;
	
	private int mType;
	private int[] mCheckPieces = new int[] { 0, 0 };
	
	private int mEnPassant;
	private int mCastlingPermissions;
	private int mHalfClockMoves;
	
	private int mMovedPiece;
	private int mMovedIndex;
	
	private int mDeadPiece;
	private int mDeadIndex;
	
	private int mSourceRook;
	private int mDestinationRook;
	private int mMovedRook;
	private int mRookIndex;
	
	private int mPromotedPiece;
	private int mPromotedPawn;
	
	private int mDirection;
	
	private boolean capture;
	private boolean check;
	private boolean checkmate;
	private boolean resign;
	private Draw mDraw;
    private Draw mPotentialDraw;
	
	public static int quiet(int from, int to, int type) {
		int move = from;
		move |= (to << TO_SHIFT);
		move |= (type << TYPE_SHIFT);
		return move;
	}
	
	public static int capture(int from, int to, int type) {
		int move = from;
		move |= (to << TO_SHIFT);
		move |= (type << TYPE_SHIFT);
		move |= CAPTURE_FLAG;
		return move;
	}
	
	public static int promotionQuiet(int from, int to, int type, int promoted) {
		int move = from;
		move |= (to << TO_SHIFT);
		move |= (type << TYPE_SHIFT);
		move |= (promoted << PROMO_SHIFT);
		return move;
	}
	
	public static int promotionCapture(int from, int to, int type, int promoted) {
		int move = from;
		move |= (to << TO_SHIFT);
		move |= (type << TYPE_SHIFT);
		move |= (promoted << PROMO_SHIFT);
		move |= CAPTURE_FLAG;
		return move;
	}
	
	public static int pawnDoubleMove(int from, int to, int type, int enPassant) {
		int move = from;
		move |= (to << TO_SHIFT);
		move |= (type << TYPE_SHIFT);
		move |= (enPassant << EN_PASSANT_SHIFT);
		return move;
	}
	
	public Move(Game game) {
		mGame = game;
		mBoard = game.getBoard();
		mPieces = game.getPieces();
		mDraw = Draw.NONE;
		mPotentialDraw = Draw.NONE;
	}
	
	public void reset() {
		mEnPassant = 0;
		mCastlingPermissions = mGame.getCastlingPermissions();
		mHalfClockMoves = mGame.getHalfClockMoves();
		
		check = checkmate = resign = false;
		capture = false;
		mCheckPieces[0] = mCheckPieces[1] = 0;
		mDraw = mPotentialDraw = Draw.NONE;
	}
	
	public void setMove(int move) {
		mMove = move;
		
		check = checkmate = resign = false;
		mDraw = mPotentialDraw = Draw.NONE;
		
		Move lastMove = mGame.getLastMove();
		mCastlingPermissions = lastMove.getCastlingPermissions();
		mHalfClockMoves = lastMove.getHalfClockMoves();
		mEnPassant = mMove >> EN_PASSANT_SHIFT & SQUARE_MASK;
		mCheckPieces[0] = mCheckPieces[1] = 0;
		
		mSourceSquare = move & SQUARE_MASK;
		mDestinationSquare = move >> TO_SHIFT & SQUARE_MASK;
		mType = move >> TYPE_SHIFT & TYPE_MASK;

		mMovedPiece = mBoard[mSourceSquare];
		mMovedIndex = mMovedPiece & Piece.INDEX_MASK;

		mSide = mGame.getCurrentSide();
		mOppositeSide = 1 - mSide;
		
		if (Definitions.RANKS[mSourceSquare] == Definitions.RANKS[mDestinationSquare]) {
			mDirection = 1;
		} else if (Definitions.FILES[mSourceSquare] == Definitions.FILES[mDestinationSquare]) {
			mDirection = 10;
		} else {
			if ((mMovedPiece & Piece.DIAGONAL_ONLY) != 0) {
				int dir = mDestinationSquare - mSourceSquare;
				mDirection = dir % 9 == 0 ? 9 : 11;
			} else {
				mDirection = Math.abs(mDestinationSquare - mSourceSquare);
			}
		}
		
		capture = (move & CAPTURE_FLAG) != 0;
		if (capture) {
			if (mType == EN_PASSANT) {
				mEnPassantDead = mSide == Game.WHITE ? mDestinationSquare - 10 : mDestinationSquare + 10;
				mDeadPiece = mBoard[mEnPassantDead];
			} else {
				mDeadPiece = mBoard[mDestinationSquare];
			}
			mDeadIndex = mDeadPiece & Piece.INDEX_MASK;
		} else {
			if (mType == CASTLING) {
				if (mSourceSquare < mDestinationSquare) {
					mSourceRook = mDestinationSquare + 1;
					mDestinationRook = mSourceSquare + 1;
				} else {
					mSourceRook = mDestinationSquare - 2;
					mDestinationRook = mSourceSquare - 1;
				}
				mMovedRook = mBoard[mSourceRook];
				mRookIndex = mMovedRook & Piece.INDEX_MASK;
			}
			mDeadPiece = mDeadIndex = mEnPassantDead = 0;
		}
		
		if (mType == PROMOTION) {
			mPromotedPawn = mMovedPiece;
			int type = move >> PROMO_SHIFT & PROMO_MASK;
			mPromotedPiece = Piece.toInt(mMovedIndex, mDestinationSquare, mSide, type);
		}
	}
	
	public void make() {
		updateHalfClock();
		
		switch (mType) {
			case QUIET:
				makeQuiet();
				break;
			case CAPTURE:
				makeCapture();
				break;
			case EN_PASSANT:
				makeEnPassant();
				break;
			case PROMOTION:
				makePromotion();
				break;
			case CASTLING:
				makeCastling();
				break;
		}
		
        updateCastlingPermissions();    
	} 
	
	public void unmake() {
		switch (mType) {
			case QUIET:
				unmakeQuiet();
				break;
			case CAPTURE:
				unmakeCapture();
				break;
			case EN_PASSANT:
				unmakeEnPassant();
				break;
			case PROMOTION:
				unmakePromotion();
				break;
			case CASTLING:
				unmakeCastling();
				break;
		}
	}
	
	private void makeQuiet() {
		checkFriendlyPinned();
		
		mMovedPiece &= Piece.RESET_SQUARE;
		mMovedPiece |= mDestinationSquare << Piece.SQUARE_SHIFT;
		mPieces[mSide][mMovedIndex] = mMovedPiece;
	
		mBoard[mSourceSquare] = Definitions.SQ_EMPTY;
		mBoard[mDestinationSquare] = mMovedPiece;
		
		hasCheck();
	}
	
	private void unmakeQuiet() {
		mMovedPiece &= Piece.RESET_SQUARE;
		mMovedPiece |= mSourceSquare << Piece.SQUARE_SHIFT;
		mPieces[mSide][mMovedIndex] = mMovedPiece;
	
		mBoard[mSourceSquare] = mMovedPiece;
		mBoard[mDestinationSquare] = Definitions.SQ_EMPTY;
	}
	
	private void makeCapture() {
		checkFriendlyPinned();
		
		mPieces[mOppositeSide][mDeadIndex] = (mDeadPiece | Piece.DEAD_FLAG);
		mMovedPiece &= Piece.RESET_SQUARE;
		mMovedPiece |= mDestinationSquare << Piece.SQUARE_SHIFT;
		mPieces[mSide][mMovedIndex] = mMovedPiece;

		mBoard[mSourceSquare] = Definitions.SQ_EMPTY;
		mBoard[mDestinationSquare] = mMovedPiece;
		
		hasCheck();
	}
	
	private void unmakeCapture() {
		mMovedPiece &= Piece.RESET_SQUARE;
		mMovedPiece |= (mSourceSquare << Piece.SQUARE_SHIFT);
		mPieces[mSide][mMovedIndex] = mMovedPiece;
		
		mBoard[mSourceSquare] = mMovedPiece;
		mBoard[mDestinationSquare] = mDeadPiece;
		mPieces[mOppositeSide][mDeadIndex] = mDeadPiece;
	}
	
	private void makeEnPassant() {
		checkFriendlyPinnedEnPassant();
		
		mPieces[mOppositeSide][mDeadIndex] = (mDeadPiece | Piece.DEAD_FLAG);
		mBoard[mEnPassantDead] = Definitions.SQ_EMPTY;

		mMovedPiece &= Piece.RESET_SQUARE;
		mMovedPiece |= mDestinationSquare << Piece.SQUARE_SHIFT;
		mPieces[mSide][mMovedIndex] = mMovedPiece;
		
		mBoard[mSourceSquare] = Definitions.SQ_EMPTY;
		mBoard[mDestinationSquare] = mMovedPiece;
		
		if (mCheckPieces[1] == 0) {
			hasCheck();
		}
	}
	
	private void unmakeEnPassant() {
		mMovedPiece &= Piece.RESET_SQUARE;
		mMovedPiece |= mSourceSquare << Piece.SQUARE_SHIFT;
		mPieces[mSide][mMovedIndex] = mMovedPiece;
		
		mBoard[mSourceSquare] = mMovedPiece;
		mBoard[mDestinationSquare] = Definitions.SQ_EMPTY;
		
		mBoard[mEnPassantDead] = mDeadPiece;
		mPieces[mOppositeSide][mDeadIndex] = mDeadPiece;
	}
	
	private void makePromotion() {
		checkFriendlyPinned();

		mMovedPiece = mPromotedPiece;
		
		mBoard[mSourceSquare] = Definitions.SQ_EMPTY;
		mBoard[mDestinationSquare] = mPromotedPiece;
		mPieces[mSide][mMovedIndex] = mPromotedPiece;
		
		if (capture) {
			mPieces[mOppositeSide][mDeadIndex] = (mDeadPiece | Piece.DEAD_FLAG);
		}
		
		hasCheck();
	}
	
	private void unmakePromotion() {
		mBoard[mSourceSquare] = mPromotedPawn;
		mPieces[mSide][mMovedIndex] = mPromotedPawn;
		
		if (capture) {
			mBoard[mDestinationSquare] = mDeadPiece;
			mPieces[mOppositeSide][mDeadIndex] = mDeadPiece;
		} else {
			mBoard[mDestinationSquare] = Definitions.SQ_EMPTY;
		}
	}
	
	private void makeCastling() {
		mMovedPiece &= Piece.RESET_SQUARE;
		mMovedPiece |= mDestinationSquare << Piece.SQUARE_SHIFT;
		mPieces[mSide][mMovedIndex] = mMovedPiece;
		
		mBoard[mSourceSquare] = Definitions.SQ_EMPTY;
		mBoard[mDestinationSquare] = mMovedPiece;

		mMovedRook &= Piece.RESET_SQUARE;
		mMovedRook |= mDestinationRook << Piece.SQUARE_SHIFT;
		mPieces[mSide][mRookIndex] = mMovedRook;
		
		mBoard[mSourceRook] = Definitions.SQ_EMPTY;
		mBoard[mDestinationRook] = mMovedRook;
		
		hasCheckCastling();
	}
	
	private void unmakeCastling() {
		mMovedPiece &= Piece.RESET_SQUARE;
		mMovedPiece |= mSourceSquare << Piece.SQUARE_SHIFT;
		mPieces[mSide][mMovedIndex] = mMovedPiece;
		
		mBoard[mSourceSquare] = mMovedPiece;
		mBoard[mDestinationSquare] = Definitions.SQ_EMPTY;
		
		mMovedRook &= Piece.RESET_SQUARE;
		mMovedRook |= mSourceRook << Piece.SQUARE_SHIFT;
		mPieces[mSide][mRookIndex] = mMovedRook;
		
		mBoard[mSourceRook] = mMovedRook;
		mBoard[mDestinationRook] = Definitions.SQ_EMPTY;
	}
	
	private void updateHalfClock() {
		int pieceType = mMovedPiece >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;
        if (pieceType == Definitions.PAWN || capture) {
            mHalfClockMoves = 0;
        } else {
            mHalfClockMoves++;
        }
	}
	
	private void updateCastlingPermissions() {
		int index = mMovedPiece & Piece.INDEX_MASK;

		if (mSide == Game.WHITE) {
	    	switch (index) {
		    	case Definitions.KING_INDEX:
		    		mCastlingPermissions &= Definitions.NO_CASTLING_WHITE;
		    		break;
		    	case Definitions.LEFT_ROOK_INDEX:
		    		mCastlingPermissions &= Definitions.NO_CASTLING_Q_W;
		    		break;
		    	case Definitions.RIGHT_ROOK_INDEX:
		    		mCastlingPermissions &= Definitions.NO_CASTLING_K_W;    		
	    	}

			if (mDeadPiece != 0) {
				int deadIndex = mDeadPiece & Piece.INDEX_MASK;
				switch (deadIndex) {
			    	case Definitions.LEFT_ROOK_INDEX:
			    		mCastlingPermissions &= Definitions.NO_CASTLING_Q_B;
			    		break;
			    	case Definitions.RIGHT_ROOK_INDEX:
			    		mCastlingPermissions &= Definitions.NO_CASTLING_K_B;
		    	}
			}
		} else {
			switch (index) {
		    	case Definitions.KING_INDEX:
		    		mCastlingPermissions &= Definitions.NO_CASTLING_BLACK;
		    		break;
		    	case Definitions.LEFT_ROOK_INDEX:
		    		mCastlingPermissions &= Definitions.NO_CASTLING_Q_B;
		    		break;
		    	case Definitions.RIGHT_ROOK_INDEX:
		    		mCastlingPermissions &= Definitions.NO_CASTLING_K_B;    		
	    	}

			if (mDeadPiece != 0) {
				int deadIndex = mDeadPiece & Piece.INDEX_MASK;
				switch (deadIndex) {
			    	case Definitions.LEFT_ROOK_INDEX:
			    		mCastlingPermissions &= Definitions.NO_CASTLING_Q_W;
			    		break;
			    	case Definitions.RIGHT_ROOK_INDEX:
			    		mCastlingPermissions &= Definitions.NO_CASTLING_K_W;
		    	}
			}
		}
    }
	
	private void hasCheck() {
		int kingSquare = mGame.getEnemyKingSquare(mSide);
		if (SquareAttack.attacksSquare(mMovedPiece, kingSquare, mGame)) {
			if (check) {
				mCheckPieces[1] = mDestinationSquare;
			} else {
				mCheckPieces[0] = mDestinationSquare;
				check = true;
			}
		}
	}
	
	private void hasCheckCastling() {
		int kingSquare = mGame.getEnemyKingSquare(mSide);
		if (SquareAttack.attacksSquare(mMovedRook, kingSquare, mGame)) {
			if (check) {
				mCheckPieces[1] = mDestinationRook;
			} else {
				mCheckPieces[0] = mDestinationRook;
				check = true;
			}
		}
	}
	
	private void checkFriendlyPinned() {
        int friendlyPinned = PinGenerator.isFriendlyPinned
        		(mMovedPiece, mGame.getEnemyKingSquare(mSide), mDirection, mGame);
        if (friendlyPinned != 0) {
            check = true;
            mCheckPieces[0] = friendlyPinned;
        }
    }

    private void checkFriendlyPinnedEnPassant() {
    	int friendlyPinned = PinGenerator.isFriendlyPinnedEnPassant
    			(mMovedPiece, mEnPassantDead, mGame.getEnemyKingSquare(mSide), mDirection, mGame);
        if (friendlyPinned != 0) {
        		check = true;
                mCheckPieces[0] = friendlyPinned & PinGenerator.SQUARE_MASK;
				int second = friendlyPinned >> PinGenerator.SECOND_PIN_SHIFT & PinGenerator.SQUARE_MASK;
				if (second != 0) {
					mCheckPieces[1] = second;
				}
        }
    }
	
    public void setCheckPiece(int piece) {
    	piece = (piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK);
    	if (!check) {
    		check = true;
    		mCheckPieces[0] = piece;
    	} else {
    		mCheckPieces[1] = piece;
    	}
	}
    
	public int getCastlingPermissions() {
		return mCastlingPermissions;
	}
	
	public void setCastlingPermissions(int mCastlingPermissions) {
		this.mCastlingPermissions = mCastlingPermissions;
	}
	
	public int getHalfClockMoves() {
		return mHalfClockMoves;
	}
	
	public void setHalfClockMoves(int mHalfClockMoves) {
		this.mHalfClockMoves = mHalfClockMoves;
	}
	
	public int getEnPassant() {
		return mEnPassant;
	}
	
	public void setEnPassant(int mEnPassant) {
		this.mEnPassant = mEnPassant;
	}
	
	public boolean isCapture() {
		return capture;
	}
	
	public boolean isCheck() {
		return check;
	}
	
	public int getSourceSquare() {
		return mSourceSquare;
	}
	
	public int getDestinationSquare() {
		return mDestinationSquare;
	}
	
	public int getType() {
		return mType;
	}
	
	public int getMoveNumber() {
		return mMove;
	}
	
	public int getDeadPiece() {
		return mDeadPiece;
	}
	
	public int getMovedPiece() {
		return mMovedPiece;
	}
	
	public int getDirection() {
		return mDirection;
	}
	
	public int[] getCheckPieces() {
		return mCheckPieces;
	}
	
	public int getSide() {
		return mSide;
	}
	
	public int getSourceRook() {
		return mSourceRook;
	}
	
	public int getDestinationRook() {
		return mDestinationRook;
	}
	
    public Draw getDraw() {
		return mDraw;
	}
    
    public void setDraw(Draw draw) {
		mDraw = draw;
	}
    
    public Draw getPotentialDraw() {
		return mPotentialDraw;
	}
    
    public void setPotentialDraw(Draw potentialDraw) {
		mPotentialDraw = potentialDraw;
	}
    
    public int getPromotedPiece() {
		return mPromotedPiece;
	}

    public int getPromotedPawn() {
        return mPromotedPawn;
    }

    public void setCheckmate(boolean checkmate) {
		this.checkmate = checkmate;
	}

	public boolean isCheckmate() {
		return checkmate;
	}

	public void setResign(boolean resign) {
		this.resign = resign;
	}

	public boolean isResign() {
		return resign;
	}
}
