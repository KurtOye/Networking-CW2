import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    /**
     * Initialise a new server. To run the server, call run().
     */
    public TCPServer() {}

    /**
     * Runs the server.
     * @throws IOException
     */
    public void run() throws IOException {

        /*** Set up to accept incoming TCP connections ***/

        // Port numbers will be in lecture 6
        int port = 8080;

        // Open the server socket
        System.out.println("Opening the server socket on port " + port);
        ServerSocket serverSocket = new ServerSocket(port);


        /*** Receive client connection ***/

        // Waits until a client connects
        System.out.println("Server waiting for client...");
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected!");

        // Set up readers and writers for convenience
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        Writer writer = new OutputStreamWriter(clientSocket.getOutputStream());



        /*** Output what the client says ***/

        String msg;
        while (true) {
            msg = reader.readLine();
            if (msg == null) {
                break;
            }
            System.out.println("Client says " + msg);
        }

        // Close down the connection
        clientSocket.close();
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        TCPServer server = new TCPServer();
        server.run();
    }
}