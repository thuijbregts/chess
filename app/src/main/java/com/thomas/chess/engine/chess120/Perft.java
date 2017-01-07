package com.thomas.chess.engine.chess120;

import com.thomas.chess.engine.movegen.*;

public class Perft {

	public static final String pos2 = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -";
	public static final String pos3 = "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -";
	public static final String pos4 = "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1";
	public static final String pos5 = "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8";
	public static final String pos6 = "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10";
	public static final String pos7 = "rnb1k2r/pp2qppp/3p1n2/2pp2B1/1bP5/2N1P3/PP2NPPP/R2QKB1R w KQkq c5 0 1";
	public static final String pos8 = "r5rk/5p1p/5R2/4B3/8/8/7P/7K w - -";
	public static final String pos9 = "1br3k1/p4p2/2p1r3/3p1b2/3Bn1p1/1P2P1Pq/P3Q1BP/2R1NRK1 b - -";
	public static final String pos10 = "4Rnk1/pr3ppp/1p3q2/5NQ1/2p5/8/P4PPP/6K1 w - - 1 0";
	public static final String pos11 = "rnbq1k1r/pp1Pbppp/2p5/8/2B3n1/8/PPP1N1PP/RNBQK2R w KQ - 1 8";
	public static final String pos12 = "rnbq1bnr/pp1kpppp/8/2pP4/8/3B4/PPPP1PPP/RNBQK1NR w KQ c6 0 1";
	public static final String pos13 = "rq5r/pp2kppp/2N5/2b1n3/8/1bn5/1PP3PK/R1BQ1R2 b - - 1 8";
	
    public static long checks = 0;
    public static long ep = 0;
    public static long captures = 0;
    public static long castles = 0;
    public static long promotions = 0;
    public static String str = "";
    
    public static long perftSimple(Game game, int depth) {
    	long nodes = 0;
        int move;
        MoveArray m = MoveGenerator.generateMoves(game);
        int[] moves = m.getMoves();
        int size = m.size();
        if (depth == 1) {
        	return size;
        }
        for(int i = 0; i < size; i++)
        {
            move = moves[i];
            game.makeMove(move);
            nodes += perftSimple(game, depth - 1);
            game.unmakeMove();
        }
        return nodes;
    }
    
	public static long perft(Game game, int depth) {
        long nodes = 0;
        
        if(depth == 0) return 1;

        int move;
        MoveArray m = MoveGenerator.generateMoves(game);
        int[] moves = m.getMoves();
        int size = m.size();
        Move lastMove;
        
        for(int i = 0; i < size; i++)
        {
            move = moves[i];
            game.makeMove(move);
            if (depth == 1) {
            	lastMove = game.getLastMove();
                if (lastMove.isCheck()) {
                    checks++;
                }
                if (lastMove.isCapture()) {
                    captures++;
                }
                if (lastMove.getType() == Move.EN_PASSANT) {
                    ep++;
                }
                if (lastMove.getType() == Move.CASTLING) {
                    castles++;
                }
                if (lastMove.getType() == Move.PROMOTION) {
                    promotions++;
                }
            }
            nodes += perft(game, depth - 1);
            game.unmakeMove();
        }
        return nodes;
    }
	
	public static long perftDivide(Game game, int depth) {
        long nodes = 0;
        long current = 0;
        int move;
        MoveArray m = MoveGenerator.generateMoves(game);
        int[] moves = m.getMoves();
        int size = m.size();
        
        for(int i = 0; i < size; i++)
        {
            move = moves[i];
            game.makeMove(move);
            current = divideNodes(game, depth - 1);
            nodes += current;
            str += Utils.getMoveAsStringSimple(move, game) + " " + current + "\n";
            game.unmakeMove();
        }
        return nodes;
    }

    private static long divideNodes(Game game, int depth) {
        long nodes = 0;
        
        if (depth == 0) {
        	return 1;
        }
 
        MoveArray m = MoveGenerator.generateMoves(game);
        int[] moves = m.getMoves();
        int size = m.size();
        int move;
        if (depth == 1) {
        	return size;
        }
        for(int i = 0; i < size; i++)
        {
            move = moves[i];     
            game.makeMove(move);  
            nodes += divideNodes(game, depth - 1);
            game.unmakeMove();
        }
        return nodes;
    }
	
    public static long perftCapture(Game game, int depth) {
        long nodes = 0;
        
        if (depth == 1) {
        	captures += CaptureGenerator.generateMoves(game).size();
        	return 1;
        }

        int move;
        MoveArray m = MoveGenerator.generateMoves(game);
        int[] moves = m.getMoves();
        int size = m.size();

        for(int i = 0; i < size; i++)
        {
            move = moves[i];
            game.makeMove(move);
            nodes += perftCapture(game, depth - 1);
            game.unmakeMove();
        }
        return nodes;
    }
    
}
