import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;

/**
 * Simple TCP File Client
 * Connects to the server and saves the received file.
 */
public class FileClient {

    public static void main(String[] args) {
        String serverIP = "127.0.0.1";    // Replace with server IP if on LAN
        int port = 5000;
        String outputFile = "received.txt";

        try (Socket socket = new Socket(serverIP, port);
             InputStream in = socket.getInputStream();
             FileOutputStream fos = new FileOutputStream(outputFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            System.out.println("Connected to server. Receiving file...");

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.flush();
            System.out.println("File received successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
