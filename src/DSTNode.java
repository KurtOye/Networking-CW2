// IN2011 Computer Networks
// Coursework 2022/2023
//
// Submission by
// Kurt Oyewole
// 210024643
// kurt.oyewole@city.ac.uk

import java.net.*;
import java.io.*;
import java.nio.charset.*;
import java.security.*;
import java.util.*;

public class DSTNode {
    private String nodeName;
    private HashMap<String, String> storedValues;
    private ArrayList<String> nodesConnected;

    // Do not change the interface!
    public DSTNode(InetAddress host, int port, String id) {
        // Using the IP address, port number and identifier compute the node name and node ID.
        this.nodeName = host.getHostAddress() + "/" + port + "/" + id;
        this.storedValues = new HashMap<>();
        handleIncomingConnections(nodeName);

    }

    // Do not change the interface!
    public void handleIncomingConnections(String startingNodeName) {
        // Connect to the DSTHash23 network using the given node name.
        try {
            ServerSocket server = new ServerSocket(Integer.parseInt(startingNodeName.split("/")[1]));

            while (true) {
                Socket accept = server.accept();
                System.out.print("User connected to Node");

                InputStream input = accept.getInputStream();
                OutputStream output = accept.getOutputStream();

                PrintWriter writer = new PrintWriter(output, true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                writer.println("HELLO " + this.nodeName);
                nodesConnected.add(accept.toString());

                String inLine;
                while ((inLine = reader.readLine()) != null) {
                    String[] call = inLine.split(" ");
                    switch (call[0]) {
                        case "PING":
                            writer.println("PONG");
                            break;
                        case "STORE":
                            int lineNo = Integer.parseInt(call[1]);
                            StringBuilder builder = new StringBuilder();
                            for (int x = 0; x < lineNo; x++) {
                                builder.append(reader.readLine());
                                if (x < lineNo - 1) builder.append("\n");
                            }
                            String value = builder.toString();
                            String key = sha256(value);
                            storedValues.put(key, value);
                            writer.println("STORED " + key);
                            break;
                        case "LOOKUP":
                            String key2 = call[1];
                            if (storedValues.containsKey(key2)) {
                                String value2 = storedValues.get(key2);
                                int linesNo2 = value2.split("\n").length;
                                writer.print("FOUND " + linesNo2);
                                writer.println(value2);
                            }
                            break;
                        case "BYE":
                            writer.println("BYE");
                            nodesConnected.remove(accept.toString());
                            accept.close();
                            break;
                        default:
                            writer.println("UNKNOWN COMMAND");
                            break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Accept multiple incoming connections and respond to all protocol commands correctly.

        //return;
    }

    public static String sha256(final String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return String.valueOf(hexString);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void listConnectedNodes(){
        System.out.println("Connected Nodes");
        for(String node: this.nodesConnected){
            System.out.println(node);
        }
        System.out.println("---------------");
    }
}
