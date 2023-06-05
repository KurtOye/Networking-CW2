// IN2011 Computer Networks
// Coursework 2022/2023
//
// Submission by
//
//
//

import java.security.*;
import java.util.*;
import java.net.*;
import java.nio.charset.*;
import java.io.*;


public class DSTNodes {

    private ArrayList<String> linkedNodes;
    private String nameOfNode;
    private HashMap<String, String> connectedNodes;


    // Do not change the interface!
    public DSTNodes(InetAddress host, int port, String id) {
        // Using the IP address, port number and identifier compute the node name and node ID.
        this.connectedNodes = new HashMap<>();
        this.nameOfNode = host.getHostAddress() + "/" + port + "/" + id;
        handleIncomingConnections(nameOfNode);

    }

    // Do not change the interface!
    public void handleIncomingConnections(String startingNodeName) {
        // Connect to the DSTHash23 network using the given node name.
        try {
            int port = Integer.parseInt(startingNodeName.split("/")[1]);
            ServerSocket socketOfServer = new ServerSocket(port);

            while (true) {
                Socket s = socketOfServer.accept();
                System.out.print("New Node Connection");

                PrintWriter writer = new PrintWriter(s.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));

                writer.println("HELLO " + this.nameOfNode);
                linkedNodes.add(s.toString());

                String serverLine;
                while ((serverLine = reader.readLine()) != null) {
                    handleRequest(s, writer, reader, serverLine);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Accept multiple incoming connections and respond to all protocol commands correctly.

        //return;
    }
    private void handleRequest(Socket s, PrintWriter pw, BufferedReader br, String appeal) throws IOException {
        String[] call = appeal.split(" ");
        String request = call[0];


        if(request.equals("BYE")){
            pw.println("BYE");
            linkedNodes.remove(s.toString());
            s.close();
        }
        else if(request.equals("LOOKUP")){
            String key1 = call[1];
            if (connectedNodes.containsKey(key1)) {
                String value1 = connectedNodes.get(key1);
                int no1 = value1.split("\n").length;

                pw.print("FOUND " + no1);

                pw.println(value1);
            }
        }
        else if(request.equals("STORE")){
            int no = Integer.parseInt(call[1]);
            StringBuilder buildString = new StringBuilder();

            for (int i1 = 0; i1 < no; i1++) {
                buildString.append(br.readLine());
                if (i1 < no - 1) buildString.append("\n");
            }

            String value = buildString.toString();
            String key = SHA(value);

            connectedNodes.put(key, value);
            pw.println("STORED " + key);
        }
        else if(request.equals("PING")){
            pw.println("PONG");
        }
        else pw.println("UNKNOWN");

    }

    private static String SHA(final String data) {
        try {
            MessageDigest digestMessage = MessageDigest.getInstance("SHA-256");
            byte[] hashKey = digestMessage.digest(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder stringHex = new StringBuilder();

            for (byte yte : hashKey) {
                stringHex.append(String.format("%02x", yte));
            }
            return stringHex.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("SHA256 Failure");
        }
        return null;
    }

    public void printLinkedNodes(){
        for(String node: this.linkedNodes){
            System.out.println(node);
        }
    }
}
