package server;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
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

                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8),
                        true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));

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

                if (message.equalsIgnoreCase("/exit")) {
                    out.println("Desconectando do servidor.");
                    break;
                }

                broadcast(message, username, out);

                if (message.equalsIgnoreCase("/help")) {
                    out.println("Para sair digite \"/exit\"");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                broadcastExit(username, out, clientSocket);
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

    private static void broadcastExit(String username, PrintWriter out, Socket clientSocket) throws IOException {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                if (writer != out) {
                    writer.println(CYAN + "Servidor: " + GREEN + username + RESET + " saiu do servidor");
                }
            }
            System.out.println(CYAN + "Servidor: " + GREEN + username + RESET + " saiu do servidor");
            clientWriters.remove(out);
        }
        // clientSocket.close();
    }

    private static String nameUser(Socket clientSocket, BufferedReader in, PrintWriter out) throws IOException {
        System.out.println("Usuário " + clientSocket.getInetAddress() + " Conectado.");
        out.println(CYAN + "Servidor: " + RESET + "Digite seu nome");
        String username = in.readLine();
        out.println(CYAN + "Servidor: " + RESET + "Bem vindo " + username);
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                if (writer != out) {
                    writer.println(CYAN + "Servidor: " + RESET + GREEN + username + RESET + " entrou no server");
                }
            }
        }
        out.println(CYAN + "Servidor: " + RESET + "Para mais informações digite \"/Help\"");

        return username;
    }
}
