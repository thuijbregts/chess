package com.thomas.chess.game.p_children;

import com.thomas.chess.game.Player;

public class OnlinePlayer extends Player {

    private int mElo;
    private int mId;

    public OnlinePlayer(int color, String name, int elo, int id) {
        super(color);
        mName = name;
        mElo = elo;
        mId = id;
    }
}
