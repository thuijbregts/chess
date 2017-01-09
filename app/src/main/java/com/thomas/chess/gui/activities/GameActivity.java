package com.thomas.chess.gui.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thomas.chess.R;
import com.thomas.chess.engine.movegen.MoveGenerator;
import com.thomas.chess.gui.fragments.OnlineFragment;
import com.thomas.chess.gui.fragments.SoloFragment;
import com.thomas.chess.gui.fragments.VersusFragment;
import com.thomas.chess.gui.utils.Utils;
import com.thomas.chess.gui.views.GameOverDialog;
import com.thomas.chess.gui.views.PromotionDialog;
import com.thomas.chess.gui.views.SquareView;
import com.thomas.chess.engine.chess120.*;
import com.thomas.chess.engine.search.*;

public class GameActivity extends FragmentActivity {

    private LinearLayout mGameLayout;
    private Fragment mFragment;

    private Game mGame;
    private int mGameType;

    private Engine mEngine;
    private BackgroundTask mBackgroundTask;

    private SquareView[][] mSquareViews;
    private SquareView mSelectedSquareView;

    private TextView mBlackPawns;
    private TextView mBlackKnights;
    private TextView mBlackBishops;
    private TextView mBlackRooks;
    private TextView mBlackQueens;
    private TextView mBlackKing;
    private LinearLayout mBlackKingContainer;

    private TextView mWhitePawns;
    private TextView mWhiteKnights;
    private TextView mWhiteBishops;
    private TextView mWhiteRooks;
    private TextView mWhiteQueens;
    private TextView mWhiteKing;
    private LinearLayout mWhiteKingContainer;

    private MoveArray mMoves;

    private String mMoveLine = "";

    private boolean animating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        Bundle extras = getIntent().getExtras();
        int gameType = extras.getInt(Utils.INTENT_GAME_TYPE);

        initializeGame(gameType);
        initializeViews(gameType);

        play();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initializeViews(int gameType) {
        mGameLayout = (LinearLayout) findViewById(R.id.game_layout);

        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (gameType) {
            case Utils.GAME_SOLO:
                mFragment = new SoloFragment();
                break;
//            case Utils.GAME_REVIEW:
//                mFragment = new HistoryFragment();
//                break;
            case Utils.GAME_VERSUS:
                mFragment = new VersusFragment();
                break;
            default:
                mFragment = new OnlineFragment();
                break;
        }
        fragmentManager.beginTransaction().replace(R.id.frame_content, mFragment).commit();

        setUpPlayerContainers();
        setUpBoardViews();
    }

    private void initializeGame(int gameType) {
        mGameType = gameType;
        mGame = new Game();
        switch (gameType) {
            case Utils.GAME_SOLO:
                mEngine = new Engine(mGame);
                break;
        }
    }

    private void setUpPlayerContainers() {
        TextView blackName = (TextView) findViewById(R.id.pieces_header_black);
        TextView whiteName = (TextView) findViewById(R.id.player_pieces_header_white);

        whiteName.setText("White");
        blackName.setText("Black");

        mBlackPawns = (TextView) findViewById(R.id.pieces_black_pawn);
        mBlackKnights = (TextView) findViewById(R.id.pieces_black_knight);
        mBlackBishops = (TextView) findViewById(R.id.pieces_black_bishop);
        mBlackRooks = (TextView) findViewById(R.id.pieces_black_rook);
        mBlackQueens = (TextView) findViewById(R.id.pieces_black_queen);
        mBlackKing = (TextView) findViewById(R.id.pieces_black_king);
        mBlackKingContainer = (LinearLayout) findViewById(R.id.black_king_container);

        mWhitePawns = (TextView) findViewById(R.id.pieces_white_pawn);
        mWhiteKnights = (TextView) findViewById(R.id.pieces_white_knight);
        mWhiteBishops = (TextView) findViewById(R.id.pieces_white_bishop);
        mWhiteRooks = (TextView) findViewById(R.id.pieces_white_rook);
        mWhiteQueens = (TextView) findViewById(R.id.pieces_white_queen);
        mWhiteKing = (TextView) findViewById(R.id.pieces_white_king);
        mWhiteKingContainer = (LinearLayout) findViewById(R.id.white_king_container);

        updatePlayerContainers(mGame.getLastMove());
    }

    private void setUpBoardViews() {
        mSquareViews = new SquareView[8][8];

        LinearLayout rowLayout;
        SquareView squareView;
        for (int i = Utils.ROWS-1; i >= 0; i--) {
            rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            layoutParams.weight = 1;
            rowLayout.setLayoutParams(layoutParams);
            for (int j = 0; j < Utils.COLUMNS; j++) {
                squareView = new SquareView(this, i, j, mGameType);
                final LinearLayout.LayoutParams squareParams
                        = new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.MATCH_PARENT);
                squareParams.weight = 1;
                squareView.setLayoutParams(squareParams);

                squareView.update();
                mSquareViews[i][j] = squareView;

                rowLayout.addView(squareView);
            }
            mGameLayout.addView(rowLayout);
        }
    }

    public void play() {
        switch (mGameType) {
            case Utils.GAME_VERSUS:
                mMoves = MoveGenerator.generateMoves(mGame);
                if (mMoves.size() == 0) {
                    Move lastMove = mGame.getLastMove();
                    if (lastMove.isCheck()) {
                        lastMove.setCheckmate(true);
                    } else {
                        lastMove.setDraw(Move.Draw.STALEMATE);
                    }
                    showGameOverDialog();
                } else {
                    dispatchMoves();
                }
                break;
            case Utils.GAME_SOLO:
                if (mGame.getCurrentSide() == Game.WHITE) {
                    mMoves = MoveGenerator.generateMoves(mGame);
                    if (mMoves.size() == 0) {
                        Move lastMove = mGame.getLastMove();
                        if (lastMove.isCheck()) {
                            lastMove.setCheckmate(true);
                        } else {
                            lastMove.setDraw(Move.Draw.STALEMATE);
                        }
                        showGameOverDialog();
                    } else {
                        dispatchMoves();
                    }
                } else {
                    mBackgroundTask = new BackgroundTask();
                    mBackgroundTask.execute();
                }
                break;
        }
    }

    public void makeMove(int move) {
        clearSelection();
        mGame.makeMove(move);
        mMoveLine += com.thomas.chess.engine.chess120.Utils.getMoveAsStringSimple(move, mGame);
        Move mv = mGame.getLastMove();
        animateMove(mv);
    }

    public void unmakeMove() {
        mGame.unmakeMove();
        clearSelection();
        updateGameView();
        play();
    }

    public void updateMoves(int count) {
        mMoveLine = mMoveLine.substring(0, mMoveLine.length() - (count * 4));
        mEngine.useBook();
    }

    public void updateGameView() {
        Move lastMove = mGame.getLastMove();

        updatePlayerContainers(lastMove);
        updateFragmentButtons();

        for (SquareView[] line : mSquareViews) {
            for (SquareView squareView : line) {
                squareView.update();
            }
        }

        if (mGame.getMoveCount() > 0) {
            int src = lastMove.getSourceSquare();
            int srcRank = Definitions.RANKS[src];
            int srcFile = Definitions.FILES[src];
            int dest = lastMove.getDestinationSquare();
            int dstRank = Definitions.RANKS[dest];
            int dstFile = Definitions.FILES[dest];
            mSquareViews[srcRank][srcFile].setLastMove();
            mSquareViews[dstRank][dstFile].setLastMove();
        }
    }

    private void updatePlayerContainers(Move lastMove) {
        int[][] pieces = mGame.getPieces();
        int piece, type;
        int wp, wn, wb, wr, wq, bp, bn, bb, br, bq;
        wp = wn = wb = wr = wq = bp = bn = bb = br = bq = 0;
        for (int i = 0; i < 15; i++) {
            piece = pieces[Game.WHITE][i];
            if ((piece & Piece.DEAD_FLAG) != 0) {
                type = piece >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;
                switch (type) {
                    case Definitions.PAWN:
                        wp++;
                        break;
                    case Definitions.KNIGHT:
                        wn++;
                        break;
                    case Definitions.BISHOP:
                        wb++;
                        break;
                    case Definitions.ROOK:
                        wr++;
                        break;
                    case Definitions.QUEEN:
                        wq++;
                }
            }

            piece = pieces[Game.BLACK][i];
            if ((piece & Piece.DEAD_FLAG) != 0) {
                type = piece >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;
                switch (type) {
                    case Definitions.PAWN:
                        bp++;
                        break;
                    case Definitions.KNIGHT:
                        bn++;
                        break;
                    case Definitions.BISHOP:
                        bb++;
                        break;
                    case Definitions.ROOK:
                        br++;
                        break;
                    case Definitions.QUEEN:
                        bq++;
                }
            }
        }

        mWhitePawns.setText("" + wp);
        mWhiteKnights.setText("" + wn);
        mWhiteBishops.setText("" + wb);
        mWhiteRooks.setText("" + wr);
        mWhiteQueens.setText("" + wq);

        mBlackPawns.setText("" + bp);
        mBlackKnights.setText("" + bn);
        mBlackBishops.setText("" + bb);
        mBlackRooks.setText("" + br);
        mBlackQueens.setText("" + bq);

        int side = mGame.getCurrentSide();
        if (side == Game.WHITE) {
            mWhiteKingContainer.setVisibility(View.VISIBLE);
            mBlackKingContainer.setVisibility(View.INVISIBLE);

            if (lastMove.isCheckmate()) {
                mWhiteKing.setText("MATE");
            } else if (lastMove.isCheck()) {
                mWhiteKing.setText("CHECK");
            } else {
                mWhiteKing.setText("SAFE");
            }
        } else {
            mWhiteKingContainer.setVisibility(View.INVISIBLE);
            mBlackKingContainer.setVisibility(View.VISIBLE);

            if (lastMove.isCheckmate()) {
                mBlackKing.setText("MATE");
            } else if (lastMove.isCheck()) {
                mBlackKing.setText("CHECK");
            } else {
                mBlackKing.setText("SAFE");
            }
        }
    }

    private void updateFragmentButtons() {
        switch (mGameType) {
            case Utils.GAME_SOLO:
                ((SoloFragment)mFragment).updateButtons();
                break;
//            case Utils.GAME_REVIEW:
//                ((HistoryFragment)mFragment).updateButtons();
//                break;
            case Utils.GAME_VERSUS:
                ((VersusFragment)mFragment).updateButtons();
                break;
            default:
                ((OnlineFragment)mFragment).updateButtons();
                break;
        }
    }

    private void dispatchMoves() {
        int src, dst, srcRank, srcFile, dstRank, dstFile, move, type;
        int[] moves = mMoves.getMoves();
        int size = mMoves.size();
        SquareView srcSquare, dstSquare;

        for (int i = 0; i < size; i++) {
            move = moves[i];
            type = move >> Move.TYPE_SHIFT & Move.TYPE_MASK;
            src = move & Move.SQUARE_MASK;
            dst = move >> Move.TO_SHIFT & Move.SQUARE_MASK;
            srcRank = Definitions.RANKS[src];
            srcFile = Definitions.FILES[src];
            dstRank = Definitions.RANKS[dst];
            dstFile = Definitions.FILES[dst];
            srcSquare = mSquareViews[srcRank][srcFile];
            dstSquare = mSquareViews[dstRank][dstFile];
            if (type == Move.PROMOTION) {
                if ((move >> Move.PROMO_SHIFT & Move.PROMO_MASK) == Definitions.QUEEN) {
                    dstSquare.addMove(move);
                    srcSquare.addSquare(dstSquare);
                }
            } else {
                dstSquare.addMove(move);
                srcSquare.addSquare(dstSquare);
            }
        }

        int[] pieces = mGame.getPieces()[mGame.getCurrentSide()];
        int piece;
        int square;
        for (int i = 0; i < 16; i++) {
            piece = pieces[i];
            if ((piece & Piece.DEAD_FLAG) == 0) {
                square = piece >> Piece.SQUARE_SHIFT & Piece.SQUARE_MASK;
                mSquareViews[Definitions.RANKS[square]][Definitions.FILES[square]].setSourceSquare(true);
            }
        }
    }

    public void clearSelection() {
        if (mSelectedSquareView != null) {
            mSelectedSquareView.clearBackground();
            for (SquareView square : mSelectedSquareView.getDestinationSquares()) {
                square.clearBackground();
            }
            mSelectedSquareView = null;
        }
    }

    public Game getGame() {
        return mGame;
    }

    public boolean isAnimating() {
        return animating;
    }

    public SquareView getSelectedSquareView() {
        return mSelectedSquareView;
    }

    public void updateSelectedSquare(SquareView selectedSquareView) {
        if (mSelectedSquareView != null) {
            mSelectedSquareView.clearBackground();
            for (SquareView square : mSelectedSquareView.getDestinationSquares()) {
                square.clearBackground();
            }
        }

        mSelectedSquareView = selectedSquareView;
        mSelectedSquareView.setSelected();
        for (SquareView square : selectedSquareView.getDestinationSquares()) {
            square.setPossibleMove();
        }
    }

    public void showPromotionDialog(int move) {
        PromotionDialog dialog = new PromotionDialog(this, move);
        dialog.show();
    }

    public void showGameOverDialog() {
        GameOverDialog dialog = new GameOverDialog(this, getGame().getLastMove());
        dialog.show();
    }

    public void animateMove(Move move) {
        animating = true;
        switch (move.getType()) {
            case Move.CASTLING:
                animateCastling(move);
                break;
            default:
                animateRegular(move);
        }
    }

    private void animateCastling(Move move) {
        //KING ANIMATION
        int kingSrc = move.getSourceSquare();
        int srcRank = Definitions.RANKS[kingSrc];
        int srcFile = Definitions.FILES[kingSrc];
        int kingDst = move.getDestinationSquare();
        int dstRank = Definitions.RANKS[kingDst];
        int dstFile = Definitions.FILES[kingDst];
        SquareView sourceSquare = mSquareViews[srcRank][srcFile];
        SquareView destinationSquare = mSquareViews[dstRank][dstFile];

        Animation animation = Utils.getAnimation(sourceSquare, destinationSquare);

        sourceSquare.startAnimation(animation);

        //ROOK ANIMATION
        int rookSrc = move.getSourceRook();
        srcRank = Definitions.RANKS[rookSrc];
        srcFile = Definitions.FILES[rookSrc];
        int rookDst = move.getDestinationRook();
        dstRank = Definitions.RANKS[rookDst];
        dstFile = Definitions.FILES[rookDst];
        sourceSquare = mSquareViews[srcRank][srcFile];
        destinationSquare = mSquareViews[dstRank][dstFile];

        animation = Utils.getAnimation(sourceSquare, destinationSquare);
        animation.setAnimationListener(new SquareAnimationListener());

        sourceSquare.startAnimation(animation);
    }

    private void animateRegular(Move move) {
        int src = move.getSourceSquare();
        int srcRank = Definitions.RANKS[src];
        int srcFile = Definitions.FILES[src];
        int dst = move.getDestinationSquare();
        int dstRank = Definitions.RANKS[dst];
        int dstFile = Definitions.FILES[dst];
        SquareView sourceSquare = mSquareViews[srcRank][srcFile];
        SquareView destinationSquare = mSquareViews[dstRank][dstFile];

        Animation animation = Utils.getAnimation(sourceSquare, destinationSquare);
        animation.setAnimationListener(new SquareAnimationListener());
        sourceSquare.startAnimation(animation);
    }

    private class SquareAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            animating = false;
            updateGameView();

            if (!mGame.isGameOver()) {
                play();
            } else {
                showGameOverDialog();
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    private class BackgroundTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            return mEngine.findMove(mMoveLine);
        }

        @Override
        protected void onPostExecute(Integer move) {
            if (move == 0) {
                Move lastMove = mGame.getLastMove();
                if (lastMove.isCheck()) {
                    lastMove.setCheckmate(true);
                } else {
                    lastMove.setDraw(Move.Draw.STALEMATE);
                }
                showGameOverDialog();
            } else {
                makeMove(move);
            }
        }
    }
}
