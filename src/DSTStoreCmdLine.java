// IN2011 Computer Networks
// Coursework 2022/2023
//
// This is an example of how the src.DSTStore object can be tested.
// This file should work with your submission without any changes.
// You do not need to submit this file; it is just to help you test.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DSTStoreCmdLine {

    public static void main(String[] args) {
	if (args.length != 1) {
	    System.err.println("Usage error!");
	    System.err.println("src.DSTStoreCmdLine startingNodeName");
	    return;
	} else {
	    String startingNodeName = args[0];

	    // Read in the value to store from stdin
	    String value = "";
	    try {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = "Hello";
		do {
		    line = br.readLine();
		    if (line == null) {
			break;
		    } else {
			value += (line + '\n');
		    }
		} while (true);
	    } catch (IOException e) {
		System.err.println("Exception thrown while reading in value to store");
		System.err.println(e);
		e.printStackTrace(System.err);
	    }
		
	    
	    // Use a src.DSTStore object to get the value from the network
	    DSTStore store = new DSTStore();

	    if (store.storeValue(startingNodeName, value)) {
		System.out.println("Store worked! :-)");
	    } else {
		System.out.println("Store failed! :-(");
	    }
	    return;
	}
    }
}
