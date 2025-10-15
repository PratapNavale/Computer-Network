import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Simple TCP File Server
 * Listens on a port and sends the specified file to a client.
 */
public class FileServer {

    public static void main(String[] args) {
        int port = 5000;                 // Port number for connection
        String fileName = "D:\\HTML\\WT_Notes.txt";  // File to send

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Waiting for client on port " + port + "...");

            try (Socket socket = serverSocket.accept();
                 FileInputStream fis = new FileInputStream(fileName);
                 BufferedInputStream bis = new BufferedInputStream(fis);
                 OutputStream out = socket.getOutputStream()) {

                System.out.println("Client connected: " + socket.getInetAddress());

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                out.flush();
                System.out.println("File sent successfully.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
