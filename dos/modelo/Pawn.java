package modelo;

import controlador.Type;
import controlador.GameController;

public class Pawn extends Piece {

	GameController gamec = new GameController();
	
	public Pawn(int color, int col, int row) {
		super(color, col, row);
		
		type = Type.PAWN;
		
		if (color == gamec.WHITE) {
			image = getImage("piece/w-pawn");
		}
		else {
			image = getImage("piece/b-pawn");
		}
	}
	
	@Override
	public boolean canMove(int targetCol, int targetRow) {
		
		if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
			// Color move
			int moveValue;
			if(color == gamec.WHITE) {
				moveValue = -1;
			}
			else {
				moveValue = 1;
			}
			
			//Hitting piece
			hittingP = getHittingP(targetCol, targetRow);
			
			//1 rule movement
			if(targetCol == preCol && targetRow == preRow + moveValue && hittingP == null) {
				return true;
			}
			//2 rule movement
			if(targetCol == preCol && targetRow == preRow + moveValue*2 && hittingP == null && moved == false && pieceIsOnStraightLine(targetCol, targetRow) == false) {
				return true;
			}
			
			//last rule capture
			if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && hittingP != null && hittingP.color != color) {
				return true;
			}
			//Passant rule
			if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue) {
				for(Piece piece : gamec.simPieces) {
					if(piece.col == targetCol && piece.row == preRow && piece.twoStepped == true) {
						hittingP = piece;
						return true;
					}
				}
			}
			
		}
		
		return false;
	}

}
