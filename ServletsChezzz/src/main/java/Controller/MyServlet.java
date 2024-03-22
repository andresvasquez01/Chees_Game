package Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/play")
public class MyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final GameController gameController;

    public MyServlet() {
        super();
        gameController = new GameController(); 
        gameController.setpieces(); 
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String boardState = gameController.getBoardState();
        out.println(boardState);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {      
        String fromSquare = request.getParameter("from");
        String toSquare = request.getParameter("to");
        
       
        boolean isValidMove = gameController.makeMove(fromSquare, toSquare);

        PrintWriter out = response.getWriter();
        if (isValidMove) {
            out.println("Movimiento válido. Estado actual del tablero: " + gameController.getBoardState());
        } else {
            out.println("Movimiento inválido. Por favor, intenta de nuevo.");
        }
    }
}
