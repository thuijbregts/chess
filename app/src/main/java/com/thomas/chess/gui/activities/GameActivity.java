package com.thomas.chess.gui.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

import java.util.ArrayList;

public class GameActivity extends FragmentActivity {

    private LinearLayout mGameLayout;
    private Fragment mFragment;

    private Game mGame;
    private int mGameType;

    private Engine mEngine;
    private BackgroundTask mBackgroundTask;

    private SquareView[][] mSquareViews;
    private SquareView mSelectedSquareView;

    private ArrayList<ImageView> mBlackDeadPieces;
    private ArrayList<ImageView> mWhiteDeadPieces;

    private MoveArray mMoves;

    private String mMoveLine = "";

    private boolean animating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        Bundle extras = getIntent().getExtras();
        int gameType = extras.getInt(Utils.INTENT_GAME_TYPE);

        initializeViews(gameType);
        initializeGame(gameType);
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
    }

    private void initializeGame(int gameType) {
        mGameType = gameType;
        mGame = new Game();
        switch (gameType) {
            case Utils.GAME_SOLO:
                mEngine = new Engine(mGame);
                break;
        }
        setUpPlayerContainers();
        setUpBoardViews();

        play();
    }

    private void setUpPlayerContainers() {
        TextView blackName = (TextView) findViewById(R.id.player_pieces_header_black);
        TextView whiteName = (TextView) findViewById(R.id.player_pieces_header_white);

        whiteName.setText("White");
        blackName.setText("Black");

        LinearLayout blackContainer = (LinearLayout) findViewById(R.id.player_pieces_container_black);
        LinearLayout whiteContainer = (LinearLayout) findViewById(R.id.player_pieces_container_white);

        mBlackDeadPieces = new ArrayList<>();
        mWhiteDeadPieces = new ArrayList<>();
        for (int i = 0; i < blackContainer.getChildCount(); i++) {
            mBlackDeadPieces.add((ImageView) blackContainer.getChildAt(i));
            mWhiteDeadPieces.add((ImageView) whiteContainer.getChildAt(i));
        }
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

    public void updateMoves(int count) {
        mMoveLine = mMoveLine.substring(0, mMoveLine.length() - (count * 4));
        mEngine.useBook();
    }

    public void updateGameView() {
        updatePlayerContainers();
        updateFragmentButtons();

        for (SquareView[] line : mSquareViews) {
            for (SquareView squareView : line) {
                squareView.update();
            }
        }

        if (mGame.getMoveCount() > 0) {
            Move lastMove = mGame.getLastMove();
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

    private void updatePlayerContainers() {
        int[] whitePieces = mGame.getPieces()[0];

        int[] blackPieces = mGame.getPieces()[1];
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
