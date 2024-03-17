package modelo;

import vista.GamePanel;
import controlador.Type;
import controlador.GameController;

public class Rook extends Piece {
	
	GameController gamec = new GameController();
	
	public Rook(int color, int col, int row) {
		super(color, col, row);
		
		type = Type.ROOK;
		
		if (color == gamec.WHITE) {
			image = getImage("/piece/w-rook");
		}
		else {
			image = getImage("/piece/b-rook");
		}	
	}
	
	public boolean canMove(int targetCol, int targetRow) {
		if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
			if(targetCol == preCol || targetRow == preRow) {
				if(isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false) {
					return true;
				}
			}
		}
		
		return super.canMove(targetCol, targetRow);
	}

}
