package com.thomas.chess.views;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.thomas.chess.R;
import com.thomas.chess.activities.GameActivity;
import com.thomas.chess.game.Game;
import com.thomas.chess.game.Move;
import com.thomas.chess.game.pieces.Bishop;
import com.thomas.chess.game.pieces.Knight;
import com.thomas.chess.game.pieces.Piece;
import com.thomas.chess.game.pieces.Queen;
import com.thomas.chess.game.pieces.Rook;
import com.thomas.chess.utils.Utils;


public class PromotionDialog extends Dialog {

    private GameActivity mGameActivity;
    private Piece mPromotionPiece;
    private Move mMove;

    public PromotionDialog(GameActivity gameActivity, Move move) {
        super(gameActivity);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.promotion_dialog);
        setCancelable(false);

        mGameActivity = gameActivity;
        mMove = move;

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mMove.setPromotion(mPromotionPiece);
                mGameActivity.executeMove(mMove, true);
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
                Game game = mGameActivity.getGame();
                mPromotionPiece = new Queen(game.getCurrentPlayer().getColor(), null, game);
                dismiss();
            }
        });

        ImageView rook = (ImageView) findViewById(R.id.promotion_rook);
        rook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game game = mGameActivity.getGame();
                mPromotionPiece = new Rook(game.getCurrentPlayer().getColor(), null, game);
                dismiss();
            }
        });

        ImageView bishop = (ImageView) findViewById(R.id.promotion_bishop);
        bishop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game game = mGameActivity.getGame();
                mPromotionPiece = new Bishop(game.getCurrentPlayer().getColor(), null, game);
                dismiss();
            }
        });

        ImageView knight = (ImageView) findViewById(R.id.promotion_knight);
        knight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game game = mGameActivity.getGame();
                mPromotionPiece = new Knight(game.getCurrentPlayer().getColor(), null, game);
                dismiss();
            }
        });

        switch (mGameActivity.getGame().getCurrentPlayer().getColor()) {
            case Utils.WHITE:
                queen.setImageResource(R.drawable.queen_w);
                knight.setImageResource(R.drawable.knight_w);
                bishop.setImageResource(R.drawable.bishop_w);
                rook.setImageResource(R.drawable.rook_w);
                break;
            case Utils.BLACK:
                queen.setImageResource(R.drawable.queen_b);
                knight.setImageResource(R.drawable.knight_b);
                bishop.setImageResource(R.drawable.bishop_b);
                rook.setImageResource(R.drawable.rook_b);
        }
    }
}
