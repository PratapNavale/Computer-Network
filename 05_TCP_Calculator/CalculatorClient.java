import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class CalculatorClient {

    public static void main(String[] args) {
        // Use "localhost" if the server is running on the same machine
        final String serverAddress = "localhost";
        final int serverPort = 9999;

        try (
            // Establish a connection to the server
            Socket socket = new Socket(serverAddress, serverPort);
            // Create input and output streams for communication
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            // Create a scanner to read user input from the console
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Connected to the calculator server.");
            System.out.println("Enter an arithmetic expression (e.g., 5 * 10) or type 'exit' to quit.");

            while (true) {
                System.out.print("Expression: ");
                String expression = scanner.nextLine();

                // Send the expression to the server
                dos.writeUTF(expression);

                // If the user types "exit", break the loop and close the client
                if (expression.equalsIgnoreCase("exit")) {
                    System.out.println("Closing the connection.");
                    break;
                }

                // Read the result from the server
                String result = dis.readUTF();
                System.out.println("Server Result: " + result);
            }

        } catch (UnknownHostException e) {
            System.err.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O Error: " + e.getMessage());
        }
    }
}
