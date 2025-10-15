import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CombinedServer {

    public static void main(String[] args) {
        // The server will listen on port 9999
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Combined Server is running and waiting for clients...");

            // The server runs indefinitely, waiting for client connections
            while (true) {
                Socket clientSocket = null;
                try {
                    // Accept a new client connection
                    clientSocket = serverSocket.accept();
                    System.out.println("A new client has connected: " + clientSocket.getInetAddress().getHostAddress());

                    // Create a new thread to handle the client's requests
                    Thread clientHandler = new ClientHandler(clientSocket);
                    clientHandler.start();

                } catch (IOException e) {
                    if (clientSocket != null) {
                        clientSocket.close();
                    }
                    System.err.println("Error accepting client connection: " + e.getMessage());
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

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())) {

            // Read the client's choice to determine which service to provide
            int choice = dis.readInt();

            switch (choice) {
                case 1:
                    handleMessaging(dis, dos);
                    break;
                case 2:
                    handleFileTransfer(dis, dos);
                    break;
                case 3:
                    handleCalculator(dis, dos);
                    break;
                default:
                    dos.writeUTF("Error: Invalid choice.");
                    break;
            }

        } catch (IOException e) {
            System.err.println("Connection lost with client " + clientSocket.getInetAddress().getHostAddress());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private void handleMessaging(DataInputStream dis, DataOutputStream dos) throws IOException {
        System.out.println("Client selected: Messaging");
        dos.writeUTF("Messaging service started. Say 'hello' or 'exit' to quit.");
        String clientMessage;
        while (true) {
            clientMessage = dis.readUTF();
            if (clientMessage.equalsIgnoreCase("exit")) {
                break;
            }
            System.out.println("Client says: " + clientMessage);
            dos.writeUTF("Hello back from the server!");
        }
        System.out.println("Messaging session ended for client.");
    }

    private void handleFileTransfer(DataInputStream dis, DataOutputStream dos) throws IOException {
        System.out.println("Client selected: File Transfer");
        String fileName = dis.readUTF();
        long fileSize = dis.readLong();
        System.out.println("Receiving file: " + fileName + " (" + fileSize + " bytes)");
        
        File dir = new File("server_files");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream("server_files/" + fileName)) {
            byte[] buffer = new byte[4096];
            int read;
            long totalRead = 0;
            while (totalRead < fileSize && (read = dis.read(buffer, 0, (int)Math.min(buffer.length, fileSize - totalRead))) != -1) {
                fos.write(buffer, 0, read);
                totalRead += read;
            }
            dos.writeUTF("File '" + fileName + "' received successfully.");
            System.out.println("File received: " + fileName);
        } catch (IOException e) {
            dos.writeUTF("Error receiving file: " + e.getMessage());
            System.err.println("Error during file reception: " + e.getMessage());
        }
    }

    private void handleCalculator(DataInputStream dis, DataOutputStream dos) throws IOException {
        System.out.println("Client selected: Calculator");
        dos.writeUTF("Calculator service started. Send expressions (e.g., 5 + 2) or 'exit' to quit.");
        String receivedExpression;

        while (true) {
            receivedExpression = dis.readUTF();
            if (receivedExpression.equalsIgnoreCase("exit")) {
                break;
            }
            System.out.println("Received expression: " + receivedExpression);

            String result = evaluateExpression(receivedExpression);
            dos.writeUTF(result);
        }
        System.out.println("Calculator session ended for client.");
    }

    private String evaluateExpression(String expression) {
        // This is a simple evaluator for expressions like "number operator number"
        try {
            // Remove all whitespace
            expression = expression.replaceAll("\\s+", "");

            // Find the operator, skipping the first character for negative numbers
            int operatorIndex = -1;
            char operator = ' ';
            for (int i = 1; i < expression.length(); i++) {
                char c = expression.charAt(i);
                if (c == '+' || c == '-' || c == '*' || c == '/') {
                    operatorIndex = i;
                    operator = c;
                    break;
                }
            }

            if (operatorIndex == -1) {
                // Check if the first character is an operator (e.g. +5 or -2)
                char firstChar = expression.charAt(0);
                 if (firstChar == '+' || firstChar == '-' || firstChar == '*' || firstChar == '/') {
                    return "Error: Invalid expression format. Operator at the beginning.";
                 }
                return "Error: Operator not found or invalid format.";
            }

            String op1Str = expression.substring(0, operatorIndex);
            String op2Str = expression.substring(operatorIndex + 1);

            double operand1 = Double.parseDouble(op1Str);
            double operand2 = Double.parseDouble(op2Str);

            double result = 0;
            switch (operator) {
                case '+':
                    result = operand1 + operand2;
                    break;
                case '-':
                    result = operand1 - operand2;
                    break;
                case '*':
                    result = operand1 * operand2;
                    break;
                case '/':
                    if (operand2 == 0) {
                        return "Error: Division by zero.";
                    }
                    result = operand1 / operand2;
                    break;
            }

            // Return an integer string if the result is a whole number
            if (result == (long) result) {
                return String.format("%d", (long) result);
            } else {
                return String.format("%s", result);
            }

        } catch (NumberFormatException e) {
            return "Error: Invalid number in expression.";
        } catch (Exception e) {
            // Catch any other parsing errors
            return "Error: Invalid expression format.";
        }
    }
}

