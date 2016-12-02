package com.thomas.chess.game.p_children;

import com.thomas.chess.game.Player;
import com.thomas.chess.utils.Utils;

public class HistoryPlayer extends Player {

    public HistoryPlayer(int color) {
        super(color);
        switch (color) {
            case Utils.WHITE:
                mName = "White";
                break;
            case Utils.BLACK:
                mName = "Black";
        }
    }
}
