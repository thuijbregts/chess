package com.thomas.chess.engine.chess120;

public class MoveArray {
	
	private int[] mMoves;
	private int mSize;
	
	public MoveArray() {
		mMoves = new int[200];
		mSize = 0;
	}
	
	public void add(int move) {
		mMoves[mSize++] = move;
	}

	public void setSize(int mSize) {
		this.mSize = mSize;
	}
	
	public int size() {
		return mSize;
	}
	
	public int[] getMoves() {
		return mMoves;
	}
}
