package com.thomas.chess.player;

public abstract class Player {

    protected String mName;
    protected int mColor;

    public Player(String name, int color) {
        mName = name;
        mColor = color;
    }

    public Player(int color) {
        mColor = color;
    }

    public String getName() {
        return mName;
    }

    public int getColor() {
        return mColor;
    }
}
