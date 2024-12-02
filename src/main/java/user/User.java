package user;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class User {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 7777;

    public static void main(String[] args) {
        try {
            // Estabelece a conexão com o servidor
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);

            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"),
                    true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        System.out.println(serverResponse); // Exibe as mensagens recebidas do servidor
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Lê as mensagens digitadas pelo usuário e envia ao servidor
            Scanner scanner = new Scanner(System.in);
            String userInput;
            while (true) {
                userInput = scanner.nextLine();
                out.println(userInput); // Envia a mensagem ao servidor
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
