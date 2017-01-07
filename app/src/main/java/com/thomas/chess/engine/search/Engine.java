package com.thomas.chess.engine.search;

import com.thomas.chess.engine.chess120.*;
import com.thomas.chess.engine.movegen.*;

import java.util.Timer;
import java.util.TimerTask;

public class Engine {
	
	//time in milliseconds
	public static final int SEARCH_TIME = 4000;
	
	//killer heuristic
	public static final int MAX_DEPTH = 50;
	public static final int KILLER_STORED = 2;

	public static final int BOARD_SIZE = 120;
	
	//depth (R)eduction for null-move pruning
	public static final int R = 2;

    private Game mGame;
    
    private Timer mTimer;
    private boolean useBook;
    
    private HashMap mHashMap;
    
    private int[][] mMVVLVAValues = new int[Evaluator.MVV_LVA_LENGTH][Evaluator.MVV_LVA_LENGTH];
    //KillerMove heuristic
    private int[][] mKillerMoves = new int[KILLER_STORED][MAX_DEPTH];
    private int[][] mMateKillers = new int[KILLER_STORED][MAX_DEPTH];
    //CounterMove heuristic
    private int[][] mCounterMoves = new int[Definitions.DIFF_PIECES][BOARD_SIZE];
    private int[][] mHistoryMoves = new int[Definitions.DIFF_PIECES][BOARD_SIZE];
    
    private int mCurrentPly;
    
    private int mSearchDepth;

    private int[] mMovesPattern = new int[50];
    private int mPatternCount;
    
    private boolean stop;

    public Engine(Game game) {
        mGame = game;
        useBook = true;
        mHashMap = new HashMap();
        generateMVVLVA();
    }
    
    private void generateMVVLVA() {
    	for (int i = 0; i < Evaluator.MVV_LVA_LENGTH; i++) {
    		for (int j = 0; j < Evaluator.MVV_LVA_LENGTH; j++) {
    			mMVVLVAValues[i][j] = Evaluator.MVV_LVA[j] + 6 - (Evaluator.MVV_LVA[i] / 100); 
    		}
    	}
    }

    public int findMove(String moves) {
        if (useBook) {
            String move = OpeningBook.nextMove(moves);
            if (move != null) {
                return OpeningBook.toInt(move, mGame);
            }
        }
        useBook = false;
        return think();
    }

    //iterative deepening
    private int think() {
    	clearValues();
    	
        int bestMove = 0;
        int bestScore;
        long currentPosition = mGame.getCurrentPosition();
        HashMap.Node node = mHashMap.get(currentPosition);
        int startDepth = 1;
        
        if (node != null) {
        	bestMove = node.getPVMove();
        	startDepth = node.getSearchDepth() + 1;
        }

        for (mSearchDepth = startDepth; mSearchDepth < MAX_DEPTH; mSearchDepth++) {
            alphaBeta(-Evaluator.INFINITE, Evaluator.INFINITE, mSearchDepth, true);
            
            if (stop) {
            	break;
            }
            
            node = mHashMap.get(mGame.getCurrentPosition());
            if (node != null) {
            	bestMove = node.getPVMove();
            	bestScore = node.getScore();
            } else {
            	break;
            }

            if (bestScore >= Evaluator.CHECKMATE) {
            	break;
            }
        }
        mTimer.cancel();
        return bestMove;
    }

    private int alphaBeta(int alpha, int beta, int depth, boolean nullMove) {
        if (depth == 0) {
            return quiescenceSearch(alpha, beta, mSearchDepth);
        }

        long currentPosition = mGame.getCurrentPosition();
    	
    	HashMap.Node node = mHashMap.get(currentPosition);
    	boolean notNull = (node != null);
    	
    	MoveArray moves;
    	if (notNull && node.getMoves() != null) {
    		moves = node.getMoves();
    	} else {
    		moves = MoveGenerator.generateMoves(mGame);
    		//mHashMap.putMoves(currentPosition, moves);
    	}
    	int size = moves.size();

        Move lastMove = mGame.getLastMove();
        boolean check = lastMove.isCheck();
        int moveCount = mGame.getMoveCount();
 	
		if (size == 0) {
	        if (check) {       
	        	return -(Evaluator.CHECKMATE + depth);
	        }
	    	return drawValue();
	    }

	    if (lastMove.getDraw() != Move.Draw.NONE) {
	    	return drawValue();
	    }
	    
	    if (lastMove.getPotentialDraw() != Move.Draw.NONE) {
	    	int draw = drawValue();
	    	if (draw > 0) {
	    		return draw;
	    	}
	    }
        
        if (check) {
    		nullMove = false;
    		depth++;
        }
        
        int pvMove = 0;
        if (notNull) {
        	pvMove = node.getPVMove();
        	if (depth <= node.getSearchDepth()) {
        		return node.getScore();      	
        	}
        }
        
        //null move pruning
        if (nullMove && mGame.sufficientMaterial() && depth > R) {
        	mGame.makeNullMove();
        	int nullScore = -alphaBeta(-beta, 1-beta, depth-1-R, false);
        	mGame.unmakeNullMove();
        	if (nullScore >= beta) {
        		return beta;
        	}
        }
        
        int[] mv = moves.getMoves();
        
        int bestMove = 0;
        int previousAlpha = alpha;
        int score;        
        int move;

        for (int i = 0; i < size; i++) {
        	nextMove(i, mv, size, pvMove);
        	move = mv[i];
            mGame.makeMove(move);
            mCurrentPly++;
            score = -alphaBeta(-beta, -alpha, depth-1, true);
            mGame.unmakeMove();
            mCurrentPly--;
            if (stop) {
            	return 0;
            }
            if (score > alpha) {
                if (score >= beta) {
                    //NON CAPTURE
                    if ((move & Move.CAPTURE_FLAG) == 0) {
                    	if (score >= Evaluator.CHECKMATE) {
                    		mMateKillers[1][mCurrentPly] = mMateKillers[0][mCurrentPly];
                        	mMateKillers[0][mCurrentPly] = move;
                    	} else {
                    		mKillerMoves[1][mCurrentPly] = mKillerMoves[0][mCurrentPly];
                        	mKillerMoves[0][mCurrentPly] = move;
                    	}              	
                    	
                    	int moved = lastMove.getMovedPiece();
                    	int type = moved >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;
                    	int side = lastMove.getSide();
                    	
                    	if (moveCount > 0) {
                    		mCounterMoves[type+side][lastMove.getDestinationSquare()] = move;
                    	}
                    	
                    	if (mCurrentPly <= 6) {
                        	mHistoryMoves[type+side][move >> Move.TO_SHIFT & Move.SQUARE_MASK] = depth * depth;
                    	}
                    }
                    return beta;
                }
                alpha = score;
                bestMove = move;       
            }
        }

        if (previousAlpha != alpha) {
            mHashMap.putPV(mGame.getCurrentPosition(), bestMove, alpha, depth);
        }

        return alpha;
    }
    
    private int quiescenceSearch(int alpha, int beta, int qsDepth) {
    	Move lastMove = mGame.getLastMove();

		if (lastMove.getDraw() != Move.Draw.NONE) {
	    	return drawValue();
	    }
         
        if (lastMove.getPotentialDraw() != Move.Draw.NONE) {
         	int draw = drawValue();
         	if (draw > 0) {
         		return draw;
         	}
        }
    	
    	int score = Evaluator.evaluateBoard(mGame);
    	
    	if (score >= beta) {
    		return beta;
    	}
    	
    	//delta pruning
    	int delta = Evaluator.QUEEN;
    	if (lastMove.getType() == Move.PROMOTION) {
    		delta += delta - Evaluator.PAWN;
    	}
    	if (score < alpha - delta) {
    		return alpha;
    	}

    	if (score > alpha) {
    		alpha = score;
    	}
    	
    	long currentPosition = mGame.getCurrentPosition();
    	
    	HashMap.Node node = mHashMap.get(currentPosition);
    	boolean notNull = (node != null);
    	
    	MoveArray moves;
    	if (notNull && node.getQuietMoves() != null) {
    		moves = node.getQuietMoves();
    	} else {
    		moves = CaptureGenerator.generateMoves(mGame);
    		//mHashMap.putQuietMoves(currentPosition, moves);
    	}
    	int size = moves.size();
    	int[] mv = moves.getMoves();
    	
    	int pvMove = 0;
    	
    	if (notNull) {
        	pvMove = node.getQuietMove();
        	if (qsDepth <= node.getQuietDepth()) {
        		return node.getQuietScore();
        	}
        }
    	
        int bestMove = 0;
        int previousAlpha = alpha;
        int move;
        
        for (int i = 0; i < size; i++) {
        	nextMove(i, mv, size, pvMove);
        	move = mv[i];
            mGame.makeMove(move);
            score = -quiescenceSearch(-beta, -alpha, qsDepth+1);
            mGame.unmakeMove();
            if (stop) {
            	return 0;
            }
            if (score > alpha) {
                if (score >= beta) {
                    return beta;
                }
                alpha = score;
                bestMove = move;
            }
        }

        if (previousAlpha != alpha) {
            mHashMap.putQuiet(currentPosition, bestMove, alpha, qsDepth);
        }

        return alpha;
    }

    
    
    //TODO am�liorer qualit�
    //evaluate the probability to win the game => if win, return checkmate/2 value, if lose return -checkmate/2 else 0
    private int drawValue() {
//    	int value = Values.CHECKMATE/2;
//    	Piece[] pieces = mGame.getPieces();
//    	Piece piece;
//    	int score = 0;
//    	 for (int i = 0; i < 30; i++) {
//             piece = pieces[i];
//             if (!piece.isDead()) {
//                 if (i%2 == 0) {
//                     score += Values.pieceValue(piece);
//                 } else {
//                     score -= Values.pieceValue(piece);
//                 }
//             }
//         }
//    	 if (score < 0) {
//    		 return (mGame.getCurrentSide() == Game.WHITE ? -value : value); 
//    	 }
//    	 if (score > 0) {
//    		 return (mGame.getCurrentSide() == Game.WHITE ? value : -value); 
//    	 }
    	 return 0;
    }

    private void nextMove(int currentIndex, int[] moves, int size, int pvMove) {
    	int current = moves[currentIndex];
    	int bestIndex = -1;
    	int bestMove = current;
    	int bestScore = moveScore(bestMove);
    	int score;
    	int move;
    	int newIndex;
    	
    	for (newIndex = currentIndex+1; newIndex < size; newIndex++) {
    		move = moves[newIndex];
    		if (move == pvMove) {
    			bestIndex = newIndex;
    			bestMove = move;
    			break;
    		}
    		score = moveScore(move);
    		if (score > bestScore) {
    			bestIndex = newIndex;
    			bestMove = move;
    			bestScore = score;
    		}
    	}
    	if (bestIndex != -1) {
	    	moves[currentIndex] = bestMove;
			moves[bestIndex] = current;
    	}
    }
    
    private int moveScore(int move) {
    	int[] board = mGame.getBoard();
    	int src = move & Move.SQUARE_MASK;
    	int dst = move >> Move.TO_SHIFT & Move.SQUARE_MASK;
    	int moveType = move >> Move.TYPE_SHIFT & Move.TYPE_MASK;
    	int moved = board[src];
    	int type = moved >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;
    	int side = moved >> Piece.SIDE_SHIFT & 1;
		int dead;
    	if (moveType == Move.EN_PASSANT) {
    		return 1105;
    	}
    	if ((move & Move.CAPTURE_FLAG) != 0) {
    		dead = board[dst];
    		int dType = dead >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;
    		return mMVVLVAValues[type][dType] + 1000;
    	}
    	if (move == mMateKillers[0][mCurrentPly]) {
    		return 1000;
    	}
    	if (move == mMateKillers[1][mCurrentPly]) {
    		return 900;
    	}
    	if (move == mKillerMoves[0][mCurrentPly]) {
    		return 800;
    	}
    	if (move == mKillerMoves[1][mCurrentPly]) {
    		return 700;
    	}
    	if (mGame.getMoveCount() > 0) {
    		Move lastMove = mGame.getLastMove();
    		if (move == mCounterMoves[type+side][lastMove.getDestinationSquare()]) {
    			return 500;
    		}
    	}
    	return mHistoryMoves[type+side][move >> Move.TO_SHIFT & Move.SQUARE_MASK];
    }
    
    private void bestMovePattern(int depth, int move) {
    	mPatternCount = 0;
    	
    	while(move != 0 && mPatternCount < depth) {    	
    		if (moveExists(move)) {
    			mGame.makeMove(move);
    			mMovesPattern[mPatternCount++] = move;			
    		} else {
    			break;
    		}		
    		HashMap.Node node = mHashMap.get(mGame.getCurrentPosition());
    		move = node == null ? 0 : node.getPVMove();
    	}
    	
    	for (int i = 0; i < mPatternCount; i++) {
    		mGame.unmakeMove();
    	}
    }
    
    //checks if a specific move is generated at the current board position
    private boolean moveExists(int move) {
    	int[] board = mGame.getBoard();
    	int piece = board[move & Move.SQUARE_MASK];
    	int side = piece >> Piece.SIDE_SHIFT & 1;
    	if (side != mGame.getCurrentSide()) {
    		return false;
    	}
    	if ((piece & Piece.DEAD_FLAG) != 0) {
    		return false;
    	}
    	MoveArray moves = MoveGenerator.generateMoves(mGame);
    	
    	int[] mv = moves.getMoves();
    	int size = moves.size();
    	for (int i = 0; i < size; i++) {
    		if(mv[i] == move) {
    			return true;
    		}
    	}
    	return false;
    } 

    private void clearValues() {
    	stop = false;
    	
    	for (int i = 0; i < KILLER_STORED; i++) {
    		for (int j = 0; j < MAX_DEPTH; j++) {
    			mKillerMoves[i][j] = 0;
    			mMateKillers[i][j] = 0;
    		}
    	}
    	
    	for (int i = 0; i < Definitions.DIFF_PIECES; i++) {
    		for (int j = 0; j < BOARD_SIZE; j++) {
    			mCounterMoves[i][j] = 0;
    			mHistoryMoves[i][j] = 0;
    		}
    	}
    	
    	//mHashMap.clear();
    	
    	mTimer = new Timer();
    	mTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					stop = true;
				}
     	}, SEARCH_TIME);
    }

    public void useBook() {
        useBook = true;
    }
}
