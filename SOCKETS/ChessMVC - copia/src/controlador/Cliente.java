package controlador;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Cliente {
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Socket socket;

    public Cliente(String servidorIP, int puerto) {
        try {
            socket = new Socket(servidorIP, puerto);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

            Thread t = new Thread(this::recibirActualizaciones);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recibirActualizaciones() {
        try {
            while (true) {
                Serializable objetoRecibido = (Serializable) inputStream.readObject();

                // Realizar la lógica correspondiente con el objeto recibido
                if (objetoRecibido instanceof List<?>) {
                    // Actualizar la lista de jugadores en JuegoRuta
                    GameController.updatePieceInfo((ArrayList<PieceInfo>) objetoRecibido);
                    
                } else if (objetoRecibido instanceof Integer) {
                    // Actualizar el turno actual en JuegoRuta
                    GameController.currentColor = (Integer) objetoRecibido;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void enviarJugadoresYTurno(ArrayList<PieceInfo> objetoRecibido, int currentColor) {
        try {
            
            outputStream.writeObject(objetoRecibido);
            outputStream.writeObject(currentColor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void iniciar() {
        // Realiza acciones de inicio si es necesario
        System.out.println("Cliente iniciado con éxito.");
    
        // Aquí puedes agregar cualquier lógica adicional que necesites al iniciar el cliente
    }
}



