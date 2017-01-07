package com.thomas.chess.engine.chess120;

import com.thomas.chess.engine.movegen.*;

public class Utils {

    public static String getMoveAsStringSimple(int move, Game game) {
        String result = "";
        int from = move & Move.SQUARE_MASK;
        int to = move >> Move.TO_SHIFT & Move.SQUARE_MASK;
        result += Utils.getColumnCode(Definitions.FILES[from]);
        result += Definitions.RANKS[from] + 1;
        result += Utils.getColumnCode(Definitions.FILES[to]);
        result += Definitions.RANKS[to] + 1;
        
        if ((move >> Move.TYPE_SHIFT & Move.TYPE_MASK) == Move.PROMOTION) {
        	int type = move >> Move.PROMO_SHIFT & Move.PROMO_MASK;
    		switch (type) {
	    		case Definitions.BISHOP:
	    			result += "B";
	    			break;
	    		case Definitions.KNIGHT:
	    			result += "N";
	    			break;
	    		case Definitions.ROOK:
	    			result += "R";
	    			break;
    			default:
    				result += "Q";
    		}
        }
        return result;
    }
    
    public static char getColumnCode(int column) {
        switch (column) {
            case 0:
                return 'a';
            case 1:
                return 'b';
            case 2:
                return 'c';
            case 3:
                return 'd';
            case 4:
                return 'e';
            case 5:
                return 'f';
            case 6:
                return 'g';
            case 7:
                return 'h';
            default:
                return 'x';
        }
    }

    public static int getColumnInt(char column) {
        switch (column) {
            case 'a':
                return 0;
            case 'b':
                return 1;
            case 'c':
                return 2;
            case 'd':
                return 3;
            case 'e':
                return 4;
            case 'f':
                return 5;
            case 'g':
                return 6;
            case 'h':
                return 7;
            default:
                return 0;
        }
    }

    public static void setFEN(Game game, String fen) {
   
    		game.initializeGame();
    		
    		Move lastMove = game.getLastMove();
    		
	    	String[] split = fen.split(" ");
	    	
	    	String[] newBoard = split[0].split("/");
	    	String rank;
	    	char ch;
	    	int[] board = game.getBoard();

	    	//blb = black light bishop, wdb = white dark bishop
	    	
	    	int wp, wb, wr, wn, wq, bp, bb, br, bn, bq, blb, bdb, wlb, wdb;
	    	int[] wPieces = game.getPieces()[0];
	    	int[] bPieces = game.getPieces()[1];
	    	int[] wRooks = new int[10];
	    	int[] bRooks = new int[10];
	    	int piece = 0;
	    	int square;
	    	int index = 0;
	    	int bPromos = 0;
	    	int wPromos = 0;
	    	wp = wb = wr = wn = wq = bp = bb = br = bn = bq = blb = bdb = wlb = wdb = 0;
	    	for (int i = 0; i < 8; i++) {
	    		rank = newBoard[7-i];
	    		square = 21 + 10 * i;
	    		for (int j = 0; j < rank.length(); j++) {
	    			ch = rank.charAt(j);
	    			if (Character.isDigit(ch)) {
	    				square += Character.getNumericValue(ch);
	    				continue;
	    			}
	    			switch (ch) {
		    			case 'p':
		    				index = bp;
		    				piece = Piece.toInt(index, square, Game.BLACK, Definitions.PAWN);
		    				bp++;
		    				break;
		    			case 'b':
		    				if ((Definitions.RANKS[square] + Definitions.FILES[square]) % 2 == 0) {
		    					bdb++;
		    				} else {
		    					blb++;
		    				}
		    				if (bb == 2) {
		    					index = 7 - bPromos;
		    					piece = Piece.toInt(index, square, Game.BLACK, Definitions.BISHOP);
		    					bPromos++;
		    				} else {
			    				index = 10 + bb;
			    				piece = Piece.toInt(index, square, Game.BLACK, Definitions.BISHOP);
		    				}
		    				bb++;
		    				break;
		    			case 'r':
		    				bRooks[br] = square;
		    				br++;
		    				square++;
		    				continue;
		    			case 'k':
		    				index = 15;
		    				piece = Piece.toInt(index, square, Game.BLACK, Definitions.KING);
		    				break;
		    			case 'n':
		    				if (bn == 2) {
		    					index = 7 - bPromos;
		    					piece = Piece.toInt(index, square, Game.BLACK, Definitions.KNIGHT);
		    					bPromos++;
		    				} else {
			    				index = 8 + bn;
			    				piece = Piece.toInt(index, square, Game.BLACK, Definitions.KNIGHT);
		    				}
		    				bn++;
		    				break;
		    			case 'q':
		    				if (bq == 1) {
		    					index = 7 - bPromos;
		    					piece = Piece.toInt(index, square, Game.BLACK, Definitions.QUEEN);
		    					bPromos++;
		    				} else {
			    				index = 14;
			    				piece = Piece.toInt(index, square, Game.BLACK, Definitions.QUEEN);
		    				}
		    				bq++;
		    				break;
		    			case 'P':
		    				index = wp;
		    				piece = Piece.toInt(index, square, Game.WHITE, Definitions.PAWN);
		    				wp++;
		    				break;
		    			case 'B':
		    				if ((Definitions.RANKS[square] + Definitions.FILES[square]) % 2 == 0) {
		    					wdb++;
		    				} else {
		    					wlb++;
		    				}
		    				if (wb == 2) {
		    					index = 7 - wPromos;
		    					piece = Piece.toInt(index, square, Game.WHITE, Definitions.BISHOP);
		    					wPromos++;
		    				} else {
			    				index = 10 + wb;
			    				piece = Piece.toInt(index, square, Game.WHITE, Definitions.BISHOP);
		    				}
		    				wb++;
		    				break;
		    			case 'R':
		    				wRooks[wr] = square;
		    				wr++;
		    				square++;
		    				continue;
		    			case 'K':
		    				index = 15;
		    				piece = Piece.toInt(index, square, Game.WHITE, Definitions.KING);
		    				break;
		    			case 'N':
		    				if (wn == 2) {
		    					index = 7 - wPromos;
		    					piece = Piece.toInt(index, square, Game.WHITE, Definitions.KNIGHT);
		    					wPromos++;
		    				} else {
			    				index = 8 + wn;
			    				piece = Piece.toInt(index, square, Game.WHITE, Definitions.KNIGHT);
		    				}
		    				wn++;
		    				break;
		    			case 'Q':
		    				if (wq == 1) {
		    					index = 7 - wPromos;
		    					piece = Piece.toInt(index, square, Game.WHITE, Definitions.QUEEN);
		    					wPromos++;
		    				} else {
			    				index = 14;
			    				piece = Piece.toInt(index, square, Game.WHITE, Definitions.QUEEN);
		    				}
		    				wq++;
	    			}
	    			if ((piece & Piece.SIDE_ONLY) == 0) {
	    				wPieces[index] = piece;
	    			} else {
	    				bPieces[index] = piece;
	    			}
	    			
	    			board[square] = piece;
	    			square++;
	    		}
	    	}
	    	for (int i = wp; i < 8-wPromos; i++) {
	    		wPieces[i] = Piece.toInt(i, 31+i, Game.WHITE, Definitions.PAWN) | Piece.DEAD_FLAG;
	    	}
	    	for (int i = bp; i < 8-bPromos; i++) {
	    		bPieces[i] = Piece.toInt(i, 81+i, Game.BLACK, Definitions.PAWN) | Piece.DEAD_FLAG;
	    	}
	    	for (int i = wn; i < 2; i++) {
	    		wPieces[8 + i] = Piece.toInt(8 + i, 22+(i*5), Game.WHITE, Definitions.KNIGHT) | Piece.DEAD_FLAG;
	    	}
	    	for (int i = bn; i < 2; i++) {
	    		bPieces[8 + i] = Piece.toInt(8 + i, 92+(i*5), Game.BLACK, Definitions.KNIGHT) | Piece.DEAD_FLAG;
	    	}
	    	for (int i = wb; i < 2; i++) {
	    		wPieces[10 + i] = Piece.toInt(10 + i, 23+(i*3), Game.WHITE, Definitions.BISHOP) | Piece.DEAD_FLAG;
	    	}
	    	for (int i = bb; i < 2; i++) {
	    		bPieces[10 + i] = Piece.toInt(10 + i, 93+(i*3), Game.BLACK, Definitions.BISHOP) | Piece.DEAD_FLAG;
	    	}
	    	if (wq == 0) {
	    		wPieces[14] = Piece.toInt(14, 24, Game.WHITE, Definitions.QUEEN) | Piece.DEAD_FLAG;
	    	}
	    	if (bq == 0) {
	    		bPieces[14] = Piece.toInt(14, 24, Game.BLACK, Definitions.QUEEN) | Piece.DEAD_FLAG;
	    	}
	    	
	    	int temp;
	    	for (int i = 0; i < wr; i++) {
	    		if (wRooks[i] == 21) {
	    			temp = wRooks[i];
	    			wRooks[i] = wRooks[0];
	    			wRooks[0] = temp;
	    		} else if (wRooks[i] == 28) {
	    			temp = wRooks[i];
	    			wRooks[i] = wRooks[1];
	    			wRooks[1] = temp;
	    		}
	    	}
	    
	    	if (wr <= 2) {
	    		if (wRooks[0] == 0) {
	    	    	wPieces[12] = Piece.toInt(12, 21, Game.WHITE, Definitions.ROOK) | Piece.DEAD_FLAG;	    	    	
	    		} else {
	    			wPieces[12] = Piece.toInt(12, wRooks[0], Game.WHITE, Definitions.ROOK);
	    			board[wRooks[0]] = wPieces[12];
	    		}
	    		if (wRooks[1] == 0) {
	    			wPieces[13] = Piece.toInt(13, 28, Game.WHITE, Definitions.ROOK) | Piece.DEAD_FLAG;
	    		} else {
	    			wPieces[13] = Piece.toInt(13, wRooks[1], Game.WHITE, Definitions.ROOK);
	    			board[wRooks[1]] = wPieces[13];
	    		}
	    	} else {
	    		for (int i = 0; i < 2; i++) {
	    			wPieces[12 + i] = Piece.toInt(12 + i, wRooks[i], Game.WHITE, Definitions.ROOK);
	    			board[wRooks[i]] = wPieces[12 + i];
	    		}
	    		for (int i = 2; i < wr; i++) {
	    			index = 7 - wPromos;
	    			wPieces[index] = Piece.toInt(index, wRooks[i], Game.WHITE, Definitions.ROOK);
	    			board[wRooks[i]] = wPieces[index];
	    			wPromos++;
	    		}
	    	}
	    	
	    	for (int i = 0; i < br; i++) {
	    		if (bRooks[i] == 91) {
	    			temp = bRooks[i];
	    			bRooks[i] = bRooks[0];
	    			bRooks[0] = temp;
	    		} else if (bRooks[i] == 98) {
	    			temp = bRooks[i];
	    			bRooks[i] = bRooks[1];
	    			bRooks[1] = temp;
	    		}
	    	}
	    	
	    	if (br <= 2) {
	    		if (bRooks[0] == 0) {
	    	    	bPieces[12] = Piece.toInt(12, 91, Game.BLACK, Definitions.ROOK) | Piece.DEAD_FLAG;	    	    	
	    		} else {
	    			bPieces[12] = Piece.toInt(12, bRooks[0], Game.BLACK, Definitions.ROOK);
	    			board[bRooks[0]] = bPieces[12];
	    		}
	    		if (bRooks[1] == 0) {
	    			bPieces[13] = Piece.toInt(13, 98, Game.BLACK, Definitions.ROOK) | Piece.DEAD_FLAG;
	    		} else {
	    			bPieces[13] = Piece.toInt(13, bRooks[1], Game.BLACK, Definitions.ROOK);
	    			board[bRooks[1]] = bPieces[13];
	    		}
	    	} else {
	    		for (int i = 0; i < 2; i++) {
	    			bPieces[12 + i] = Piece.toInt(12 + i, bRooks[i], Game.BLACK, Definitions.ROOK);
	    			board[bRooks[i]] = bPieces[12 + i];
	    		}
	    		for (int i = 2; i < br; i++) {
	    			index = 7 - bPromos;
	    			bPieces[index] = Piece.toInt(index, bRooks[i], Game.BLACK, Definitions.ROOK);
	    			board[bRooks[i]] = bPieces[index];
	    			bPromos++;
	    		}
	    	}
	    	
	    	int[][] piecesCount = game.getPiecesCount();
	    	int[] whiteCount = piecesCount[Game.WHITE];
	    	whiteCount[Definitions.PAWN] = wp;
	    	whiteCount[Definitions.KNIGHT] = wn;
	    	whiteCount[Definitions.DARK_BISHOP] = wdb;
	    	whiteCount[Definitions.LIGHT_BISHOP] = wlb;
	    	whiteCount[Definitions.ROOK] = wr;
	    	whiteCount[Definitions.QUEEN] = wq;
	    	int[] blackCount = piecesCount[Game.BLACK];
	    	blackCount[Definitions.PAWN] = bp;
	    	blackCount[Definitions.KNIGHT] = bn;
	    	blackCount[Definitions.DARK_BISHOP] = bdb;
	    	blackCount[Definitions.LIGHT_BISHOP] = blb;
	    	blackCount[Definitions.ROOK] = br;
	    	blackCount[Definitions.QUEEN] = bq;
	    	
	    	String side = split[1];
	    	game.setSide(side.equals("w") ? Game.WHITE : Game.BLACK);
	    	
	    	
	    	String castling = split[2];
	    	int perm = 0;
	    	for (int i = 0; i < castling.length(); i++) {
	    		if (castling.charAt(i) == '-') {
	    			continue;
	    		}
	    		switch (castling.charAt(i)) {
		    		case 'K':
		    			perm |= Definitions.CASTLING_K_WHITE; 
		    			break;
		    		case 'Q':
		    			perm |= Definitions.CASTLING_Q_WHITE; 
		    			break;
		    		case 'k':
		    			perm |= Definitions.CASTLING_K_BLACK; 
		    			break;
		    		case 'q':
		    			perm |= Definitions.CASTLING_Q_BLACK; 
	    		}
	    	}
	    	
	    	lastMove.setCastlingPermissions(perm);
	
	    	String enPassant = split[3];
	    	int ep = 0;
	    	if (enPassant.length() > 1) {
	    		int f = getColumnInt(enPassant.charAt(0));
	    		int r = Character.getNumericValue(enPassant.charAt(1)) - 1;
	    		
	    		ep = (21 + f) + (r * 10);
	    	}
	    	lastMove.setEnPassant(ep);
	    	
	    	if (split.length == 6) {
	    		String fiftyMove = split[4];
	    		lastMove.setHalfClockMoves(Integer.parseInt(fiftyMove));
	    		
	    		String turns = split[5];
	    	}
	    	
	    	//verify check
	    	
	    	int[] pieces = game.getPieces()[1-game.getCurrentSide()];
	    	int king = game.getFriendlyKingSquare(game.getCurrentSide());
	    	int i, ePiece;
	    	for (i = 0; i < 15; i++) {
	        	ePiece = pieces[i];
	            if ((ePiece & Piece.DEAD_FLAG) == 0) {
	                if (SquareAttack.attacksSquare(ePiece, king, game)) {
	                    game.getLastMove().setCheckPiece(ePiece);
	                }
	            }
	        }
	    	
	    	game.generatePositionHash();
    }
    
    public static String getFEN(Game game) {
    	String fen = "";
    	int[] board = game.getBoard();
    	int empty = 0;
    	int square = 0;
    	for (int i = 7; i >= 0; i--) {
    		for (int j = 0; j < 8; j++) {
    			square = (21 + j) + (10 * i);

    			if (board[square] == Definitions.SQ_EMPTY) {
    				empty++;
    				continue;
    			}
    			if (empty != 0) {
    				fen += empty;
    				empty = 0;
    			}
    			fen += Utils.getPieceFenCode(board[square]);
    		}
			if (empty != 0) {
    			fen += empty;
    		}
			if (i > 0) {
				fen += "/";
				empty = 0;
			}
    	}
    	
    	fen += " ";
    	fen += game.getCurrentSide() == Game.WHITE ? 'w' : 'b';
    	fen += " ";
    	
    	int castlingPerm = game.getCastlingPermissions();
    	if (castlingPerm == 0) {
    		fen += "-";
    	} else {
    		fen += (castlingPerm & Definitions.CASTLING_K_WHITE) != 0 ? 'K' : "";
    		fen += (castlingPerm & Definitions.CASTLING_Q_WHITE) != 0 ? 'Q' : "";
    		fen += (castlingPerm & Definitions.CASTLING_K_BLACK) != 0 ? 'k' : "";
    		fen += (castlingPerm & Definitions.CASTLING_Q_BLACK) != 0 ? 'q' : "";
    	}

    	fen += " ";
    	
    	int ep = game.getEnPassant();
    	if (ep == 0) {
    		fen += "-";
    	} else {
    		fen += getColumnCode(Definitions.FILES[ep]);
    		fen += (Definitions.RANKS[ep] + 1);
    	}
//    	
//    	fen += " ";
//    	fen += game.getHalfClockMoves();
//    	fen += " ";
//    	fen += (int) (1 + Math.floor(game.getMoveCount()/2f));
    	return fen;
    }
    
    private static String getPieceFenCode(int piece) {
    	String code = "";
    	int type = piece >> Piece.TYPE_SHIFT & Piece.TYPE_MASK;
	    int side = piece & Piece.SIDE_ONLY;
	    
    	switch (type) {
	    	case Definitions.PAWN:
	    		code = "P";
	    		break;
	    	case Definitions.KNIGHT:
	    		code = "N";
	    		break;
	    	case Definitions.BISHOP:
	    		code = "B";
	    		break;
	    	case Definitions.ROOK:
	    		code = "R";
	    		break;
	    	case Definitions.QUEEN:
	    		code = "Q";
	    		break;
    		default:
    			code = "K";
    	}
        return side == Game.WHITE ? code : code.toLowerCase();
    }
}
