package Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


import Model.Bishop;
import Model.Board;
import Model.King;
import Model.Knight;
import Model.Pawn;
import Model.Piece;
import Model.Queen;
import Model.Rook;

public class GameController {
	//PIECES
		public static ArrayList<Piece> pieces = new ArrayList<>();
		public static ArrayList<Piece> simPieces = new ArrayList<>();
		public ArrayList<Piece> promoPieces = new ArrayList<>();
		public static ArrayList<PieceInfo> piecesInfo = new ArrayList<>();
		public static Piece activeP;
		public Piece checkingP;
		public static Piece castlingP;
		
		
	//color
		public static final int WHITE = 0;
		public static final int Black = 1;
		public static int currentColor = WHITE;
		
	//Booleans
		public boolean canMove;
		public boolean validSquare;
		public boolean promotion;
		public boolean gameover;
		public static Map<Type, Supplier<Piece>> PROMO_MAP = new HashMap<>();
		
		
		Board board = new Board();
		Mouse mouse = new Mouse();
		
		public void setpieces() {
			//WhiteTeam
			pieces.add(new Pawn(WHITE,0,6));
			pieces.add(new Pawn(WHITE,1,6));
			pieces.add(new Pawn(WHITE,2,6));
			pieces.add(new Pawn(WHITE,3,6));
			pieces.add(new Pawn(WHITE,4,6));
			pieces.add(new Pawn(WHITE,5,6));
			pieces.add(new Pawn(WHITE,6,6));
			pieces.add(new Pawn(WHITE,7,6));
			pieces.add(new Rook(WHITE,0,7));
			pieces.add(new Rook(WHITE,7,7));
			pieces.add(new Knight(WHITE,1,7));
			pieces.add(new Knight(WHITE,6,7));
			pieces.add(new Bishop(WHITE,2,7));
			pieces.add(new Bishop(WHITE,5,7));
			pieces.add(new Queen(WHITE,3,7));
		    pieces.add(new King(WHITE,4,7));

			
			
			//BlackTeam
			pieces.add(new Pawn(Black,0,1));
			pieces.add(new Pawn(Black,1,1));
			pieces.add(new Pawn(Black,2,1));
			pieces.add(new Pawn(Black,3,1));
			pieces.add(new Pawn(Black,4,1));
			pieces.add(new Pawn(Black,5,1));
			pieces.add(new Pawn(Black,6,1));
			pieces.add(new Pawn(Black,7,1));
			pieces.add(new Rook(Black,0,0));
			pieces.add(new Rook(Black,7,0));
			pieces.add(new Knight(Black,1,0));
			pieces.add(new Knight(Black,6,0));
			pieces.add(new Bishop(Black,2,0));
			pieces.add(new Bishop(Black,5,0));
			pieces.add(new Queen(Black,3,0));
			pieces.add(new King(Black,4,0));
		}
		
		
		public void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
			target.clear();
			for(int i = 0; i < source.size(); i++) {
				target.add(source.get(i));
			}
		}
		
		public boolean isIlligal(Piece king) {
			
			if(king.type == Type.KING) {
				for(Piece piece : simPieces) {
					if(piece != king && piece.color != king.color && piece.canMove(king.col, king.row)) {
						return true;
					}
				}
			}
			
			return false;
		}
		
		public boolean opponentCanCaptureKing() {
			
			Piece king = getKing(false);
			
			for(Piece piece : simPieces) {
				if(piece.color != king.color && piece.canMove(king.col, king.row)) {
					return true;
				}
			}
			
			return false;
		}
		
		public boolean isKingInCheck() {
			
			Piece king = getKing(true);
			
			if(activeP.canMove(king.col, king.row)) {
				checkingP = activeP;
				return true;
			}
			else {
				checkingP = null;
			}
			
			return false;
		}
		
		private Piece getKing(boolean opponent) {
			
			Piece king = null;
			
			for(Piece piece : simPieces) {
				if(opponent) {
					if(piece.type == Type.KING && piece.color != currentColor) {
						king = piece;
					}
				}
				else {
					if(piece.type == Type.KING && piece.color == currentColor) {
						king = piece;
					}
				}
			}
			return king;
		}
		
		public boolean isCheckmate() {
			
			Piece king = getKing(true);
			if(kingCanMove(king)) {
				return false;
			}
			else {
				int colDiff = Math.abs(checkingP.col - king.col);
				int rowDiff = Math.abs(checkingP.row - king.row);
				
				if(colDiff == 0) {
					if(checkingP.row < king.row) {
						for(int row = checkingP.row; row < king.row; row++) {
							for(Piece piece : simPieces) {
								if(piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
									return false;
								}
							}
						}
					}
					if(checkingP.row > king.row) {
						if(checkingP.row > king.row) {
							for(int row = checkingP.row; row > king.row; row--) {
								for(Piece piece : simPieces) {
									if(piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
										return false;
									}
								}
							}
						}
					}
				}
				else if(rowDiff == 0) {
					
					if(checkingP.col < king.col) {
						for(int col = checkingP.col; col < king.col; col++) {
							for(Piece piece : simPieces) {
								if(piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
									return false;
								}
							}
						}
					}
					if(checkingP.col > king.col) {
						for(int col = checkingP.col; col > king.col; col--) {
							for(Piece piece : simPieces) {
								if(piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
									return false;
								}
							}
						}
					}
					
				}
				else if(colDiff == rowDiff) {
					
					if(checkingP.row < king.row) {
						
						if(checkingP.col < king.col) {
							for(int col = checkingP.col, row = checkingP.row; col < king.col; col++, row++) {
								for(Piece piece : simPieces) {
									if(piece != king && piece.color != currentColor && piece.canMove(col, row)) {
										return false;
									}
								}
							}
						}
						if(checkingP.col > king.col) {
							for(int col = checkingP.col, row = checkingP.row; col > king.col; col--, row++) {
								for(Piece piece : simPieces) {
									if(piece != king && piece.color != currentColor && piece.canMove(col, row)) {
										return false;
									}
								}
							}
						}
					}
					if(checkingP.row < king.row) {
						
						if(checkingP.col < king.col) {
							for(int col = checkingP.col, row = checkingP.row; col < king.col; col++, row--) {
								for(Piece piece : simPieces) {
									if(piece != king && piece.color != currentColor && piece.canMove(col, row)) {
										return false;
									}
								}
							}
						}
						if(checkingP.col > king.col) {
							for(int col = checkingP.col, row = checkingP.row; col > king.col; col--, row--) {
								for(Piece piece : simPieces) {
									if(piece != king && piece.color != currentColor && piece.canMove(col, row)) {
										return false;
									}
								}
							}
						}
					}
					
				}
				else {
					
				}
			}
			
			return true;
		}
		public static void mostrarPosicionPiezas() {
			System.out.println("Posición de las piezas:");
			for (Piece piece : pieces) {
				System.out.println(piece.getType() + " en columna " + piece.getCol() + ", fila " + piece.getRow());
			}
		}
		public void setPiecesInfo() {
			for (Piece piece : pieces) {
				piecesInfo.add(new PieceInfo(piece.getType(), piece.getCol(), piece.getRow(), piece.getColor()));
			}
		}
		
	    public String getBoardState() {
	        StringBuilder boardState = new StringBuilder();
	        for (Piece piece : pieces) {
	            boardState.append(piece.getType()).append(" en columna ").append(piece.getCol()).append(", fila ").append(piece.getRow()).append("\n");
	        }
	        return boardState.toString();
	    }
	    

		private boolean isValidSquare(int col, int row) {
			mostrarPosicionPiezas();
		    return col >= 0 && col < 8 && row >= 0 && row < 8;
		}
		
		private boolean isProtected(int col, int row, int color) {
		    for (Piece piece : simPieces) {
		        if (piece.color == color) continue; 
		        if (piece.canMove(col, row)) {
		            return true; 
		        }
		    }
		    return false; 
		}
		
		private boolean kingCanMove(Piece king) {
		    for (int colPlus = -1; colPlus <= 1; colPlus++) {
		        for (int rowPlus = -1; rowPlus <= 1; rowPlus++) {
		            if (colPlus == 0 && rowPlus == 0) continue; 
		            int newCol = king.col + colPlus;
		            int newRow = king.row + rowPlus;
		            if (isValidSquare(newCol, newRow) && !isProtected(newCol, newRow, king.color)) {
		                return true; 
		            }
		        }
		    }
		    return false; 
		}
		private boolean isValidMove(Piece king, int colPlus, int rowPlus) {
			
			boolean isValidMove = false;
			
			//update king position
			king.col += colPlus;
			king.row += rowPlus;
			
			if(king.canMove(colPlus, rowPlus)) {
				
				if(king.hittingP != null) {
					simPieces.remove(king.hittingP.getIndex());
				}
				if(isIlligal(king) == false) {
					isValidMove = true;
				}
			}
			
			king.resetPosition();
			copyPieces(pieces, simPieces);
			
			return isValidMove;
		}
		
		public void checkCastling() {
			
			if(castlingP != null) {
				if(castlingP.col == 0) {
					castlingP.col += 3;
				}
				else if(castlingP.col == 7) {
					castlingP.col -= 2;
				}
				castlingP.x = castlingP.getX(castlingP.col);
			}
		}
		
		//change turn
		public static void changePlayer() {
			
			if(currentColor == WHITE) {
				currentColor = Black;
				
				//Two stepped status
				for(Piece piece : pieces) {
					if(piece.color == Black) {
						piece.twoStepped = false;
					}
				}
				
			}
			
			else {
				currentColor = WHITE;
				//Two stepped status
				for(Piece piece : pieces) {
					if(piece.color == WHITE) {
						piece.twoStepped = false;
					}
				}
			}
			activeP = null;
			
		}
		
		public boolean canPromote() {
			
			if(activeP.type == Type.PAWN) {
				if(currentColor == WHITE && activeP.row == 0 || currentColor == Black && activeP.row == 7) {
					promoPieces.clear();
					promoPieces.add(new Rook(currentColor, 9, 2));
					promoPieces.add(new Knight(currentColor, 9, 3));
					promoPieces.add(new Bishop(currentColor, 9, 4));
					promoPieces.add(new Queen(currentColor, 9, 5));
					return true;
				}
			}
			
			return false;
		}
		
		
		
		static {
	        PROMO_MAP.put(Type.ROOK, () -> new Rook(currentColor, activeP.col, activeP.row));
	        PROMO_MAP.put(Type.KNIGHT, () -> new Knight(currentColor, activeP.col, activeP.row));
	        PROMO_MAP.put(Type.BISHOP, () -> new Bishop(currentColor, activeP.col, activeP.row));
	        PROMO_MAP.put(Type.QUEEN, () -> new Queen(currentColor, activeP.col, activeP.row));
	    }
		
		public void promoting() {
		    if (mouse.pressed) {
		        for (Piece piece : promoPieces) {
		            if (piece.col == mouse.x / Board.SQUARE_SIZE && piece.row == mouse.y / Board.SQUARE_SIZE) {
		                Supplier<Piece> constructor = PROMO_MAP.get(piece.type);
		                if (constructor != null) {
		                    simPieces.add(constructor.get());
		                    simPieces.remove(activeP.getIndex());
		                    copyPieces(simPieces, pieces);
		                    activeP = null;
		                    promotion = false;
		                    changePlayer();
		                    	                    
							
		                }
		            }
		        }
		    }
		}
		
		public boolean makeMove(String fromSquare, String toSquare) {
		    
		    int fromCol = getColumnIndex(fromSquare.charAt(0));
		    int fromRow = getRowIndex(fromSquare.charAt(1));
		    int toCol = getColumnIndex(toSquare.charAt(0));
		    int toRow = getRowIndex(toSquare.charAt(1));

		    if (fromCol == -1 || fromRow == -1 || toCol == -1 || toRow == -1) {
		        System.out.println("Coordenadas inválidas.");
		        return false;
		    }

		   
		    Piece pieceToMove = null;
		    for (Piece piece : pieces) {
		        if (piece.getCol() == fromCol && piece.getRow() == fromRow) {
		            pieceToMove = piece;
		            break;
		        }
		    }

		    if (pieceToMove == null) {
		        System.out.println("No hay una pieza en la posición de origen especificada.");
		        return false;
		    }

		  
		    if (!pieceToMove.canMove(toCol, toRow)) {
		        System.out.println("Movimiento inválido para la pieza seleccionada.");
		        return false;
		    }

		    
		    Piece pieceAtDestination = null;
		    for (Piece piece : pieces) {
		        if (piece.getCol() == toCol && piece.getRow() == toRow) {
		            pieceAtDestination = piece;
		            break;
		        }
		    }

		  
		    if (pieceAtDestination != null) {
		        
		        pieces.remove(pieceAtDestination);
		        System.out.println("Pieza capturada: " + pieceAtDestination.getType());
		    }

		   
		    pieceToMove.setCol(toCol);
		    pieceToMove.setRow(toRow);

		    System.out.println("Movimiento realizado con éxito.");
		    return true;
		}

		
		private int getColumnIndex(char column) {
		    return column >= 'A' && column <= 'H' ? column - 'A' : -1;
		}

		private int getRowIndex(char row) {
		    return row >= '1' && row <= '8' ? '8' - row : -1;
		}

		
}
