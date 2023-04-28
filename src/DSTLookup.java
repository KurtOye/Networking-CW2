// IN2011 Computer Networks
// Coursework 2022/2023
//
// Submission by
// Kurt Oyewole
// 20384463
// kurt.oyewole@city.ac.uk

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DSTLookup {

    // Do not change the interface!
    public DSTLookup () {}

    // Do not change the interface!
    public String getValue(String startingNodeName, String key) {
        List<String> nodeList = new ArrayList<>();
        nodeList.add(startingNodeName);
	// Connect to the DSTHash23 network using startingNodeName.
        try {
            Socket nodeSocket = new Socket(startingNodeName, 123);

            InputStream nodeInputStream = nodeSocket.getInputStream();
            OutputStream nodeOutputStream = nodeSocket.getOutputStream();

            nodeOutputStream.write("get_next_node_address".getBytes());

            byte[] buffer = new byte[1024];
            int bytesRead = nodeInputStream.read(buffer);

            String nextNodeAddress = new String(buffer, 0, bytesRead);

            while (!nextNodeAddress.isEmpty()){

                Socket nextNodeSocket = new Socket(nextNodeAddress, 1234);

                InputStream nextNodeInputStream = nextNodeSocket.getInputStream();
                OutputStream nextNodeOutputStream = nextNodeSocket.getOutputStream();

                buffer = new byte[1024];
                bytesRead = nextNodeInputStream.read(buffer);
                nextNodeAddress  = new String(buffer, 0, bytesRead);

                String nextNodeName = "node-" +nodeList.size();
                nodeList.add(nextNodeName);

                nextNodeInputStream.close();
                nextNodeOutputStream.close();
                nextNodeSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    // Find the node in the network with the ID closest to the key.
        for (String nodename : nodeList){
            //nodename;
        }
	// Use the key to get the value from the closest node.

	// If the value is found, return it.
	
        // If the value is not found, return null.
	return null;
    }
}
