import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Server {
    public static final int PORT = 5000;

    public static void main(String[] args) {
        System.out.println("Server starting on port " + PORT + " ...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Waiting for client to connect...");
            try (Socket clientSocket = serverSocket.accept()) {
                System.out.println("Client connected from " + clientSocket.getRemoteSocketAddress());

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                Scanner console = new Scanner(System.in);

                // Thread to read messages from client and print to console
                Thread reader = new Thread(() -> {
                    String line;
                    try {
                        while ((line = in.readLine()) != null) {
                            System.out.println("[Client] " + line);
                        }
                    } catch (IOException e) {
                        System.out.println("Connection closed by client.");
                    }
                });
                reader.start();

                // Thread to read from console and send to client
                Thread writer = new Thread(() -> {
                    System.out.println("Type messages to send to the client. Type 'exit' to quit.");
                    while (true) {
                        String msg = console.nextLine();
                        out.println(msg);
                        if ("exit".equalsIgnoreCase(msg.trim())) {
                            try {
                                clientSocket.close();
                            } catch (IOException ignored) {}
                            break;
                        }
                    }
                });
                writer.start();

                // wait for threads to finish
                writer.join();
                reader.interrupt(); // if still blocked
            } catch (InterruptedException e) {
                System.out.println("Server interrupted.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server stopped.");
    }
}