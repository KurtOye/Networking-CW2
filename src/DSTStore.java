// IN2011 Computer Networks
// Coursework 2022/2023
//
// Submission by
// Sofiane Zerrouk
// 210022770
// sofiane.zerrouk@city.ac.uk

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



public class DSTStore {

    private final ArrayList<String> failed = new ArrayList<>();

    // Do not change the interface!
    public DSTStore() {
    }

    // Do not change the interface!
    public boolean storeValue(String startingNodeName, String value) {

        String keyValue = SHA(value);
        String binaryKey = hexaToDecimal(keyValue); // Compute the key for the input using the SHA-256 hash.

        // Connect to the DSTHash23 network using startingNodeName.
        ArrayList<String> inspectedNodes = new ArrayList<>();
        Stack<String> nodesViewed = new Stack<>();

        firstNodeConnection(nodesViewed,startingNodeName );

        threeNearestNodes(inspectedNodes,keyValue,nodesViewed );

        for(String string: failed){
            inspectedNodes.remove(string);
        }

        String[] closestNodesArray = sortNodes(inspectedNodes,binaryKey);
        return storeKeyOnNode(closestNodesArray, value);
    }

    // Locates the closest nodes to a particular one and add them to the "nodesViewed" stack.

    public void findNearestNodes(Stack<String> nodesViewed, String presentNode, String key) {

        try {

            String[] split1 = presentNode.split("/");
            int port = Integer.parseInt(split1[1]);
            String ip = split1[0];

            Socket s = new Socket(ip, port);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            bufferedReader.readLine();

            PrintWriter printWriter = new PrintWriter(s.getOutputStream(), true);
            printWriter.println("HELLO ephemeral");
            printWriter.println("FINDNEAREST " + key); // FINDNEAREST call on system

            bufferedReader.readLine();

            for (int i = 0; i < 4; i++) {
                nodesViewed.push(bufferedReader.readLine());
            }

            printWriter.println("BYE Time-out");
            printWriter.close();
            bufferedReader.close();
            s.close();

        } catch (IOException ignore) {
            System.out.println("Connection Error");
            failed.add(presentNode);
        }
    }

    public void threeNearestNodes(ArrayList<String> nodesInspected,String key,Stack<String> nodesViewed) {

        String presentNode;
        while (!nodesViewed.isEmpty()) {
            presentNode = nodesViewed.pop();
            if (!nodesInspected.contains(presentNode)) {
                nodesInspected.add(presentNode);
                findNearestNodes(nodesViewed, presentNode, key);
            }
        }

    }

    private void firstNodeConnection(Stack<String> nodesNoticed,String firstNodeName) {

        String[] split = firstNodeName.split("/");
        int port = Integer.parseInt(split[1]);
        String IP = split[0];
        try {
            Socket s = new Socket(IP, port);
            PrintWriter writer = new PrintWriter(s.getOutputStream(), true);
            writer.println("HELLO ephemeral");

            BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String firstNode = reader.readLine();

            nodesNoticed.push(firstNode.substring(6));

            writer.println("BYE Timeout");
            s.close();
        } catch (IOException e) {
            System.out.println("Connection to firstNode failed");
        }
    }

    public boolean storeKeyOnNode(String[] nodesList, String valueStore) {
        boolean successStored = false;

        for (int i = 0; i < nodesList.length; i++) {
            try {
                String[] nodeInfo = nodesList[i].split("/");
                String ip = nodeInfo[0];
                int port = Integer.parseInt(nodeInfo[1]);
                Socket socket = new Socket(ip, port);

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println("HELLO ephemeral");

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                in.readLine();

                out.println("STORE " + valueStore.split("\n").length); // Stored the value on the nearest nodes
                out.println(valueStore);

                String stored = in.readLine();
                System.out.println(stored);

                out.println("BYE Timeout");
                in.close();
                out.close();
                socket.close();
                System.out.println("Disconnected from Node");

                if (stored.startsWith("STORED")) successStored = true;
                else {
                    successStored = false;
                    break;
                }

            } catch (IOException ignore) {
                System.out.println("Error connecting to Node");
            }
        }
        return successStored;
    }

    public String[] sortNodes(ArrayList<String> inspectedNodes, String binaryKey){
        String[] closestNodes = new String[3];
        int[] binaryDistance = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};

        for (String node : inspectedNodes) {
            String hash = SHA(node);
            String nodeBinary = hexaToDecimal(hash);
            int rangeFrom = rangeFromKeyToBin(nodeBinary, binaryKey);
            if (rangeFrom < binaryDistance[2]) {
                if (rangeFrom < binaryDistance[1]) {
                    if (rangeFrom < binaryDistance[0]) {
                        binaryDistance[2] = binaryDistance[1];
                        closestNodes[2] = closestNodes[1];
                        binaryDistance[1] = binaryDistance[0];
                        closestNodes[1] = closestNodes[0];
                        binaryDistance[0] = rangeFrom;
                        closestNodes[0] = node;
                    } else {
                        binaryDistance[2] = binaryDistance[1];
                        closestNodes[2] = closestNodes[1];
                        binaryDistance[1] = rangeFrom;
                        closestNodes[1] = node;
                    }
                } else {
                    binaryDistance[2] = rangeFrom;
                    closestNodes[2] = node;
                }
            }
        }
        return closestNodes;
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

    private String hexaToDecimal(String hexString) {
        StringBuilder binaryString = new StringBuilder();
        int startIndex = 0;
        int endIndex = hexString.length();
        if (hexString.startsWith("0x")) {
            startIndex = 2;
        }
        for (int i = startIndex; i < endIndex; i++) {
            char hexChar = hexString.charAt(i);
            String binaryValue = Integer.toBinaryString(Integer.parseInt(String.valueOf(hexChar), 16));
            binaryString.append(String.format("%4s", binaryValue).replace(' ', '0'));
        }
        return binaryString.toString();
    }


    public int rangeFromKeyToBin(String string1, String string2) {
        int range = 0;
        for (int x = 0; x < string1.length(); x++) {
            if (string1.charAt(x) != string2.charAt(x)) {
                range++;
            }
        }
        return range;
    }

}
