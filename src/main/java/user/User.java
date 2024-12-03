package user;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class User {
    private static Scanner scanner = new Scanner(System.in);
    private static final String SERVER_ADDRESS_PADRAO = "192.168.208.101";
    private static final int SERVER_PORT_PADRAO = 7777;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(getServerAddress(), getServerPort());

            PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            startClientThread(in);

            sendMessageLoop(socket, out);
        } catch (IOException e) {
            System.out.println("Servidor não achado, verifique o IP e a Porta.");
        }
    }

    private static String getServerAddress() {
        System.out.println(
                "Digite o IP do server desejado (Com pontuação), ou digite \"PADRÃO\" para conectar no server padrão");
        String response = scanner.nextLine();
        if (response.equalsIgnoreCase("PADRÃO") || response.equalsIgnoreCase("PADRAO")) {
            return SERVER_ADDRESS_PADRAO;
        }
        return response;
    }

    private static int getServerPort() {
        System.out.println(
                "Digite a Porta do server desejado, ou digite \"PADRÃO\" para conectar no server padrão");
        String response = scanner.nextLine();
        if (response.equalsIgnoreCase("PADRÃO") || response.equalsIgnoreCase("PADRAO")) {
            return SERVER_PORT_PADRAO;
        }
        return Integer.parseInt(response);
    }

    private static void checkServerStatus(String serverResponse) {
        if (serverResponse == null) {
            System.out.println("Servidor desligado");
            System.exit(0);
        }
    }

    private static void readServerResponse(BufferedReader in) throws IOException {
        String serverResponse;

        while ((serverResponse = in.readLine()) != null) {
            System.out.println(serverResponse);
        }
        checkServerStatus(serverResponse);
    }

    private static void startClientThread(BufferedReader in) {
        new Thread(() -> {
            try {
                readServerResponse(in);
            } catch (IOException e) {
                System.exit(0);
            }
        }).start();
    }

    private static void sendMessageLoop(Socket socket, PrintWriter out) throws IOException {
        String userInput;
        while (!socket.isClosed()) {
            userInput = scanner.nextLine();
            if (userInput.equalsIgnoreCase("/exit")) {
                out.println(userInput);
                socket.close();
            } else {
                out.println(userInput);
            }
        }
    }
}
