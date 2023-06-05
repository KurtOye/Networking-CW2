// IN2011 Computer Networks
// Coursework 2022/2023
//
// Submission by
//
//
//

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class DSTLookup {

    // Do not change the interface!
    public DSTLookup() {
    }

    // Do not change the interface!
    public String getValue(String startingNodeName, String key) {
        String out = "NOTFOUND";



        // Connect to the DSTHash23 network using startingNodeName.
        System.out.println("Connecting to " + startingNodeName);
        try {

            String[] split1 = startingNodeName.split("/");
            int port = Integer.parseInt(split1[1]);
            String IP = split1[0];


            Socket socket = new Socket(IP, port);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.println("HELLO ephemeral");

            String in = reader.readLine();
            System.out.println(in);

            // Find the node in the network with the ID closest to the key.
            writer.println("FINDNEAREST " + key);
            ArrayList<String> nodes = new ArrayList<>();

            for (int i = 0; i < 3; i++) {
                String line = reader.readLine();
                nodes.add(line);
            }

            if (nodes.size() < 2) {
                out = "No nearest node found for key: " + key;
                writer.close();
                reader.close();
                socket.close();
                return out;
            }

            for (int i = 1; i < nodes.size(); i++) {

                String str1 = nodes.get(i);
                String[] split = str1.split("/");
                String ip1 = split[0];
                int port1 = Integer.parseInt(split[1]);

                Socket nearSocket = new Socket(ip1, port1);

                PrintWriter nearPW = new PrintWriter(nearSocket.getOutputStream(), true);
                BufferedReader nearBR = new BufferedReader(new InputStreamReader(nearSocket.getInputStream()));

                nearPW.println("HELLO ephemeral");
                nearPW.println("LOOKUP " + key);

                nearBR.readLine();
                String nearOut = nearBR.readLine();

                if (nearOut.startsWith("FOUND")) {
                    String[] split2 = nearOut.split(" ");
                    int num = Integer.parseInt(split2[1]);
                    out = "";
                    for (int i1 = 0; i1 < num; i1++) {
                        out = out.concat(nearBR.readLine() + "\n");
                    }
                }

                nearPW.close();
                nearBR.close();
                nearSocket.close();
                if (!out.equals("NOTFOUND")) {
                    break;
                }
            }

            writer.close();
            reader.close();
            socket.close();
            return out;


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}