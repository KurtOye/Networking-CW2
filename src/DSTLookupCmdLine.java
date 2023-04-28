// IN2011 Computer Networks
// Coursework 2022/2023
//
// This is an example of how the src.DSTLookup object can be tested.
// This file should work with your submission without any changes.
// You do not need to submit this file; it is just to help you test.

public class DSTLookupCmdLine {

    public static void main(String[] args) {
	if (args.length != 2) {
	    System.err.println("Usage error!");
	    System.err.println("src.DSTLookupCmdLine startingNodeName key");
	    return;
	} else {
	    String startingNodeName = args[0];
	    String keyInHex = args[1];

	    if (keyInHex.length() != 64 || !keyInHex.matches("^[0-9A-Fa-f]+$")) {
		System.err.println("A key must have 64 hex digits.");
		return;
	    }

	    // Use a src.DSTLookup object to get the value from the network
	    DSTLookup lookup = new DSTLookup();
	    String output = lookup.getValue(startingNodeName, keyInHex);

	    if (output != null) {
		System.out.print(output);
	    }
	    return;
	}
    }
}
