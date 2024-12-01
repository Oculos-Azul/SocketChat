package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT = 7777;
    private static List<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Server roda aqui mano " + PORT + "...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Usuáiro " + clientSocket.getInetAddress() + "Conectado.");

                // Create a PrintWriter and BufferedReader for this client
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Add this client to the list of clients
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                // Handle this client in a new thread
                new Thread(() -> UserHandler(clientSocket, in, out)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void UserHandler(Socket clientSocket, BufferedReader in, PrintWriter out) {
        try {
        	String Username;
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Message from client: " + message);

                // Broadcast the message to all clients
                synchronized (clientWriters) {
                    for (PrintWriter writer : clientWriters) {
                        if (writer != out) {
                            writer.println("Eu aqui foda: " + message);
                        }
                    }
                }

                if (message.equalsIgnoreCase("bye")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Remove this client from the list
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
