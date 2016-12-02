package com.thomas.chess.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thomas.chess.R;
import com.thomas.chess.game.Game;
import com.thomas.chess.game.Move;
import com.thomas.chess.game.Square;
import com.thomas.chess.game.p_children.AIPlayer;
import com.thomas.chess.game.p_children.RealPlayer;
import com.thomas.chess.utils.Utils;
import com.thomas.chess.views.HistoryDialog;
import com.thomas.chess.views.PromotionDialog;
import com.thomas.chess.views.SquareView;
import com.thomas.chess.game.pieces.Piece;

import java.util.ArrayList;

public class GameActivity extends Activity {

    private LinearLayout mGameLayout;
    private Game mGame;
    private int mGameType;
    private SquareView[][] mSquareViews;
    private SquareView mSelectedSquareView;

    private TextView mGameStatus;

    private ArrayList<ImageView> mBlackDeadPieces;
    private ArrayList<ImageView> mWhiteDeadPieces;

    private ArrayList<SquareMoves> mAnalyzedSquares = new ArrayList<>();
    private ArrayList<Move> mPossibleMoves;

    private boolean moving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        Bundle extras = getIntent().getExtras();
        int gameType = extras.getInt(Utils.INTENT_GAME_TYPE);

        mGameLayout = (LinearLayout) findViewById(R.id.game_layout);
        mGameStatus = (TextView) findViewById(R.id.game_status);
        initializeGame(gameType);
    }

    private void initializeGame(int gameType) {
        mGameType = gameType;
        switch (gameType) {
            case Utils.GAME_SOLO:
                mGame = new Game(new RealPlayer(Utils.WHITE), new AIPlayer(Utils.BLACK), this);
                break;
            default:
                mGame = new Game(new RealPlayer(Utils.WHITE), new RealPlayer(Utils.BLACK), this);
        }
        setUpPlayerContainers();
        setUpBoardViews();
        setUpButtons();
    }

    private void setUpPlayerContainers() {
        TextView blackName = (TextView) findViewById(R.id.player_pieces_header_black);
        TextView whiteName = (TextView) findViewById(R.id.player_pieces_header_white);

        whiteName.setText(mGame.getWhitePlayer().getName());
        blackName.setText(mGame.getBlackPlayer().getName());

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

                squareView.updateImage();
                mSquareViews[i][j] = squareView;

                rowLayout.addView(squareView);
            }
            mGameLayout.addView(rowLayout);
        }
    }

    private void setUpButtons() {
        Button showHistory = (Button) findViewById(R.id.game_show_history);
        showHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryDialog dialog = new HistoryDialog(GameActivity.this);
                dialog.setMoves(mGame.getMoves(), mGame.getMoveCount());
                dialog.show();
            }
        });

        Button undo = (Button) findViewById(R.id.game_undo);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGame.cancelMove();
                clearSelection();
                updateGameView();
            }
        });

        Button redo = (Button) findViewById(R.id.game_redo);
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGame.getMoves().size() > mGame.getMoveCount()) {
                    executeMove(mGame.getMoves().get(mGame.getMoveCount()));
                }
            }
        });
    }

    public void executeMove(Move move) {
        clearSelection();

        moving = true;
        mGame.executeMove(move);
        animateMove(move);
    }

    public void updateGameView() {
        if (getGame().isCheckmate()) {
            mGameStatus.setText("Checkmate");
        } else if (getGame().isStalemate()) {
            mGameStatus.setText("Stalemate");
        } else if (getGame().isDraw()) {
            mGameStatus.setText("Draw");
        } else if (getGame().isCheck()) {
            mGameStatus.setText("Check");
        } else {
            mGameStatus.setText("");
        }
        updatePlayerContainers();
        mAnalyzedSquares.clear();

        for (SquareView[] line : mSquareViews) {
            for (SquareView squareView : line) {
                squareView.updateImage();
            }
        }
    }

    private void updatePlayerContainers() {
        ArrayList<Piece> whiteDeadPieces = mGame.getWhitePlayer().getDeadPieces();
        for (int i = 0; i < whiteDeadPieces.size(); i++) {
            Utils.setImageViewForPiece(mWhiteDeadPieces.get(i), whiteDeadPieces.get(i));
        }
        for (int i = whiteDeadPieces.size(); i < mWhiteDeadPieces.size(); i++) {
            mWhiteDeadPieces.get(i).setImageResource(android.R.color.transparent);
        }

        ArrayList<Piece> blackDeadPieces = mGame.getBlackPlayer().getDeadPieces();
        for (int i = 0; i < blackDeadPieces.size(); i++) {
            Utils.setImageViewForPiece(mBlackDeadPieces.get(i), blackDeadPieces.get(i));
        }
        for (int i = blackDeadPieces.size(); i < mBlackDeadPieces.size(); i++) {
            mBlackDeadPieces.get(i).setImageResource(android.R.color.transparent);
        }
    }

    public void clearSelection() {
        if (mSelectedSquareView != null) {
            mSelectedSquareView.clearBackground();
            if (mPossibleMoves != null) {
                for (Move move : mPossibleMoves) {
                    Square destination = move.getDestinationSquare();
                    mSquareViews[destination.getRow()][destination.getColumn()].clearBackground();
                }
            }
            mSelectedSquareView = null;
            mPossibleMoves = null;
        }
    }

    public void choosePromotionPiece(Move move) {
        PromotionDialog dialog = new PromotionDialog(this);
        dialog.setMove(move);
        dialog.show();
    }

    public Game getGame() {
        return mGame;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setPossibleMoves(ArrayList<Move> possibleMoves) {
        this.mPossibleMoves = possibleMoves;
    }

    public void addPossibleMoves(Square square, ArrayList<Move> possibleMoves) {
        mPossibleMoves = possibleMoves;
        mAnalyzedSquares.add(new SquareMoves(square, possibleMoves));
    }

    public ArrayList<Move> getMovesForSquare(Square square) {
        for (SquareMoves squareMoves:mAnalyzedSquares) {
            if (square.equals(squareMoves.getSquare())) {
                return squareMoves.getPossibleMoves();
            }
        }
        return null;
    }

    public SquareView getSelectedSquareView() {
        return mSelectedSquareView;
    }

    public void updateSelectedSquare(SquareView selectedSquareView) {
        mSelectedSquareView = selectedSquareView;
        mSelectedSquareView.setSelected();
        if (mPossibleMoves != null) {
            for (Move move : mPossibleMoves) {
                Square destination = move.getDestinationSquare();
                mSquareViews[destination.getRow()][destination.getColumn()].setPossibleMove();
            }
        }
    }

    public Move getMoveForSquare(Square square) {
        if (mPossibleMoves == null) {
            return null;
        }
        for (Move move : mPossibleMoves) {
            if (move.getDestinationSquare().equals(square)) {
                return move;
            }
        }
        return null;
    }

    public void animateMove(Move move) {
        switch (move.getMoveType()) {
            case Utils.MOVE_TYPE_CASTLING:
                animateCastling(move);
                break;
            default:
                animateRegular(move);
        }
    }

    private void animateCastling(Move move) {
        SquareView sourceSquare = mSquareViews[move.getSourceSquare().getRow()]
                [move.getSourceSquare().getColumn()];
        SquareView destinationSquare = mSquareViews[move.getCastlingKing().getRow()]
                [move.getCastlingKing().getColumn()];
        int[] coord = new int[2];
        sourceSquare.getLocationOnScreen(coord);
        int sourceX = coord[0];
        int sourceY = coord[1];
        destinationSquare.getLocationOnScreen(coord);
        int destinationX = coord[0];
        int destinationY = coord[1];
        TranslateAnimation animation = new TranslateAnimation(Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, destinationX-sourceX,
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, destinationY-sourceY);
        animation.setDuration(Utils.PIECE_ANIMATION_DURATION);
        animation.setRepeatCount(0);
        animation.setZAdjustment(Animation.ZORDER_TOP);
        animation.setFillAfter(false);

        sourceSquare.startAnimation(animation);

        sourceSquare = mSquareViews[move.getDestinationSquare().getRow()]
                [move.getDestinationSquare().getColumn()];
        destinationSquare = mSquareViews[move.getCastlingRook().getRow()]
                [move.getCastlingRook().getColumn()];
        sourceSquare.getLocationOnScreen(coord);

        sourceX = coord[0];
        sourceY = coord[1];
        destinationSquare.getLocationOnScreen(coord);
        destinationX = coord[0];
        destinationY = coord[1];
        animation = new TranslateAnimation(Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, destinationX-sourceX,
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, destinationY-sourceY);
        animation.setDuration(Utils.PIECE_ANIMATION_DURATION);
        animation.setRepeatCount(0);
        animation.setZAdjustment(Animation.ZORDER_TOP);
        animation.setFillAfter(false);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moving = false;
                updateGameView();
                mGame.getCurrentPlayer().play();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        sourceSquare.startAnimation(animation);
    }

    private void animateRegular(Move move) {
        SquareView sourceSquare = mSquareViews[move.getSourceSquare().getRow()]
                [move.getSourceSquare().getColumn()];
        SquareView destinationSquare = mSquareViews[move.getDestinationSquare().getRow()]
                [move.getDestinationSquare().getColumn()];
        int[] coord = new int[2];
        sourceSquare.getLocationOnScreen(coord);
        int sourceX = coord[0];
        int sourceY = coord[1];
        destinationSquare.getLocationOnScreen(coord);
        int destinationX = coord[0];
        int destinationY = coord[1];
        TranslateAnimation animation = new TranslateAnimation(Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, destinationX-sourceX,
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, destinationY-sourceY);
        animation.setDuration(Utils.PIECE_ANIMATION_DURATION);
        animation.setRepeatCount(0);
        animation.setZAdjustment(Animation.ZORDER_TOP);
        animation.setFillAfter(false);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moving = false;
                updateGameView();
                mGame.getCurrentPlayer().play();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        sourceSquare.startAnimation(animation);
    }

    private class SquareMoves {

        Square mSquare;
        ArrayList<Move> mPossibleMoves;

        public SquareMoves(Square square, ArrayList<Move> possibleMoves) {
            mSquare = square;
            mPossibleMoves = possibleMoves;
        }

        public ArrayList<Move> getPossibleMoves() {
            return mPossibleMoves;
        }

        public Square getSquare() {
            return mSquare;
        }
    }
}
