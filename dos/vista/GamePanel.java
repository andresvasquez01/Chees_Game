package vista;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import controlador.Mouse;
import controlador.PieceInfo;
import controlador.Type;
import controlador.Cliente;
import controlador.GameController;
import modelo.Bishop;
import modelo.Board;
import modelo.King;
import modelo.Knight;
import modelo.Pawn;
import modelo.Piece;
import modelo.Queen;
import modelo.Rook;

public class GamePanel extends JPanel implements Runnable {
	private JButton sendButton;
	private static ObjectOutputStream outputStream; 
	public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GamePanel gamePanel = new GamePanel();
            JFrame window = new JFrame("Chess");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);
            window.add(gamePanel);
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }
	
	public BufferedImage image;
	public static final int Width = 1100;
	public static final int Height = 800;
	
	
	
	final int FPS = 60;
	Thread gameThread;
	
	
	
	Board board = new Board();
	Mouse mouse = new Mouse();
	GameController gamec = new GameController();
	
	private static Cliente cliente =  new Cliente("192.168.1.13", 12345);
	
	
	public GamePanel() {	
		setPreferredSize(new Dimension(Width,Height));
		setBackground(Color.black);
		addMouseMotionListener(mouse);
		addMouseListener(mouse);
		
		gamec.setpieces();
		gamec.copyPieces(gamec.pieces, gamec.simPieces);
		
		JFrame window = new JFrame("Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.add(this);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
		sendButton = new JButton("Enviar Piezas");
        sendButton.addActionListener(new SendButtonListener());
        add(sendButton);
		cliente.iniciar();

        launchGame();
	}
	
	public void launchGame() {
		gameThread = new Thread(this);
		gameThread.start();
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
		
		if(gamec.promotion) {
			gamec.promoting();
		}
		else if (gamec.gameover == false) {
			//MOUSE BUTTON PRESSED
			if (mouse.pressed) {
				if(gamec.activeP == null) {
					
					for(Piece piece : gamec.simPieces) {
						if(piece.color == gamec.currentColor &&
								piece.col == mouse.x/Board.SQUARE_SIZE &&
								piece.row == mouse.y/Board.SQUARE_SIZE) {
							
							gamec.activeP = piece;
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
				if(gamec.activeP != null) {
					
					if(gamec.validSquare) {
						GameController.mostrarPosicionPiezas();
						//Move confirmed
						
						//Update the piece list in case a piece has been captured and removed during the simulation 
						gamec.copyPieces(gamec.simPieces, gamec.pieces);
						gamec.activeP.updatePosition();
						if(gamec.castlingP != null) {
							gamec.castlingP.updatePosition();
						}
						
						if(gamec.isKingInCheck() && gamec.isCheckmate()) {
							//game over
							gamec.gameover = true;
						}
						else{
							if(gamec.canPromote()) {
								gamec.promotion = true;
							}
							else {
								gamec.changePlayer();
							}	
						}	
					}
					else {
						//the move is invalid so reset everything
						gamec.copyPieces(gamec.pieces, gamec.simPieces);
						gamec.activeP.resetPosition();
						gamec.activeP = null;
					}
				}
			}
		}
		

	}
	
	private void simulate(){
		
		gamec.canMove = false;
		gamec.validSquare = false;
		
		//Reset the pieces loop
		gamec.copyPieces(gamec.pieces, gamec.simPieces);
		
		//reset castling
		if(gamec.castlingP != null) {
			gamec.castlingP.col = gamec.castlingP.preCol;
			gamec.castlingP.x = gamec.castlingP.getX(gamec.castlingP.col);
			gamec.castlingP = null;
		}
		
		gamec.activeP.x = mouse.x - Board.HALS_SQUARE_SIZE;
		gamec.activeP.y = mouse.y - Board.HALS_SQUARE_SIZE;
		gamec.activeP.col = gamec.activeP.getCol(gamec.activeP.x);
		gamec.activeP.row = gamec.activeP.getRow(gamec.activeP.y);
		
		//Check if the piece is hovering over a reachable square
		if(gamec.activeP.canMove(gamec.activeP.col, gamec.activeP.row)) {
			
			gamec.canMove = true;
			
			//if the hitting a piece, remove it from the list
			if(gamec.activeP.hittingP != null) {
				gamec.simPieces.remove(gamec.activeP.hittingP.getIndex());
			}
			
			gamec.checkCastling();
			
			if(gamec.isIlligal(gamec.activeP) == false && gamec.opponentCanCaptureKing() == false) {				
				gamec.validSquare = true;
			}
		}
		
	}
	

	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		//Board
		board.draw(g2);
		
		//Pieces
		List<Piece> piecesCopy = new ArrayList<>(gamec.simPieces);
    
		//Pieces
		for (Piece p : piecesCopy) {
			p.draw(g2);
		}
		
		if(gamec.activeP != null) {
			if(gamec.canMove) {
				if(gamec.isIlligal(gamec.activeP) || gamec.opponentCanCaptureKing()) {
					g2.setColor(Color.gray);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					g2.fillRect(gamec.activeP.col*Board.SQUARE_SIZE, gamec.activeP.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}
				else {
					g2.setColor(Color.white);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					g2.fillRect(gamec.activeP.col*Board.SQUARE_SIZE, gamec.activeP.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}
			}

			//draw the active piece in the end so it won`t be the board or the colored square
			gamec.activeP.draw(g2);
		}
		
		//Turns
		//g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
		g2.setColor(Color.white);
		
		if(gamec.promotion) {
			g2.drawString("Promovido a: ", 840, 150);
			for(Piece piece : gamec.promoPieces) {
				g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
			}
		}
		
		if(gamec.currentColor == gamec.WHITE) {
			g2.drawString("White", 900, 750);
			if(gamec.checkingP != null && gamec.checkingP.color == gamec.Black) {
				g2.setColor(Color.green);
				g2.drawString("Check", 900, 400);
			}
		}
		else {
			g2.drawString("Black", 900, 750);
			if(gamec.checkingP != null && gamec.checkingP.color == gamec.WHITE) {
				g2.setColor(Color.green);
				g2.drawString("Check", 900, 400);
			}
		}
		
	
	
	if(gamec.gameover) {
		String s = "";
		if(gamec.currentColor == gamec.WHITE) {
			s = "Gana el Blanco";
		}
		else {
			s = "gana el Negro";
		}
		g2.setFont(new Font(" ITALIC", Font.PLAIN, 90));
		g2.setColor(Color.red);
		g2.drawString(s,200,420);
	}
	}
	
	private class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Crear una lista para almacenar la información de las piezas
            ArrayList<PieceInfo> pieceInfos = new ArrayList<>();

            // Llenar la lista con la información de las piezas del juego
            for (Piece piece : gamec.pieces) {
                PieceInfo pieceInfo = new PieceInfo(piece.getType(), piece.getCol(), piece.getRow(), piece.getColor());
                pieceInfos.add(pieceInfo);
            }
			if (cliente != null) {
				cliente.enviarJugadoresYTurno(pieceInfos,gamec.currentColor);
			}
           
        }
    }
	
	
}
