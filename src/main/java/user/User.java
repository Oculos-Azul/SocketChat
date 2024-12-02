package user;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class User {
    static Scanner scanner = new Scanner(System.in);
    private static final String SERVER_ADDRESS_PADRAO = "localhost";
    private static final int SERVER_PORT_PADRAO = 7777;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(serverADDRESS(), serverPort());

            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"),
                    true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            clientThread(in);

            messageSend(socket, out);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String serverADDRESS() {
        System.out.println(
                "Digite o IP do server desejado (Com pontuação), ou digite \"PADRÃO\" para conectar no server padrão");
        String response = scanner.nextLine();
        if (response.equalsIgnoreCase("PADRÂO") || response.equalsIgnoreCase("PADRAO"))
            return SERVER_ADDRESS_PADRAO;
        return response;
    }

    private static int serverPort() {
        System.out.println(
                "Digite o Porta do server desejado, ou digite \"PADRÃO\" para conectar no server padrão");
        String response = scanner.nextLine();
        if (response.equalsIgnoreCase("PADRÂO") || response.equalsIgnoreCase("PADRAO"))
            return SERVER_PORT_PADRAO;
        return Integer.parseInt(response);
    }

    private static void serverOff(String serverResponse) {
        if (serverResponse == null) {
            System.out.println("Servidor desligado");
            System.exit(0);
        }
    }

    private static void serverRespost(BufferedReader in) throws IOException {
        String serverResponse;

        while ((serverResponse = in.readLine()) != null)
            System.out.println(serverResponse);
        serverOff(serverResponse);
    }

    private static void clientThread(BufferedReader in) {
        new Thread(() -> {
            try {
                serverRespost(in);

            } catch (IOException e) {
                System.exit(0);
            }
        }).start();
    }

    private static void messageSend(Socket socket, PrintWriter out) throws IOException {

        String userInput;
        while (true) {
            userInput = scanner.nextLine();
            if (userInput.equalsIgnoreCase("/exit")) {
                out.println(userInput);
                socket.close();
            } else
                out.println(userInput);
        }
    }
}