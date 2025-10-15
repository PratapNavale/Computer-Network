import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static final String HOST = "127.0.0.1"; // localhost
    public static final int PORT = 5000;

    public static void main(String[] args) {
        System.out.println("Client starting. Connecting to " + HOST + ":" + PORT + " ...");
        try (Socket socket = new Socket(HOST, PORT)) {
            System.out.println("Connected to server at " + socket.getRemoteSocketAddress());

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner console = new Scanner(System.in);

            // Thread to read messages from server
            Thread reader = new Thread(() -> {
                String line;
                try {
                    while ((line = in.readLine()) != null) {
                        System.out.println("[Server] " + line);
                    }
                } catch (IOException e) {
                    System.out.println("Connection closed by server.");
                }
            });
            reader.start();

            // Thread to read from console and send to server
            Thread writer = new Thread(() -> {
                System.out.println("Type messages to send to the server. Type 'exit' to quit.");
                while (true) {
                    String msg = console.nextLine();
                    out.println(msg);
                    if ("exit".equalsIgnoreCase(msg.trim())) {
                        try {
                            socket.close();
                        } catch (IOException ignored) {}
                        break;
                    }
                }
            });
            writer.start();

            // wait for threads
            writer.join();
            reader.interrupt();
        } catch (IOException e) {
            System.out.println("Unable to connect to server: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Client interrupted.");
        }
        System.out.println("Client stopped.");
    }
}