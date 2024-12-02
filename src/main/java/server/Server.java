package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static final String RESET = "\u001B[0m"; // reseta a cor para o padrão
    public static final String GREEN = "\u001B[32m";
    public static final String CYAN = "\u001B[36m";
    private static final int PORT = 7777;
    private static List<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server roda aqui mano " + PORT + "...");
            while (true) {
                Socket clientSocket = serverSocket.accept();

                PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"),
                        true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));

                String username = nameUser(clientSocket, in, out);

                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                new Thread(() -> userHandler(username, clientSocket, in, out)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void userHandler(String username, Socket clientSocket, BufferedReader in, PrintWriter out) {
        try {

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(GREEN + username + ": " + RESET + message);

                broadcast(message, username, out);

                if (message.equalsIgnoreCase("/help")) {
                    out.println("Para sair digite \"Help\"");
                }

                if (message.equalsIgnoreCase("/exit")) {
                    break;
                }
            }
        } catch (IOException e) {

            broadcastExit(username, out);

        } finally {
            try {

                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }

                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void broadcast(String message, String username, PrintWriter out) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                if (writer != out) {
                    writer.println(GREEN + username + ": " + RESET + message);
                }
            }
        }
    }

    private static void broadcastExit(String username, PrintWriter out) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                if (writer != out) {
                    writer.println(CYAN + "Servidor: " + GREEN + username + RESET + " saiu do servidor");
                }
            }
        }
    }

    private static String nameUser(Socket clientSocket, BufferedReader in, PrintWriter out) throws IOException {
        System.out.println("Usuário " + clientSocket.getInetAddress() + "Conectado.");
        out.println(CYAN + "Servidor: " + "Digite seu nome");
        String username = in.readLine();
        out.println(CYAN + "Servidor: " + "Bem vindo " + username);
        out.println(CYAN + "Servidor: " + "Para mais informações digite \"Help\"");

        return username;
    }
}
