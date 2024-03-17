package modelo;

import vista.GamePanel;
import controlador.Type;
import controlador.GameController;

public class King extends Piece {
	
	GameController gamec = new GameController();

	public King(int color, int col, int row) {
		super(color, col, row);
		
		type = Type.KING;
		
		if (color == gamec.WHITE) {
			image = getImage("/piece/w-king");
		}
		else {
			image = getImage("/piece/b-king");
		}	

	}
	
	public boolean canMove(int targetCol, int targetRow) {
		
		if(isWithinBoard(targetCol, targetRow)) {
			
			if(Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1 || Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 1) {
				
				if(isValidSquare(targetCol, targetRow)) {
					return true;
				}
			}	
				//castling
				if(moved == false) {
					
					//right
					if(targetCol == preCol+2 && targetRow == preRow && pieceIsOnStraightLine(targetCol, targetRow) == false) {
						for(Piece piece : gamec.simPieces) {
							if(piece.col == preCol+3 && piece.row == preRow && piece.moved == false) {
								gamec.castlingP = piece;
								return true;
							}
						}
					}
					
					//left
					if(targetCol == preCol-2 && targetRow == preRow && pieceIsOnStraightLine(targetCol, targetRow) == false) {
						Piece p[] = new Piece[2];
						for(Piece piece : gamec.simPieces) {
							if(piece.col == preCol-3 && piece.row == targetRow) {
								p[0] = piece;
							}
							if(piece.col == preCol-4 && piece.row == targetRow) {
								p[1] = piece;
							}
							if(p[0] == null && p[1] != null && p[1].moved == false) {
								gamec.castlingP = p[1];
								return true;
							}
						}
					}
				}
				
		}
		return false;
	}

}
