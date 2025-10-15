import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class CalculatorServer {

    public static void main(String[] args) {
        // The server will listen on port 9999
        try (ServerSocket serverSocket = new ServerSocket(9999)) {
            System.out.println("Server is running and waiting for clients...");

            // The server runs indefinitely, waiting for client connections
            while (true) {
                Socket clientSocket = null;
                try {
                    // Accept a new client connection
                    clientSocket = serverSocket.accept();
                    System.out.println("A new client has connected: " + clientSocket.getInetAddress().getHostAddress());

                    // Create input and output streams for communication with the client
                    DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

                    System.out.println("Assigning new thread for this client");

                    // Create a new thread to handle the client's requests
                    // This allows the server to handle multiple clients simultaneously
                    Thread clientHandler = new ClientHandler(clientSocket, dis, dos);
                    clientHandler.start();

                } catch (IOException e) {
                    if (clientSocket != null) {
                        clientSocket.close();
                    }
                    System.err.println("Error accepting client connection: " + e.getMessage());
                    // Continue to the next iteration to wait for another client
                }
            }
        } catch (IOException e) {
            System.err.println("Server could not start on port 9999: " + e.getMessage());
        }
    }
}

// ClientHandler class to manage each client connection in a separate thread
class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final DataInputStream dis;
    private final DataOutputStream dos;

    public ClientHandler(Socket socket, DataInputStream dis, DataOutputStream dos) {
        this.clientSocket = socket;
        this.dis = dis;
        this.dos = dos;
    }

    @Override
    public void run() {
        String receivedExpression;
        while (true) {
            try {
                // Read the arithmetic expression from the client
                receivedExpression = dis.readUTF();

                // If the client sends "exit", close the connection and terminate the thread
                if (receivedExpression.equalsIgnoreCase("exit")) {
                    System.out.println("Client " + this.clientSocket.getInetAddress().getHostAddress() + " requested to disconnect.");
                    System.out.println("Closing this connection.");
                    this.clientSocket.close();
                    break;
                }

                System.out.println("Received expression: " + receivedExpression);

                // Use the JavaScript engine to evaluate the mathematical expression
                ScriptEngineManager manager = new ScriptEngineManager();
                ScriptEngine engine = manager.getEngineByName("JavaScript");
                String result;
                try {
                    // Evaluate the expression and convert the result to a string
                    Object evalResult = engine.eval(receivedExpression);
                    result = evalResult.toString();
                } catch (ScriptException e) {
                    // If the expression is invalid, send an error message
                    result = "Error: Invalid Expression";
                }
                
                System.out.println("Sending result: " + result);
                // Send the result back to the client
                dos.writeUTF(result);

            } catch (IOException e) {
                // Handle cases where the client disconnects unexpectedly
                System.err.println("Connection lost with client " + this.clientSocket.getInetAddress().getHostAddress());
                break; // Exit the loop and terminate the thread
            }
        }

        // Close the resources
        try {
            this.dis.close();
            this.dos.close();
        } catch (IOException e) {
            System.err.println("Error closing streams: " + e.getMessage());
        }
    }
}
