// IN2011 Computer Networks
// Coursework 2022/2023
//
// Submission by
// Kurt Oyewole
// 20384463
// kurt.oyewole@city.ac.uk

import java.io.*;
import java.net.Socket;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


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
            PrintWriter pr = new PrintWriter(socket.getOutputStream(), true);
            pr.println("HELLO ephemeral");

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String strIn = br.readLine();
            System.out.println(strIn);

            // Find the node in the network with the ID closest to the key.
            pr.println("FINDNEAREST " + key);
            ArrayList<String> nodes = new ArrayList<>();
            String response;
            for (int i = 0; i < 5; i++) {
                String line = br.readLine();
                nodes.add(line);
                //System.out.println(line);
            }

            if (nodes.size() < 2) {
                output = "No nearest node found for key: " + key;
                pr.close();
                br.close();
                socket.close();
                return output;
            }

            for (int i = 1; i < nodes.size(); i++) {

                String str1 = nodes.get(i);
                String[] split = str1.split("/");
                String ip = split[0];
                int port = Integer.parseInt(split[1]);

                Socket nearSocket = new Socket(ip, port);

                PrintWriter nearPW = new PrintWriter(nearSocket.getOutputStream(), true);
                nearPW.println("HELLO ephemeral");
                nearPW.println("LOOKUP " + key);

                BufferedReader nearBR = new BufferedReader(new InputStreamReader(nearSocket.getInputStream()));
                nearBR.readLine();
                String nearOut = nearBR.readLine();

                if (nearOut.startsWith("FOUND")) {
                    String[] split2 = nearOut.split(" ");
                    int num = Integer.parseInt(split2[1]);
                    output = "";
                    for (int y = 0; y < num; y++) {
                        output = output.concat(nearBR.readLine() + "\n");
                    }
                }

                nearPW.println("BYE Time-out");
                nearPW.close();
                nearBR.close();
                nearSocket.close();

                if (!output.equals("NOTFOUND")) {
                    break;
                }
            }

            pr.println("BYE Time-out");
            pr.close();
            br.close();
            socket.close();
            return output;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}

