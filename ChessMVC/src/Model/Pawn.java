package Model;

import View.GamePanel;

public class Pawn extends Piece {

	public Pawn(int color, int col, int row) {
		super(color, col, row);
		
		if (color == GamePanel.WHITE) {
			image = getImage("/piece/w-pawn");
		}
		else {
			image = getImage("/piece/b-pawn");
		}
	}
	
	@Override
	public boolean canMove(int targetCol, int targetRow) {
		
		if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
			// Color move
			int moveValue;
			if(color == GamePanel.WHITE) {
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
		}
		
		return false;
	}

}
