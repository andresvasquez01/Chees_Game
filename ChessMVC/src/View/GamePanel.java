package View;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import Controller.Mouse;
import Model.Bishop;
import Model.Board;
import Model.King;
import Model.Knight;
import Model.Pawn;
import Model.Piece;
import Model.Queen;
import Model.Rook;

public class GamePanel extends JPanel implements Runnable {
	
	public BufferedImage image;
	public static final int Width = 1100;
	public static final int Height = 800;
	final int FPS = 60;
	Thread gameThread;
	Board board = new Board();
	Mouse mouse = new Mouse();
	
	//PIECES
	public static ArrayList<Piece> pieces = new ArrayList<>();
	public static ArrayList<Piece> simPieces = new ArrayList<>();
	Piece activeP;
	
	//color
	public static final int WHITE = 0;
	public static final int Black = 1;
	int currentColor = WHITE;
	
	//Booleans
	boolean canMove;
	boolean validSquare;
	
		
	public GamePanel() {	
		setPreferredSize(new Dimension(Width,Height));
		setBackground(Color.black);
		addMouseMotionListener(mouse);
		addMouseListener(mouse);
		
		setpieces();
		copyPieces(pieces, simPieces);
	}
	
	public void launchGame() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	//********************************************************************************************************
	/*public BufferedImage getImage(String imagePath) {
		
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
		}catch (IOException e) {
			e.printStackTrace();
		}
		return image;
		
	}*/
	//*********************************************************************************************************
	
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
		//pieces.add(new Pawn(WHITE,4,4));

		
		
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
	
	private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
		target.clear();
		for(int i = 0; i < source.size(); i++) {
			target.add(source.get(i));
		}
	}
	
	@Override
	public void run() {
		
		//game loop
		double drawInterval = 1000000000/FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		
		while(gameThread != null) {
			
			currentTime = System.nanoTime();
			
			delta += (currentTime - lastTime)/drawInterval;
			lastTime = currentTime;
			
			if (delta >=1) {
				update();
				repaint();
				delta--;
			}
			
		}
		
	}
	
	private void update() {
		
		//MOUSE BUTTON PRESSED
		if (mouse.pressed) {
			if(activeP == null) {
				
				for(Piece piece : simPieces) {
					if(piece.color == currentColor &&
							piece.col == mouse.x/Board.SQUARE_SIZE &&
							piece.row == mouse.y/Board.SQUARE_SIZE) {
						
						activeP = piece;
					}
				}
					
			}
			else {
				//if the player is holding a piece, simulate the move
				simulate();
			}
		}
		
		//mouse button released
		if(mouse.pressed == false) {
			if(activeP != null) {
				
				if(validSquare) {
					
					//Move confirmed
					
					//Update the piece list in case a piece has been captured and removed during the simulation 
					copyPieces(simPieces, pieces);
					activeP.updatePosition();
					
					changePlayer();
				}
				else {
					//the move is invalid so reset everything
					copyPieces(pieces, simPieces);
					activeP.resetPosition();
					activeP = null;
				}
			}
		}
	}
	
	private void simulate(){
		
		canMove = false;
		validSquare = false;
		
		//Reset the pieces loop
		copyPieces(pieces, simPieces);
		
		activeP.x = mouse.x - Board.HALS_SQUARE_SIZE;
		activeP.y = mouse.y - Board.HALS_SQUARE_SIZE;
		activeP.col = activeP.getCol(activeP.x);
		activeP.row = activeP.getRow(activeP.y);
		
		//Check if the piece is hovering over a reachable square
		if(activeP.canMove(activeP.col, activeP.row)) {
			
			canMove = true;
			
			//if the hitting a piece, remove it from the list
			if(activeP.hittingP != null) {
				simPieces.remove(activeP.hittingP.getIndex());
			}
			
			validSquare = true;
		}
		
	}
	
	//change turn
	public void changePlayer() {
		
		if(currentColor == WHITE) {
			currentColor = Black;
		}
		else {
			currentColor = WHITE;
		}
		activeP = null;
		
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		//Board
		board.draw(g2);
		
		//Pieces
		for (Piece p : simPieces) {
			p.draw(g2);
			
		}
		
		if(activeP != null) {
			if(canMove) {
				g2.setColor(Color.white);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
				g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			}

			//draw the active piece in the end so it won`t be the board or the colored square
			activeP.draw(g2);
		}
		
		//Turns
		//g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
		g2.setColor(Color.white);
		
		if(currentColor == WHITE) {
			g2.drawString("White", 900, 750);
		}
		else {
			g2.drawString("Black", 900, 750);
		}
		
	}
}
