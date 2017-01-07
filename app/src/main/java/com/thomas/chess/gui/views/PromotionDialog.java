package com.thomas.chess.gui.views;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.thomas.chess.R;
import com.thomas.chess.gui.activities.GameActivity;
import com.thomas.chess.engine.chess120.*;


public class PromotionDialog extends Dialog {

    private GameActivity mGameActivity;

    private int mMove;

    public PromotionDialog(GameActivity gameActivity, int move) {
        super(gameActivity);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.promotion_dialog);
        setCancelable(false);

        mGameActivity = gameActivity;
        move = move & Move.NO_PROMO;
        mMove = move;

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mGameActivity.makeMove(mMove);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeComponents();
    }

    private void initializeComponents() {
        ImageView queen = (ImageView) findViewById(R.id.promotion_queen);
        queen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMove |= Definitions.QUEEN << Move.PROMO_SHIFT;
                dismiss();
            }
        });

        ImageView rook = (ImageView) findViewById(R.id.promotion_rook);
        rook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMove |= Definitions.ROOK << Move.PROMO_SHIFT;
                dismiss();
            }
        });

        ImageView bishop = (ImageView) findViewById(R.id.promotion_bishop);
        bishop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMove |= Definitions.BISHOP << Move.PROMO_SHIFT;
                dismiss();
            }
        });

        ImageView knight = (ImageView) findViewById(R.id.promotion_knight);
        knight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMove |= Definitions.KNIGHT << Move.PROMO_SHIFT;
                dismiss();
            }
        });

        switch (mGameActivity.getGame().getCurrentSide()) {
            case Game.WHITE:
                queen.setImageResource(R.drawable.queen_w);
                knight.setImageResource(R.drawable.knight_w);
                bishop.setImageResource(R.drawable.bishop_w);
                rook.setImageResource(R.drawable.rook_w);
                break;
            case Game.BLACK:
                queen.setImageResource(R.drawable.queen_b);
                knight.setImageResource(R.drawable.knight_b);
                bishop.setImageResource(R.drawable.bishop_b);
                rook.setImageResource(R.drawable.rook_b);
        }
    }
}
