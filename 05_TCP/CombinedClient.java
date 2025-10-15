import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class CombinedClient {

    public static void main(String[] args) {
        final String serverAddress = "localhost";
        final int serverPort = 12345;

        try (Socket socket = new Socket(serverAddress, serverPort);
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to the combined server.");
            System.out.println("Please select an option:");
            System.out.println("1. Messaging (Say Hello)");
            System.out.println("2. File Transfer");
            System.out.println("3. Calculator");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            dos.writeInt(choice);

            switch (choice) {
                case 1:
                    startMessaging(dis, dos, scanner);
                    break;
                case 2:
                    startFileTransfer(dis, dos, scanner);
                    break;
                case 3:
                    startCalculator(dis, dos, scanner);
                    break;
                default:
                    System.out.println("Invalid choice. Closing connection.");
                    break;
            }

        } catch (UnknownHostException e) {
            System.err.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O Error: " + e.getMessage());
        }
    }

    private static void startMessaging(DataInputStream dis, DataOutputStream dos, Scanner scanner) throws IOException {
        System.out.println(dis.readUTF()); // Read initial server message
        while (true) {
            System.out.print("Enter message: ");
            String message = scanner.nextLine();
            dos.writeUTF(message);
            if (message.equalsIgnoreCase("exit")) {
                break;
            }
            String serverResponse = dis.readUTF();
            System.out.println("Server: " + serverResponse);
        }
    }

    private static void startFileTransfer(DataInputStream dis, DataOutputStream dos, Scanner scanner) throws IOException {
        System.out.print("Enter the path of the file to send: ");
        String filePath = scanner.nextLine();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("File does not exist.");
            // We need to inform the server we are aborting.
            dos.writeUTF("ABORT"); // A simple protocol to abort
            return;
        }

        dos.writeUTF(file.getName());
        dos.writeLong(file.length());

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, read);
            }
            dos.flush();
        }

        System.out.println(dis.readUTF()); // Read confirmation from server
    }

    private static void startCalculator(DataInputStream dis, DataOutputStream dos, Scanner scanner) throws IOException {
        System.out.println(dis.readUTF()); // Read initial server message
        while (true) {
            System.out.print("Expression: ");
            String expression = scanner.nextLine();
            dos.writeUTF(expression);
            if (expression.equalsIgnoreCase("exit")) {
                break;
            }
            String result = dis.readUTF();
            System.out.println("Server Result: " + result);
        }
    }
}
