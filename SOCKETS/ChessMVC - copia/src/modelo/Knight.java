package modelo;

import controlador.Type;
import controlador.GameController;

public class Knight extends Piece {

	GameController gamec = new GameController();
	
	public Knight(int color, int col, int row) {
		super(color, col, row);
		
		type = Type.KNIGHT;
		
		if (color == gamec.WHITE) {
			image = getImage("/piece/w-knight");
		}
		else {
			image = getImage("/piece/b-knight");
		}	

	}
	
	public boolean canMove(int targetCol, int targetRow) {
		// movement ratio for col and row is 1:2 or 2:1
		if(Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 2) {
			if(isValidSquare(targetCol, targetRow)) {
				return true;
			}
		}
		
		return super.canMove(targetCol, targetRow);
	}

}
