package com.thomas.chess.gui.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.thomas.chess.R;
import com.thomas.chess.gui.activities.GameActivity;

import java.util.ArrayList;
import java.util.List;
import com.thomas.chess.engine.chess120.*;
import com.thomas.chess.gui.utils.Utils;

public class ScoreSheetDialog extends Dialog {

    private GameActivity mGameActivity;
    private List<Turn> mTurns;

    public ScoreSheetDialog(GameActivity gameActivity) {
        super(gameActivity);
        mGameActivity = gameActivity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.score_sheet_dialog);
        setCancelable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView listView = (ListView) findViewById(R.id.game_score_sheet_list);
        MyListAdapter listAdapter = new MyListAdapter(mGameActivity, R.layout.history_list_item, mTurns);
        listView.setAdapter(listAdapter);
    }

    public void setMoves(Move[] moves, int moveCount) {
        mTurns = new ArrayList<>();
        int turnsCount = (int) Math.ceil(moveCount / 2.f);
        Turn turn;
        for (int i = 0; i < turnsCount; i++) {
            if (moveCount < i*2 + 2) {
                turn = new Turn(i+1, moves[1+i*2], null);
            } else {
                turn = new Turn(i+1, moves[1+i*2], moves[1+i*2 + 1]);
            }
            mTurns.add(turn);
        }
    }

    private class MyListAdapter extends ArrayAdapter {

        private List<Turn> mTurns;

        public MyListAdapter(Context context, int resource, List<Turn> turns) {
            super(context, resource, turns);
            mTurns = turns;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                LayoutInflater vi = LayoutInflater.from(getContext());
                view = vi.inflate(R.layout.history_list_item, null);
            }

            Turn turn = mTurns.get(position);

            TextView moveNumber = (TextView) view.findViewById(R.id.history_move_number);
            moveNumber.setText("" + turn.getTurnNumber());

            TextView moveWhite = (TextView) view.findViewById(R.id.history_move_white);
            moveWhite.setText("" + Utils.getMoveAsString(turn.getWhiteMove(), mGameActivity.getGame()));

            TextView moveBlack = (TextView) view.findViewById(R.id.history_move_black);
            if (turn.getBlackMove() == null) {
                moveBlack.setText("");
            } else {
                moveBlack.setText("" + Utils.getMoveAsString(turn.getBlackMove(), mGameActivity.getGame()));
            }

            return view;
        }
    }

    private class Turn {

        private int mTurnNumber;
        private Move mWhiteMove;
        private Move mBlackMove;

        public Turn (int turnNumber, Move whiteMove, Move blackMove) {
            mTurnNumber = turnNumber;
            mWhiteMove = whiteMove;
            mBlackMove = blackMove;
        }

        public int getTurnNumber() {
            return mTurnNumber;
        }

        public Move getWhiteMove() {
            return mWhiteMove;
        }

        public Move getBlackMove() {
            return mBlackMove;
        }
    }
}
