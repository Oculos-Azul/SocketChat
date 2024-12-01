package user;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class User {
    private static final String SERVER_ADDRESS = "localhost";  // Endereço do servidor
    private static final int SERVER_PORT = 7777;  // Porta de comunicação

    public static void main(String[] args) {
        try {
            // Estabelece a conexão com o servidor
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
//            System.out.println("Connected to the chat server!");

            // Configura os fluxos de entrada e saída
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);  // Enviar mensagens ao servidor
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  // Receber mensagens do servidor

            // Thread para receber mensagens do servidor
            new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        System.out.println(serverResponse);  // Exibe as mensagens recebidas do servidor
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Lê as mensagens digitadas pelo usuário e envia ao servidor
            Scanner scanner = new Scanner(System.in);
            String userInput;
            while (true) {
                userInput = scanner.nextLine();  // Lê a entrada do usuário
                out.println(userInput);  // Envia a mensagem ao servidor
            }

        } catch (IOException e) {
            e.printStackTrace();  // Exibe qualquer erro que ocorrer durante a execução
        }
    }
}
