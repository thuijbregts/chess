package com.thomas.chess.overrides;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.thomas.chess.R;
import com.thomas.chess.activities.GameActivity;
import com.thomas.chess.game.Game;
import com.thomas.chess.game.Move;
import com.thomas.chess.pieces.Bishop;
import com.thomas.chess.pieces.Knight;
import com.thomas.chess.pieces.Piece;
import com.thomas.chess.pieces.Queen;
import com.thomas.chess.pieces.Rook;

import java.util.List;


public class HistoryDialog extends Dialog {

    private GameActivity mGameActivity;
    private ListView mListView;
    private MyListAdapter myListAdapter;
    private List<Move> mMoves;

    public HistoryDialog(GameActivity gameActivity) {
        super(gameActivity);
        mGameActivity = gameActivity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.history_dialog);
        setCancelable(true);

        mListView = (ListView) findViewById(R.id.game_history_list);
        myListAdapter = new MyListAdapter(mGameActivity, R.layout.history_list_item, mMoves);
        mListView.setAdapter(myListAdapter);
    }

    public void setMoves(List<Move> moves) {
        this.mMoves = moves;
    }

    private class MyListAdapter extends ArrayAdapter {

        private List<Move> mMoves;

        public MyListAdapter(Context context, int resource, List<Move> globalTests) {
            super(context, resource, globalTests);
            mMoves = globalTests;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                LayoutInflater vi = LayoutInflater.from(getContext());
                view = vi.inflate(R.layout.history_list_item, null);
            }

            Move move = mMoves.get(position);

            TextView moveNumber = (TextView) view.findViewById(R.id.history_move_number);
            moveNumber.setText("" + (int)Math.ceil((position + 1) / 2));

            TextView moveWhite = (TextView) view.findViewById(R.id.history_move_white);
            moveWhite.setText("" + position);

            TextView moveBlack = (TextView) view.findViewById(R.id.history_move_black);
            moveBlack.setText("" + position);

            return view;
        }
    }
}
