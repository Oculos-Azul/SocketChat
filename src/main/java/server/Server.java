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
    public static final String SERVER_NAME = "Servidor: ";
    private static final int PORT = 7777;
    private static List<PrintWriter> clientWriters = new ArrayList<>();
    private static boolean isRunning = true;

    public static void main(String[] args) {
        startServerTimer(1 * 60 * 1000L);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server roda aqui mano " + PORT + "...");
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();

                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));

                String username = promptUsername(clientSocket, in, out);

                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                new Thread(() -> handleUserMessages(username, in, out)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startServerTimer(long duration) {
        new Thread(() -> {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrompida: " + e.getMessage());
            }
            broadcastMessage("O tempo de execução do servidor expirou. Encerrando...", "Servidor", null);
            isRunning = false;
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.close();
                }
            }
            System.exit(0);
        }).start();
    }

    private static void handleUserMessages(String username, BufferedReader in, PrintWriter out) {
        try {
            String message;
            while ((message = in.readLine()) != null && isRunning == true) {
                System.out.println(GREEN + username + ": " + RESET + message);

                if (message.equalsIgnoreCase("/exit")) {
                    out.println("Desconectando do servidor.");
                    break;
                }

                broadcastMessage(message, username, out);

                if (message.equalsIgnoreCase("/help")) {
                    out.println("Para sair digite \"/exit\"");
                }
            }
        } catch (IOException e) {
            System.out.println(SERVER_NAME + "fechamento do serviço.");
        } finally {
            notifyUserExit(username, out);
        }
    }

    private static void broadcastMessage(String message, String username, PrintWriter out) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                if (writer != out) {
                    writer.println(GREEN + username + ": " + RESET + message);
                }
            }
        }
    }

    private static void notifyUserExit(String username, PrintWriter out) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                if (writer != out) {
                    writer.println(CYAN + SERVER_NAME + GREEN + username + RESET + " saiu do servidor");
                }
            }
            System.out.println(CYAN + SERVER_NAME + GREEN + username + RESET + " saiu do servidor");
            clientWriters.remove(out);
        }
    }

    private static String promptUsername(Socket clientSocket, BufferedReader in, PrintWriter out) throws IOException {
        System.out.println("Usuário " + clientSocket.getInetAddress() + " Conectado.");
        out.println(CYAN + SERVER_NAME + RESET + "Digite seu nome");
        String username = in.readLine();
        out.println(CYAN + SERVER_NAME + RESET + "Bem vindo " + username);
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                if (writer != out) {
                    writer.println(CYAN + SERVER_NAME + RESET + GREEN + username + RESET + " entrou no server");
                }
            }
        }
        out.println(CYAN + SERVER_NAME + RESET + "Para mais informações digite \"/Help\"");

        return username;
    }
}
