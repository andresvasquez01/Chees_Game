package controlador;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor {
    private ServerSocket serverSocket;
    private List<ObjectOutputStream> clientesStreams;

    public Servidor(int puerto) {
        try {
            serverSocket = new ServerSocket(puerto);
            clientesStreams = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void iniciar() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                clientesStreams.add(outputStream);

                // Mostrar mensaje cuando se conecta un nuevo cliente
                System.out.println("Nuevo cliente conectado: " + socket.getInetAddress());

                Thread t = new Thread(() -> manejarCliente(socket));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void manejarCliente(Socket socket) {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
    
            while (true) {
                // Leer objeto del cliente
                Serializable objetoRecibido = (Serializable) inputStream.readObject();
    
                // Enviar objeto a todos los clientes
                enviarAClientes(objetoRecibido);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void enviarAClientes(Serializable objeto) {
        for (ObjectOutputStream clienteStream : clientesStreams) {
            try {
                clienteStream.writeObject(objeto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor(12345);
        servidor.iniciar();
    }
}
