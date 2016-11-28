package com.thomas.chess.overrides;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.thomas.chess.R;
import com.thomas.chess.activities.GameActivity;
import com.thomas.chess.game.Game;
import com.thomas.chess.pieces.Bishop;
import com.thomas.chess.pieces.Knight;
import com.thomas.chess.pieces.Piece;
import com.thomas.chess.pieces.Queen;
import com.thomas.chess.pieces.Rook;


public class PromotionDialog extends Dialog {

    private GameActivity mGameActivity;
    private Piece mPromotionPiece;

    public PromotionDialog(GameActivity gameActivity) {
        super(gameActivity);
        mGameActivity = gameActivity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.promotion_dialog);
        setCancelable(false);

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
    }

    public Piece getPromotionPiece() {
        return mPromotionPiece;
    }
}
