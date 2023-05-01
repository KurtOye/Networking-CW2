// IN2011 Computer Networks
// Coursework 2022/2023
//
// Submission by
// Kurt Oyewole
// 20384463
// kurt.oyewole@city.ac.uk

import org.w3c.dom.Node;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class DSTLookup {


    // Do not change the interface!
    public DSTLookup() {
    }

    // Do not change the interface!
    public String getValue(String startingNodeName, String key) {
        String output = "NOTFOUND";

        // Connect to the DSTHash23 network using startingNodeName.
        try {
            Socket socket = new Socket(startingNodeName, 20111);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("HELLO ephemeral");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String str = in.readLine();
            System.out.println("client" + str);

            // Find the node in the network with the ID closest to the key.
            out.println("FINDNEAREST " + key);
            ArrayList<String> nodes = new ArrayList<>();
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                nodes.add(line);
                System.out.println(line);
            }

            if(nodes.size() < 2){
                output = "No nearest node found for key: " + key;
                out.close();
                in.close();
                socket.close();
                return output;
            }

            for (int i = 1; i < nodes.size(); i++) {

                String str1 = nodes.get(i);
                String[] split = str1.split("/");
                String port = split[1];

                Socket nearSocket = new Socket(str1, Integer.parseInt(port));
                PrintWriter nearOut = new PrintWriter(socket.getOutputStream(), true);
                nearOut.println("LOOKUP" + key);

                BufferedReader nearIn = new BufferedReader(new InputStreamReader(nearSocket.getInputStream()));
                output = nearIn.readLine();

                nearOut.close();
                nearIn.close();
                nearSocket.close();

                if (!output.equals("NOTFOUND")) {
                    break;
                }
            }

            out.println("BYE Time-out");
            out.close();
            in.close();
            socket.close();
            return output;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Otherwise return "NOTFOUND"

    }
}

