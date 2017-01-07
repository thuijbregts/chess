package com.thomas.chess.engine.search;

import com.thomas.chess.engine.chess120.MoveArray;

public class HashMap {
	//2^20
	public static final int ARRAY_SIZE = 1048576;
	
	private Node[] mNodes;
	private long[] mHashes;

	public HashMap() {
		mNodes = new Node[ARRAY_SIZE];
		for (int i = 0; i < ARRAY_SIZE; i++) {
			mNodes[i] = new Node();
		}
		mHashes = new long[ARRAY_SIZE];
	}
	
	public void putPV(long hash, int move, int score, int searchDepth) {
		int index = (int) (hash % ARRAY_SIZE);
		
		Node node = mNodes[index];	
		node.setPVMove(move);
		node.setScore(score);
		node.setSearchDepth(searchDepth);
		mHashes[index] = hash;
	}
	
	public void putQuiet(long hash, int move, int score, int searchDepth) {
		int index = (int) (hash % ARRAY_SIZE);
		
		if (mHashes[index] == hash) {
			Node node = mNodes[index];
			node.setQuietMove(move);
			node.setQuietScore(score);
			node.setQuietDepth(searchDepth);
		} else if (mHashes[index] == 0) {
			Node node = mNodes[index];
			node.setQuietMove(move);
			node.setQuietScore(score);
			node.setQuietDepth(searchDepth);
			mHashes[index] = hash;
		}
	}
	
	public void putMoves(long hash, MoveArray moves) {
		int index = (int) (hash % ARRAY_SIZE);
		
		Node node = mNodes[index];
		if (mHashes[index] == 0) {
			node.setMoves(moves);
			mHashes[index] = hash;
		} else if (mHashes[index] == hash) {
			if (node.getMoves() == null) {
				node.setMoves(moves);
			}
		}
	}
	
	public void putQuietMoves(long hash, MoveArray quietMoves) {
		int index = (int) (hash % ARRAY_SIZE);
		
		Node node = mNodes[index];
		if (mHashes[index] == 0) {
			node.setQuietMoves(quietMoves);
			mHashes[index] = hash;
		} else if (mHashes[index] == hash) {
			if (node.getQuietMoves() == null) {
				node.setQuietMoves(quietMoves);
			}
		}
	}
	
	public Node get(long hash) {
		int index = (int) (hash % ARRAY_SIZE);
		return mHashes[index] == hash ? mNodes[index] : null;
	}
	
	public class Node {
		
		private int mPVMove;
		private int mScore;
		private int mSearchDepth;

		private int mQuietMove;
		private int mQuietScore;
		private int mQuietDepth;
		
		private MoveArray mMoves;
		private MoveArray mQuietMoves;
		
		public void clear() {
			mQuietMove = mQuietScore = mQuietDepth = 0;
			
			mMoves = null;
			mQuietMoves = null;
		}
		
		public void setPVMove(int move) {
			mPVMove = move;
		}
		
		public int getPVMove() {
			return mPVMove;
		}
		
		public void setScore(int score) {
			mScore = score;
		}
		
		public int getScore() {
			return mScore;
		}
		
		public void setSearchDepth(int searchDepth) {
			mSearchDepth = searchDepth;
		}
		
		public int getSearchDepth() {
			return mSearchDepth;
		}

		public int getQuietMove() {
			return mQuietMove;
		}
		
		public void setQuietMove(int quietMove) {
			mQuietMove = quietMove;
		}
		
		public int getQuietDepth() {
			return mQuietDepth;
		}
		
		public void setQuietDepth(int quietDepth) {
			mQuietDepth = quietDepth;
		}
		
		public int getQuietScore() {
			return mQuietScore;
		}
		
		public void setQuietScore(int quietScore) {
			mQuietScore = quietScore;
		}
		
		public MoveArray getMoves() {
			return mMoves;
		}
		
		public void setMoves(MoveArray moves) {
			mMoves = moves;
		}
		
		public MoveArray getQuietMoves() {
			return mQuietMoves;
		}
		
		public void setQuietMoves(MoveArray quietMoves) {
			mQuietMoves = quietMoves;
		}
	}
}
