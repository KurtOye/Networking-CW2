// IN2011 Computer Networks
// Coursework 2022/2023
//
// Submission by
// Kurt Oyewole
// 20384463
// kurt.oyewole@city.ac.uk

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;

public class DSTStore {

    // Do not change the interface!
    public DSTStore() {
    }

    // Do not change the interface!
    public boolean storeValue(String startingNodeName, String value) {

        // Compute the key for the input using the SHA-256 hash.
            String key = sha256(value);

        // Connect to the DSTHash23 network using startingNodeName.
        boolean stored = false;
        System.out.println("here");
        try {
            Socket socket = new Socket(startingNodeName, 20111);
            PrintWriter pr = new PrintWriter(socket.getOutputStream(), true);
            pr.println("HELLO ephemeral");

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String strIn = br.readLine();
            System.out.println(strIn);


            pr.println("FINDNEAREST " + key);
            ArrayList<String> nodes = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                String line = br.readLine();
                nodes.add(line);
                System.out.println("here");
            }

            for (int i = 1; i < nodes.size(); i++) {

                System.out.println("here");

                String str1 = nodes.get(i);
                String[] split = str1.split("/");
                String ip = split[0];
                int port = Integer.parseInt(split[1]);

                Socket nearSocket = new Socket(ip, port);

                PrintWriter otherPW = new PrintWriter(nearSocket.getOutputStream(), true);
                BufferedReader otherBR = new BufferedReader(new InputStreamReader(nearSocket.getInputStream()));
                otherPW.println("HELLO ephemeral");
                System.out.println(otherBR.readLine());

                otherPW.println("STORE " + key.length());
                String isStored = otherBR.readLine();
                System.out.println(isStored);

                if(isStored.startsWith("STORED")){
                    stored = true;
                } else break;
            }

            pr.println("BYE Time-out");
            pr.close();
            br.close();
            socket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stored;
    }

    public static String sha256(final String data) {
        try {
            final byte[] hash = MessageDigest.getInstance("").digest(data.getBytes(StandardCharsets.UTF_8));
            final StringBuilder hashStr = new StringBuilder(hash.length);

            for (byte hashByte: hash){
                hashStr.append(Integer.toHexString(255 & hashByte));
            }
            return hashStr.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String HexToBinary(String hex){
        int num = (Integer.parseInt("hex", 16));
        return Integer.toBinaryString(num);
    }
}

